package tiiehenry.viewcontroller.injection.hook;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;
import static tiiehenry.viewcontroller.injection.util.ViewBitmapUtils.recycleNullableBitmap;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.widget.TooltipCompat;

import tiiehenry.viewcontroller.R;
import tiiehenry.viewcontroller.injection.control.ViewController;
import tiiehenry.viewcontroller.injection.control.ViewExtractor;
import tiiehenry.viewcontroller.injection.control.ViewFinder;
import tiiehenry.viewcontroller.injection.control.ViewHelper;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.injector.InjectorImplApps;
import tiiehenry.viewcontroller.injection.util.GmLayoutInflater;
import tiiehenry.viewcontroller.injection.util.GmResources;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.injection.util.Property;
import tiiehenry.viewcontroller.injection.weiget.MaskView;
import tiiehenry.viewcontroller.injection.weiget.ParticleView;
import tiiehenry.viewcontroller.rule.ViewRule;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public final class DispatchKeyEventHook extends XC_MethodHook implements Property.OnPropertyChangeListener<Boolean>, SeekBar.OnSeekBarChangeListener {

    private static final int OVERLAY_COLOR = Color.argb(150, 255, 0, 0);
    private final List<WeakReference<View>> mViewNodes = new ArrayList<>();
    private final InjectorImplApps injectorImplApps;
    private int mCurrentViewIndex = 0;

    private boolean showing = false;
    private MaskView mMaskView;
    private View mNodeSelectorPanel;
    private Activity activity = null;
    private SeekBar seekbar = null;
    public static volatile boolean mKeySelecting = false;
    private EventHandlerHook eventHandlerHook;

    public DispatchKeyEventHook(InjectorImplApps injectorImplApps) {
        this.injectorImplApps = injectorImplApps;
    }

    public void setActivity(final Activity a) {
        reset();
        activity = a;
    }

    private void reset() {
        dismissNodeSelectPanel();
    }

    public void setDisplay(Boolean display) {
        if (display) {
            if (showing)return;
            if (activity == null) return;
            showNodeSelectPanel(activity);
        } else {
            dismissNodeSelectPanel();
        }
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) {
        if (injectorImplApps.editModeProp.get() && !DispatchTouchEventHook.mDragging) {
            Activity activity = (Activity) param.thisObject;
            KeyEvent event = (KeyEvent) param.args[0];
            param.setResult(dispatchKeyEvent(activity, event));
        }
    }

    private boolean dispatchKeyEvent(final Activity activity, KeyEvent keyEvent) {
        Logger.d(TAG, keyEvent.toString());
        int action = keyEvent.getAction();
        int keyCode = keyEvent.getKeyCode();
        if (action == KeyEvent.ACTION_UP &&
                (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if (!mKeySelecting) {
                showNodeSelectPanel(activity);
            } else {
                //hide node select panel
                dismissNodeSelectPanel();
            }
        }
        return true;
    }

    private void showViewDetailDialog(View view) {
        ViewRule viewRule;
        try {
            viewRule = ViewExtractor.makeRule(view);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        eventHandlerHook.hasDialog = true;
        new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle("Attribute")
                .setMessage(viewRule.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventHandlerHook.hasDialog = false;
                    }
                })
                .show();
    }

    private void animateShowNodeSelectorPanel() {
        mNodeSelectorPanel.animate()
                .alpha(1.0f)
                .setInterpolator(new DecelerateInterpolator(1.0f))
                .setDuration(300)
                .start();
    }

    private void showNodeSelectPanel(final Activity activity) {
        /*if (showing)
            return;*/
        showing = true;
        mViewNodes.clear();
        mCurrentViewIndex = 0;
        //build view hierarchy tree
        final ViewGroup container = (ViewGroup) activity.getWindow().getDecorView();
        mViewNodes.addAll(ViewHelper.buildViewNodes(container));
        mMaskView = MaskView.makeMaskView(activity);
        mMaskView.setMaskOverlay(OVERLAY_COLOR);
        mMaskView.attachToContainer(container);
        try {
            LayoutInflater layoutInflater = GmLayoutInflater.from(activity);
            mNodeSelectorPanel = layoutInflater.inflate(R.layout.layout_node_selector, container, false);
            seekbar = mNodeSelectorPanel.findViewById(R.id.slider);
            seekbar.setMax(mViewNodes.size() - 1);
            seekbar.setOnSeekBarChangeListener(this);
            View btnBlock = mNodeSelectorPanel.findViewById(R.id.block);
            TooltipCompat.setTooltipText(btnBlock, GmResources.getText(activity, R.string.accessibility_block));
            btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = mViewNodes.get(mCurrentViewIndex).get();
                    if (view == null) {
                        return;
                    }
                    showViewDetailDialog(view);
                }
            });
            btnBlock.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        mNodeSelectorPanel.setAlpha(0f);
                        final View view = mViewNodes.get(mCurrentViewIndex).get();
                        Logger.d(TAG, "removed view = " + view);
                        if (view != null) {
                            //hide overlay
                            mMaskView.updateOverlayBounds(new Rect());
                            final Bitmap snapshot = ViewExtractor.snapshotView(ViewFinder.findTopParentViewByChildView(view));
                            final ViewRule viewRule = ViewExtractor.makeRule(view);
                            final ParticleView particleView = new ParticleView(activity);
                            particleView.setDuration(1000);
                            particleView.attachToContainer(container);
                            particleView.setOnAnimationListener(new ParticleView.OnAnimationListener() {
                                @Override
                                public void onAnimationStart(View animView, Animator animation) {
                                    viewRule.visibility = View.GONE;
                                    ViewController.applyRule(view, viewRule);
                                }

                                @Override
                                public void onAnimationEnd(View animView, Animator animation) {
                                    GodModeManager.getDefault().writeRule(activity.getPackageName(), viewRule, snapshot);
                                    recycleNullableBitmap(snapshot);
                                    particleView.detachFromContainer();
                                    animateShowNodeSelectorPanel();
                                }
                            });
                            particleView.boom(view);
                        }
                        mViewNodes.remove(mCurrentViewIndex--);
                        seekbar.setMax(mViewNodes.size() - 1);
                        return true;
                    } catch (Exception e) {
                        Logger.e(TAG, "block fail", e);
                        animateShowNodeSelectorPanel();
                        Toast.makeText(activity, GmResources.getString(activity, R.string.block_fail, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
            View exchange = mNodeSelectorPanel.findViewById(R.id.exchange);
            ViewGroup topcentent = mNodeSelectorPanel.findViewById(R.id.topcentent);
            exchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Display display = activity.getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    int padding = topcentent.getPaddingBottom();
                    int targetWidth = width - topcentent.getChildAt(0).getWidth() - padding * 2;
                    if (topcentent.getPaddingRight() >= targetWidth / 2) {
                        topcentent.setPadding(padding, padding, padding, padding);
                    } else {
                        topcentent.setPadding(padding, padding, targetWidth, padding);
                    }
                }
            });
            exchange.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    eventHandlerHook.exchangeEditMode();
                    return true;
                }
            });
            View btnUp = mNodeSelectorPanel.findViewById(R.id.Up);
            btnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekbaradd();
                }
            });
            View btnDown = mNodeSelectorPanel.findViewById(R.id.Down);
            btnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekbarreduce();
                }
            });
            container.addView(mNodeSelectorPanel);
            mNodeSelectorPanel.setAlpha(0);
            mNodeSelectorPanel.post(new Runnable() {
                @Override
                public void run() {
                    mNodeSelectorPanel.setTranslationX(mNodeSelectorPanel.getWidth() / 2.0f);
                    mNodeSelectorPanel.animate()
                            .alpha(1)
                            .translationX(0)
                            .setDuration(300)
                            .setInterpolator(new DecelerateInterpolator(1.0f))
                            .start();
                }
            });
            mKeySelecting = true;
            XposedHelpers.findAndHookMethod(Activity.class, "dispatchKeyEvent", KeyEvent.class, new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (injectorImplApps.editModeProp.get() && !DispatchTouchEventHook.mDragging) {
                        KeyEvent event = (KeyEvent) param.args[0];
                        int action = event.getAction();
                        int keyCode = event.getKeyCode();
                        if (action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            seekbarreduce();
                        } else if (action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                            seekbaradd();
                        }
                        param.setResult(true);
                    }
                }
            });
        } catch (Exception e) {
            //god mode package uninstalled?
            Logger.e(TAG, "showNodeSelectPanel fail", e);
            mKeySelecting = false;
        }
    }

    private void seekbaradd() {
        if (seekbar.getProgress() == seekbar.getMax()) {
            return;
        }
        int Progress = seekbar.getProgress() + 1;
        seekbar.setProgress(Progress);
        onProgressChanged(seekbar, Progress, true);
    }

    private void seekbarreduce() {
        if (seekbar.getProgress() == 0) {
            return;
        }
        int Progress = seekbar.getProgress() - 1;
        seekbar.setProgress(Progress);
        onProgressChanged(seekbar, Progress, true);
    }

    private void dismissNodeSelectPanel() {
        /*if (!showing)
            return;*/
        showing = false;
        if (mMaskView != null) {
            mMaskView.detachFromContainer();
            mMaskView = null;
        }
        if (mNodeSelectorPanel != null) {
            removeNodeSelectorPanel(mNodeSelectorPanel);
        }
        mNodeSelectorPanel = null;
        mViewNodes.clear();
        mCurrentViewIndex = 0;
        mKeySelecting = false;
    }

    private void removeNodeSelectorPanel(View nodeSelectorPanel) {
        nodeSelectorPanel.post(new Runnable() {
            @Override
            public void run() {
                nodeSelectorPanel.animate()
                        .alpha(0)
                        .translationX(nodeSelectorPanel.getWidth() / 2.0f)
                        .setDuration(250)
                        .setInterpolator(new AccelerateInterpolator(1.0f))
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ViewGroup parent = (ViewGroup) nodeSelectorPanel.getParent();
                                if (parent != null) parent.removeView(nodeSelectorPanel);
                            }
                        })
                        .start();
            }
        });
    }

    @Override
    public void onPropertyChange(Boolean enable) {
        if (mMaskView != null) {
            dismissNodeSelectPanel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mCurrentViewIndex = progress;
            View view = mViewNodes.get(mCurrentViewIndex).get();
            Logger.d(TAG, String.format(Locale.getDefault(), "progress=%d selected view=%s", progress, view));
            if (view != null) {
                mMaskView.updateOverlayBounds(ViewHelper.getLocationInWindow(view));
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mNodeSelectorPanel.setAlpha(0.2f);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mNodeSelectorPanel.setAlpha(1f);
    }

    public void setEventHandlerHook(EventHandlerHook eventHandlerHook) {
        this.eventHandlerHook = eventHandlerHook;
    }
}
