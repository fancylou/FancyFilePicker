package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.annotation.ColorInt
import net.muliba.fancyfilepickerlibrary.ui.PictureChooseActivity
import net.muliba.fancyfilepickerlibrary.util.Utils

/**
 * Created by fancy on 2017/5/22.
 */

class PicturePicker {
    companion object {
        val FANCY_REQUEST_CODE = 1024

        val FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY = "fancy_picture_picker_array_result"
        val FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY = "fancy_picture_picker_single_result"
        //选择类型
        val CHOOSE_TYPE_MULTIPLE = 0
        val CHOOSE_TYPE_SINGLE = 1
    }

    private var requestCode: Int = FANCY_REQUEST_CODE
    private var activity: Activity? = null
    private var actionBarColor: Int = Color.parseColor("#F44336")
    private var actionBarTitle: String = ""
    private var chooseType = CHOOSE_TYPE_SINGLE //默认多选


    fun withActivity(activity: Activity): PicturePicker {
        this.activity = activity
        return this
    }

    fun chooseType(type: Int = CHOOSE_TYPE_SINGLE) : PicturePicker {
        if (type != CHOOSE_TYPE_SINGLE && type!= CHOOSE_TYPE_MULTIPLE) {
            throw IllegalArgumentException("chooseType value is illegal , must be one of #PicturePicker.CHOOSE_TYPE_MULTIPLE or #PicturePicker.CHOOSE_TYPE_SINGLE ")
        }
        chooseType = type
        return this
    }

    /**
     * 定义requestCode
     * @param requestCode
     */
    fun requestCode(requestCode: Int): PicturePicker {
        this.requestCode = requestCode
        return this
    }

    /**
     * 设置actionBar的背景色
     * @param color actionBar的背景色
     */
    fun actionBarColor(@ColorInt color: Int): PicturePicker {
        actionBarColor = color
        return this
    }

    /**
     * 设置标题
     * @param title
     */
    fun title(title: String): PicturePicker {
        this.actionBarTitle = title
        return this
    }

    /**
     * 启动选择器
     */
    fun start() {
        if (activity==null) {
            throw RuntimeException("not found Activity, Please execute the function 'withActivity' ")
        }
        val intent = Intent(activity, PictureChooseActivity::class.java)
        intent.putExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        intent.putExtra(Utils.ACTION_BAR_TITLE_KEY, actionBarTitle)
        intent.putExtra(Utils.CHOOSE_TYPE_KEY, chooseType)
        activity?.startActivityForResult(intent, requestCode)
    }
}