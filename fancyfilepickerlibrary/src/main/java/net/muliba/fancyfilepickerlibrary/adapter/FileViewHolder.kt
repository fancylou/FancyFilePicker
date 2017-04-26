package net.muliba.fancyfilepickerlibrary.adapter

import android.graphics.Bitmap
import android.support.annotation.IdRes
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by fancy on 2017/4/26.
 */

class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val views: SparseArray<View> = SparseArray()
    val convertView: View by lazy { itemView }

    fun <T : View> getView(viewId: Int): T {
        var view : View? = views.get(viewId)
        if (view == null) {
            view = convertView.findViewById(viewId)
            views.put(viewId, view)
        }

        return view as T
    }

    fun setText(@IdRes resId: Int, text: String): FileViewHolder{
        val view = getView<TextView>(resId)
        view.text = text
        return this
    }
    fun setImageByResource(@IdRes resId: Int, imageRes: Int) : FileViewHolder {
        val image = getView<ImageView>(resId)
        image.setImageResource(imageRes)
        return this
    }
    fun setImageBitmap(@IdRes resId: Int, bitmap: Bitmap) : FileViewHolder {
        val image = getView<ImageView>(resId)
        image.setImageBitmap(bitmap)
        return this
    }
}