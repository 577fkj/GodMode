package tiiehenry.viewcontroller.injection.control;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import tiiehenry.viewcontroller.BuildConfig;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.DisplayUtils;
import tiiehenry.viewcontroller.util.Preconditions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by jrsen on 17-10-13.
 */

public final class ViewHelper {

    public static final String TAG_GM_CMP = "gm_cmp";

    public static List<WeakReference<View>> buildViewNodes(View view) {
        ArrayList<WeakReference<View>> views = new ArrayList<>();
        if (view.getVisibility() == View.VISIBLE && !TAG_GM_CMP.equals(view.getTag())) {
            views.add(new WeakReference<>(view));
            if (view instanceof ViewGroup) {
                final int N = ((ViewGroup) view).getChildCount();
                for (int i = 0; i < N; i++) {
                    View childView = ((ViewGroup) view).getChildAt(i);
                    views.addAll(buildViewNodes(childView));
                }
            }
        }
        return views;
    }

    public static Rect getLocationInWindow(View v) {
        int[] out = new int[2];
        v.getLocationInWindow(out);
        int l = out[0];
        int t = out[1];
        int r = l + v.getWidth();
        int b = t + v.getHeight();
        return new Rect(l, t, r, b);
    }

    public static Rect getLocationOnScreen(View v) {
        int[] out = new int[2];
        v.getLocationOnScreen(out);
        int l = out[0];
        int t = out[1];
        int r = l + v.getWidth();
        int b = t + v.getHeight();
        return new Rect(l, t, r, b);
    }

}
