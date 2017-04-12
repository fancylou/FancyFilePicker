package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.content.Intent

/**
 * Created by fancy on 2017/4/12.
 */

class FilePicker {

    companion object {
        val FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY = "fancy_file_picker_result"
        val FANCY_REQUEST_CODE = 1024
    }

    var requestCode: Int = FANCY_REQUEST_CODE
    var activity: Activity? = null


    fun withActivity(activity: Activity) : FilePicker {
        this.activity = activity
        return this
    }
    fun requestCode(requestCode: Int): FilePicker {
        this.requestCode = requestCode
        return this
    }



    fun start() {
        if (activity==null) {
            throw RuntimeException("not found Activity, Please execute the fun 'withActivity' ")
        }
        startFilePicker(requestCode)
    }

    private fun startFilePicker(requestCode: Int) {
        val intent = Intent(activity, FilePickerActivity::class.java)
        activity?.startActivityForResult(intent, requestCode)
    }
}