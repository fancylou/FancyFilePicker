package net.muliba.fancyfilepickerlibrary.ui

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_file.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.adapter.FilePickerViewPagerAdapter
import net.muliba.fancyfilepickerlibrary.ui.fragment.FileClassificationPickerFragment
import net.muliba.fancyfilepickerlibrary.ui.fragment.FileLocalStoragePickerFragment
import net.muliba.fancyfilepickerlibrary.util.Utils
import org.jetbrains.anko.toast

class FileActivity : AppCompatActivity() {

    private val tabs: Array<String> by lazy { arrayOf(getString(R.string.tab_classification),
            getString(R.string.tab_local_storage)) }
    private val fragmentList: ArrayList<Fragment> = arrayListOf(FileClassificationPickerFragment(), FileLocalStoragePickerFragment())
    private val mAdapter: FilePickerViewPagerAdapter by lazy { FilePickerViewPagerAdapter(supportFragmentManager, tabs, fragmentList) }

    /**
     * fragment can read arguments below
     */
    var chooseType = FilePicker.CHOOSE_TYPE_MULTIPLE
    val mSelected = HashSet<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        chooseType = intent.getIntExtra(Utils.CHOOSE_TYPE_KEY, FilePicker.CHOOSE_TYPE_MULTIPLE)
        val mBackResults = intent.getStringArrayListExtra(Utils.MULIT_CHOOSE_BACK_RESULTS_KEY) ?: ArrayList<String>()
        if (!mBackResults.isEmpty()) {
            mBackResults.map {
                mSelected.add(it)
            }
        }

        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener { finish() }
        viewPager.adapter = mAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.setupWithViewPager(viewPager)

        viewPager.currentItem = 0
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (chooseType== FilePicker.CHOOSE_TYPE_MULTIPLE){
            menuInflater.inflate(R.menu.menu_file_picker, menu)
        }
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

    override fun onBackPressed() {
        when (viewPager.currentItem) {
            0 -> {
                val cFragment = fragmentList[0] as FileClassificationPickerFragment
                if (!cFragment.onBackPressed()){
                    super.onBackPressed()
                }
            }
            1 ->{
                val lFragment = fragmentList[1] as FileLocalStoragePickerFragment
                if (!lFragment.onBackPressed()) {
                    super.onBackPressed()
                }
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    /**
     * 多选的时候选中某一个文件
     */
    fun toggleChooseFile(filePath: String, isChecked: Boolean) {
        if (isChecked) {
            mSelected.add(filePath)
        } else {
            mSelected.remove(filePath)
        }
        refreshMenu()
    }

    /**
     * 单选的时候点击返回选择的图片结果
     */
    fun chooseFileSingle(filePath: String) {
        intent.putExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY, filePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * 多选的时候返回选择的图片结果
     */
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

    /**
     * 更新菜单按钮文字
     */
    private fun refreshMenu() {
        // getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        invalidateOptionsMenu()
    }
}
