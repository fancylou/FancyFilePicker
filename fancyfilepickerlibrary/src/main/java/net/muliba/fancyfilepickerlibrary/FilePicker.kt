package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.annotation.ColorInt
import net.muliba.fancyfilepickerlibrary.ui.FileClassificationPickerActivity
import net.muliba.fancyfilepickerlibrary.ui.FilePickerActivity
import net.muliba.fancyfilepickerlibrary.util.Utils

/**
 * Created by fancy on 2017/4/12.
 */

class FilePicker {

    companion object {
        @JvmStatic val FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY @JvmName("FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY")get() = "fancy_file_picker_array_result"
        @JvmStatic val FANCY_FILE_PICKER_SINGLE_RESULT_KEY @JvmName("FANCY_FILE_PICKER_SINGLE_RESULT_KEY")get() = "fancy_file_picker_single_result"

        @JvmStatic val FANCY_REQUEST_CODE @JvmName("FANCY_REQUEST_CODE")get() = 1024
        //选择类型
        @JvmStatic val CHOOSE_TYPE_MULTIPLE @JvmName("CHOOSE_TYPE_MULTIPLE")get() = 0
        @JvmStatic val CHOOSE_TYPE_SINGLE @JvmName("CHOOSE_TYPE_SINGLE")get() = 1
        //选择方式
        @JvmStatic val CHOOSE_MODE_NORMAL @JvmName("CHOOSE_MODE_NORMAL")get() = 0 //普通文件夹模式
        @JvmStatic val CHOOSE_MODE_CLASSIFICATION @JvmName("CHOOSE_MODE_CLASSIFICATION")get() = 1//分类模式
    }

    private var requestCode: Int = FANCY_REQUEST_CODE
    private var activity: Activity? = null
    private var actionBarColor: Int = Color.parseColor("#F44336")
    private var actionBarTitle: String = ""
    private var chooseType = CHOOSE_TYPE_MULTIPLE //默认多选
    private var mode = CHOOSE_MODE_NORMAL

    fun withActivity(activity: Activity) : FilePicker {
        this.activity = activity
        return this
    }

    /**
     * 设置选择类型
     * @param type One of {@link #CHOOSE_TYPE_MULTIPLE}, {@link #CHOOSE_TYPE_SINGLE}.
     */
    fun chooseType(type: Int = CHOOSE_TYPE_MULTIPLE): FilePicker {
        if (type != CHOOSE_TYPE_SINGLE && type!= CHOOSE_TYPE_MULTIPLE) {
            throw IllegalArgumentException("chooseType value is illegal , must be one of #FilePicker.CHOOSE_TYPE_MULTIPLE or #FilePicker.CHOOSE_TYPE_SINGLE ")
        }
        chooseType = type
        return this
    }

    fun mode(mode: Int = CHOOSE_MODE_NORMAL) : FilePicker {
        if (mode != CHOOSE_MODE_NORMAL && mode != CHOOSE_MODE_CLASSIFICATION) {
            throw IllegalArgumentException("choose mode value is illegal, must be one of #FilePicker.CHOOSE_MODE_NORMAL or #FilePicker.CHOOSE_MODE_CLASSIFICATION")
        }
        this.mode = mode
        return this
    }
    /**
     * 定义requestCode
     * @param requestCode
     */
    fun requestCode(requestCode: Int): FilePicker {
        this.requestCode = requestCode
        return this
    }

    /**
     * 设置actionBar的背景色
     * @param color actionBar的背景色
     */
    fun actionBarColor(@ColorInt color: Int): FilePicker {
        actionBarColor = color
        return this
    }

    /**
     * 设置标题
     * @param title
     */
    fun title(title: String): FilePicker {
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
        when(mode) {
            0 -> startFilePicker()
            else -> startFileClassificationPicker()
        }
    }

    private fun startFilePicker() {
        val intent = Intent(activity, FilePickerActivity::class.java)
        intent.putExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        intent.putExtra(Utils.ACTION_BAR_TITLE_KEY, actionBarTitle)
        intent.putExtra(Utils.CHOOSE_TYPE_KEY, chooseType)
        activity?.startActivityForResult(intent, requestCode)
    }

    private fun startFileClassificationPicker() {
        val intent = Intent(activity, FileClassificationPickerActivity::class.java)
        intent.putExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        intent.putExtra(Utils.ACTION_BAR_TITLE_KEY, actionBarTitle)
        intent.putExtra(Utils.CHOOSE_TYPE_KEY, chooseType)
        activity?.startActivityForResult(intent, requestCode)
    }
}