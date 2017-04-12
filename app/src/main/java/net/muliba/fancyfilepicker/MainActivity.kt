package net.muliba.fancyfilepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import net.muliba.fancyfilepickerlibrary.FilePicker

class MainActivity : AppCompatActivity() {

    val FILE_PICKER_REQUEST_CODE = 1111

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { FilePicker().withActivity(MainActivity@this).requestCode(FILE_PICKER_REQUEST_CODE).start() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_PICKER_REQUEST_CODE) {
                val buffer = StringBuffer()
                val array = data?.getStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY)
                array?.map { Log.i("MainActivity", "filePath:$it"); buffer.append(it).append(" ; ") }
                textView.text = buffer.toString()
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
