package net.muliba.fancyfilepickerlibrary.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_picture_choose.*
import kotlinx.android.synthetic.main.toolbar.*
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.adapter.PictureAdapter
import net.muliba.fancyfilepickerlibrary.model.DataSource
import net.muliba.fancyfilepickerlibrary.ui.view.ListImageDirPopupWindow
import net.muliba.fancyfilepickerlibrary.util.ImageLoader
import net.muliba.fancyfilepickerlibrary.util.TransparentItemDecoration
import net.muliba.fancyfilepickerlibrary.util.Utils
import org.jetbrains.anko.toast
import java.io.File

class PictureChooseActivity : AppCompatActivity(), PictureChooseContract.View {


    private val mItemDecoration by lazy { TransparentItemDecoration(this@PictureChooseActivity, LinearLayoutManager.VERTICAL) }

    private var chooseType = PicturePicker.CHOOSE_TYPE_SINGLE
    private val mSelected = HashSet<String>()
    private var mImgs = ArrayList<String>()
    private var mCurrentDir: File? = null
    private var mScreenWidth = 0
    lateinit var popupWindow: ListImageDirPopupWindow

    private val presenter: PictureChoosePresenter = PictureChoosePresenter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_choose)
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var metrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(metrics)
        mScreenWidth = metrics.widthPixels
        //获取数据
        var actionBarColor = intent.getIntExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, 0xF44336)
        var actionBarTitle = intent.getStringExtra(Utils.ACTION_BAR_TITLE_KEY)
        chooseType = intent.getIntExtra(Utils.CHOOSE_TYPE_KEY, PicturePicker.CHOOSE_TYPE_SINGLE)
        if (TextUtils.isEmpty(actionBarTitle)) {
            actionBarTitle = getString(R.string.title_activity_file_picker)
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
            popupWindow.showAsDropDown(bottomToolBarRL, 0, 0)
            lightOff()
        }

        pictureListRV.layoutManager = GridLayoutManager(this, 3)
        pictureListRV.removeItemDecoration(mItemDecoration)
        pictureListRV.addItemDecoration(mItemDecoration)
        pictureListRV.adapter = adapter

        presenter.firstInit()
    }


    override fun myActivity(): Activity {
        return this
    }

    override fun firstInit(pictureFolders: List<DataSource.PictureFolder>, mCurrentDir: File?) {
        this.mCurrentDir = mCurrentDir
        dirNameTV.text = mCurrentDir?.name
        presenter.loadPictures(mCurrentDir)
        popupWindow = ListImageDirPopupWindow(this, pictureFolders)
        popupWindow.setOnDismissListener { lightOn() }
        popupWindow.setOnDirSelectedListener {
            this.mCurrentDir = File(it.dir)
            presenter.loadPictures(this.mCurrentDir)
            popupWindow.dismiss()
        }

    }

    override fun refreshPictureList(pictures: List<String>) {
        mImgs.clear()
        mImgs.addAll(pictures)
        adapter.notifyDataSetChanged()
    }

    private val adapter: PictureAdapter by lazy { object : PictureAdapter(mImgs) {
        override fun bindView(holder: FileViewHolder, name: String) {
            val image = holder.getView<ImageView>(R.id.image_picture_picker)
            image.setImageResource(R.drawable.ic_file_image_48dp)
            val path = mCurrentDir?.absolutePath + File.separator + name
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path, image)

            val checkbox = holder.getView<CheckBox>(R.id.checkBox_picture_picker)
            if (chooseType == PicturePicker.CHOOSE_TYPE_SINGLE) {
                checkbox.visibility = View.GONE
            }else {
                checkbox.visibility = View.VISIBLE
                checkbox.isChecked = false
                if (mSelected.contains(path)){
                    checkbox.isChecked = true
                }
                //checkbox click
                checkbox.setOnClickListener {
                    var check = checkbox.isChecked
                    toggleItem(path, check)
                }
            }

            holder.convertView.setOnClickListener {
                if (chooseType == PicturePicker.CHOOSE_TYPE_SINGLE) {
                    intent.putExtra(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, path)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }else {
                    var check = checkbox.isChecked
                    checkbox.isChecked = !check
                    toggleItem(path, !check)
                }
            }
        }
    }}

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
            pictureChooseBtn.text = getString(R.string.picker) + "(" + mSelected.size.toString() + ")"
        }
    }


    /**
     * 关闭popupWindow后内容区域变亮
     */
    private fun lightOn() {
        val lp = window.attributes
        lp.alpha = 1.0f
        window.attributes = lp
    }

    private fun lightOff() {
        val lp = window.attributes
        lp.alpha = 0.3f
        window.attributes = lp
    }
}
