package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.muliba.fancyfilepickerlibrary.model.Classification
import net.muliba.fancyfilepickerlibrary.model.DataSource
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FilenameFilter

/**
 * Created by fancy on 2017/4/26.
 */

interface FileClassificationUIView {
    fun returnItems(items: ArrayList<DataSource>)
}

class FileClassificationPresenter(val mView: FileClassificationUIView, val activity: Activity) {

    /**
     * 查询列表
     */
    fun loadingItems(level: Int = -1, parentPath: String = "") {
        when (level) {
            -1 -> loadMainItems()
            0 -> loadPictureFolderItems()
            1 -> loadAudioItems()
            2 -> loadVideoItems()
            3 -> loadDocumentItems()
            4 -> loadArchiveItems()
            5 -> loadApplicationItems()
            6 -> loadPictureItems(parentPath)
        }
    }


    /**
     * 计算每个分类的数量
     */
    fun countFiles(classification: Classification, countTv: TextView) {
        when (classification) {
            Classification.PICTURE -> countPictures(countTv)
            Classification.APPLICATION -> countApplication(countTv)
            Classification.ARCHIVE -> countArchive(countTv)
            Classification.AUDIO -> countAudio(countTv)
            Classification.DOCUMENT -> countDocument(countTv)
            Classification.VIDEO -> countVideo(countTv)
        }
    }


    /**
     * 加载主页面
     */
    private fun loadMainItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            Classification.values().map { items.add(DataSource.Main(it.stringResId, it.imageResId, 0)) }
            uiThread {
                mView.returnItems(items)
            }
        }
    }

    /**
     * 加载图片文件夹
     */
    private fun loadPictureFolderItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val set = HashSet<String>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selectStr: String = MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Images.Media.MIME_TYPE + " = ?"
            val array = arrayOf("image/jpeg", "image/jpg", "image/png")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Images.Media.DATE_MODIFIED + " DESC ")
            while (query.moveToNext()) {
                val filePath = query.getString(query.getColumnIndex(MediaStore.Images.Media.DATA))
                val parent = File(filePath).parentFile
                if (!parent.exists()) {
                    continue
                }
                val parentDir = parent.absolutePath
                if (set.contains(parentDir)) {
                    continue
                }
                set.add(parentDir)
                val picSize = parent.list(FilenameFilter { _, filename ->
                    if (filename.endsWith(".jpg") ||
                            filename.endsWith(".jpeg") ||
                            filename.endsWith(".png")) {
                        return@FilenameFilter true
                    }
                    false
                }).size
                if (picSize < 1) {
                    continue
                }
                items.add(DataSource.PictureFolder(parent.name, parentDir, filePath, picSize))
            }
            //sort
            items.sortByDescending {
                val o = it as DataSource.PictureFolder
                o.childrenCount
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载某个目录下所有图片
     */
    private fun loadPictureItems(parentPath: String) {
        doAsync {
            val items = ArrayList<DataSource>()
            val list = File(parentPath).listFiles(FilenameFilter { _, filename ->
                if (filename.endsWith(".jpg") ||
                        filename.endsWith(".jpeg") ||
                        filename.endsWith(".png")) {
                    return@FilenameFilter true
                }
                false
            })
            list.sortByDescending(File::lastModified)
            items.addAll(list.map { DataSource.Picture(it.absolutePath) })
            uiThread { mView.returnItems(items) }
        }
    }




    /**
     * 加载音频文件列表
     */
    private fun loadAudioItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            //用MIME_TYPE查不到文件？？？
//            val selectStr: String = MediaStore.Audio.Media.MIME_TYPE+" = ?"+" or "+ MediaStore.Audio.Media.MIME_TYPE+" = ?"
//            val array = arrayOf("audio/x-mpeg", "audio/mp3")
            val query = activity.contentResolver.query(uri, null, null, null, MediaStore.Audio.Media.DATE_MODIFIED + " DESC")
            while (query.moveToNext()) {
                val path = query.getString(query.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                val file = File(path)
                if (file.exists()) {
                    items.add(DataSource.File(path, file))
                } else {
                    Log.e("loadAudioItems", "path:$path not exists!")
                }
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载视频文件列表
     */
    private fun loadVideoItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val selectStr: String = MediaStore.Video.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Video.Media.MIME_TYPE + " = ?"
            val array = arrayOf("video/mp4", "video/x-matroska")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Video.Media.DATE_MODIFIED + " DESC")
            while (query.moveToNext()) {
                val path = query.getString(query.getColumnIndex(MediaStore.Video.Media.DATA))
                val file = File(path)
                if (file.exists()) {
                    items.add(DataSource.File(path, file))
                } else {
                    Log.e("loadVideoItems", "path:$path not exists!")
                }
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载文档列表
     */
    private fun loadDocumentItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
            val array = arrayOf("text/plain", "text/xml", "text/html", "application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            while (query.moveToNext()) {
                val path = query.getString(query.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val file = File(path)
                if (file.exists()) {
                    items.add(DataSource.File(path, file))
                } else {
                    Log.e("loadDocumentItems", "path:$path not exists!")
                }
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载压缩包
     */
    private fun loadArchiveItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            val array = arrayOf("application/zip")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            while (query.moveToNext()) {
                val path = query.getString(query.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val file = File(path)
                if (file.exists()) {
                    items.add(DataSource.File(path, file))
                } else {
                    Log.e("loadArchiveItems", "path:$path not exists!")
                }
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载应用安装包文件
     */
    private fun loadApplicationItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            val array = arrayOf("application/vnd.android.package-archive")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            while (query.moveToNext()) {
                val path = query.getString(query.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val file = File(path)
                if (file.exists()) {
                    items.add(DataSource.File(path, file))
                } else {
                    Log.e("loadApplicationItems", "path:$path not exists!")
                }
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 计算图片数量
     */
    private fun countPictures(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selectStr: String = MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Images.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Images.Media.MIME_TYPE + " = ?"
            val array = arrayOf("image/jpeg", "image/jpg", "image/png")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Images.Media.DATE_MODIFIED + " DESC ")
            val count = query.count
            Log.d("countPictures", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.PICTURE)) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算mp3数量
     */
    private fun countAudio(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            //用MIME_TYPE查不到文件？？？
//            val selectStr: String = MediaStore.Audio.Media.MIME_TYPE+" = ?"+" or "+ MediaStore.Audio.Media.MIME_TYPE+" = ?"
//            val array = arrayOf("audio/x-mpeg", "audio/mp3")
            val query = activity.contentResolver.query(uri, null, null, null, MediaStore.Audio.Media.DATE_MODIFIED + " DESC")
            val count = query.count
            Log.d("countAudio", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.AUDIO)) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算视频数量
     */
    private fun countVideo(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val selectStr: String = MediaStore.Video.Media.MIME_TYPE + " = ?" + " or " + MediaStore.Video.Media.MIME_TYPE + " = ?"
            val array = arrayOf("video/mp4", "video/x-matroska")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Video.Media.DATE_MODIFIED + " DESC")
            val count = query.count
            Log.d("countVideo", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.VIDEO)) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算视频数量
     */
    private fun countDocument(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                    .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")

            Log.d("countDocument", selectStr)
            val array = arrayOf("text/plain", "text/xml", "application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            val count = query.count
            Log.d("countDocument", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.DOCUMENT)) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算压缩包的数量
     */
    private fun countArchive(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            Log.d("countArchive", selectStr)
            val array = arrayOf("application/zip")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            val count = query.count
            Log.d("countArchive", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.ARCHIVE)) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算apk安装包的数量
     */
    private fun countApplication(countTv: TextView) {
        doAsync {
            val uri = MediaStore.Files.getContentUri("external")
            var selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            Log.d("countArchive", selectStr)
            val array = arrayOf("application/vnd.android.package-archive")
            val query = activity.contentResolver.query(uri, null, selectStr, array, MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
            val count = query.count
            Log.d("countArchive", "count:$count")
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.APPLICATION)) {
                    countTv.text = "($count)"
                }
            }
        }
    }
}