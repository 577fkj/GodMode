package tiiehenry.viewcontroller.service;


import static tiiehenry.viewcontroller.injection.util.FilePermissionUtils.S_IRWXG;
import static tiiehenry.viewcontroller.injection.util.FilePermissionUtils.S_IRWXO;
import static tiiehenry.viewcontroller.injection.util.FilePermissionUtils.S_IRWXU;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import tiiehenry.io.Filej;
import tiiehenry.viewcontroller.BuildConfig;
import tiiehenry.viewcontroller.IGodModeManager;
import tiiehenry.viewcontroller.IObserver;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.FilePermissionUtils;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.AppRules;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.Preconditions;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XposedHelpers;


/**
 * 上帝模式核心管理服务所有跨进程通讯均通过此服务
 * 该服务通过Xposed注入到SystemServer进程作为一个系统服务
 * Client端可以使用{@link GodModeManager#getDefault()}使用该服务提供的接口
 */

public final class GodModeManagerService extends IGodModeManager.Stub implements Handler.Callback {

    // /data/godmode
    private static final String BASE_DIR = String.format("%s/%s", Environment.getDataDirectory().getAbsolutePath(), "godmode");
    // /data/godmode/conf
    private static final String CONFIG_FILE_NAME = "conf";
    // /data/godmode/{package}/package.rule
    private static final String RULE_FILE_SUFFIX = ".rule";
    // /data/godmode/{package}/xxxxxxxxx.webp
    private static final String IMAGE_FILE_SUFFIX = ".webp";

    private static final int WRITE_RULE = 0x00002;
    private static final int DELETE_RULE = 0x00004;
    private static final int DELETE_RULES = 0x00008;
    private static final int UPDATE_RULE = 0x000016;
    private static final int UPDATE_APP_STATE = 0x000032;

    private final Logger mLogger;
    private final RemoteCallbackList<ObserverProxy> mRemoteCallbackList = new RemoteCallbackList<>();
    private final AppRules mAppRulesCache = new AppRules();
    private final Context mContext;
    private final Handler mHandle;
    private boolean mInEditMode;
    private boolean mStarted;


    public GodModeManagerService(Context context) {
        mLogger = Logger.getLogger("GMMService");
        mContext = context;
        HandlerThread workThread = new HandlerThread("work-thread");
        workThread.start();
        mHandle = new Handler(workThread.getLooper(), this);
        try {
            loadRuleData();
            mStarted = true;
        } catch (Exception e) {
            mStarted = false;
            mLogger.e("loadPreferenceData failed", e);
        }
    }

    private void loadRuleData() throws IOException {
        File dataDir = new File(getBaseDir());
        File[] packageDirs = dataDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (packageDirs != null && packageDirs.length > 0) {
            HashMap<String, ActRules> appRules = new HashMap<>();
            Gson gson = new Gson();
            for (File packageDir : packageDirs) {
                try {
                    String packageName = packageDir.getName();
                    File disabledFile = getAppDiabledFile(packageName);
                    String json = readAppRuleFile(packageName);
                    ActRules rules = gson.fromJson(json, ActRules.class);
                    Preconditions.checkNotNull(rules, packageName + " rules is null");
                    rules.setEnabled(!disabledFile.exists());
                    //compact rule
                    Iterator<Map.Entry<String, List<ViewRule>>> iterator = rules.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<ViewRule>> listEntry = iterator.next();
                        List<ViewRule> value = listEntry.getValue();
                        if (value == null || value.isEmpty()) {
                            iterator.remove();
                        }
                    }
                    if (rules.isEmpty()) {
                        Filej.deleteDir(packageDir);
                        continue;
                    }
                    appRules.put(packageName, rules);
                } catch (IOException e) {
                    mLogger.w("load rule fail", e);
                } catch (NullPointerException | JsonSyntaxException e) {
                    mLogger.e("load rule error", e);
                    Filej.deleteDir(packageDir);
                }
            }
            mAppRulesCache.putAll(appRules);
            mLogger.d("app rules cache=" + mAppRulesCache.size());
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WRITE_RULE: {
                Object[] args = (Object[]) msg.obj;
                ActRules actRules = (ActRules) args[0];
                String packageName = (String) args[1];
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(actRules);
                    writeToAppRuleFile(packageName,json);

                    notifyObserverRuleChanged(packageName, actRules);
                } catch (IOException e) {
                    mLogger.w("write rule failed", e);
                }
            }
            break;
            case DELETE_RULE: {
                Object[] args = (Object[]) msg.obj;
                ActRules actRules = (ActRules) args[0];
                String packageName = (String) args[1];
                ViewRule viewRule = (ViewRule) args[2];

                try {
                    Filej.deleteDir(new File(viewRule.imagePath));

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(actRules);
                    writeToAppRuleFile(packageName,json);

                    notifyObserverRuleChanged(packageName, actRules);
                } catch (IOException e) {
                    mLogger.w("delete rule failed", e);
                }
            }
            break;
            case DELETE_RULES: {
                try {
                    String packageName = (String) msg.obj;
                    Filej.deleteDir(new File(getAppDataDir(packageName)));
                    notifyObserverRuleChanged(packageName, new ActRules());
                } catch (FileNotFoundException e) {
                    mLogger.w("delete rules failed", e);
                }
            }
            break;
            case UPDATE_RULE: {
                Object[] args = (Object[]) msg.obj;
                ActRules actRules = (ActRules) args[0];
                String packageName = (String) args[1];
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(actRules);
                    writeToAppRuleFile(packageName,json);

                    notifyObserverRuleChanged(packageName, actRules);
                } catch (IOException e) {
                    mLogger.w("update rule failed", e);
                }
                break;
            }
            case UPDATE_APP_STATE: {
                Object[] args = (Object[]) msg.obj;
                String packageName = (String) args[0];
                boolean enable = (boolean) args[1];
                try {
                    File disabledFile = getAppDiabledFile(packageName);
                    if (enable) {
                        disabledFile.delete();
                    } else {
                        disabledFile.createNewFile();
                    }
                    notifyObserverAppStatusChanged(packageName, enable);
                } catch (IOException e) {
                    mLogger.w("update app state failed", e);
                }
                break;
            }
            default: {
                //not implements
            }
            break;
        }
        return true;
    }

    private boolean checkPermission(@NonNull String permPackage) {
        int callingUid = Binder.getCallingUid();
        String[] packagesForUid = mContext.getPackageManager().getPackagesForUid(callingUid);
        return packagesForUid != null && Arrays.asList(packagesForUid).contains(permPackage);
    }

    private void enforcePermission(@NonNull String[] permPackages, String message) throws RemoteException {
        for (String permPackage : permPackages) {
            if (checkPermission(permPackage)) {
                return;
            }
        }
        throw new RemoteException(message);
    }

    private void enforcePermission(@NonNull String permPackage, String message) throws RemoteException {
        if (!checkPermission(permPackage)) {
            throw new RemoteException(message);
        }
    }

    /**
     * Set edit mode
     *
     * @param enable enable or disable
     */
    @Override
    public void setEditMode(boolean enable) throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "set edit mode fail permission denied");
        if (!mStarted) return;
        mInEditMode = enable;
        if (Build.VERSION.SDK_INT >= 30) {
//                    try {
//                        Process process=   Runtime.getRuntime().exec("setprop debug.layout "+ enable+"\n");
//                        process.waitFor();
//                        process.destroy();
//                    } catch (IOException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
            try {
                @SuppressLint("PrivateApi")
                Class<?> DisplayPropertiesClass = Class.forName("android.sysprop.DisplayProperties");
                XposedHelpers.callStaticMethod(DisplayPropertiesClass, "debug_layout", enable);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        notifyObserverEditModeChanged(enable);
    }

    /**
     * Check in edit mode
     *
     * @return enable or disable
     */
    @Override
    public boolean isInEditMode() {
        return mInEditMode;
    }

    /**
     * Register an observer to be notified when status changed.
     *
     * @param packageName package name
     * @param observer    client observer
     */
    @Override
    public void addObserver(String packageName, IObserver observer) throws RemoteException {
        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "register observer fail permission denied");
        if (!mStarted) return;
        synchronized (mRemoteCallbackList) {
            mRemoteCallbackList.register(new ObserverProxy(packageName, observer));
        }
    }

    /**
     * Unregister an observer
     *
     * @param packageName package name
     * @param observer    client observer
     * @throws RemoteException nothing
     */
    @Override
    public void removeObserver(String packageName, IObserver observer) throws RemoteException {
        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "unregister observer fail permission denied");
        if (!mStarted) return;
        synchronized (mRemoteCallbackList) {
            mRemoteCallbackList.unregister(new ObserverProxy(packageName, observer));
        }
    }

    /**
     * Get all packages rules
     *
     * @return packages rules
     */
    @Override
    public AppRules getAllRules() throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "get all rules fail permission denied");
        if (!mStarted) return new AppRules();
        return mAppRulesCache;
    }

    /**
     * Get rules by package name
     *
     * @param packageName package name of the rule
     * @return rules
     */
    @Override
    public ActRules getRules(String packageName) throws RemoteException {
        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "get rules fail permission denied");
        if (!mStarted) return new ActRules();
        return mAppRulesCache.containsKey(packageName) ? mAppRulesCache.get(packageName) : new ActRules();
    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        try (
                FileChannel inputChannel = new FileInputStream(source).getChannel();
                FileChannel outputChannel = new FileOutputStream(dest).getChannel()
        ) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    /**
     * Write rule
     *
     * @param packageName package name of the rule
     * @param snapshot    snapshot image of the view
     */
//    @Override
    public boolean writeBitmap(String packageName, String fileName, Bitmap snapshot) {
//        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "write bitmap fail permission denied");
        try {
            String appDataDir = getAppDataDir(packageName);
            File file = new File(appDataDir, fileName);
            return saveBitmapToFile(snapshot, file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Write all rule 解决列表循环导入单个rule的线程冲突
     *
     * @param packageName packageName
     * @param actRules    actRules
     */
    @Override
    public boolean writeAllRule(String packageName, ActRules actRules) throws RemoteException {
        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "write rule fail permission denied");
        if (!mStarted) return false;
        try {
            mAppRulesCache.put(packageName, actRules);
            String appDataDir = getAppDataDir(packageName);
            for (List<ViewRule> ruleList : actRules.values()) {
                for (ViewRule viewRule : ruleList) {
                    File imgNewFile = new File(appDataDir, viewRule.imagePath);
                    viewRule.imagePath = imgNewFile.getAbsolutePath();
                }
            }
            mHandle.obtainMessage(WRITE_RULE, new Object[]{actRules, packageName/*, viewRule, snapshot*/}).sendToTarget();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Write rule
     *
     * @param packageName package name of the rule
     * @param viewRule    rule object
     * @param snapshot    snapshot image of the view
     */
    @Override
    public boolean writeRule(String packageName, ViewRule viewRule, Bitmap snapshot) throws RemoteException {
        enforcePermission(new String[]{packageName, BuildConfig.APPLICATION_ID}, "write rule fail permission denied");
        if (!mStarted) return false;
        try {
            ActRules actRules = mAppRulesCache.get(packageName);
            if (actRules == null) {
                mAppRulesCache.put(packageName, actRules = new ActRules());
            }
            List<ViewRule> viewRules = actRules.get(viewRule.activityClass);
            if (viewRules == null) {
                actRules.put(viewRule.activityClass, viewRules = new ArrayList<>());
            }
            viewRules.add(viewRule);

            String appDataDir = getAppDataDir(packageName);
            File file = new File(appDataDir, System.currentTimeMillis() + IMAGE_FILE_SUFFIX);
            viewRule.imagePath = file.getAbsolutePath();
            saveBitmapToFile(snapshot, file);

            mHandle.obtainMessage(WRITE_RULE, new Object[]{actRules, packageName}).sendToTarget();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update rule of package
     *
     * @param packageName package name of the rule
     * @param viewRule    rule object
     * @return success or fail
     */
    @Override
    public boolean updateRule(String packageName, ViewRule viewRule) throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "update rule fail permission denied");
        if (!mStarted) return false;
        try {
            ActRules actRules = mAppRulesCache.get(packageName);
            if (actRules == null) {
                mAppRulesCache.put(packageName, actRules = new ActRules());
            }
            List<ViewRule> viewRules = actRules.get(viewRule.activityClass);
            if (viewRules == null) {
                actRules.put(viewRule.activityClass, viewRules = new ArrayList<>());
            }
            //单独处理enable visibility
            boolean contains = false;
            for (ViewRule rule : viewRules) {
                if (viewRule.equalsSimple(rule)) {
                    contains = true;
                    rule.copy(viewRule);
                    break;
                }
            }
            if (contains) {

            } else {
                viewRules.add(viewRule);
            }
//            if (!viewRules.contains(viewRule)) {
//                viewRules.add(viewRule);
//            }
/*            int index = viewRules.indexOf(viewRule);
            if (index >= 0) {
                viewRules.set(index, viewRule);
            } else {
                viewRules.add(viewRule);
            }*/
            mHandle.obtainMessage(UPDATE_RULE, new Object[]{actRules, packageName}).sendToTarget();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete the single rule of package
     *
     * @param packageName package name of the rule
     * @param viewRule    rule object
     * @return success or fail
     */
    @Override
    public boolean deleteRule(String packageName, ViewRule viewRule) throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "delete rule fail permission denied");
        if (!mStarted) return false;
        try {
            ActRules actRules = Preconditions.checkNotNull(mAppRulesCache.get(packageName), "not found this rule can't delete.");
            List<ViewRule> viewRules = Preconditions.checkNotNull(actRules.get(viewRule.activityClass), "not found this rule can't delete.");
            boolean removed = viewRules.remove(viewRule);
            if (removed) {
                if (viewRules.isEmpty()) {
                    actRules.remove(viewRule.activityClass);
                    if (actRules.isEmpty()) {
                        mAppRulesCache.remove(packageName);
                    }
                }
                mHandle.obtainMessage(DELETE_RULE, new Object[]{actRules, packageName, viewRule}).sendToTarget();
            }
            return removed;
        } catch (Exception e) {
            mLogger.w("delete rule failed", e);
            return false;
        }
    }

    /**
     * Delete all rules of package
     *
     * @param packageName package name of the rule
     * @return success or fail
     */
    @Override
    public boolean deleteRules(String packageName) throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "delete rules fail permission denied");
        if (!mStarted) return false;
        mLogger.d("delete rules pkg=" + packageName + " cache=" + mAppRulesCache);
        if (mAppRulesCache.containsKey(packageName)) {
            mAppRulesCache.remove(packageName);
            mHandle.obtainMessage(DELETE_RULES, packageName).sendToTarget();
            return true;
        }
        return false;
    }

    /**
     * disable app of package
     *
     * @param packageName package name of the rule
     * @return success or fail
     */
    @Override
    public void setAppEnable(String packageName, boolean enable) throws RemoteException {
        if (!mStarted) return;
        ActRules actRules = mAppRulesCache.get(packageName);
        if (actRules == null) {
            return;
        }
        actRules.setEnabled(enable);
        mHandle.obtainMessage(UPDATE_APP_STATE, new Object[]{packageName, enable}).sendToTarget();
    }

    @Override
    public String[] getDisabledApps() throws RemoteException {
        ArrayList<String> list = new ArrayList<>();
        Set<String> names = mAppRulesCache.keySet();
        for (String name : names) {
            ActRules actRule = mAppRulesCache.get(name);
            if (actRule != null && !actRule.isEnabled()) {
                list.add(name);
            }
        }
        return list.toArray(new String[0]);
    }

    @Override
    public boolean isAppDisabled(String packageName) throws RemoteException {
        if (!mStarted)
            return false;
        ActRules actRule = mAppRulesCache.get(packageName);
        return actRule != null && !actRule.isEnabled();
    }

    @Override
    public ParcelFileDescriptor openImageFileDescriptor(String filePath) throws RemoteException {
        enforcePermission(BuildConfig.APPLICATION_ID, "open fd fail permission denied");
        if (!filePath.startsWith(BASE_DIR) || !filePath.endsWith(IMAGE_FILE_SUFFIX))
            throw new RemoteException(String.format("unauthorized access %s", filePath));
        try {
            return ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            RemoteException remoteException = new RemoteException();
            remoteException.initCause(e);
            throw remoteException;
        }
    }

    private boolean saveBitmapToFile(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        }
        try {
            try (FileOutputStream out = new FileOutputStream(file)) {
                if (bitmap.compress(Bitmap.CompressFormat.WEBP, 80, out)) {
                    FilePermissionUtils.setPermissions(file, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
                    return true;
                }
                throw new FileNotFoundException("bitmap can't compress to " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void notifyObserverAppStatusChanged(String packageName, Boolean enable) {
        runBroadcastActionForPackage(packageName, new Function<ObserverProxy, Boolean>() {
            @Override
            public Boolean apply(ObserverProxy observerProxy) {
                try {
                    observerProxy.onAppStatusChanged(enable);
                    return true;
                } catch (Exception e) {
                    mLogger.w("notify app status changed fail", e);
                }
                return false;
            }
        });

    }

    private void notifyObserverRuleChanged(String packageName, ActRules actRules) {
        runBroadcastActionForPackage(packageName, new Function<ObserverProxy, Boolean>() {
            @Override
            public Boolean apply(ObserverProxy observerProxy) {
                try {
                    observerProxy.onViewRuleChanged(packageName, actRules);
                    return true;
                } catch (Exception e) {
                    mLogger.w("notify rule changed fail", e);
                }
                return false;
            }
        });
    }

    private void runBroadcastActionForPackage(String packageName, Function<ObserverProxy, Boolean> action) {
        synchronized (mRemoteCallbackList) {
            final int N = mRemoteCallbackList.beginBroadcast();
            for (int i = 0; i < N; i++) {
                ObserverProxy observerProxy = mRemoteCallbackList.getBroadcastItem(i);
                if (TextUtils.equals(observerProxy.getPackageName(), packageName) || TextUtils.equals(observerProxy.getPackageName(), "*")) {
                    action.apply(observerProxy);
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }

    private void notifyObserverEditModeChanged(boolean enable) {
        synchronized (mRemoteCallbackList) {
            final int N = mRemoteCallbackList.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    mRemoteCallbackList.getBroadcastItem(i).onEditModeChanged(enable);
                } catch (Exception e) {
                    mLogger.w("notify edit mode changed fail", e);
                }
            }
            mRemoteCallbackList.finishBroadcast();
        }
    }

    private String getBaseDir() throws FileNotFoundException {
        File dir = new File(BASE_DIR);
        if (dir.exists() || dir.mkdirs()) {
            FilePermissionUtils.setPermissions(dir, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
            return dir.getAbsolutePath();
        }
        throw new FileNotFoundException();
    }

    private String getConfigFilePath() throws IOException {
        File file = new File(getBaseDir(), CONFIG_FILE_NAME);
        if (file.exists() || file.createNewFile()) {
            FilePermissionUtils.setPermissions(file, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
            return file.getAbsolutePath();
        }
        throw new FileNotFoundException();
    }

    private String getAppDataDir(String packageName) throws FileNotFoundException {
        File dir = new File(getBaseDir(), packageName);
        if (dir.exists() || dir.mkdirs()) {
            FilePermissionUtils.setPermissions(dir, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
            return dir.getAbsolutePath();
        }
        throw new FileNotFoundException();
    }

    private File getAppDiabledFile(String packageName) throws IOException {
        File file = new File(getAppDataDir(packageName), ".disabled");
//        if (file.exists()) {
//            FileUtils.setPermissions(file, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
//        }
        return file;
    }

    private void writeToAppRuleFile(String packageName,String json) throws IOException {
        String appRuleFilePath = getAppRuleFilePath(packageName);
        new Filej(appRuleFilePath).writeString(json);
    }

    private String readAppRuleFile(String packageName) throws IOException {
        String appRuleFilePath = getAppRuleFilePath(packageName);
        return  new Filej(appRuleFilePath).readString();
    }

    private String getAppRuleFilePath(String packageName) throws IOException {
        File file = new File(getAppDataDir(packageName), packageName + RULE_FILE_SUFFIX);
        if (file.exists() || file.createNewFile()) {
            FilePermissionUtils.setPermissions(file, S_IRWXU | S_IRWXG | S_IRWXO, -1, -1);
            return file.getAbsolutePath();
        }
        throw new FileNotFoundException();
    }

}
