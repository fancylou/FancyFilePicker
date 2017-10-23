package net.muliba.fancyfilepickerlibrary.ui.fragment

import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.breadcrumbs.*
import kotlinx.android.synthetic.main.fragment_file_picker.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.adapter.FileAdapter
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.ext.friendlyFileLength
import net.muliba.fancyfilepickerlibrary.ui.FileActivity
import net.muliba.fancyfilepickerlibrary.util.fileIcon
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.regex.Pattern

/**
 * Created by fancylou on 10/23/17.
 */

class FileLocalStoragePickerFragment: Fragment() {

    var mActivity: FileActivity? = null
    private val rootPath: String = Environment.getExternalStorageDirectory().absolutePath
    private var currentPath = rootPath
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            mActivity = (activity as FileActivity)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_file_picker, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_file_picker_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_file_picker_list.adapter = adapter
        constraint_file_picker_upper_level_button.setOnClickListener { upperLevel() }
        refreshList(currentPath)

    }


    fun onBackPressed():Boolean {
        if (rootPath.equals(currentPath)) {
            return false
        }else {
            upperLevel()
            return true
        }
    }



    /**
     * 向上一级
     */
    private fun upperLevel() {
        if (rootPath.equals(currentPath)) {
            activity.toast(getString(R.string.message_already_on_top))
        } else {
            refreshList(File(currentPath).parentFile.absolutePath)
        }
    }
    /**
     * 定义adapter
     */
    val adapter: FileAdapter by lazy {
        object : FileAdapter() {
            override fun onBind(file: File, holder: FileViewHolder) {
                if (file.isDirectory) {
                    holder.setText(R.id.tv_file_picker_folder_name, file.name)
                    val folderDesc = holder.getView<TextView>(R.id.tv_file_picker_folder_description)
                    doAsync {
                        val size = file.list().size.toString()
                        uiThread { folderDesc.text = size + getString(R.string.item_folder_description_label) }
                    }
                    holder.convertView.setOnClickListener { refreshList(file.absolutePath) }
                } else {
                    holder.setText(R.id.tv_file_picker_file_name, file.name)
                            .setImageByResource(R.id.image_file_picker_file, fileIcon(file.extension))
                    val fileDesc = holder.getView<TextView>(R.id.tv_file_picker_file_description)
                    doAsync {
                        val len = file.length().friendlyFileLength()
                        uiThread { fileDesc.text = len }
                    }
                    val checkbox = holder.getView<CheckBox>(R.id.checkBox_file_picker_file)
                    if (mActivity?.chooseType == FilePicker.CHOOSE_TYPE_SINGLE){
                        checkbox.visibility = View.GONE
                    }else{
                        checkbox.visibility = View.VISIBLE
                        checkbox.isChecked = false
                        if (mActivity?.mSelected?.contains(file.absolutePath)?:false) {
                            checkbox.isChecked = true
                        }
                        //checkbox click
                        checkbox.setOnClickListener {
                            val check = checkbox.isChecked
                            mActivity?.toggleChooseFile(file.absolutePath, check)
                        }
                    }
                    holder.convertView.setOnClickListener { v ->
                        if (mActivity?.chooseType ==  FilePicker.CHOOSE_TYPE_SINGLE){
                            mActivity?.chooseFileSingle(file.absolutePath)
                        }else{
                            val filePickerCheckbox = v.findViewById(R.id.checkBox_file_picker_file) as CheckBox
                            val check = filePickerCheckbox.isChecked
                            filePickerCheckbox.isChecked = !check
                            mActivity?.toggleChooseFile(file.absolutePath, !check)
                        }
                    }
                }
            }
        }
    }


    /**
     * 刷新list
     */
    private fun refreshList(path: String) {
        doAsync {
            val list: List<File> = File(path).listFiles{
                _, fileName ->
                val pattern = Pattern.compile("\\..*")
                !pattern.matcher(fileName).matches()
            }.map { it }.sortedWith(Comparator<File> { o1, o2 ->
                if (o1.isDirectory && o2.isDirectory) {
                    o1.name.compareTo(o2.name, true)
                } else if (o1.isFile && o2.isFile) {
                    o1.name.compareTo(o2.name, true)
                } else {
                    when (o1.isDirectory && o2.isFile) {
                        true -> -1
                        false -> 1
                    }
                }
            })
            uiThread {
                currentPath = path
                if (currentPath.equals(rootPath)){
                    breadcrumbs.visibility = View.GONE
                }else {
                    breadcrumbs.visibility = View.VISIBLE
                }
                tv_file_picker_folder_path.text = currentPath
                if (list.size > 0) {
                    recycler_file_picker_list.visibility = View.VISIBLE
                    file_picker_empty.visibility = View.GONE
                } else {
                    recycler_file_picker_list.visibility = View.GONE
                    file_picker_empty.visibility = View.VISIBLE
                }
                adapter.refreshItems(list)
            }
        }
    }
}