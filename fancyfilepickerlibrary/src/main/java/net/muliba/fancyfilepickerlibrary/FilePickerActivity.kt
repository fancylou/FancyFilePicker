package net.muliba.fancyfilepickerlibrary

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_file_picker.*
import net.muliba.fancyfilepickerlibrary.adapter.FileAdapter
import net.muliba.fancyfilepickerlibrary.adapter.FileViewHolder
import net.muliba.fancyfilepickerlibrary.ext.friendlyFileLength
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.io.File

class FilePickerActivity : AppCompatActivity() {

    private val rootPath: String = Environment.getExternalStorageDirectory().absolutePath
    private var currentPath = rootPath
    private val mSelected = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_activity_file_picker)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        recycler_file_picker_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_file_picker_list.adapter = adapter
        constraint_file_picker_upper_level_button.setOnClickListener { upperLevel() }
        refreshList(currentPath)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_file_picker, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (mSelected.size == 0) {
            menu?.findItem(R.id.menu_choose)?.title = getString(R.string.picker)
        } else {
            menu?.findItem(R.id.menu_choose)?.title = getString(R.string.picker) + "(" + mSelected.size.toString() + ")"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_choose -> chooseFile()
            else -> super.onOptionsItemSelected(item)
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
                    val fileDesc = holder.getView<TextView>(R.id.tv_file_picker_file_description)
                    val checkbox = holder.getView<CheckBox>(R.id.checkBox_file_picker_file)
                    checkbox.isChecked = false
                    if (mSelected.contains(file.absolutePath)) {
                        checkbox.isChecked = true
                    }
                    doAsync {
                        val len = file.length().friendlyFileLength()
                        uiThread { fileDesc.text = len }
                    }
                    //checkbox click
                    checkbox.setOnClickListener {
                        Log.i("onBind", "checkBox click.....................")
                        var check = checkbox.isChecked
                        toggleItem(file.absolutePath, check)
                    }
                    holder.convertView.setOnClickListener { v ->
                        Log.i("onBind", "item click.....................")
                        val checkbox = v.findViewById(R.id.checkBox_file_picker_file) as CheckBox
                        var check = checkbox.isChecked
                        checkbox.isChecked = !check
                        toggleItem(file.absolutePath, !check)
                    }
                }
            }
        }
    }


    private fun toggleItem(filePath: String, isChecked: Boolean) {
        if (isChecked) {
            mSelected.add(filePath)
        } else {
            mSelected.remove(filePath)
        }
        refreshMenu()
    }


    /**
     * 向上一级
     */
    private fun upperLevel() {
        if (rootPath.equals(currentPath)) {
            toast(getString(R.string.message_already_on_top))
        } else {
            refreshList(File(currentPath).parentFile.absolutePath)
        }
    }


    /**
     * 刷新list
     */
    private fun refreshList(path: String) {
        doAsync {
            val list: List<File> = File(path).listFiles().map { it }.sortedWith(Comparator<File> { o1, o2 ->
                if (o1.isDirectory && o2.isDirectory) {
                    o1.name.compareTo(o2.name)
                } else if (o1.isFile && o2.isFile) {
                    o1.name.compareTo(o2.name)
                } else {
                    when (o1.isDirectory && o2.isFile) {
                        true -> -1
                        false -> 1
                    }
                }
            })
            uiThread {
                currentPath = path
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

    private fun refreshMenu() {
        // getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        invalidateOptionsMenu()
    }

    private fun chooseFile(): Boolean {
        if (mSelected.size > 0) {
            val array = ArrayList<String>()
            mSelected.map { array.add(it) }
            intent.putStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY, array)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            toast(getString(R.string.message_please_select_more_than_one))
        }
        return true
    }

}
