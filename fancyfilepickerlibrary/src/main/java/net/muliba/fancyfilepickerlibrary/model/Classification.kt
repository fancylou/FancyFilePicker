package net.muliba.fancyfilepickerlibrary.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import net.muliba.fancyfilepickerlibrary.R

/**
 * Created by fancy on 2017/4/26.
 */
enum class Classification(@StringRes val stringResId: Int, @DrawableRes val imageResId: Int) {
    PICTURE(R.string.item_classification_picture, R.drawable.category_icon_image),
    AUDIO(R.string.item_classification_audio, R.drawable.category_icon_music),
    VIDEO(R.string.item_classification_video, R.drawable.category_icon_video),
    DOCUMENT(R.string.item_classification_file, R.drawable.category_icon_document),
    ARCHIVE(R.string.item_classification_archive, R.drawable.category_icon_compress),
    APPLICATION(R.string.item_classification_application, R.drawable.category_icon_application)
}