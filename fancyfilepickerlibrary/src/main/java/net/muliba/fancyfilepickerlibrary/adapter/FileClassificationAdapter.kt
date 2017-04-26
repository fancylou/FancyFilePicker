package net.muliba.fancyfilepickerlibrary.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.ext.inflate
import net.muliba.fancyfilepickerlibrary.model.DataSource

/**
 * Created by fancy on 2017/4/26.
 */

abstract class FileClassificationAdapter(val items: ArrayList<DataSource>) : RecyclerView.Adapter<FileViewHolder>() {

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        when (items[position]) {
            is DataSource.Main -> {
                val main = items[position] as DataSource.Main
                bindMain(holder, main, position)
                holder.convertView.setOnClickListener { clickMain(main, position) }
            }
            is DataSource.File -> {
                val file = items[position] as DataSource.File
                bindFile(holder, file, position)
                holder.convertView.setOnClickListener { clickFile(file, position) }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return when (viewType) {
            0 -> FileViewHolder(parent.inflate(R.layout.item_file_classification_picker_main))
            else -> FileViewHolder(parent.inflate(R.layout.item_file_classification_picker_file))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is DataSource.Main -> 0
            is DataSource.File -> 1
            else -> -1
        }
    }

    abstract fun bindMain(holder: FileViewHolder, main: DataSource.Main, position: Int)
    abstract fun clickMain(main: DataSource.Main, position: Int)
    abstract fun bindFile(holder: FileViewHolder, file:DataSource.File, position: Int)
    abstract fun clickFile(file: DataSource.File, position: Int)

}