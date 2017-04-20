package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent
import android.support.annotation.ColorInt

/**
 * Created by fancy on 2017/4/12.
 */

class FilePicker {

    companion object {
        val FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY = "fancy_file_picker_array_result"
        val FANCY_FILE_PICKER_SINGLE_RESULT_KEY = "fancy_file_picker_single_result"
        val FANCY_REQUEST_CODE = 1024
        //选择类型
        val CHOOSE_TYPE_MULTIPLE = 0
        val CHOOSE_TYPE_SINGLE = 1
    }

    private var requestCode: Int = FANCY_REQUEST_CODE
    private var activity: Activity? = null
    private var actionBarColor: Int = 0xF44336
    private var actionBarTitle: String = ""
    private var chooseType = CHOOSE_TYPE_MULTIPLE //默认多选

    fun withActivity(activity: Activity) : FilePicker {
        this.activity = activity
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
            throw RuntimeException("not found Activity, Please execute the fun 'withActivity' ")
        }
        startFilePicker(requestCode)
    }

    private fun startFilePicker(requestCode: Int) {
        val intent = Intent(activity, FilePickerActivity::class.java)
        intent.putExtra(FilePickerActivity.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        intent.putExtra(FilePickerActivity.ACTION_BAR_TITLE_KEY, actionBarTitle)
        intent.putExtra(FilePickerActivity.CHOOSE_TYPE_KEY, chooseType)
        activity?.startActivityForResult(intent, requestCode)
    }
}