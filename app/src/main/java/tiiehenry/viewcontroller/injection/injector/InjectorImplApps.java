package tiiehenry.viewcontroller.injection.injector;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import tiiehenry.viewcontroller.injection.control.ViewHelper;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.bridge.ManagerObserver;
import tiiehenry.viewcontroller.injection.hook.ActivityLifecycleHook;
import tiiehenry.viewcontroller.injection.hook.DispatchKeyEventHook;
import tiiehenry.viewcontroller.injection.hook.DisplayPropertiesHook;
import tiiehenry.viewcontroller.injection.hook.EventHandlerHook;
import tiiehenry.viewcontroller.injection.hook.SystemPropertiesHook;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.injection.util.PackageManagerUtils;
import tiiehenry.viewcontroller.injection.util.Property;
import tiiehenry.viewcontroller.rule.ActRules;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

//    Run in other application processes
public class InjectorImplApps extends InjectorImpl {
    enum State {
        UNKNOWN,
        ALLOWED,
        BLOCKED
    }

    private static State state = State.UNKNOWN;
    public final Property<Boolean> editModeProp = new Property<>();
    public final Property<Boolean> enableProp = new Property<>();
    public final Property<ActRules> actRuleProp = new Property<>();

    private final DispatchKeyEventHook dispatchKeyEventHook = new DispatchKeyEventHook(this);

    public String getPackageName() {
        return loadPackageParam.packageName;
    }

    public InjectorImplApps(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super(loadPackageParam);
    }

    private void hookDebugLayout() {
        //hook debug layout
        if (Build.VERSION.SDK_INT >= 30) {
            editModeProp.addOnPropertyChangeListener(new Property.OnPropertyChangeListener<Boolean>() {
                @Override
                public void onPropertyChange(Boolean enable) {
                    try {
                        @SuppressLint("PrivateApi")
                        Class<?> DisplayPropertiesClass = Class.forName("android.sysprop.DisplayProperties");
                        XposedHelpers.callStaticMethod(DisplayPropertiesClass, "debug_layout", enable);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (Build.VERSION.SDK_INT < 29) {
            SystemPropertiesHook systemPropertiesHook = new SystemPropertiesHook();
            editModeProp.addOnPropertyChangeListener(systemPropertiesHook);
            XposedHelpers.findAndHookMethod("android.os.SystemProperties", ClassLoader.getSystemClassLoader(), "native_get_boolean", String.class, boolean.class, systemPropertiesHook);
        } else {
            DisplayPropertiesHook displayPropertiesHook = new DisplayPropertiesHook();
            editModeProp.addOnPropertyChangeListener(displayPropertiesHook);
            XposedHelpers.findAndHookMethod("android.sysprop.DisplayProperties", ClassLoader.getSystemClassLoader(), "debug_layout", displayPropertiesHook);
        }

        //Disable show layout margin bound
        XposedHelpers.findAndHookMethod(ViewGroup.class, "onDebugDrawMargins", Canvas.class, Paint.class, XC_MethodReplacement.DO_NOTHING);

        //Disable GM component show layout bounds
        XC_MethodHook disableDebugDraw = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (editModeProp.get()) {
                    View view = (View) param.thisObject;
                    if (ViewHelper.TAG_GM_CMP.equals(view.getTag())) {
                        param.setResult(null);
                    }
                }
            }
        };
        XposedHelpers.findAndHookMethod(ViewGroup.class, "onDebugDraw", Canvas.class, disableDebugDraw);
        XposedHelpers.findAndHookMethod(View.class, "debugDrawFocus", Canvas.class, disableDebugDraw);
    }

    private void registerHook() {
        //hook activity#lifecycle block view
        ActivityLifecycleHook lifecycleHook = new ActivityLifecycleHook();
        actRuleProp.addOnPropertyChangeListener(lifecycleHook);
        XposedHelpers.findAndHookMethod(Activity.class, "onPostResume", lifecycleHook);
        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", lifecycleHook);


        hookDebugLayout();

        enableProp.addOnPropertyChangeListener(new Property.OnPropertyChangeListener<Boolean>() {
            @Override
            public void onPropertyChange(Boolean enable) {
                lifecycleHook.setEnable(enable);
            }
        });

        EventHandlerHook eventHandlerHook = new EventHandlerHook();
        editModeProp.addOnPropertyChangeListener(eventHandlerHook);
        dispatchKeyEventHook.setEventHandlerHook(eventHandlerHook);
        //Drag view support
        XposedHelpers.findAndHookMethod(View.class, "dispatchTouchEvent", MotionEvent.class, eventHandlerHook);
    }

    @Override
    public void handleLoadPackage() {
        GodModeManager gmManager = GodModeManager.getDefault();
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //Volume key select old
                dispatchKeyEventHook.setActivity((Activity) param.thisObject);
                super.afterHookedMethod(param);
            }
        });
//        onPause和onResume用来适配动态操作APP
        XposedHelpers.findAndHookMethod(Activity.class, "onPause", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                dispatchKeyEventHook.setDisplay(false);
            }
        });
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                notifyEditModeChanged(gmManager.isInEditMode());
            }
        });


//        XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//            }
//        });


        registerHook();

        gmManager.addObserver(getPackageName(), new ManagerObserver(this));
        actRuleProp.set(gmManager.getRules(getPackageName()));
        enableProp.set(!gmManager.isAppDisabled(getPackageName()));
    }

    private boolean checkBlockList(String packageName) {
        if (TextUtils.equals("com.android.systemui", packageName)) {
            return true;
        }
        try {
            //检查是否为launcher应用
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> resolveInfos;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                resolveInfos = PackageManagerUtils.queryIntentActivities(homeIntent, null, PackageManager.MATCH_ALL, 0);
            } else {
                resolveInfos = PackageManagerUtils.queryIntentActivities(homeIntent, null, 0, 0);
            }
//            Logger.d(TAG, "launcher apps:" + resolveInfos);
            if (resolveInfos != null) {
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (!TextUtils.equals("com.android.settings", packageName) && TextUtils.equals(resolveInfo.activityInfo.packageName, packageName)) {
                        return true;
                    }
                }
            }

            //检查是否为键盘应用
            Intent keyboardIntent = new Intent("android.view.InputMethod");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resolveInfos = PackageManagerUtils.queryIntentServices(keyboardIntent, null, PackageManager.MATCH_ALL, 0);
            } else {
                resolveInfos = PackageManagerUtils.queryIntentServices(keyboardIntent, null, 0, 0);
            }
//            Logger.d(TAG, "keyboard apps:" + resolveInfos);
            if (resolveInfos != null) {
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (TextUtils.equals(resolveInfo.serviceInfo.packageName, packageName)) {
                        return true;
                    }
                }
            }

            //检查是否为无界面应用
            PackageInfo packageInfo = PackageManagerUtils.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES, 0);
            if (packageInfo != null && packageInfo.activities != null && packageInfo.activities.length == 0) {
//                Logger.d(TAG, "no user interface app:" + resolveInfos);
                return true;
            }
        } catch (Throwable t) {
            Logger.e(TAG, "checkWhiteListPackage crash", t);
        }
        return false;
    }

    public void notifyEditModeChanged(boolean enable) {
        if (state == State.UNKNOWN) {
            state = checkBlockList(loadPackageParam.packageName) ? State.BLOCKED : State.ALLOWED;
        }
        if (state == State.ALLOWED) {
            editModeProp.set(enable);
            dispatchKeyEventHook.setDisplay(enable);
        }
     /*   try {
            @SuppressLint("PrivateApi")
            Class<?> DisplayPropertiesClass = Class.forName("android.sysprop.DisplayProperties");
            XposedHelpers.callStaticMethod(DisplayPropertiesClass, "debug_layout", enable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/
    }

    public void notifyViewRulesChanged(ActRules actRules) {
        actRuleProp.set(actRules);
    }

    public void notifyAppStatusChanged(boolean enable) {
        enableProp.set(enable);
    }
}
