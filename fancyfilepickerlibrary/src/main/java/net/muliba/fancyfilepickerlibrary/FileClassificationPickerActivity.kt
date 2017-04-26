package net.muliba.fancyfilepickerlibrary

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_file_classification_picker.*
import kotlinx.android.synthetic.main.toolbar.*
import net.muliba.fancyfilepickerlibrary.adapter.FileClassificationAdapter
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.ext.friendlyFileLength
import net.muliba.fancyfilepickerlibrary.model.Classification
import net.muliba.fancyfilepickerlibrary.model.DataSource
import net.muliba.fancyfilepickerlibrary.util.Utils
import net.muliba.fancyfilepickerlibrary.util.fileIcon

class FileClassificationPickerActivity : AppCompatActivity(), FileClassificationUIView {

    private var chooseType = FilePicker.CHOOSE_TYPE_MULTIPLE
    private val mSelected = HashSet<String>()
    private val mItems = ArrayList<DataSource>()
    private val adapter: FileClassificationAdapter by lazy {
        object : FileClassificationAdapter(mItems) {
            override fun bindMain(holder: FileViewHolder, main: DataSource.Main, position: Int) {
                holder.setImageByResource(R.id.image_file_classification_picker_main_icon, main.imageRes)
                        .setText(R.id.tv_file_classification_picker_main_name, getString(main.nameRes))
                //load file count
                val countTv = holder.getView<TextView>(R.id.tv_file_classification_picker_main_count)
                countTv.tag = (Classification.values()[position])
                mPresenter.countFiles(Classification.values()[position], countTv)
            }

            override fun clickMain(main: DataSource.Main, position: Int) {
                Log.d("clickMain", "position:$position")
                if (position != 0) {
                    mProgressDialog.show()
                    mLevel = position
                    mPresenter.loadingItems(position)
                }
            }

            override fun bindFile(holder: FileViewHolder, file: DataSource.File, position: Int) {
                holder.setImageByResource(R.id.image_item_classification_picker_file_icon, fileIcon(file.file.extension))
                        .setText(R.id.tv_item_classification_picker_file_name, file.file.name)
                        .setText(R.id.tv_item_classification_picker_file_time, Utils.formatTime(file.file.lastModified()))
                        .setText(R.id.tv_item_classification_picker_file_size, file.file.length().friendlyFileLength())
            }

            override fun clickFile(file: DataSource.File, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }
    private var mLevel = -1 //所处的位置
    private val mPresenter: FileClassificationPresenter = FileClassificationPresenter(this, this@FileClassificationPickerActivity)
    private val mProgressDialog: ProgressDialog by lazy { ProgressDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_classification_picker)
        //获取数据
        var actionBarColor = intent.getIntExtra(Utils.ACTION_BAR_BACKGROUND_COLOR_KEY, 0xF44336)
        var actionBarTitle = intent.getStringExtra(Utils.ACTION_BAR_TITLE_KEY)
        chooseType = intent.getIntExtra(Utils.CHOOSE_TYPE_KEY, FilePicker.CHOOSE_TYPE_MULTIPLE)
        if (TextUtils.isEmpty(actionBarTitle)) {
            actionBarTitle = getString(R.string.title_activity_file_picker)
        }
        toolbar.title = actionBarTitle
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setBackgroundColor(actionBarColor)
        toolbar.setNavigationOnClickListener { finish() }
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        mProgressDialog.show()
        mPresenter.loadingItems()
    }

    override fun onBackPressed() {
        if (mLevel == -1) {
            super.onBackPressed()
        }else {
            upperLevel()
        }
    }


    override fun returnItems(items: ArrayList<DataSource>) {
        mProgressDialog.dismiss()
        mItems.clear()
        mItems.addAll(items)
        setupLayoutManager()
        adapter.notifyDataSetChanged()
    }


    private fun upperLevel() {
        when(mLevel) {
            0,1,2,3,4,5 -> {
                mLevel = -1
                mProgressDialog.show()
                mPresenter.loadingItems()
            }
        }
    }


    private fun initAdapter() {
        setupLayoutManager()
        recycler_file_classification_picker_list.adapter = adapter
    }

    private fun setupLayoutManager() {
        when(mLevel) {
            -1 -> {
                recycler_file_classification_picker_list.layoutManager = GridLayoutManager(this, 3)
            }
            1,2,3,4,5 -> {
                recycler_file_classification_picker_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            }
        }
    }




}
