package net.muliba.fancyfilepickerlibrary.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView

/**
 * 图片缩放View
 *
 * Created by fancy on 2017/5/19.
 */

class ClipZoomImageView: AppCompatImageView, ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener{

    var SCALE_MAX = 4.0f
    var SCALE_MID = 2.0f

    /**
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
     */
    private var initScale = 1.0f

    private var once = true
    /**
     * 用于存放矩阵的9个值
     */
    private val matrixValues = FloatArray(9)

    private val mScaleMatrix = Matrix()
    /**
     * 缩放的手势检测
     */
    lateinit var mScaleGestureDetector: ScaleGestureDetector
    /**
     * 用于双击检测
     */
    lateinit var mGestureDetector: GestureDetector
    private var mTouchSlop: Int = 0
    private var isAutoScale = false
    private var mLastX = 0.0f
    private var mLastY = 0.0f
    private var isCanDrag = false
    private var lastPointerCount = 0

    //水平方向边距
    var mHorizontalPadding: Int = 0
    //垂直方向边距
    var mVerticalPadding: Int = 0

    var mClipMode = 0


    constructor(context:Context): super(context) {
        initView(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initView(context, attributeSet)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        scaleType = ImageView.ScaleType.MATRIX
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener(){
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (isAutoScale) return true
                val x = e.x
                val y = e.y
                if (getScale() < SCALE_MID) {
                    postDelayed(AutoScaleRunnable(SCALE_MID, x, y), 16)
                    isAutoScale = true
                }else {
                    postDelayed(AutoScaleRunnable(initScale, x, y), 16)
                    isAutoScale = true
                }
                return true
            }
        })
        setOnTouchListener(this)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scale = getScale()
        var scaleFactor = detector.scaleFactor
        if (drawable == null) return true

        /**
         * 缩放的范围控制
         */
        if ((scale< SCALE_MAX && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale
            }
            if (scaleFactor * scale > SCALE_MAX){
                scaleFactor = SCALE_MAX / scale
            }
            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            checkBorder()
            imageMatrix = mScaleMatrix
        }
        return true

    }

    override fun onGlobalLayout() {
        if (once) {
            val d = drawable ?: return
            //计算边距 两种模式 ClipModeEnum
            if (mClipMode == ClipModeEnum.SCREEN.key) { //按屏幕的比例模式 边距是中间区域是屏幕长宽的一半
                mVerticalPadding = height / 4
                mHorizontalPadding = width / 4
            }else {//正方形模式 需要初始化给出水平方向的边距mHorizontalPadding
                mVerticalPadding = (height - ( width - (2* mHorizontalPadding))) / 2
            }
            val oWidth = width
            val oHeight = height
            //图片高宽
            val dw = d.intrinsicWidth
            val dh = d.intrinsicHeight
            var scale = 1.0f
            if (dw < (width - mHorizontalPadding*2) && dh > (height - mVerticalPadding *2)) {
                scale = (width * 1.0f - mHorizontalPadding*2) / dw
            }
            if (dh < (height - mVerticalPadding * 2) && dw > (width - mHorizontalPadding * 2)) {
                scale = (height * 1.0f - mVerticalPadding * 2) / dh
            }
            if (dw < (width - mHorizontalPadding*2) && dh < (height - mVerticalPadding * 2)) {
                val scaleW =  (width * 1.0f - mHorizontalPadding*2) / dw
                val scaleH = (height * 1.0f - mVerticalPadding * 2) / dh
                scale = Math.max(scaleW, scaleH)
            }
            if (dw > (width - mHorizontalPadding*2)  && dh > (height - mVerticalPadding * 2)) {
                val scaleW =  (width * 1.0f - mHorizontalPadding*2) / dw
                val scaleH = (height * 1.0f - mVerticalPadding * 2) / dh
                scale = Math.max(scaleW, scaleH)
            }
            initScale = scale
            SCALE_MAX = 4*initScale
            SCALE_MID = 2*initScale
            mScaleMatrix.postTranslate((oWidth - dw).toFloat() / 2 , (oHeight - dh).toFloat() / 2 )
            mScaleMatrix.postScale(scale, scale, oWidth.toFloat()/2, oHeight.toFloat()/2)
            imageMatrix = mScaleMatrix
            once = false
        }
    }

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (mGestureDetector.onTouchEvent(event)) return true
        mScaleGestureDetector.onTouchEvent(event)
        var x = 0f
        var y = 0f
        // 拿到触摸点的个数
        val pointerCount = event.pointerCount
        // 得到多个触摸点的x与y均值
        for (i in 0 until pointerCount) {
            x +=  event.getX(i)
            y += event.getY(i)
        }
        x /= pointerCount
        y /= pointerCount

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false
            mLastX = x
            mLastY = y
        }
        lastPointerCount = pointerCount
        when(event.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx = x - mLastX
                var dy = y - mLastY
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy)
                }
                if (isCanDrag) {
                    if (drawable !=null) {
                        val rectF = getMatrixRectF()
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() <= width - 2 * mHorizontalPadding) {
                            dx = 0f
                        }
                        // 如果高度小于屏幕高度，则禁止上下移动
                        if (rectF.height() <= height - 2 * mVerticalPadding) {
                            dy = 0f
                        }
                        mScaleMatrix.postTranslate(dx, dy)
                        checkBorder()
                        imageMatrix = mScaleMatrix
                    }
                }
                mLastX = x
                mLastY = y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->{
                lastPointerCount = 0
            }
        }

        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }


    @SuppressLint("NewApi")
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    /**
     * 裁剪
     */
    fun clip(): Bitmap {
        var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        if (mClipMode == ClipModeEnum.SCREEN.key) {
            return Bitmap.createBitmap(bitmap, mHorizontalPadding, mVerticalPadding, width/2, height/2)
        }else {
            return Bitmap.createBitmap(bitmap, mHorizontalPadding, mVerticalPadding, (width-2*mHorizontalPadding), (width-2*mHorizontalPadding))
        }

    }

    /**
     * 获取当前缩放值
     */
    private fun getScale(): Float {
        mScaleMatrix.getValues(matrixValues)
        return  matrixValues[Matrix.MSCALE_X]
    }
    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private fun getMatrixRectF(): RectF {
        val matrix = mScaleMatrix
        val rect = RectF()
        val d = drawable
        if (d != null) {
            rect.set(0f, 0f, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())
            matrix.mapRect(rect)
        }
        return rect
    }

    private fun isCanDrag(x: Float, y: Float): Boolean {
        return Math.sqrt((x * x + y * y).toDouble()) >= mTouchSlop
    }


    private fun checkBorder() {
        val rectF = getMatrixRectF()
        var deltaX = 0f
        var deltaY = 0f
        val ow = width
        val oh = height
        // 如果宽或高大于屏幕，则控制范围 ; 这里的0.001是因为精度丢失会产生问题，但是误差一般很小，所以我们直接加了一个0.01
        if (rectF.width()+ 0.01 >= (ow - 2 * mHorizontalPadding)) {
            if (rectF.left > mHorizontalPadding) {
                deltaX = -rectF.left + mHorizontalPadding
            }
            if (rectF.right < ow - mHorizontalPadding ) {
                deltaX = ow - mHorizontalPadding - rectF.right
            }
        }
        if (rectF.height() + 0.01 >= (oh - 2*mVerticalPadding)) {
            if (rectF.top > mVerticalPadding) {
                deltaY = -rectF.top + mVerticalPadding
            }
            if (rectF.bottom < oh - mVerticalPadding) {
                deltaY = oh - mVerticalPadding - rectF.bottom
            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY)
    }

    inner class AutoScaleRunnable(targetScale: Float, x:Float, y: Float) : Runnable {
        val BIGGER = 1.07f
        val SMALLER = 0.93f
        var mTargetScale = targetScale
        var tmpScale = SMALLER
        /**
         * 缩放的中心
         */
        var x: Float = x
        var y: Float = y

        init {
            mTargetScale = targetScale
            this.x = x
            this.y = y
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER
            }else{
                tmpScale = SMALLER
            }
        }

        override fun run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y)
            checkBorder()
            imageMatrix = mScaleMatrix
            val currentScale = getScale()
            // 如果值在合法范围内，继续缩放
            if ((tmpScale > 1f && currentScale < mTargetScale) || (tmpScale < 1f && mTargetScale < currentScale)) {
                postDelayed(this, 16)
            }else {// 设置为目标的缩放比例
                val deltaScale = mTargetScale / currentScale
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y)
                checkBorder()
                imageMatrix = mScaleMatrix
                isAutoScale = false
            }
        }
    }


}