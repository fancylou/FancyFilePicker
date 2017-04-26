package net.muliba.fancyfilepickerlibrary.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by fancy on 2017/4/26.
 */

object Utils {
    val ACTION_BAR_BACKGROUND_COLOR_KEY = "ACTION_BAR_BACKGROUND_COLOR_KEY"
    val ACTION_BAR_TITLE_KEY = "ACTION_BAR_TITLE_KEY"
    val CHOOSE_TYPE_KEY = "CHOOSE_TYPE_KEY"


    fun formatTime(time: Long): String {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
        return format.format(Date(time))
    }
}