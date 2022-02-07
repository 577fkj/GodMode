package tiiehenry.viewcontroller.injection.hook;

import static tiiehenry.viewcontroller.injection.control.ViewHelper.TAG_GM_CMP;
import static tiiehenry.viewcontroller.injection.util.ViewBitmapUtils.recycleNullableBitmap;

import android.animation.Animator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Toast;

import tiiehenry.viewcontroller.injection.control.ViewController;
import tiiehenry.viewcontroller.injection.control.ViewExtractor;
import tiiehenry.viewcontroller.injection.control.ViewFinder;
import tiiehenry.viewcontroller.injection.control.ViewHelper;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.injector.InjectorImplApps;
import tiiehenry.viewcontroller.injection.weiget.CancelView;
import tiiehenry.viewcontroller.injection.weiget.MaskView;
import tiiehenry.viewcontroller.injection.weiget.ParticleView;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.Preconditions;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by jrsen on 17-12-6.
 */

public final class DispatchTouchEventHook extends XC_MethodHook {

    private final int MARK_COLOR = Color.argb(150, 139, 195, 75);
    private final InjectorImplApps injectorImplApps;

    private float mX, mY;
    private Bitmap mSnapshot;
    private ViewRule mViewRule;
    private MaskView mMaskView;
    private CancelView mCancelView;
    private boolean mHasBlockEvent;
    private boolean mLongClick;
    private CheckForLongPress mPendingCheckForLongPress;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private volatile boolean mMultiPointLock;

    public static volatile boolean mDragging;

    public DispatchTouchEventHook(InjectorImplApps injectorImplApps) {
        this.injectorImplApps = injectorImplApps;
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) {
        View view = (View) param.thisObject;
        MotionEvent event = (MotionEvent) param.args[0];
        if (injectorImplApps.editModeProp.get() && !TAG_GM_CMP.equals(view.getTag())) {
            if (DispatchKeyEventHook.mKeySelecting) {
                param.setResult(true);
            } else {
                param.setResult(dispatchTouchEvent(view, event));
            }
        }
    }

    private boolean dispatchTouchEvent(View v, MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            if (mMultiPointLock) {
                Toast.makeText(v.getContext(), "不支持多点操作", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!isAttachedToActivity(v)) {
                if (!mHasBlockEvent) {
                    Toast.makeText(v.getContext(), "该控件属于悬浮窗暂不支持编辑", Toast.LENGTH_SHORT).show();
                    mHasBlockEvent = true;
                }
                return false;
            }
            mDragging = true;
            mMultiPointLock = true;//防止多个触点同时触发
            //防止列表控件拦截事件传递
            ViewParent parent = v.getParent();
            if (parent != null) parent.requestDisallowInterceptTouchEvent(true);
            mX = event.getX();
            mY = event.getY();
            mPendingCheckForLongPress = new CheckForLongPress(v);
            mHandler.postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
        } else if (action == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            float y = event.getY();
            if (mLongClick) {
                mMaskView.updateOverlayBounds((int) (event.getRawX() - this.mX), (int) (event.getRawY() - this.mY), v.getWidth(), v.getHeight());
                mMaskView.setMarked(mCancelView.getRealBounds().intersect(mMaskView.getRealBounds()));
            } else if (x < 0 || x > v.getWidth() || y < 0 || y > v.getHeight()) {
                mHandler.removeCallbacks(mPendingCheckForLongPress);
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            ViewParent parent = v.getParent();
            if (parent != null) parent.requestDisallowInterceptTouchEvent(false);
            mHandler.removeCallbacks(mPendingCheckForLongPress);
            if (mLongClick) {
                performDetachMirrorView(v);
                mLongClick = false;
            }
            mHasBlockEvent = false;
            mMultiPointLock = false;
            mDragging = false;
        }
        return true;
    }

    private boolean isAttachedToActivity(View v) {
        Object viewRootImpl = ViewFinder.findViewRootImplByChildView(v.getParent());
        if (viewRootImpl == null) return false;
        WindowManager.LayoutParams mWindowAttributes = (WindowManager.LayoutParams) XposedHelpers.getObjectField(viewRootImpl, "mWindowAttributes");
        return mWindowAttributes != null && mWindowAttributes.type == WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
    }

    private void performAttachMirrorView(View v) {
        try {
            //Create mirror view and attach top view hierarchy
            Activity activity = Preconditions.checkNotNull(ViewExtractor.getAttachedActivityFromView(v));

            ViewGroup container = (ViewGroup) activity.getWindow().getDecorView();

            mCancelView = new CancelView(activity);
            mCancelView.attachToContainer(container);

            mMaskView = MaskView.makeMaskView(activity);
            mMaskView.setMaskOverlay(v);
            mMaskView.setMarkColor(MARK_COLOR);
            mMaskView.updateOverlayBounds(ViewHelper.getLocationInWindow(v));
            mMaskView.attachToContainer(container);

            mSnapshot = ViewExtractor.snapshotView(ViewFinder.findTopParentViewByChildView(v));
            mViewRule = ViewExtractor.makeRule(v);
            ViewController.applyRule(v, mViewRule);
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void performDetachMirrorView(final View v) {
        Activity activity = ViewExtractor.getAttachedActivityFromView(v);
        try {
            Preconditions.checkNotNull(activity);
        } catch (NullPointerException e) {
            return;
        }

        mCancelView.detachFromContainer();
        if (mMaskView.isMarked()) {
            //丢弃该条规则
            try {
                mMaskView.detachFromContainer();
                mViewRule.visibility = View.VISIBLE;
                ViewController.revokeRule(v, mViewRule);
                recycleNullableBitmap(mSnapshot);
            } finally {
                mSnapshot = null;
                mMaskView = null;
                mCancelView = null;
                mViewRule = null;
            }
        } else {
            ViewGroup container = (ViewGroup) activity.getWindow().getDecorView();
            final ParticleView particleView = new ParticleView(activity);
            particleView.setDuration(1000);
            particleView.attachToContainer(container);
            particleView.setOnAnimationListener(new ParticleView.OnAnimationListener() {
                @Override
                public void onAnimationStart(View animView, Animator animation) {
                    //Make original view gone
                    mViewRule.visibility = View.GONE;
                    ViewController.applyRule(v, mViewRule);
                    GodModeManager.getDefault().writeRule(v.getContext().getPackageName(), mViewRule, mSnapshot);
                    recycleNullableBitmap(mSnapshot);
                    mMaskView.detachFromContainer();
                }

                @Override
                public void onAnimationEnd(View animView, Animator animation) {
                    try {
                        particleView.detachFromContainer();
                    } finally {
                        mSnapshot = null;
                        mMaskView = null;
                        mCancelView = null;
                        mViewRule = null;
                    }
                }
            });
            particleView.boom(mMaskView);
        }
    }


    private class CheckForLongPress implements Runnable {

        private final WeakReference<View> viewRef;

        private CheckForLongPress(View view) {
            this.viewRef = new WeakReference<>(view);
        }

        @Override
        public void run() {
            View view = viewRef.get();
            if (view != null) {
                performAttachMirrorView(view);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                mLongClick = true;
            }
        }
    }
}
