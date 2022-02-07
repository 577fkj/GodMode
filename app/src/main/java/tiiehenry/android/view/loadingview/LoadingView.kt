package tiiehenry.android.view.loadingview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.abs


/**
 * @author <a href="mailto:1806968131@qq.com">TIIEHenry</a>
 */
class LoadingView : View {


    /**
     * 画笔
     */
    private lateinit var mPaint: Paint

    /**
     * 圆心X坐标
     */
    private var mCircleX: Float = 0f

    /**
     * 圆心Y坐标
     */
    private var mCircleY: Float = 0f

    /**
     * 半径
     */
    private var mRadius: Float = 0f

    /**
     * 圆弧开始角度
     */
    private var mStartAngle = 0f

    /**
     * 圆弧扫描角度
     */
    private var mSweepAngle = 180f

    /**
     * 画笔宽度
     */
    private var mStrokeWidth = 0f

    /**
     * 圆弧之间的间距
     */
    private var mCirclePadding: Float = 0f

    /**
     * 圆弧数量
     */
    private var mCount = 1

    /**
     * 最大速度
     */
    private var mMaxSpeed = 5f

    /**
     * 最小速度
     */
    private var mMinSpeed = 3f

    /**
     * 偏移量数组（记录每个圆弧的偏移量）
     */
    private lateinit var mOffsets: FloatArray

    /**
     * 偏移速度数组（记录每个圆弧的偏移速度）
     */
    private lateinit var mOffsetSpeeds: FloatArray

    /**
     * 画笔颜色
     */
    private var mColor = 0

    /**
     * 画笔着色器
     */
    private lateinit var mShader: Shader

    /**
     * 着色器颜色
     */
    private var mShaderColors = intArrayOf(-0xb01554, -0x5722af, -0x172cf1, -0x5722af, -0xb01554)

    /**
     * 是否使用着色器
     */
    private var isShader = true

    /**
     * 刷新时间间隔，默认15ms
     */
    private var mRefreshInterval = 15

    /**
     * 是否逆时针方向
     */
    private var isCounterclockwise = false

    /**
     * 是否交错旋转 两个环以上生效
     */
    private var alternatingRotation = false

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    /**
     * 初始化
     */
    private fun init(context: Context?, attrs: AttributeSet?) {

        val a = context?.obtainStyledAttributes(attrs, R.styleable.LoadingView)

        val displayMetrics = resources.displayMetrics

        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, displayMetrics)
        mCirclePadding = mStrokeWidth + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)

        a?.indexCount?.let {
            for (i in 0 until it) {
                when (val attr = a.getIndex(i)) {
                    R.styleable.LoadingView_lvCount -> mCount = a.getInt(attr, 2)
                    R.styleable.LoadingView_lvStartAngle -> mStartAngle = a.getInt(attr, 0).toFloat()
                    R.styleable.LoadingView_lvSweepAngle -> mSweepAngle = a.getInt(attr, 180).toFloat()
                    R.styleable.LoadingView_lvStrokeWidth -> mStrokeWidth = a.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, displayMetrics))
                    R.styleable.LoadingView_lvMaxSpeed -> mMaxSpeed = a.getInt(attr, 5).toFloat()
                    R.styleable.LoadingView_lvMinSpeed -> mMinSpeed = a.getInt(attr, 3).toFloat()
                    R.styleable.LoadingView_lvCirclePadding -> mCirclePadding = a.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, displayMetrics))
                    R.styleable.LoadingView_lvRefreshInterval -> mRefreshInterval = a.getInt(attr, 15)
                    R.styleable.LoadingView_lvColor -> {
                        mColor = a.getColor(attr, mColor)
                        isShader = false
                    }
                    R.styleable.LoadingView_lvCounterclockwise -> isCounterclockwise = a.getBoolean(attr, false)
                    R.styleable.LoadingView_lvAlternatingRotation -> alternatingRotation = a.getBoolean(attr, false)
                }

            }

            a.recycle()
        }

        mPaint = Paint()

        mCount.takeIf { mCount < 1 }?.apply {
            mCount = 1
        }

        mOffsets = FloatArray(mCount)
        mOffsetSpeeds = FloatArray(mCount)
        val s = abs(mMaxSpeed - mMinSpeed) * 1.0f / mCount
        val offsets = 360f / mCount
        var reserve = false
        for (i in 0 until mCount) {
            val re=(if (reserve) -1 else 1)
            mOffsets[i] =re * ( mStartAngle + offsets * i)
            mOffsetSpeeds[i] =re * ( mMinSpeed + s * i)
            if (alternatingRotation) {
                reserve = !reserve
            }
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val defaultSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60f, resources.displayMetrics).toInt()

        val width = measureHandler(widthMeasureSpec, defaultSize)
        val height = measureHandler(heightMeasureSpec, defaultSize)

        mCircleX = (width + paddingLeft - paddingRight) / 2.0f
        mCircleY = (height + paddingTop - paddingBottom) / 2.0f

        val padding = (paddingLeft + paddingRight).coerceAtLeast(paddingTop + paddingBottom)

        mRadius = (width - padding - mStrokeWidth) / 2.0f

        mShader = SweepGradient(mCircleX, mCircleY, mShaderColors, null)

        setMeasuredDimension(width, height)

    }


    private fun measureHandler(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        when (measureMode) {
            MeasureSpec.EXACTLY -> result = measureSize
            MeasureSpec.AT_MOST -> result = defaultSize.coerceAtMost(measureSize)
            MeasureSpec.UNSPECIFIED -> {
            }
        }

        return result
    }

    /**
     * 绘制加载动画
     */
    private fun drawLoading(canvas: Canvas?) {

        mPaint.apply {
            reset()
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mStrokeWidth

            takeIf { isShader }?.apply {
                shader = mShader
            } ?: run {
                color = mColor
            }

        }

        //遍历绘制圆弧
        for (i in 0 until mCount) {
            val rectF = RectF(mCircleX - mRadius + i * mCirclePadding, mCircleY - mRadius + i * mCirclePadding, mCircleX + mRadius - i * mCirclePadding, mCircleY + mRadius - i * mCirclePadding)
            canvas?.drawArc(rectF, mOffsets[i], mSweepAngle, false, mPaint)
            if (isCounterclockwise) {
                mOffsets[i] = (mOffsets[i] - mOffsetSpeeds[i]) % 360
            } else {
                mOffsets[i] = (mOffsets[i] + mOffsetSpeeds[i]) % 360
            }
        }

        //延迟循环刷新
        postInvalidateDelayed(mRefreshInterval.toLong())
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLoading(canvas)
    }

    /**
     * 设置圆弧偏移速度（角度）
     */
    fun setOffsetSpeeds(offsetSpeeds: FloatArray) {
        mOffsetSpeeds = offsetSpeeds
    }

    /**
     * 设置刷新时间间隔，单位ms
     */
    fun setRefreshInterval(refreshInterval: Int) {
        mRefreshInterval = refreshInterval
    }

    /**
     * 设置着色器
     * @param shader
     */
    fun setShader(shader: Shader) {
        isShader = true
        this.mShader = shader
    }

    /**
     * 设置着色器
     */
    fun setShaderColor(colors: IntArray) {
        isShader = true
        this.mShader = SweepGradient(mCircleX, mCircleY, colors, null)
    }

    /**
     * 设置颜色
     * @param resId
     */
    fun setColorResource(resId: Int) {
        val color = resources.getColor(resId)
        setColor(color)
    }

    /**
     * 设置颜色
     */
    fun setColor(color: Int) {
        isShader = false
        mColor = color
    }

}
