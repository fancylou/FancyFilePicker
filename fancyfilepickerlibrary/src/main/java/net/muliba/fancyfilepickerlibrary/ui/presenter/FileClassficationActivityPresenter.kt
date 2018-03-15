package net.muliba.fancyfilepickerlibrary.ui.presenter

import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.muliba.fancyfilepickerlibrary.model.Classification
import net.muliba.fancyfilepickerlibrary.model.DataSource
import net.muliba.fancyfilepickerlibrary.ui.view.FileClassificationUIView
import net.muliba.fancyfilepickerlibrary.util.Utils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by fancyLou on 2018/3/15.
 * Copyright © 2018 O2. All rights reserved.
 */


class FileClassficationActivityPresenter {

    private val QUERY_FILE_URI = MediaStore.Files.getContentUri("external")
    private val QUERY_AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    private val QUERY_VIDEO_URI =  MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    private val QUERY_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI


    private var mView: FileClassificationUIView? = null

    fun attachView(view: FileClassificationUIView) {
        mView = view
    }

    fun detachView() {
        mView = null
    }


    /**
     * 计算每个分类的数量
     */
    fun countFiles(classification: Classification, countTv: android.widget.TextView) {
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
     * 查询列表
     */
    fun loadingItems(level: Int = -1, parentPath: String = "", filter: HashSet<String> = HashSet()) {
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
     * 计算视频数量
     */
    private fun countVideo(countTv: TextView) {
        doAsync {
            val count = if (mView!=null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_VIDEO_URI,
                        arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA),
                        "${MediaStore.Video.Media.SIZE} > ? ",
                        arrayOf("0"),
                        null)
                val count = query.count
                query.close()
                count
            }else {
                0
            }
            uiThread {
                if (countTv.tag == Classification.VIDEO) {
                    countTv.text = "($count)"
                }
            }
        }
    }
    /**
     * 计算文档数量
     */
    private fun countDocument(countTv: TextView) {
        doAsync {
            val count = if (mView!=null) {
                val selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")
                        .concat(" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ?")

                val list = Utils.DOCUMENT_TYPE_LABEL_ARRAY.map {
                    Utils.getMimeTypeFromExtension(it)
                }.toTypedArray()
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
                        arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                        selectStr,
                        list,
                        null)
                val count = query.count
                query.close()
                count
            }else {
                0
            }
            uiThread {
                if (countTv.tag == Classification.DOCUMENT) {
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
            val count = if(mView!=null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_AUDIO_URI,
                        arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA),
                        MediaStore.Audio.Media.SIZE + " > ? ",
                        arrayOf("0"),
                        null)
                val count = query.count
                query.close()
                count
            }else {
                0
            }
            uiThread {
                if (countTv.tag == Classification.AUDIO) {
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
            val count = if(mView!=null) {
                val selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
                val array = arrayOf(Utils.getMimeTypeFromExtension(Utils.DOUMENT_TYPE_LABEL_ZIP))
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
                        arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                        selectStr,
                        array,
                        null)
                val count = query.count
                query.close()
                count
            }else {
                0
            }
            uiThread {
                if (countTv.tag == Classification.ARCHIVE) {
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
            val count = if(mView!=null) {
                val selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
                val array = arrayOf(Utils.getMimeTypeFromExtension(Utils.DOUMENT_TYPE_LABEL_APK))
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
                        arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA),
                        selectStr,
                        array,
                        null)
                val count = query.count
                query.close()
                count
            }else {
                0
            }

            uiThread {
                if (countTv.tag == Classification.APPLICATION) {
                    countTv.text = "($count)"
                }
            }
        }
    }

    /**
     * 计算图片数量
     */
    private fun countPictures(countTv: TextView) {
        doAsync {
            val count = if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_IMAGE_URI,
                        arrayOf(MediaStore.Images.Media._ID,
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_TAKEN),
                        "${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null",
                        arrayOf("0"),
                        null)
                val count = query.count
                query.close()
                count
            } else {
                0
            }
            uiThread {
                if (countTv.tag == Classification.PICTURE) {
                    countTv.text = "($count)"
                }
            }
        }
    }


    /**
     * 加载某个目录下所有图片
     */
    private fun loadPictureItems(bucketId: String) {
        doAsync {
            val items = ArrayList<DataSource>()
            val PROJECTION = arrayOf(MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_TAKEN)
            val SELECTION = "${MediaStore.Images.Media.BUCKET_ID}  = ? and ( ${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null ) "
            val ORDER_BY = " ${MediaStore.Images.Media.DATE_TAKEN} DESC"
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_IMAGE_URI,
                        PROJECTION,
                        SELECTION,
                        arrayOf(bucketId, "0"),
                        ORDER_BY)
                while (query.moveToNext()) {
                    val filePath = query.getString(query.getColumnIndex(MediaStore.Images.Media.DATA))
                    items.add(DataSource.Picture(filePath))
                }
                query.close()
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载应用安装包文件
     */
    private fun loadApplicationItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            val array = arrayOf(Utils.getMimeTypeFromExtension(Utils.DOUMENT_TYPE_LABEL_APK))
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
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
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载压缩包
     */
    private fun loadArchiveItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val selectStr: String = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
            val array = arrayOf(Utils.getMimeTypeFromExtension(Utils.DOUMENT_TYPE_LABEL_ZIP))
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
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
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载文档列表
     */
    private fun loadDocumentItems(filter: HashSet<String>) {
        doAsync {
            val items = ArrayList<DataSource>()
            val queryList = ArrayList<String>()
            if (filter.isEmpty()) {
                Utils.DOCUMENT_TYPE_LABEL_ARRAY.map { queryList.add(Utils.getMimeTypeFromExtension(it)) }
            } else {
                filter.map { queryList.add(it) }
            }
            var selectStr = ""
            queryList.mapIndexed { index, _ ->
                selectStr += if (index > 0) {
                    (" or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")
                } else {
                    (MediaStore.Files.FileColumns.MIME_TYPE + " = ? ")
                }
            }
            val array = queryList.toTypedArray()
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_FILE_URI,
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
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载视频文件列表
     */
    private fun loadVideoItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val selectStr: String = MediaStore.Video.Media.SIZE + " > ?"
            val array = arrayOf("0")
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_VIDEO_URI, arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA),
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
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载音频文件列表
     */
    private fun loadAudioItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            //FIXME 用MIME_TYPE查不到文件？？？
            val selectStr: String = MediaStore.Audio.Media.SIZE + " > ? "
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_AUDIO_URI,
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
            }
            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载图片文件夹
     */
    private fun loadPictureFolderItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            val projection = arrayOf(MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    "count(bucket_id) as cou",
                    MediaStore.Images.Media._ID)
            val selectStr = " _size > ? or _size is null ) GROUP BY  1,(2"
            val array = arrayOf("0")
            if (mView != null) {
                val query = mView!!.contextInstance().contentResolver.query(QUERY_IMAGE_URI,
                        projection,
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
                    items.add(DataSource.PictureFolder(bucketName, bucketId, filePath, folderCount))
                }
                query.close()
            }

            uiThread { mView?.returnItems(items) }
        }
    }

    /**
     * 加载主页面
     */
    private fun loadMainItems() {
        doAsync {
            val items = ArrayList<DataSource>()
            Classification.values().map { items.add(DataSource.Main(it.stringResId, it.imageResId)) }
            uiThread {
                mView?.returnItems(items)
            }
        }
    }


}