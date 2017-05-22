package net.muliba.fancyfilepickerlibrary.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.ext.inflate

/**
 * Created by fancy on 2017/5/22.
 */

abstract class PictureAdapter(val imgs: List<String>) : RecyclerView.Adapter<FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(parent.inflate(R.layout.item_picture_picker_list))
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        bindView(holder, imgs[position])


    }

    override fun getItemCount(): Int {
       return imgs.size
    }

    abstract fun bindView(holder: FileViewHolder, name: String)

}