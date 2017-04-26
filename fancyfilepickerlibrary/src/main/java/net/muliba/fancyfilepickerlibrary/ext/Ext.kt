package net.muliba.fancyfilepickerlibrary.ext

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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


fun ViewGroup.inflate(layout: Int) : View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

/**
 * 连加
 */
fun String.concat(other: String): String {
    return this + other
}

/**
 * 长度转成文件大小名称
 */
fun Long.friendlyFileLength() : String{
    if (this < 1024) {
        return this.toString()+" B"
    }else {
        val kb : Long = this/1024
        if (kb < 1024) {
            return kb.toString() + " KB"
        }else {
            val mb = kb / 1024
            if (mb < 1024) {
                return mb.toString() + " MB"
            }else {
                val gb = mb /1024
                return gb.toString() + " GB"
            }
        }
    }
}