package tiiehenry.viewcontroller.injection.control;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.Objects;

import de.robv.android.xposed.XposedHelpers;
import tiiehenry.viewcontroller.BuildConfig;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.DisplayUtils;

public class ViewExtractor {
    public static int[] getViewHierarchyDepth(View view) {
        int[] depth = new int[0];
        ViewParent parent = view.getParent();
        while (parent instanceof ViewGroup) {
            int[] newDepth = new int[depth.length + 1];
            System.arraycopy(depth, 0, newDepth, 1, depth.length);
            newDepth[0] = ((ViewGroup) parent).indexOfChild(view);
            depth = newDepth;
            view = (View) parent;
            parent = parent.getParent();
        }
        return depth;
    }

    public static ViewRule makeRule(View v) throws PackageManager.NameNotFoundException {
        Activity activity = getAttachedActivityFromView(v);
        Objects.requireNonNull(activity, "Can't found attached activity");
        int[] out = new int[2];
        v.getLocationInWindow(out);
        int x = out[0];
        int y = out[1];
        int width = v.getWidth();
        int height = v.getHeight();

        int[] viewHierarchyDepth = getViewHierarchyDepth(v);
        String activityClassName = activity.getComponentName().getClassName();
        String viewClassName = v.getClass().getName();
        Context context = v.getContext();
        Resources res = context.getResources();
        String resourceName = null;
        try {
            resourceName = v.getId() != View.NO_ID ? res.getResourceName(v.getId()) : null;
        } catch (Resources.NotFoundException ignore) {
            //the resource id may be declared in the plugin apk
        }
        String text = (v instanceof TextView && !TextUtils.isEmpty(((TextView) v).getText())) ? ((TextView) v).getText().toString() : "";
        String description = (!TextUtils.isEmpty(v.getContentDescription())) ? v.getContentDescription().toString() : "";
        String alias = !TextUtils.isEmpty(text) ? text : description;
        String packageName = context.getPackageName();
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        String label = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        String versionName = packageInfo.versionName;
        int versionCode = packageInfo.versionCode;
        return new ViewRule(true, label, packageName, versionName, versionCode, BuildConfig.VERSION_CODE, "", alias, (int) (v.getAlpha() * 255), x, y, width, height, viewHierarchyDepth, activityClassName, viewClassName, resourceName, text, description, false, View.INVISIBLE, 0, DisplayUtils.px2dp(v.getResources(), width), DisplayUtils.px2dp(v.getResources(), height), System.currentTimeMillis());
    }

    public static Activity getAttachedActivityFromView(View view) {
        Activity activity = getActivityFromViewContext(view.getContext());
        if (activity != null) {
            return activity;
        } else {
            ViewParent parent = view.getParent();
            return parent instanceof ViewGroup ? getAttachedActivityFromView((View) parent) : null;
        }
    }

    private static Activity getActivityFromViewContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            //这是不直接getBaseContext方法获取 因为撒比微信有个PluginContextWrapper getBaseContext返回的是this导致栈溢出
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            if (baseContext == context) {
                baseContext = (Context) XposedHelpers.getObjectField(context, "mBase");
            }
            return getActivityFromViewContext(baseContext);
        } else {
            return null;
        }
    }

    public static Bitmap snapshotView(View view) {
        boolean enable = view.isDrawingCacheEnabled();
        view.setDrawingCacheEnabled(true);
        Bitmap b = view.getDrawingCache();
        b = b == null ? snapshotViewCompat(view) : Bitmap.createBitmap(b);
        view.setDrawingCacheEnabled(enable);
        return b;
    }

    private static Bitmap snapshotViewCompat(View v) {
        //有些view宽高为0神奇!!!
        Bitmap b = Bitmap.createBitmap(Math.max(v.getWidth(), 1), Math.max(v.getHeight(), 1), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}
