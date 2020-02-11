package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.wugang.activityresult.library.ActivityResult
import net.muliba.fancyfilepickerlibrary.ui.FileActivity
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

    }

    private var requestCode: Int = FANCY_REQUEST_CODE
    private var activity: Activity? = null
    private var chooseType = CHOOSE_TYPE_MULTIPLE //默认多选
    private var existingResults = ArrayList<String>()

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
    /**
     * 定义requestCode
     * @param requestCode
     */
    @Deprecated(message = "4.0.0开始不再使用，用forResult直接返回结果，不需要onActivityResult接收结果")
    fun requestCode(requestCode: Int): FilePicker {
        this.requestCode = requestCode
        return this
    }

    fun existingResults(results:ArrayList<String>): FilePicker {
        this.existingResults.clear()
        if (results.isNotEmpty()) {
            this.existingResults.addAll(results)
        }
        return this
    }

    /**
     * 启动选择器
     */
    @Deprecated(message = "4.0.0开始不再使用，用forResult直接返回结果，不需要onActivityResult接收结果")
    fun start() {
        if (activity==null) {
            throw RuntimeException("not found Activity, Please execute the function 'withActivity' ")
        }
        startFilePicker()
    }

    /**
     * 4.0.0 新增
     * 返回选择的结果 如果是单选 result[0]获取
     * 不再需要到onActivityResult中去接收结果
     */
    fun forResult(listener: (filePaths: List<String>) -> Unit) {
        if (activity==null) {
            throw RuntimeException("not found Activity, Please execute the function 'withActivity' ")
        }
        val bundle = Bundle()
        bundle.putInt(Utils.CHOOSE_TYPE_KEY, chooseType)
        bundle.putStringArrayList(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY, existingResults)
        ActivityResult.of(activity!!)
                .className(FileActivity::class.java)
                .params(bundle)
                .greenChannel()
                .forResult { resultCode, data ->
                    val result = ArrayList<String>()
                    if (resultCode == Activity.RESULT_OK) {
                        if (chooseType == CHOOSE_TYPE_SINGLE) {
                            val filePath = data?.getStringExtra(FANCY_FILE_PICKER_SINGLE_RESULT_KEY) ?: ""
                            if (filePath.isNotEmpty()) {
                                result.add(filePath)
                            }
                        }else {
                            val array = data?.getStringArrayListExtra(FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY)
                            if (array !=null && array.isNotEmpty()) {
                                result.addAll(array)
                            }
                        }
                    }
                    listener(result)
                }
    }



    private fun startFilePicker() {
        val intent = Intent(activity, FileActivity::class.java)
        intent.putExtra(Utils.CHOOSE_TYPE_KEY, chooseType)
        intent.putExtra(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY, existingResults)
        activity?.startActivityForResult(intent, requestCode)
    }

}