package net.muliba.fancyfilepickerlibrary.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.breadcrumbs.*
import kotlinx.android.synthetic.main.fragment_file_classification_picker.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.adapter.FileClassificationAdapter
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.ext.concat
import net.muliba.fancyfilepickerlibrary.ext.friendlyFileLength
import net.muliba.fancyfilepickerlibrary.model.Classification
import net.muliba.fancyfilepickerlibrary.model.DataSource
import net.muliba.fancyfilepickerlibrary.ui.FileActivity
import net.muliba.fancyfilepickerlibrary.ui.FileClassificationPresenter
import net.muliba.fancyfilepickerlibrary.ui.FileClassificationUIView
import net.muliba.fancyfilepickerlibrary.util.*
import org.jetbrains.anko.alert

/**
 * Created by fancylou on 10/23/17.
 */


class FileClassificationPickerFragment: Fragment(), FileClassificationUIView {

    var mActivity: FileActivity? = null
    private val mDocumentTypeFilters = HashSet<String>()
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

            override fun clickMain(v: View, main: DataSource.Main, position: Int) {
                mLevel = position
                refreshItems()
            }

            override fun bindFile(holder: FileViewHolder, file: DataSource.File, position: Int) {
                holder.setImageByResource(R.id.image_item_classification_picker_file_icon, fileIcon(file.file.extension))
                        .setText(R.id.tv_item_classification_picker_file_name, file.file.name)
                        .setText(R.id.tv_item_classification_picker_file_time, Utils.formatTime(file.file.lastModified()))
                        .setText(R.id.tv_item_classification_picker_file_size, file.file.length().friendlyFileLength())
                val checkbox = holder.getView<CheckBox>(R.id.checkBox_item_classification_picker_file)
                if (mActivity?.chooseType == FilePicker.CHOOSE_TYPE_SINGLE) {
                    checkbox.visibility = View.GONE
                }else {
                    checkbox.visibility = View.VISIBLE
                    checkbox.isChecked = false
                    if (mActivity?.mSelected?.contains(file.path)?: false){
                        checkbox.isChecked = true
                    }
                    //checkbox click
                    checkbox.setOnClickListener {
                        var check = checkbox.isChecked
                        mActivity?.toggleChooseFile(file.path, check)
                    }
                }
            }

            override fun clickFile(v: View, file: DataSource.File, position: Int) {
                if (mActivity?.chooseType ==  FilePicker.CHOOSE_TYPE_SINGLE){
                    mActivity?.chooseFileSingle(file.path)
                }else{
                    val checkbox = v.findViewById(R.id.checkBox_item_classification_picker_file) as CheckBox
                    var check = checkbox.isChecked
                    checkbox.isChecked = !check
                    mActivity?.toggleChooseFile(file.path, !check)
                }
            }

            override fun bindPictureFolder(holder: FileViewHolder, folder: DataSource.PictureFolder, position: Int) {
                holder.setText(R.id.tv_item_classification_picture_folder_name, folder.name)
                        .setText(R.id.tv_item_classification_picture_folder_count, "" + folder.childrenCount + " " + getString(R.string.item_picture_folder_count_unit))
                val icon = holder.getView<ImageView>(R.id.image_item_classification_picture_folder_icon)
                icon.setImageResource(R.drawable.ic_file_image_48dp)
                ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(folder.firstImagePath, icon)
            }

            override fun clickPictureFolder(v: View, folder: DataSource.PictureFolder, position: Int) {
                mLevel = 6
                mPictureFolderId = folder.bucketId
                mPictureFolderName = folder.name
                refreshItems()
            }

            override fun bindPicture(holder: FileViewHolder, picture: DataSource.Picture, position: Int) {
                val image = holder.getView<ImageView>(R.id.image_item_classification_picture)
                image.setImageResource(R.drawable.ic_file_image_48dp)
                ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(picture.path, image)
                val checkbox = holder.getView<CheckBox>(R.id.checkBox_item_classification_picture)
                if (mActivity?.chooseType == FilePicker.CHOOSE_TYPE_SINGLE) {
                    checkbox.visibility = View.GONE
                }else {
                    checkbox.visibility = View.VISIBLE
                    checkbox.isChecked = false
                    if (mActivity?.mSelected?.contains(picture.path)?:false){
                        checkbox.isChecked = true
                    }
                    //checkbox click
                    checkbox.setOnClickListener {
                        var check = checkbox.isChecked
                        mActivity?.toggleChooseFile(picture.path, check)
                    }
                }
            }

            override fun clickPicture(v: View, picture: DataSource.Picture, position: Int) {
                if (mActivity?.chooseType ==  FilePicker.CHOOSE_TYPE_SINGLE){
                    mActivity?.chooseFileSingle(picture.path)
                }else{
                    val checkbox = v.findViewById(R.id.checkBox_item_classification_picture) as CheckBox
                    var check = checkbox.isChecked
                    checkbox.isChecked = !check
                    mActivity?.toggleChooseFile(picture.path, !check)
                }
            }
        }
    }
    private val mPresenter: FileClassificationPresenter by lazy { FileClassificationPresenter(this, activity) }
    private val mProgressDialog: ProgressDialog by lazy { ProgressDialog(activity) }

    private val mItemDecoration by lazy { TransparentItemDecoration(activity, LinearLayoutManager.VERTICAL) }
    /*状态*/
    private var mLevel = -1 //所处的位置
    private var mPictureFolderName = ""
    private var mPictureFolderId = ""

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            mActivity = (activity as FileActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_file_classification_picker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        constraint_file_picker_upper_level_button.setOnClickListener { upperLevel() }
        layout_file_picker_filter_bar.setOnClickListener {
            //show filter Window
            showDocumentTypeFilterDialog()
        }
    }


    override fun returnItems(items: ArrayList<DataSource>) {
        mProgressDialog.dismiss()
        mItems.clear()
        mItems.addAll(items)
        setupLayoutManager()
        adapter.notifyDataSetChanged()
        refreshBreadcrumbs()

        if (mLevel == 3) {
            if (mDocumentTypeFilters.isEmpty()) {
                image_file_picker_filter_bar.setImageResource(R.drawable.ic_filter_off)
                tv_file_picker_filter_bar.setTextColor(ContextCompat.getColor(activity, R.color.secondary_text))
            }else {
                image_file_picker_filter_bar.setImageResource(R.drawable.ic_filter_on)
                tv_file_picker_filter_bar.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            }
            layout_file_picker_filter_bar.visibility= View.VISIBLE

        }else {
            layout_file_picker_filter_bar.visibility= View.GONE
        }

        if (items.size > 0) {
            recycler_file_classification_picker_list.visibility = View.VISIBLE
            file_picker_empty.visibility = View.GONE
        }else {
            recycler_file_classification_picker_list.visibility = View.GONE
            file_picker_empty.visibility = View.VISIBLE
        }
    }

    fun onBackPressed(): Boolean {
        if (mLevel == -1) {
            return false
        } else {
            upperLevel()
            return true
        }
    }
    private fun upperLevel() {
        when (mLevel) {
            0, 1, 2, 3, 4, 5 -> {
                mLevel = -1
                refreshItems()
            }
            6 -> {
                mLevel = 0
                refreshItems()
            }
        }
        mPictureFolderId = ""
        mPictureFolderName = ""
    }

    /**
     *
     */
    private fun refreshItems() {
        mProgressDialog.show()
        if (mLevel!=3) {
            mDocumentTypeFilters.clear()
        }
        mPresenter.loadingItems(mLevel, mPictureFolderId, mDocumentTypeFilters)
    }


    private fun refreshBreadcrumbs() {
        when (mLevel) {
            -1 -> {
                breadcrumbs.visibility = View.GONE
            }
            0 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_picture))
                breadcrumbs.visibility = View.VISIBLE
            }
            1 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_audio))
                breadcrumbs.visibility = View.VISIBLE
            }
            2 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_video))
                breadcrumbs.visibility = View.VISIBLE
            }
            3 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_file))
                breadcrumbs.visibility = View.VISIBLE
            }
            4 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_archive))
                breadcrumbs.visibility = View.VISIBLE
            }
            5 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_application))
                breadcrumbs.visibility = View.VISIBLE
            }
            6 -> {
                tv_file_picker_folder_path.text = getString(R.string.classification_root)
                        .concat(getString(R.string.picker_arrow))
                        .concat(getString(R.string.item_classification_picture))
                        .concat(getString(R.string.picker_arrow))
                        .concat(mPictureFolderName)
                breadcrumbs.visibility = View.VISIBLE
            }
        }
    }

    private fun initAdapter() {
        setupLayoutManager()
        recycler_file_classification_picker_list.adapter = adapter
    }
    private fun setupLayoutManager() {
        when (mLevel) {
            -1, 6 -> {
                recycler_file_classification_picker_list.layoutManager = GridLayoutManager(activity, 3)
                recycler_file_classification_picker_list.removeItemDecoration(mItemDecoration)
                recycler_file_classification_picker_list.addItemDecoration(mItemDecoration)
            }
            0, 1, 2, 3, 4, 5 -> {
                recycler_file_classification_picker_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                recycler_file_classification_picker_list.removeItemDecoration(mItemDecoration)
            }
        }
    }


    private fun showDocumentTypeFilterDialog() {
        val alertTitle = getString(R.string.filter_label)
        val dialog =  activity.alert(title = alertTitle, message = "") {
            customView = LayoutInflater.from(activity).inflate(R.layout.popup_picture_folders,null, false)
            positiveButton(R.string.positive){
                refreshItems()
            }
            negativeButton(R.string.cancel) {
            }
        }.show()
        val listView = dialog?.findViewById(R.id.id_dir_list) as ListView
        listView.adapter = object : ArrayAdapter<DocumentTypeEnum>(activity, 0, DocumentTypeEnum.values()){
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
                var view: View? = convertView
                if(view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.item_document_type_filter_window, parent, false)
                }
                if(view!=null) {
                    val tv = view.findViewById(R.id.tv_document_type_name) as TextView
                    tv.text = getItem(position).label
                    val check = view.findViewById(R.id.checkBox_document_type_check) as CheckBox
                    check.isChecked = false
                    mDocumentTypeFilters.filter { it.equals(getItem(position).value) }.map {
                        check.isChecked = true
                    }
                    check.setOnClickListener {
                        val isCheck = check.isChecked
                        toggleDocumentType(isCheck, getItem(position).value)
                    }
                }
                return view
            }
        }
    }

    private fun toggleDocumentType(check: Boolean, value: String) {
        Log.i("PICKER", "toggleDocumentType $check, value:$value")
        if (check) {
            mDocumentTypeFilters.add(value)
        }else {
            mDocumentTypeFilters.remove(value)
        }
    }
}