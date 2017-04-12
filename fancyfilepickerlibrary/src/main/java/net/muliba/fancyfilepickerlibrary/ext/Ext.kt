package net.muliba.fancyfilepickerlibrary.ext

import android.app.Activity
import android.content.Intent

/**
 * Created by fancy on 2017/4/12.
 */


/**
 * 启动选择器
 */
inline fun <reified T : Activity> Activity.startWithRequestCode(requestCode: Int) {
    val intent = Intent(this, T::class.java)
    startActivityForResult(intent, requestCode)
}