package net.muliba.fancyfilepickerlibrary.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.ext.inflate
import java.io.File

/**
 * Created by fancy on 2017/4/12.
 */

abstract class FileAdapter : RecyclerView.Adapter<FileViewHolder>() {


    val items: ArrayList<File> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return when(viewType) {
            0 -> FileViewHolder(parent.inflate(R.layout.item_file_picker_folder))
            else -> FileViewHolder(parent.inflate(R.layout.item_file_picker_file))
        }
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = items[position]
        onBind(file, holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].isDirectory) {
            true  -> 0
            false -> 1
        }
    }

    fun refreshItems(list: List<File>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    abstract fun onBind(file: File, holder: FileViewHolder)
}
