package net.muliba.fancyfilepickerlibrary.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Created by fancy on 2017/5/19.
 */

class ClipMaskLayerView: View {
    //水平方向边距
    var mHorizontalPadding: Int = 0
    //垂直方向边距
    var mVerticalPadding: Int = 0

    var mClipMode = 0

    var mBorderWidth = 1f
    lateinit var mPaint: Paint

    constructor(context: Context): super(context) {
        initView(context, null, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initView(context, attributeSet, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int): super(context, attributeSet, defStyle) {
        initView(context, attributeSet, defStyle)
    }


    fun  initView(context: Context, attributeSet: AttributeSet?, defStyle: Int){
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
        mPaint = Paint()
        mPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //计算边距 两种模式 ClipModeEnum
        if (mClipMode == ClipModeEnum.SCREEN.key) { //按屏幕的比例模式 边距是中间区域是屏幕长宽的一半
            mVerticalPadding = height / 4
            mHorizontalPadding = width / 4
        }else {//正方形模式 需要初始化给出水平方向的边距mHorizontalPadding
            mVerticalPadding = (height - ( width - (2* mHorizontalPadding))) / 2
        }
        mPaint.color = Color.parseColor("#aa000000")
        mPaint.style = Paint.Style.FILL
        // 绘制左边1
        canvas.drawRect(0f, 0f, mHorizontalPadding.toFloat(), height.toFloat(), mPaint)
        // 绘制右边2
        canvas.drawRect((width - mHorizontalPadding).toFloat(), 0f, width.toFloat(),
                height.toFloat(), mPaint)
        // 绘制上边3
        canvas.drawRect(mHorizontalPadding.toFloat(), 0f, (width - mHorizontalPadding).toFloat(),
                mVerticalPadding.toFloat(), mPaint)
        // 绘制下边4
        canvas.drawRect(mHorizontalPadding.toFloat(), (height - mVerticalPadding).toFloat(),
                (width - mHorizontalPadding).toFloat(), height.toFloat(), mPaint)
        //绘制边框
        mPaint.color = Color.parseColor("#ffffff")
        mPaint.strokeWidth = mBorderWidth
        mPaint.style = Paint.Style.STROKE
        canvas.drawRect(mHorizontalPadding.toFloat(), mVerticalPadding.toFloat(),
                (width - mHorizontalPadding).toFloat(), (height - mVerticalPadding).toFloat(), mPaint)

    }
}