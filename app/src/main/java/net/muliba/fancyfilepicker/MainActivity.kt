package net.muliba.fancyfilepicker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.PicturePicker
import org.jetbrains.anko.toast


class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_ASK_PERMISSIONS = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            gotoFilePicker()
        }
        buttonSingle.setOnClickListener {
            gotoSingleFilePicker()
        }

        buttonClip.setOnClickListener {
            PicturePicker().withActivity(this)
                    .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                    .forResult { filePaths ->
                        if (filePaths.isNotEmpty()) {
                            textView.text = filePaths[0]
                        }
                    }
        }
        buttonMultiChoose.setOnClickListener {
            PicturePicker().withActivity(this)
                    .actionBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .forResult { filePaths ->
                        val buffer = StringBuffer()
                        filePaths.map { buffer.append(it).append(" ; ") }
                        textView.text = buffer.toString()
                    }
        }
        buttonJavaMain.setOnClickListener {
            val intent = Intent(this@MainActivity, JavaMainActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            FilePicker().withActivity(this)
                    .chooseType(FilePicker.CHOOSE_TYPE_MULTIPLE)
                    .existingResults(arrayListOf("/storage/emulated/0/DCIM/Camera/IMG_20170805_143117.jpg","/storage/emulated/0/DCIM/Camera/IMG_20170805_142052.jpg",
                            "/storage/emulated/0/DCIM/Camera/VID_20170805_200227.mp4"))
                    .forResult { filePaths ->
                        val buffer = StringBuffer()
                        filePaths.map { buffer.append(it).append(" ; ") }
                        textView.text = buffer.toString()
                    }
        }

        checkStoragePermission()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoFilePicker()
                } else {
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

    }

    private fun gotoFilePicker() {
        FilePicker()
                .withActivity(this)
                .forResult { filePaths ->
                    val buffer = StringBuffer()
                    filePaths.map { buffer.append(it).append(" ; ") }
                    textView.text = buffer.toString()
                }

    }

    private fun gotoSingleFilePicker() {
        FilePicker()
                .withActivity(this)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                .forResult { filePaths ->
                    if (filePaths.isNotEmpty()) {
                        textView.text = filePaths[0]
                    }
                }
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
