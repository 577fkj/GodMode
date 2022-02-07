package com.kaisar.xposed.godmode.injection.hook;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.kaisar.xposed.godmode.injection.ViewController;
import com.kaisar.xposed.godmode.injection.util.Logger;
import com.kaisar.xposed.godmode.injection.util.Property;
import com.kaisar.xposed.godmode.rule.ActRules;
import com.kaisar.xposed.godmode.rule.ViewRule;
import com.kaisar.xposed.godmode.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import de.robv.android.xposed.XC_MethodHook;

import static com.kaisar.xposed.godmode.GodModeApplication.TAG;

/**
 * Created by jrsen on 17-10-15.
 */

public final class ActivityLifecycleHook extends XC_MethodHook implements Property.OnPropertyChangeListener<ActRules> {

    private static final WeakHashMap<Activity, OnLayoutChangeListener> sActivities = new WeakHashMap<>();
    private static final ActRules sActRules = new ActRules();

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

    @Override
    public void onPropertyChange(ActRules newActRules) {
        Set<Map.Entry<String, List<ViewRule>>> entries = newActRules.entrySet();
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            String key = entry.getKey();
            List<ViewRule> oldRules = sActRules.get(key);
            List<ViewRule> newRules = entry.getValue();
            if (newRules != null && oldRules != null) {
                oldRules.removeAll(newRules);
                if (oldRules.isEmpty()) sActRules.remove(key);
            }
        }
        //revoke old rules
        entries = sActRules.entrySet();
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            List<ViewRule> rules = entry.getValue();
            for (Activity activity : sActivities.keySet()) {
                if (TextUtils.equals(activity.getComponentName().getClassName(), entry.getKey())) {
                    ViewController.revokeRuleBatch(activity, rules);
                }
            }
        }
        //apply new rules
        sActRules.clear();
        sActRules.putAll(newActRules);
        entries = sActRules.entrySet();
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            List<ViewRule> rules = entry.getValue();
            for (Activity activity : sActivities.keySet()) {
                if (TextUtils.equals(activity.getComponentName().getClassName(), entry.getKey())) {
                    ViewController.applyRuleBatch(activity, rules);
                }
            }
        }
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
