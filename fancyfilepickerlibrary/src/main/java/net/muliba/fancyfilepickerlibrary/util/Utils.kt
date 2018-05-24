package net.muliba.fancyfilepickerlibrary.util

import android.webkit.MimeTypeMap
import net.muliba.fancyfilepickerlibrary.R
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

    val DOCUMENT_TYPE_LABEL_ARRAY = arrayListOf(
            "TXT",
            "XML",
            "HTML",
            "DOC",
            "XLS",
            "PPT",
            "PDF")
    val DOUMENT_TYPE_LABEL_ZIP = "ZIP"
    val DOUMENT_TYPE_LABEL_APK = "APK"


    fun getMimeTypeFromExtension(extension: String): String =  MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())

    fun formatTime(time: Long): String {
        if (Locale.getDefault() == Locale.CHINA || Locale.getDefault() == Locale.CHINESE || Locale.getDefault() == Locale.SIMPLIFIED_CHINESE){
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm" , Locale.getDefault())
            return format.format(Date(time))
        }else {
            val format = SimpleDateFormat("MMMM d, yyyy HH:mm" , Locale.getDefault())
            return format.format(Date(time))
        }

    }


    /**
     * 根据后缀名获取资源
     */
    fun fileIcon(extension: String) : Int {
        return when(extension) {
            "ai" -> R.drawable.ic_file_ai_48dp
            "mp3","wav" -> R.drawable.ic_file_audio_48dp
            "mp4","m4v","mov" -> R.drawable.ic_file_mp4_48dp
            "mkv","rmvb","avi","wmv","mpg","mpeg" -> R.drawable.ic_file_video_48dp
            "jpg","jpeg","png","gif","bmp","raw","tiff" -> R.drawable.ic_file_image_48dp
            "html" -> R.drawable.ic_file_html_48dp
            "xml" -> R.drawable.ic_file_xml_48dp
            "csv" -> R.drawable.ic_file_csv_48dp
            "xls","xlsx" -> R.drawable.ic_file_excel_48dp
            "doc","docx" -> R.drawable.ic_file_word_48dp
            "ppt","pptx" -> R.drawable.ic_file_ppt_48dp
            "psd" -> R.drawable.ic_file_psd_48dp
            "one","note" -> R.drawable.ic_file_box_notes_48dp
            "eps" -> R.drawable.ic_file_eps_48dp
            "exe" -> R.drawable.ic_file_exe_48dp
            "fla" -> R.drawable.ic_file_flash_48dp
            "keynote" -> R.drawable.ic_file_keynote_48dp
            "pages" -> R.drawable.ic_file_pages_48dp
            "pdf" -> R.drawable.ic_file_pdf_48dp
            "rtf" -> R.drawable.ic_file_rtf_48dp
            "txt" -> R.drawable.ic_file_txt_48dp
            "vsd" -> R.drawable.ic_file_visio_48dp
            "zip","gzip" -> R.drawable.ic_file_zip_48dp
            "rar","7z","tar" -> R.drawable.ic_file_attachment_48dp
            else -> R.drawable.ic_file_unknown_48dp
        }
    }
}