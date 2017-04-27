package net.muliba.fancyfilepickerlibrary.model

import net.muliba.fancyfilepickerlibrary.R

/**
 * Created by fancy on 2017/4/26.
 */
sealed class DataSource {

    class Main(var nameRes: Int = R.string.item_classification_picture,
               var imageRes: Int = R.drawable.category_icon_image) : DataSource()

    class File(var path: String,
               var file: java.io.File) : DataSource()

    class PictureFolder(var name: String,
                        var dir: String,
                        var firstImagePath: String,
                        var childrenCount: Int) : DataSource()

    class Picture(var path: String) : DataSource()

}