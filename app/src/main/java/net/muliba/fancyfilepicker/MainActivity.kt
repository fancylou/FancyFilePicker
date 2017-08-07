package net.muliba.fancyfilepicker

import android.Manifest
import android.app.Activity
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

    val FILE_PICKER_REQUEST_CODE = 1111
    val FILE_PICKER_SINGLE_REQUEST_CODE = 2222
    val PICTURE_PICKER_REQUEST_CODE = 1100
    val PICTURE_PICKER_SINGLE_REQUEST_CODE = 2200
    val REQUEST_CODE_ASK_PERMISSIONS = 120

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            checkStoragePermission()
        }
        buttonSingle.setOnClickListener {
            gotoSingleFilePicker()
        }
        buttonClassificationMode.setOnClickListener {
            gotoClassificationModeFilePicker()
        }
        buttonClip.setOnClickListener {
            PicturePicker().withActivity(this)
                    .requestCode(PICTURE_PICKER_SINGLE_REQUEST_CODE)
                    .start()
        }
        buttonMultiChoose.setOnClickListener {
            PicturePicker().withActivity(this)
                    .chooseType(PicturePicker.CHOOSE_TYPE_MULTIPLE)
                    .requestCode(PICTURE_PICKER_REQUEST_CODE)
                    .start()
        }
        buttonJavaMain.setOnClickListener {
            val intent = Intent(this@MainActivity, JavaMainActivity::class.java)
            startActivity(intent)
        }


        button2.setOnClickListener {
            FilePicker().withActivity(this)
                    .chooseType(FilePicker.CHOOSE_TYPE_MULTIPLE)
                    .mode(FilePicker.CHOOSE_MODE_CLASSIFICATION)
                    .existingResults(arrayListOf("/storage/emulated/0/DCIM/Camera/IMG_20170805_143117.jpg","/storage/emulated/0/DCIM/Camera/IMG_20170805_142052.jpg",
                            "/storage/emulated/0/DCIM/Camera/VID_20170805_200227.mp4"))
                    .requestCode(FILE_PICKER_REQUEST_CODE)
                    .start()
//            PicturePicker().withActivity(this)
//                    .chooseType(PicturePicker.CHOOSE_TYPE_MULTIPLE)
//                    .existingResults(arrayListOf("/storage/emulated/0/DCIM/Camera/IMG_20170805_143117.jpg","/storage/emulated/0/DCIM/Camera/IMG_20170805_142052.jpg"))
//                    .requestCode(PICTURE_PICKER_REQUEST_CODE)
//                    .start()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FILE_PICKER_REQUEST_CODE -> {
                    val buffer = StringBuffer()
                    val array = data?.getStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY)
                    array?.map { buffer.append(it).append(" ; ") }
                    textView.text = buffer.toString()
                    return
                }
                FILE_PICKER_SINGLE_REQUEST_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    textView.text = result
                    return
                }
                PICTURE_PICKER_REQUEST_CODE ->{
                    val buffer = StringBuffer()
                    val array = data?.getStringArrayListExtra(PicturePicker.FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY)
                    array?.map { buffer.append(it).append(" ; ") }
                    textView.text = buffer.toString()
                    return
                }
                PICTURE_PICKER_SINGLE_REQUEST_CODE -> {
                    val result = data?.getStringExtra(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY)
                    textView.text = result
                    return
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data)
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
        gotoFilePicker()
    }

    private fun gotoFilePicker() {
        FilePicker()
                .withActivity(this)
                .title("自定义标题")
                .actionBarColor(ContextCompat.getColor(this, R.color.colorAccent))
                .requestCode(FILE_PICKER_REQUEST_CODE)
                .start()
    }

    private fun gotoSingleFilePicker() {
        FilePicker()
                .withActivity(this)
                .requestCode(FILE_PICKER_SINGLE_REQUEST_CODE)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                .start()
    }

    private fun gotoClassificationModeFilePicker() {
        FilePicker()
                .withActivity(this)
                .requestCode(FILE_PICKER_REQUEST_CODE)
                .mode(FilePicker.CHOOSE_MODE_CLASSIFICATION)
                .start()
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
