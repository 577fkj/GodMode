package tiiehenry.viewcontroller.injection.control;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.DisplayUtils;
import tiiehenry.viewcontroller.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 */

public final class ViewController {

    private final static SparseArray<Pair<WeakReference<View>, ViewProperty>> blockedViewCache = new SparseArray<>();

    private static int getVersionCode(Activity activity) {
        boolean strictMode = false;
        try {
            ClassLoader cl = activity.getClassLoader();
            Class<?> BuildConfigClass = cl.loadClass(activity.getPackageName() + ".BuildConfig");
            return BuildConfigClass.getField("VERSION_CODE").getInt(null);
        } catch (Exception ignore) {
            try {
                PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
                return packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                Logger.w(TAG, "See what happened!", e);
            }
        }
        return -1;
    }

    public static void applyRuleBatch(Activity activity, List<ViewRule> rules) {
        int versionCode=getVersionCode(activity);
        Logger.d(TAG, "[ApplyRuleBatch info start------------------------------------]");
        for (ViewRule rule : new ArrayList<>(rules)) {
            if (!rule.enable) {
                continue;
            }
            try {
                Logger.d(TAG, "[Apply rule]:" + rule);
                int ruleHashCode = rule.hashCode();
                Pair<WeakReference<View>, ViewProperty> viewInfo = blockedViewCache.get(ruleHashCode);
                View view = viewInfo != null ? viewInfo.first.get() : null;
                if (view == null || !view.isAttachedToWindow()) {
                    blockedViewCache.delete(ruleHashCode);
                    view = ViewFinder.findViewBestMatch(activity, rule,versionCode);
                    Preconditions.checkNotNull(view, "apply rule fail not match any view");
                }
                boolean blocked = applyRule(view, rule);
                if (blocked) {
                    Logger.i(TAG, String.format("[Success] %s#%s has been blocked", activity, view));
                } else {
                    Logger.i(TAG, String.format("[Skipped] %s#%s already be blocked", activity, view));
                }
            } catch (NullPointerException e) {
                Logger.w(TAG, String.format("[Failed] %s#%s block failed because %s", activity, rule.viewClass, e.getMessage()));
            }
        }
        Logger.d(TAG, "[ApplyRuleBatch info end------------------------------------]");
    }

    private static int getNormalParamPxValue(View v, int value) {
        if (value > 0) {
            return DisplayUtils.dp2px(v.getResources(), value);
        }
        return value;
    }

    //    动态使用目标宽高没有改变，是因为在列表中，后面的rule覆盖了前面的rule
    private static void applyViewParamType(ViewGroup.LayoutParams lp, View v, ViewRule viewRule, ViewProperty viewProperty) {
        switch (viewRule.targetParamType) {
            case 0:
                break;
            case 1:
                lp.width = getNormalParamPxValue(v, viewRule.targetWidth);
//                lp.height = viewProperty.layout_params_height;
                break;
            case 2:
//                lp.width = viewProperty.layout_params_width;
                lp.height = getNormalParamPxValue(v, viewRule.targetHeight);
                break;
            case 3:
                lp.width = getNormalParamPxValue(v, viewRule.targetWidth);
                lp.height = getNormalParamPxValue(v, viewRule.targetHeight);
                break;
        }
    }

    private static void applyRuleVisibility(View v, ViewRule viewRule, ViewProperty viewProperty) {
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        switch (viewRule.visibility) {
            case View.VISIBLE:
                v.setAlpha(viewRule.getAlphaNormalized());
                v.setClickable(viewProperty.clickable);
                if (lp != null) {
                    lp.width = viewProperty.layout_params_width;
                    lp.height = viewProperty.layout_params_height;
                    applyViewParamType(lp, v, viewRule, viewProperty);
                }
                break;
            case View.INVISIBLE:
                v.setAlpha(viewRule.getAlphaNormalized());
                v.setClickable(false);
                if (lp != null) {
                    lp.width = viewProperty.layout_params_width;
                    lp.height = viewProperty.layout_params_height;
                    applyViewParamType(lp, v, viewRule, viewProperty);
                }
                break;
            case View.GONE:
                v.setAlpha(0f);
                v.setClickable(false);
                if (lp != null) {
                    lp.width = 0;
                    lp.height = 0;
                }
                break;
        }
        v.requestLayout();
        ViewCompat.setVisibility(v, viewRule.visibility);
    }

    public static boolean applyRule(View v, ViewRule viewRule) {
//        if (!viewRule.enable) {
//            return false;
//        }
        int ruleHashCode = viewRule.hashCode();
        Pair<WeakReference<View>, ViewProperty> viewInfo = blockedViewCache.get(ruleHashCode);
        View blockedView = viewInfo != null ? viewInfo.first.get() : null;
        ViewProperty viewProperty;
        if (blockedView == v) {
//            if (v.getVisibility() == viewRule.visibility && v.getVisibility() == View.VISIBLE) {
//                //no change
//                return false;
//            }
            viewProperty = viewInfo.second;
        } else {
            viewProperty = ViewProperty.create(v);
        }
        applyRuleAuto(v, viewRule);
        applyRuleVisibility(v, viewRule, viewProperty);
        blockedViewCache.put(ruleHashCode, Pair.create(new WeakReference<>(v), viewProperty));
//        Logger.d(TAG, String.format(Locale.getDefault(), "apply rule add view cache %d=%s", ruleHashCode, v));
//        Logger.d(TAG, "blockedViewCache:" + blockedViewCache);
        return true;
    }

    private static final ArrayList<Integer> clickedViews = new ArrayList<>();

    private static void applyRuleAuto(View v, ViewRule viewRule) {
        int code = viewRule.hashCode();
        if (!clickedViews.contains(code)) {
            if (viewRule.autoClick) {
                v.callOnClick();
                clickedViews.add(code);
            }
        }
    }

    public static void revokeRuleBatch(Activity activity, List<ViewRule> rules) {
        int versionCode=getVersionCode(activity);
        for (ViewRule rule : new ArrayList<>(rules)) {
            if (!rule.enable) {
                continue;
            }
            try {
                Logger.d(TAG, "revoke rule:" + rule);
                int ruleHashCode = rule.hashCode();
                Pair<WeakReference<View>, ViewProperty> viewInfo = blockedViewCache.get(ruleHashCode);
                View view = viewInfo != null ? viewInfo.first.get() : null;
                if (view == null || !view.isAttachedToWindow()) {
                    Logger.w(TAG, "view cache not found");
                    blockedViewCache.delete(ruleHashCode);
                    view = ViewFinder.findViewBestMatch(activity, rule, versionCode);
                    Logger.w(TAG, "find view in activity" + view);
                    Preconditions.checkNotNull(view, "revoke rule fail can't found block view");
                }
                revokeRule(view, rule);
                Logger.i(TAG, String.format("###revoke rule success [Act]:%s  [View]:%s", activity, view));
            } catch (NullPointerException e) {
                Logger.w(TAG, String.format("###revoke rule fail [Act]:%s  [View]:%s [Reason]:%s", activity, null, e.getMessage()));
            }
        }
    }

    public static void revokeRule(View v, ViewRule viewRule) {
        int ruleHashCode = viewRule.hashCode();
        Pair<WeakReference<View>, ViewProperty> viewInfo = blockedViewCache.get(ruleHashCode);
        if (viewInfo != null && viewInfo.first.get() == v) {
            ViewProperty viewProperty = viewInfo.second;
            v.setAlpha(viewProperty.alpha);
            v.setClickable(viewProperty.clickable);
            ViewCompat.setVisibility(v, viewProperty.visibility);
            ViewGroup.LayoutParams lp = v.getLayoutParams();
            if (lp != null) {
                lp.width = viewProperty.layout_params_width;
                lp.height = viewProperty.layout_params_height;
//                v.setLayoutParams(lp);
                v.requestLayout();
            }
            blockedViewCache.delete(viewRule.hashCode());
            Logger.d(TAG, String.format(Locale.getDefault(), "revoke blocked view %d=%s %s", ruleHashCode, v, viewProperty));
        } else {
            // cache missing why?
            Logger.w(TAG, "view cache missing why?");
            v.setAlpha(viewRule.getAlphaNormalized());
            ViewCompat.setVisibility(v, viewRule.visibility);
        }
    }

    private static final class ViewProperty {

        final float alpha;
        final boolean clickable;
        final int visibility;
        final int layout_params_width;
        final int layout_params_height;

        public ViewProperty(float alpha, boolean clickable, int visibility, int layout_params_width, int layout_params_height) {
            this.alpha = alpha;
            this.clickable = clickable;
            this.visibility = visibility;
            this.layout_params_width = layout_params_width;
            this.layout_params_height = layout_params_height;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ViewProperty{");
            sb.append("alpha=").append(alpha);
            sb.append(", clickable=").append(clickable);
            sb.append(", visibility=").append(visibility);
            sb.append(", layout_params_width=").append(layout_params_width);
            sb.append(", layout_params_height=").append(layout_params_height);
            sb.append('}');
            return sb.toString();
        }

        public static ViewProperty create(View view) {
            float alpha = view.getAlpha();
            boolean clickable = view.isClickable();
            int visibility = view.getVisibility();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            int width = layoutParams != null ? layoutParams.width : 0;
            int height = layoutParams != null ? layoutParams.height : 1;
            return new ViewProperty(alpha, clickable, visibility, width, height);
        }
    }

}
