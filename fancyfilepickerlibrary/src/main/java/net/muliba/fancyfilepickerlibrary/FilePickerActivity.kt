package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_file_picker.*
import net.muliba.fancyfilepickerlibrary.adapter.FileAdapter
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.ext.friendlyFileLength
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

class FilePickerActivity : AppCompatActivity() {

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
                    holder.convertView.setOnClickListener { listFiles(file.absolutePath) }
                } else {
                    holder.setText(R.id.tv_file_picker_file_name, file.name)
                    val fileDesc = holder.getView<TextView>(R.id.tv_file_picker_file_description)
                    doAsync {
                        val len = file.length().friendlyFileLength()
                        uiThread { fileDesc.text = len }
                    }
                    holder.convertView.setOnClickListener { v ->
                        val checkbox = v.findViewById(R.id.checkBox_file_picker_file) as CheckBox
                        var check = checkbox.isChecked
                        checkbox.isChecked = !check
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_activity_file_picker)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        recycler_file_picker_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_file_picker_list.adapter = adapter
        tv_file_picker_choose.setOnClickListener { chooseFile() }


        val externalPath = Environment.getExternalStorageDirectory().absolutePath
        listFiles(externalPath)

    }


    private fun listFiles(path: String) {
        doAsync {
            val list: List<File> = File(path).listFiles().map { it }.sortedWith (Comparator<File> { o1, o2 ->
                if (o1.isDirectory && o2.isDirectory) {
                    o1.name.compareTo(o2.name)
                }else if (o1.isFile && o2.isFile) {
                    o1.name.compareTo(o2.name)
                }else {
                    when(o1.isDirectory && o2.isFile){
                        true-> -1
                        false -> 1
                    }
                }
            })
            uiThread {
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

    private fun chooseFile() {
        val array = ArrayList<String>()
        array.add("filePath1")
        array.add("filePath2")
        array.add("filePath3")
        intent.putStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY, array)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
