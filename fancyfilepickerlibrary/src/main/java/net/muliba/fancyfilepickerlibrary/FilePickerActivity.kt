package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_file_picker.*

class FilePickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)


        tv_file_picker_choose.setOnClickListener { chooseFile() }
    }

    private fun chooseFile() {
        val array = ArrayList<String>()
        array.add("filePath1")
        array.add("filePath2")
        array.add("filePath3")
        intent.putStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY, array)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
