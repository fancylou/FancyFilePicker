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
    val MULIT_CHOOSE_BACK_RESULTS_KEY = "MULIT_CHOOSE_BACK_RESULTS_KEY"

    val RECENT_ALBUM_ID = "-1"
    val EMPTY_MEDIA_ID = "-1"


    fun formatTime(time: Long): String {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
        return format.format(Date(time))
    }
}