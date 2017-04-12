package net.muliba.fancyfilepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    val FILE_PICKER_REQUEST_CODE = 1111
    val REQUEST_CODE_ASK_PERMISSIONS = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            checkStoragePermission()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            REQUEST_CODE_ASK_PERMISSIONS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoFilePicker()
                }else {
                    toast("没有文件读写权限，无法使用文件选择器！")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkStoragePermission() {
        //检查权限，6.0版本以上的机器必须检查权限
        if (Build.VERSION.SDK_INT >= 23) {
            val hasWriteStoragePermission = checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    toast("没有文件读写权限，请到系统的应用管理里面开启权限！")
                    return
                }
                requestWritePermission()
                return
            }
        }
        gotoFilePicker()
    }

    private fun gotoFilePicker() {
        FilePicker().withActivity(MainActivity@this).requestCode(FILE_PICKER_REQUEST_CODE).start()
    }

    /**
     * 请求写入权限
     */
    private fun requestWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_ASK_PERMISSIONS)
        }
    }
}
