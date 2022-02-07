package tiiehenry.viewcontroller.injection.weiget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import static tiiehenry.viewcontroller.injection.control.ViewHelper.TAG_GM_CMP;

/**
 * Created by jrsen on 17-10-13.
 */
public final class ParticleView extends View {

    private ValueAnimator mParticleAnimator;
    //动画持续时间
    public int duration = 4000;
    //动画监听
    private OnAnimationListener mOnAnimationListener;
    //画笔
    private Paint mPaint;
    //所有粒子
    private Particle[][] mParticles;

    public void setOnAnimationListener(OnAnimationListener mOnAnimationListener) {
        this.mOnAnimationListener = mOnAnimationListener;
    }

    public ParticleView(Context context) {
        this(context, null);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTag(TAG_GM_CMP);
        initWidget();
    }

    private void initWidget() {
        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mParticleAnimator != null)
            drawParticle(canvas);
    }

    public void drawParticle(Canvas canvas) {
        for (Particle[] particle : mParticles) {
            for (Particle p : particle) {
                p.update((Float) mParticleAnimator.getAnimatedValue());
                mPaint.setColor(p.color);
                mPaint.setAlpha((int) (Color.alpha(p.color) * p.alpha));
                canvas.drawCircle(p.cx, p.cy, p.radius, mPaint);//
            }
        }
    }

    public void boom(final View view) {

        if (view.getVisibility() != View.VISIBLE || view.getAlpha() == 0 || (mParticleAnimator != null && mParticleAnimator.isRunning())) {
            return;
        }
        view.post(new Runnable() {
            @Override
            public void run() {
                int[] location = new int[2];
                view.getLocationInWindow(location);
                Rect rect = new Rect(location[0], location[1], location[0] + view.getMeasuredWidth(), location[1] + view.getMeasuredHeight());

                mParticles = Particle.generateParticles(getCacheBitmapFromView(view), rect);
                mParticleAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                mParticleAnimator.setDuration(duration);
                mParticleAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (mOnAnimationListener != null)
                            mOnAnimationListener.onAnimationStart(view, animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mOnAnimationListener != null)
                            mOnAnimationListener.onAnimationEnd(view, animation);
                    }
                });
                mParticleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        invalidate();
                    }
                });
                mParticleAnimator.start();
            }
        });
    }

    /**
     * 获取一个 View 的缓存视图
     *
     * @param view
     * @return
     */
    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void attachToContainer(ViewGroup container) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(this, lp);
    }

    public void detachFromContainer() {
        ViewGroup parent = (ViewGroup) getParent();
        parent.removeView(this);
    }

    public interface OnAnimationListener {
        //当前正在执行的view
        void onAnimationStart(View v, Animator animation);

        void onAnimationEnd(View v, Animator animation);
    }
}
