package net.muliba.fancyfilepickerlibrary.ui

import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.muliba.fancyfilepickerlibrary.model.Classification
import net.muliba.fancyfilepickerlibrary.model.DataSource
import net.muliba.fancyfilepickerlibrary.util.DocumentTypeEnum
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancy on 2017/4/26.
 */

interface FileClassificationUIView {
    fun returnItems(items: ArrayList<net.muliba.fancyfilepickerlibrary.model.DataSource>)
}

class FileClassificationPresenter(val mView: net.muliba.fancyfilepickerlibrary.ui.FileClassificationUIView, val activity: android.app.Activity) {

    /**
     * 查询列表
     */
    fun loadingItems(level: Int = -1, parentPath: String = "", filter:HashSet<String> = HashSet()) {
        when (level) {
            -1 -> loadMainItems()
            0 -> loadPictureFolderItems()
            1 -> loadAudioItems()
            2 -> loadVideoItems()
            3 -> loadDocumentItems(filter)
            4 -> loadArchiveItems()
            5 -> loadApplicationItems()
            6 -> loadPictureItems(parentPath)
        }
    }


    /**
     * 计算每个分类的数量
     */
    fun countFiles(classification: net.muliba.fancyfilepickerlibrary.model.Classification, countTv: android.widget.TextView) {
        when (classification) {
            net.muliba.fancyfilepickerlibrary.model.Classification.PICTURE -> countPictures(countTv)
            net.muliba.fancyfilepickerlibrary.model.Classification.APPLICATION -> countApplication(countTv)
            net.muliba.fancyfilepickerlibrary.model.Classification.ARCHIVE -> countArchive(countTv)
            net.muliba.fancyfilepickerlibrary.model.Classification.AUDIO -> countAudio(countTv)
            net.muliba.fancyfilepickerlibrary.model.Classification.DOCUMENT -> countDocument(countTv)
            net.muliba.fancyfilepickerlibrary.model.Classification.VIDEO -> countVideo(countTv)
        }
    }


    /**
     * 加载主页面
     */
    private fun loadMainItems() {
        doAsync {
            val items = ArrayList<net.muliba.fancyfilepickerlibrary.model.DataSource>()
            net.muliba.fancyfilepickerlibrary.model.Classification.values().map { items.add(net.muliba.fancyfilepickerlibrary.model.DataSource.Main(it.stringResId, it.imageResId)) }
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
            val items = ArrayList<net.muliba.fancyfilepickerlibrary.model.DataSource>()
            val uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val PROJECTION = arrayOf(MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    "count(bucket_id) as cou",
                    MediaStore.Images.Media._ID)
            val selectStr: String = " _size > ? or _size is null ) GROUP BY  1,(2"
            val array = arrayOf("0")
            val query = activity.contentResolver.query(uri,
                    PROJECTION,
                    selectStr,
                    array,
                    "MAX(datetaken) DESC")
            while (query.moveToNext()) {
                val bucketId = query.getString(query.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val bucketName = query.getString(query.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                val filePath = query.getString(query.getColumnIndex(android.provider.MediaStore.Images.Media.DATA))
                val folderCount = query.getLong(3)
                if (folderCount < 1) {
                    continue
                }
                items.add(net.muliba.fancyfilepickerlibrary.model.DataSource.PictureFolder(bucketName, bucketId, filePath, folderCount))
            }
            query.close()
            uiThread { mView.returnItems(items) }
        }
    }

    /**
     * 加载某个目录下所有图片
     */
    private fun loadPictureItems(bucketId: String) {
        doAsync {
            val items = ArrayList<net.muliba.fancyfilepickerlibrary.model.DataSource>()
            val PROJECTION = arrayOf(MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_TAKEN)
            val SELECTION=  "${MediaStore.Images.Media.BUCKET_ID}  = ? and ( ${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null ) "
            val ORDER_BY = " ${MediaStore.Images.Media.DATE_TAKEN } DESC"
            val query = activity.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION,
                    SELECTION,
                    arrayOf(bucketId, "0"),
                    ORDER_BY)
            while (query.moveToNext()) {
                val filePath = query.getString(query.getColumnIndex(MediaStore.Images.Media.DATA))
                items.add(DataSource.Picture(filePath))
            }
            query.close()
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
            val selectStr: String = MediaStore.Audio.Media.SIZE+" > ? "
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Audio.AudioColumns._ID, MediaStore.Audio.AudioColumns.DATA),
                    selectStr,
                    arrayOf("0"),
                    MediaStore.Audio.Media.DATE_MODIFIED + " DESC")
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
            val selectStr: String = MediaStore.Video.Media.SIZE + " > ?"
            val array = arrayOf("0")
            val query = activity.contentResolver.query(uri, arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA),
                    selectStr,
                    array,
                    MediaStore.Video.Media.DATE_MODIFIED + " DESC")
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
    private fun loadDocumentItems(filter: HashSet<String>) {
        doAsync {
            val items = ArrayList<DataSource>()
            val uri = MediaStore.Files.getContentUri("external")
            val queryList = ArrayList<String>()
            if (filter.isEmpty()) {
                DocumentTypeEnum.values().map { queryList.add(it.value) }
            }else {
                filter.map { queryList.add(it) }
            }

            var selectStr: String = ""
            queryList.mapIndexed { index, s ->
                if (index>0) {
                    selectStr += (" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")
                }else {
                    selectStr += (MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")
                }
            }
            Log.i("loadDocumentItems", "selectStr:$selectStr")
            val array = queryList.toTypedArray()
            Log.i("loadDocumentItems", "array:${array.size}")
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
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
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
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
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
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
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.DATE_TAKEN),
                    "${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null",
                    arrayOf("0"),
                    null)
            val count = query.count
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
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA),
                    MediaStore.Audio.Media.SIZE+" > ? ",
                    arrayOf("0"),
                    null)
            val count = query.count
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
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA),
                    "${MediaStore.Video.Media.SIZE} > ? ",
                    arrayOf("0"),
                    null)
            val count = query.count
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

            val array = arrayOf("text/plain", "text/xml", "application/msword", "application/pdf", "application/vnd.ms-powerpoint", "application/vnd.ms-excel")
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    null)
            val count = query.count
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
            val array = arrayOf("application/zip")
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    null)
            val count = query.count
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
            val array = arrayOf("application/vnd.android.package-archive")
            val query = activity.contentResolver.query(uri,
                    arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                    selectStr,
                    array,
                    null)
            val count = query.count
            query.close()
            uiThread {
                if (countTv.tag.equals(Classification.APPLICATION)) {
                    countTv.text = "($count)"
                }
            }
        }
    }
}