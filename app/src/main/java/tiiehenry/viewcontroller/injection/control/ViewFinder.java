package tiiehenry.viewcontroller.injection.control;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.ArrayList;

import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.Preconditions;

public class ViewFinder {
   private static View findView(View view, ViewRule rule, boolean strictMode) {
       if (isViewMatched(view, rule, strictMode)) {
           return view;
       }
       if (view instanceof ViewGroup) {
           ViewGroup viewGroup = ((ViewGroup) view);
           final int N = viewGroup.getChildCount();
           for (int i = 0; i < N; i++) {
               View v = viewGroup.getChildAt(i);
               if (findView(v, rule, strictMode) != null) {
                   return v;
               }
           }
       }
       return null;
   }

   private static ArrayList<View> findViewList(View view, ViewRule rule, boolean strictMode) {
       ArrayList<View> list = new ArrayList<>();
       if (isViewMatched(view, rule, strictMode)) {
           list.add(view);
       }
       if (view instanceof ViewGroup) {
           ViewGroup viewGroup = ((ViewGroup) view);
           final int N = viewGroup.getChildCount();
           for (int i = 0; i < N; i++) {
               View v = viewGroup.getChildAt(i);
               ArrayList<View> childList = findViewList(v, rule, strictMode);
               list.addAll(childList);
           }
       }
       return list;
   }

   public static View findViewBestMatch(Activity activity, ViewRule rule, int versionCode) {
       // if the rule version and the application version are the same, use strict mode.
       boolean strictMode = versionCode == rule.matchVersionCode;

       if (!TextUtils.isEmpty(rule.description)) {
           Logger.i(TAG, String.format("strict mode %b, match view by description", strictMode));
           View view = matchView(findViewByDescription(activity.getWindow().getDecorView(), rule.description), rule, strictMode);
           if (view != null) {
               return view;
           }
       }
       if (!TextUtils.isEmpty(rule.text)) {
           Logger.i(TAG, String.format("strict mode %b, match view by text", strictMode));
           View view = matchView(findViewByText(activity.getWindow().getDecorView(), rule.text), rule, strictMode);
           if (view != null) {
               return view;
           }
       }
       if (!TextUtils.isEmpty(rule.resourceName)) {
           Logger.i(TAG, String.format("strict mode %b, match view by resource name", strictMode));
           View view = matchView(activity.findViewById(rule.getViewId(activity.getResources())), rule, strictMode);
           if (view != null) {
               return view;
           }
       }
       Logger.i(TAG, String.format("strict mode %b, match view by depth", strictMode));
       View view = matchView(findViewByDepth(activity, rule.depth), rule, strictMode);
       return view;
   }

   private static boolean isViewMatched(View view, ViewRule rule, boolean strictMode) {
       try {
           String resourceName = null;
           try {
               resourceName = view.getResources().getResourceName(view.getId());
           } catch (Resources.NotFoundException ignore) {
           }
           String text = (view instanceof TextView) ? Preconditions.optionDefault(((TextView) view).getText(), "").toString() : "";
           String description = Preconditions.optionDefault(view.getContentDescription(), "").toString();
           String viewClass = view.getClass().getName();
           Logger.i(TAG, String.format("view res name:%s matched:%b", resourceName, TextUtils.equals(resourceName, rule.resourceName)));
           Logger.i(TAG, String.format("view text:%s matched:%b", text, TextUtils.equals(text, rule.text)));
           Logger.i(TAG, String.format("view description:%s matched:%b", description, TextUtils.equals(description, rule.description)));
           Logger.i(TAG, String.format("view class:%s matched:%b", viewClass, TextUtils.equals(viewClass, rule.viewClass)));
           if (strictMode) {
               return TextUtils.equals(resourceName, rule.resourceName)
                       && TextUtils.equals(text, rule.text)
                       && TextUtils.equals(description, rule.description)
                       && TextUtils.equals(viewClass, rule.viewClass);
           } else {
               return ((!TextUtils.isEmpty(rule.resourceName) && TextUtils.equals(resourceName, rule.resourceName))
                       || (!TextUtils.isEmpty(rule.text) && TextUtils.equals(text, rule.text))
                       || (!TextUtils.isEmpty(rule.description) && TextUtils.equals(description, rule.description))
                       || (!TextUtils.isEmpty(rule.viewClass) && TextUtils.equals(viewClass, rule.viewClass)));

           }
       } catch (Exception ignore) {
//            ignore.printStackTrace();
       }
       return false;
   }

   private static View matchView(View view, ViewRule rule, boolean strictMode) {
       try {
           Preconditions.checkNotNull(view, "view can't be null");
           Preconditions.checkNotNull(rule, "rule can't be null");
           String resourceName = null;
           try {
               resourceName = view.getResources().getResourceName(view.getId());
           } catch (Resources.NotFoundException ignore) {
           }
           String text = (view instanceof TextView) ? Preconditions.optionDefault(((TextView) view).getText(), "").toString() : "";
           String description = Preconditions.optionDefault(view.getContentDescription(), "").toString();
           String viewClass = view.getClass().getName();
           Logger.i(TAG, String.format("view res name:%s matched:%b", resourceName, TextUtils.equals(resourceName, rule.resourceName)));
           Logger.i(TAG, String.format("view text:%s matched:%b", text, TextUtils.equals(text, rule.text)));
           Logger.i(TAG, String.format("view description:%s matched:%b", description, TextUtils.equals(description, rule.description)));
           Logger.i(TAG, String.format("view class:%s matched:%b", viewClass, TextUtils.equals(viewClass, rule.viewClass)));
           if (strictMode) {
               return TextUtils.equals(resourceName, rule.resourceName)
                       && TextUtils.equals(text, rule.text)
                       && TextUtils.equals(description, rule.description)
                       && TextUtils.equals(viewClass, rule.viewClass) ? view : null;
           } else {
               return ((!TextUtils.isEmpty(rule.resourceName) && TextUtils.equals(resourceName, rule.resourceName))
                       || (!TextUtils.isEmpty(rule.text) && TextUtils.equals(text, rule.text))
                       || (!TextUtils.isEmpty(rule.description) && TextUtils.equals(description, rule.description))
                       || (!TextUtils.isEmpty(rule.viewClass) && TextUtils.equals(viewClass, rule.viewClass))) ? view : null;

           }
       } catch (Exception ignore) {
//            ignore.printStackTrace();
       }
       return null;
   }

   public static View findViewByText(View view, String text) {
       if (view instanceof TextView && TextUtils.equals(((TextView) view).getText(), text)) {
           return view;
       }
       if (view instanceof ViewGroup) {
           final int N = ((ViewGroup) view).getChildCount();
           for (int i = 0; i < N; i++) {
               View childView = findViewByText(((ViewGroup) view).getChildAt(i), text);
               if (childView != null) {
                   return childView;
               }
           }
       }
       return null;
   }

   public static View findViewByDescription(View view, String description) {
       if (TextUtils.equals(view.getContentDescription(), description)) {
           return view;
       }
       if (view instanceof ViewGroup) {
           final int N = ((ViewGroup) view).getChildCount();
           for (int i = 0; i < N; i++) {
               View childView = findViewByDescription(((ViewGroup) view).getChildAt(i), description);
               if (childView != null) {
                   return childView;
               }
           }
       }
       return null;
   }

   public static View findViewByDepth(Activity activity, int[] depths) {
       View view = activity.getWindow().getDecorView();
       for (int depth : depths) {
           view = view instanceof ViewGroup
                   ? ((ViewGroup) view).getChildAt(depth) : null;
           if (view == null) break;
       }
       return view;
   }

   public static View findTopParentViewByChildView(View v) {
       if (v.getParent() == null || !(v.getParent() instanceof ViewGroup)) {
           return v;
       } else {
           return findTopParentViewByChildView((View) v.getParent());
       }
   }

   public static Object findViewRootImplByChildView(ViewParent parent) {
       if (parent.getParent() == null) {
           return !(parent instanceof ViewGroup) ? parent : null;
       } else {
           return findViewRootImplByChildView(parent.getParent());
       }
   }
}
