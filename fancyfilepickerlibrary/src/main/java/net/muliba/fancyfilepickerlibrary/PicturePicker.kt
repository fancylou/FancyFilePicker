package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import com.wugang.activityresult.library.ActivityResult
import net.muliba.fancyfilepickerlibrary.ui.PictureLoaderActivity
import net.muliba.fancyfilepickerlibrary.util.Utils

/**
 * Created by fancy on 2017/5/22.
 */

class PicturePicker {
    companion object {
        @JvmStatic val FANCY_REQUEST_CODE @JvmName("FANCY_REQUEST_CODE") get() = 1024

        @JvmStatic val FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY @JvmName("FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY") get() = "fancy_picture_picker_array_result"
        @JvmStatic val FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY @JvmName("FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY") get() = "fancy_picture_picker_single_result"
        //选择类型
        @JvmStatic val CHOOSE_TYPE_MULTIPLE @JvmName("CHOOSE_TYPE_MULTIPLE") get() = 0
        @JvmStatic val CHOOSE_TYPE_SINGLE @JvmName("CHOOSE_TYPE_SINGLE") get() = 1
    }

    private var requestCode: Int = FANCY_REQUEST_CODE
    private var activity: Activity? = null
    private var actionBarColor: Int = Color.parseColor("#F44336")
    private var actionBarTitle: String = ""
    private var chooseType = CHOOSE_TYPE_MULTIPLE //默认多选
    private var existingResults = ArrayList<String>()


    fun withActivity(activity: Activity): PicturePicker {
        this.activity = activity
        return this
    }

    fun chooseType(type: Int = CHOOSE_TYPE_SINGLE): PicturePicker {
        if (type != CHOOSE_TYPE_SINGLE && type != CHOOSE_TYPE_MULTIPLE) {
            throw IllegalArgumentException("chooseType value is illegal , must be one of #PicturePicker.CHOOSE_TYPE_MULTIPLE or #PicturePicker.CHOOSE_TYPE_SINGLE ")
        }
        chooseType = type
        return this
    }

    /**
     * 定义requestCode
     * @param requestCode
     */
    @Deprecated(message = "4.0.0开始不再使用，用forResult直接返回结果，不需要onActivityResult接收结果")
    fun requestCode(requestCode: Int): PicturePicker {
        this.requestCode = requestCode
        return this
    }

    fun existingResults(results:ArrayList<String>): PicturePicker {
        this.existingResults.clear()
        if (!results.isEmpty()) {
           this.existingResults.addAll(results)
        }
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
    @Deprecated(message = "4.0.0开始不再使用，用forResult直接返回结果，不需要onActivityResult接收结果")
    fun start() {
        if (activity == null) {
            throw RuntimeException("not found Activity, Please execute the function 'withActivity' ")
        }
        val intent = Intent(activity, PictureLoaderActivity::class.java)
        intent.putExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        intent.putExtra(Utils.ACTION_BAR_TITLE_KEY, actionBarTitle)
        intent.putExtra(Utils.CHOOSE_TYPE_KEY, chooseType)
        intent.putExtra(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY, existingResults)
        activity?.startActivityForResult(intent, requestCode)
    }

    /**
     * 4.0.0 新增
     * 返回选择的结果 如果是单选 result[0]获取
     * 不再需要到onActivityResult中去接收结果
     */
    fun forResult(listener: (filePaths: List<String>) -> Unit) {
        if (activity == null) {
            throw RuntimeException("not found Activity, Please execute the function 'withActivity' ")
        }
        val bundle = Bundle()
        bundle.putInt(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, actionBarColor)
        bundle.putString(Utils.ACTION_BAR_TITLE_KEY, actionBarTitle)
        bundle.putInt(Utils.CHOOSE_TYPE_KEY, chooseType)
        bundle.putStringArrayList(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY, existingResults)
        ActivityResult.of(activity!!)
                .className(PictureLoaderActivity::class.java)
                .params(bundle)
                .greenChannel()
                .forResult { resultCode, data ->
                    val result = ArrayList<String>()
                    if (resultCode == Activity.RESULT_OK) {
                        if (chooseType == CHOOSE_TYPE_SINGLE) {
                            val single = data?.getStringExtra(FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY)
                            if (single!=null && single.isNotEmpty()) {
                                result.add(single)
                            }
                        }else {
                            val array = data?.getStringArrayListExtra(FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY)
                            if (array!=null && array.isNotEmpty()) {
                                result.addAll(array)
                            }
                        }
                    }
                    listener(result)
                }
    }
}