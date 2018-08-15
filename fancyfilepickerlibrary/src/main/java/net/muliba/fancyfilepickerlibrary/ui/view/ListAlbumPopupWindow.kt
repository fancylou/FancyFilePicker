package net.muliba.fancyfilepickerlibrary.ui.view

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.widget.CursorAdapter
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.util.ImageLoader
import net.muliba.fancyfilepickerlibrary.util.Utils


/**
 * Created by fancylou on 10/19/17.
 */



class ListAlbumPopupWindow(val fragmentActivity: FragmentActivity): PopupWindow(fragmentActivity), LoaderManager.LoaderCallbacks<Cursor> {

    private val TAG = "ListAlbumPopupWindow"
    val ALBUM_LOADER_ID = 10024

    var screenWidth : Int = 0
    var screenHeight : Int = 0
    var albumClickListener: AlbumClickListener? = null

    val mAdapter:ListAlbumAdapter by lazy { ListAlbumAdapter(fragmentActivity, null) }


    init {
        calWidthAndHeight()
        contentView = LayoutInflater.from(fragmentActivity).inflate(R.layout.popup_picture_folders, null)
        width = screenWidth
        height = screenHeight
        isFocusable = true
        isTouchable = true
        isOutsideTouchable = true
        setTouchInterceptor(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE) {
                dismiss()
                return@OnTouchListener true
            }
            false
        })

        initViews()
        //加载相册
        fragmentActivity.supportLoaderManager.initLoader(ALBUM_LOADER_ID, null, this)
    }

    private fun initViews() {
        val listView = contentView.findViewById(R.id.id_dir_list) as ListView
        listView.adapter = mAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val item = mAdapter.getItem(position)
            if (item!=null) {
                val currentCursor = item as Cursor
                val bucketId = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                val bucketName = currentCursor.getString(currentCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                albumClickListener?.onClick(bucketId, bucketName)
            }
        }
    }

    private fun calWidthAndHeight() {
        val windowManager = fragmentActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = (metrics.heightPixels * 0.7).toInt()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            ALBUM_LOADER_ID -> {
                return AlbumCursorLoader(fragmentActivity)
            }
            else -> {
                 val PROJECTION = arrayOf(MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_TAKEN)
                 val ORDER_BY = " ${MediaStore.Images.Media.DATE_TAKEN } DESC"
                 val SELECTION_SIZE =  "${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null"
                return CursorLoader(fragmentActivity,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        PROJECTION,
                        SELECTION_SIZE,
                        arrayOf("0"),
                        ORDER_BY)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mAdapter.swapCursor(null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        mAdapter.swapCursor(data)
    }


    interface AlbumClickListener {
        fun onClick(bucketId:String, bucketName: String)
    }



    /**
     * 重写CursorLoader 里面的loadInBackground函数
     * 主要是添加一个“最近”的相册出来，按照使用时间排序
     */
    open class AlbumCursorLoader : CursorLoader {

        constructor(context: Context): super(context,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.BUCKET_ID,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.DATA,
                        "count(bucket_id) as cou",
                        MediaStore.Images.Media._ID),
                " _size > ? or _size is null ) GROUP BY  1,(2",
                arrayOf("0"),
                "MAX(datetaken) DESC")

        override fun loadInBackground(): Cursor {
            val albums =  super.loadInBackground()
            val recentAlbum = MatrixCursor(arrayOf(MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                    "count(bucket_id) as cou",
                    MediaStore.Images.Media._ID))
            var count = 0L
            if (albums!=null) {
                if (albums.count > 0) {
                    while (albums.moveToNext()) {
                        count += albums.getLong(3)
                    }
                }
            }
            recentAlbum.addRow(arrayOf(Utils.RECENT_ALBUM_ID,
                    context.getString(R.string.recent_photo),
                    "",//TODO 没有图片
                    "$count",
                    Utils.EMPTY_MEDIA_ID))
            return MergeCursor(arrayOf(recentAlbum, albums))

        }
    }

    inner class ListAlbumAdapter(context: Context, cursor: Cursor?): CursorAdapter(context, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {

        val layoutInflater: LayoutInflater by lazy { LayoutInflater.from(context) }

        override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
            val view = layoutInflater.inflate( R.layout.item_picture_folder_list, parent, false)
            val viewHolder = ViewHolder(view)
            view.tag = viewHolder
            return view
        }

        override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
            val tag = view?.tag
            if (tag!=null) {
                val holder = tag as ViewHolder
                val displayName = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)) ?: ""
                val filePath = cursor?.getString(2) ?: ""
                val count = cursor?.getLong(3) ?: 0
                Log.d(TAG, "albumName:$displayName, filePath:$filePath, count:$count")
                if (!TextUtils.isEmpty(filePath)) {
                    ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(filePath, holder.imageView)
                }
                holder.nameTv.text = displayName
                holder.countTv.text = "$count"
            }
        }

        inner class ViewHolder(itemView:View) {
            val imageView = itemView.findViewById(R.id.id_dir_item_image) as ImageView
            val nameTv = itemView.findViewById(R.id.id_dir_item_name) as TextView
            val countTv = itemView.findViewById(R.id.id_dir_item_count) as TextView
        }
    }
}
