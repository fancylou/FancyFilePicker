package net.muliba.fancyfilepickerlibrary.ui

import android.app.Activity
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_picture_choose.*
import kotlinx.android.synthetic.main.toolbar.*
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.adapter.PictureCursorRecyclerViewAdapter
import net.muliba.fancyfilepickerlibrary.ext.lightOff
import net.muliba.fancyfilepickerlibrary.ext.lightOn
import net.muliba.fancyfilepickerlibrary.ui.view.ListAlbumPopupWindow
import net.muliba.fancyfilepickerlibrary.util.ImageLoader
import net.muliba.fancyfilepickerlibrary.util.TransparentItemDecoration
import net.muliba.fancyfilepickerlibrary.util.Utils
import org.jetbrains.anko.toast

class PictureLoaderActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private val TAG = "PictureLoaderActivity"
    private val PHOTO_LOADER_ID = 1024
    private val mItemDecoration by lazy { TransparentItemDecoration(this@PictureLoaderActivity, LinearLayoutManager.VERTICAL) }
    val popupWindow: ListAlbumPopupWindow by lazy { ListAlbumPopupWindow(this) }
    val mAdapter: PictureCursorRecyclerViewAdapter by lazy { object : PictureCursorRecyclerViewAdapter() {
        override fun bindView(holder: FileViewHolder, cursor: Cursor?) {
            cursor?.let {
                val image = holder.getView<ImageView>(R.id.image_picture_picker)
                image.setImageResource(R.drawable.ic_file_image_48dp)
                val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(filePath, image)
                val checkbox = holder.getView<CheckBox>(R.id.checkBox_picture_picker)
                if (chooseType == PicturePicker.CHOOSE_TYPE_SINGLE) {
                    checkbox.visibility = View.GONE
                }else {
                    checkbox.visibility = View.VISIBLE
                    checkbox.isChecked = false
                    if (mSelected.contains(filePath)){
                        checkbox.isChecked = true
                    }
                    //checkbox click
                    checkbox.setOnClickListener {
                        var check = checkbox.isChecked
                        toggleItem(filePath, check)
                    }
                }

                holder.convertView.setOnClickListener {
                    if (chooseType == PicturePicker.CHOOSE_TYPE_SINGLE) {
                        intent.putExtra(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, filePath)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }else {
                        var check = checkbox.isChecked
                        checkbox.isChecked = !check
                        toggleItem(filePath, !check)
                    }
                }
            }
        }
    }}
    private val mSelected = HashSet<String>()

    private var chooseType = PicturePicker.CHOOSE_TYPE_SINGLE
    private var mCurrentAlbumName:String = ""
    private var mCurrentAlbumId = Utils.RECENT_ALBUM_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_choose)
        //获取数据
        var actionBarColor = intent.getIntExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, 0xF44336)
        var actionBarTitle = intent.getStringExtra(Utils.ACTION_BAR_TITLE_KEY)
        chooseType = intent.getIntExtra(Utils.CHOOSE_TYPE_KEY, PicturePicker.CHOOSE_TYPE_SINGLE)
        val mBackResults = intent.getStringArrayListExtra(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY) ?: ArrayList<String>()
        if (!mBackResults.isEmpty()) {
            mBackResults.map {
                mSelected.add(it)
            }
        }
        if (TextUtils.isEmpty(actionBarTitle)) {
            actionBarTitle = getString(R.string.title_activity_image_picker)
        }
        toolbar.title = actionBarTitle
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setBackgroundColor(actionBarColor)
        toolbar.setNavigationOnClickListener { finish() }

        if (chooseType == PicturePicker.CHOOSE_TYPE_SINGLE) {
            pictureChooseBtn.visibility = View.GONE
        }else {
            pictureChooseBtn.visibility = View.VISIBLE
        }
        //choose
        pictureChooseBtn.setOnClickListener {
            if (mSelected.size > 0) {
                val array = ArrayList<String>()
                mSelected.map { array.add(it) }
                intent.putStringArrayListExtra(PicturePicker.FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY, array)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }else {
                toast(getString(R.string.message_please_select_more_than_one_picture))
            }
        }
        bottomDirChooseBtn.setOnClickListener {
            popupWindow.animationStyle = R.style.dir_popupwindow_anim
            popupWindow.showAtLocation(content, Gravity.BOTTOM, 0, 0)
            lightOff()
        }
        popupWindow.setOnDismissListener { lightOn() }
        popupWindow.albumClickListener = object : ListAlbumPopupWindow.AlbumClickListener {
            override fun onClick(bucketId: String, bucketName: String) {
                mCurrentAlbumId = bucketId
                mCurrentAlbumName = bucketName
                dirNameTV.text = mCurrentAlbumName
                restartLoadPhoto()
                popupWindow.dismiss()
            }
        }
        pictureListRV.layoutManager = GridLayoutManager(this, 3)
        pictureListRV.removeItemDecoration(mItemDecoration)
        pictureListRV.addItemDecoration(mItemDecoration)
        pictureListRV.adapter = mAdapter
        mCurrentAlbumName = getString(R.string.recent_photo)
        dirNameTV.text = mCurrentAlbumName
        loadPhoto()
    }

    override fun onDestroy() {
        if (popupWindow!=null) {
            supportLoaderManager?.destroyLoader(popupWindow.ALBUM_LOADER_ID)
        }
        supportLoaderManager?.destroyLoader(PHOTO_LOADER_ID)
        super.onDestroy()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        when (id) {
            PHOTO_LOADER_ID -> {
                val albumId = args?.getString(ALBUM_ID_KEY) ?: ""
                return createCursorLoader(albumId)
            }
            else -> {
                Log.e(TAG, "do not recognize onCreateLoader id , id:$id")
                return createCursorLoader(Utils.RECENT_ALBUM_ID)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d(TAG, "onLoaderReset ......")
        mAdapter.changeCursor(null)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        Log.d(TAG, "onLoadFinished ......")
        mAdapter.changeCursor(cursor)
    }


    private fun toggleItem(filePath: String, isChecked: Boolean) {
        if (isChecked) {
            mSelected.add(filePath)
        } else {
            mSelected.remove(filePath)
        }
        refreshMenu()
    }

    private fun refreshMenu() {
        if (mSelected.size == 0) {
            pictureChooseBtn.text = getString(R.string.picker)
        }else {
            pictureChooseBtn.text = getString(R.string.picker) + "(${mSelected.size})"
        }
    }


    private val ALBUM_ID_KEY = "ALBUM_ID_KEY"

    private fun loadPhoto() {
        val args = Bundle()
        args.putString(ALBUM_ID_KEY, mCurrentAlbumId)
        supportLoaderManager.initLoader(PHOTO_LOADER_ID, args, this)
    }

    /**
     * 已经initLoader过的 就需要用restartLoader
     */
    private fun restartLoadPhoto() {
        val args = Bundle()
        args.putString(ALBUM_ID_KEY, mCurrentAlbumId)
        supportLoaderManager.restartLoader(PHOTO_LOADER_ID, args, this)
    }

    private val PROJECTION = arrayOf(MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_TAKEN)
    private val ORDER_BY = " ${MediaStore.Images.Media.DATE_TAKEN } DESC"
    private val SELECTION_SIZE =  "${MediaStore.Images.Media.SIZE} > ? or ${MediaStore.Images.Media.SIZE} is null"
    //查询全部图片 还是 某一个相册的图片
    private fun createCursorLoader(albumId: String) : CursorLoader {
        return if (TextUtils.isEmpty(albumId) || albumId == Utils.RECENT_ALBUM_ID) {
            CursorLoader(this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION,
                    SELECTION_SIZE,
                    arrayOf("0"),
                    ORDER_BY)
        }else {
            CursorLoader(this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    PROJECTION,
                    MediaStore.Images.Media.BUCKET_ID + " = ? and (" + SELECTION_SIZE + ")",
                    arrayOf(albumId,"0"),
                    ORDER_BY)
        }
    }
}
