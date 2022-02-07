package tiiehenry.viewcontroller.injection.hook;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import tiiehenry.viewcontroller.injection.control.ViewController;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.injection.util.Property;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import de.robv.android.xposed.XC_MethodHook;

/**
 */

public final class ActivityLifecycleHook extends XC_MethodHook implements Property.OnPropertyChangeListener<ActRules> {

    private static final WeakHashMap<Activity, OnLayoutChangeListener> sActivities = new WeakHashMap<>();
    private static final ActRules sActRules = new ActRules();
    private static final ActRules sBackup = new ActRules();
    private boolean enable = true;

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        Activity activity = (Activity) param.thisObject;
        String methodName = param.method.getName();
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        /*!!!这里有坑不要hook onCreate和onResume 因为getDecorView会执行installContentView的操作
         所以在Activity的子类中有可能去requestFeature会导致异常所以尽量找一个很靠后的生命周期函数*/
        if ("onPostResume".equals(methodName)) {
            if (!sActivities.containsKey(activity)) {
                OnLayoutChangeListener listener = new OnLayoutChangeListener(activity);
                decorView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
                sActivities.put(activity, listener);
            }
            Logger.d(TAG, "resume:" + sActivities);
        } else if ("onDestroy".equals(methodName)) {
            OnLayoutChangeListener listener = sActivities.remove(activity);
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            Logger.d(TAG, "destroy:" + sActivities);
        }
    }

    @Nullable
    private Activity getActivityByName(String name) {
        for (Activity activity : sActivities.keySet()) {
            if (TextUtils.equals(activity.getComponentName().getClassName(), name)) {
                return activity;
            }
        }
        return null;
    }

    @Override
    public void onPropertyChange(ActRules newActRules) {
        Set<Map.Entry<String, List<ViewRule>>> entries;
        if (!enable) {
            sBackup.addRules(newActRules);
//            sBackup.removeRules(newActRules);
//            sBackup.putAll(newActRules);
            //revoke old rules
            entries = sActRules.entrySet();
            for (Map.Entry<String, List<ViewRule>> entry : entries) {
                List<ViewRule> rules = entry.getValue();
                Activity activity=getActivityByName(entry.getKey());
                if (activity != null) {
                    ViewController.revokeRuleBatch(activity, rules);
                }
            }
            sActRules.clear();
            return;
        }

        sActRules.removeRules(newActRules);

        //revoke old rules
        entries = sActRules.entrySet();
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            List<ViewRule> rules = entry.getValue();
            Activity activity=getActivityByName(entry.getKey());
            if (activity != null) {
                ViewController.revokeRuleBatch(activity, rules);
            }
        }
        //apply new rules
        sActRules.clear();
        sActRules.putAll(newActRules);
        entries = newActRules.entrySet();
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            List<ViewRule> rules = entry.getValue();
            Activity activity=getActivityByName(entry.getKey());
            if (activity != null) {
                ViewController.applyRuleBatch(activity, rules);
            }
        }

    }

    public void setEnable(Boolean e) {
        if (enable == e) {
            return;
        }
        enable = e;
        if (enable) {
            onPropertyChange(sBackup);
            sBackup.clear();
        } else {
            sBackup.putAll(sActRules);
            onPropertyChange(new ActRules());
        }
//        Set<Map.Entry<String, List<ViewRule>>> entries = sActRules.entrySet();
//        sActRules.putAll(entries);
    }

    static final class OnLayoutChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        final WeakReference<Activity> activityReference;

        OnLayoutChangeListener(Activity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void onGlobalLayout() {
            applyRuleIfMatchCondition();
        }

        private void applyRuleIfMatchCondition() {
            try {
                Activity activity = Preconditions.checkNotNull(activityReference.get());
                List<ViewRule> rules = Preconditions.checkNotNull(sActRules.get(activity.getComponentName().getClassName()));
                if (!rules.isEmpty()) {
                    ViewController.applyRuleBatch(activity, rules);
                }
            } catch (Exception ignore) {
//                ignore.printStackTrace();
            }
        }

    }

}
