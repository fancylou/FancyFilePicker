package net.muliba.fancyfilepickerlibrary.ui

import android.app.Activity
import net.muliba.fancyfilepickerlibrary.model.DataSource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

/**
 * Created by fancy on 2017/5/22.
 */


object PictureChooseContract {
    interface View {
        fun myActivity(): Activity
        fun firstInit(pictureFolders: List<DataSource.PictureFolder>, mCurrentDir: File?)
        fun refreshPictureList(pictures: List<String>)
    }

    interface Presenter {
        fun firstInit()
        fun loadPictures(mCurrentDir: File?)
    }
}

//typealias  MyFileFileter = (File, String) -> Boolean

class PictureChoosePresenter(val view: PictureChooseContract.View): PictureChooseContract.Presenter {

//    val filter: MyFileFileter = {_, filename ->
//        if (filename.endsWith(".jpeg") ||
//                filename.endsWith(".png") ||
//                filename.endsWith("jpg")) {
//            true
//        }
//        false
//    }

    override fun firstInit() {
        doAsync {
            var maxCount = 0
            var mCurrentDir: File? = null
            val list = ArrayList<DataSource.PictureFolder>()
            val set = HashSet<String>()
            val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selectStr: String = android.provider.MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + android.provider.MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + android.provider.MediaStore.Images.Media.MIME_TYPE + " = ?"
            val array = arrayOf("image/jpeg", "image/jpg", "image/png")
            val query = view.myActivity().contentResolver.query(uri, null, selectStr, array, android.provider.MediaStore.Images.Media.DATE_MODIFIED + " DESC ")
            while (query.moveToNext()) {
                val filePath = query.getString(query.getColumnIndex(android.provider.MediaStore.Images.Media.DATA))
                val parent = java.io.File(filePath).parentFile
                if (!parent.exists()) {
                    continue
                }
                val parentDir = parent.absolutePath
                if (set.contains(parentDir)) {
                    continue
                }
                set.add(parentDir)
                val picSize = parent.list(java.io.FilenameFilter { _, filename ->
                    if (filename.endsWith(".jpg") ||
                            filename.endsWith(".jpeg") ||
                            filename.endsWith(".png")) {
                        return@FilenameFilter true
                    }
                    false
                }).size
                val folderName = parentDir.substring(parentDir.lastIndexOf("/")+1)
                val folder = DataSource.PictureFolder(folderName, parentDir, filePath, picSize)
                list.add(folder)
                if (picSize > maxCount) {
                    maxCount = picSize
                    mCurrentDir = parent
                }
            }
            query.close()
            uiThread { view.firstInit(list, mCurrentDir) }
        }
    }

    override fun loadPictures(mCurrentDir: File?) {
        doAsync {
            val pictures = ArrayList<String>()
            if (mCurrentDir != null) {
                val files = mCurrentDir.listFiles(java.io.FilenameFilter { _, filename ->
                    if (filename.endsWith(".jpg") ||
                            filename.endsWith(".jpeg") ||
                            filename.endsWith(".png")) {
                        return@FilenameFilter true
                    }
                    false
                })
                files.sortByDescending(File::lastModified)
                files.map { pictures.add(it.name) }
            }

            uiThread { view.refreshPictureList(pictures) }
        }
    }
}