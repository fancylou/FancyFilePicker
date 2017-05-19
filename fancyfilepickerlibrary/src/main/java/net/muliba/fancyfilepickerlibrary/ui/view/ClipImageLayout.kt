package net.muliba.fancyfilepickerlibrary.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.RelativeLayout
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.util.Utils

/**
 * 图片裁剪工具
 * Created by fancy on 2017/5/19.
 */

class  ClipImageLayout : RelativeLayout {

    val zoomImageView:ClipZoomImageView by lazy { ClipZoomImageView(context) }
    val clipMaskLayerView: ClipMaskLayerView by lazy { ClipMaskLayerView(context) }

    //水平方向边距
    var mHorizontalPadding: Int = 0
    //垂直方向边距
    var mVerticalPadding: Int = 0

    var mClipMode = 0

    var mSrc: Bitmap? = null

    constructor(context: Context): super(context) {
        initView(context, null, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initView(context, attributeSet, 0)
    }
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int): super(context, attributeSet, defStyle) {
        initView(context, attributeSet, defStyle)
    }

    private fun initView(context: Context, attributeSet: AttributeSet?, defStyle: Int) {
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.ClipImageLayout)
        mSrc = BitmapFactory.decodeResource(resources, array.getResourceId(R.styleable.ClipImageLayout_image_src, -1))
        mClipMode = array.getInt(R.styleable.ClipImageLayout_clip_mode, 0)
        mHorizontalPadding =array.getDimensionPixelSize(R.styleable.ClipImageLayout_horizontal_padding, 20)

        Log.d("ClipImageLayout", "mode:$mClipMode")
        zoomImageView.setImageBitmap(mSrc)
        add2View(zoomImageView, clipMaskLayerView)
        // 计算padding的px
        mHorizontalPadding = Utils.dip2px(context, mHorizontalPadding.toFloat())
        Log.d("ClipImageLayout", "mHorizontalPadding:$mHorizontalPadding")
        zoomImageView.mClipMode = mClipMode
        zoomImageView.mHorizontalPadding = mHorizontalPadding
        clipMaskLayerView.mClipMode = mClipMode
        clipMaskLayerView.mHorizontalPadding = mHorizontalPadding

        array.recycle()

    }

    /**
     * 动态设置图片
     */
    fun setSrc(bit: Bitmap) {
        this.removeAllViews()
        this.post(RefreshSrcRunnable(bit))
    }

    /**
     * 裁剪
     */
    fun clip(): Bitmap {
        return zoomImageView.clip()
    }


    private fun add2View(zoomImageView: ClipZoomImageView, clipMaskLayerView: ClipMaskLayerView) {
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(zoomImageView, lp)
        this.addView(clipMaskLayerView, lp)
    }

    inner class RefreshSrcRunnable(bit: Bitmap) : Runnable {
        private var bitmap: Bitmap = bit
        override fun run() {
            mSrc = bitmap
            zoomImageView.setImageBitmap(mSrc)
            add2View(zoomImageView, clipMaskLayerView)
        }
    }
}