package net.muliba.fancyfilepickerlibrary.adapter

import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.muliba.fancyfilepickerlibrary.R
import net.muliba.fancyfilepickerlibrary.ext.inflate

/**
 * Created by fancylou on 10/18/17.
 *
 * code from android.support.v4.widget.CursorAdapter
 */


abstract class PictureCursorRecyclerViewAdapter: RecyclerView.Adapter<FileViewHolder>() {

    var mCursor: Cursor? = null
    var mRowIDColumn:Int = -1
    var mDataValid: Boolean = false

    val mChangeObserver = ChangeObserver()
    val mDataSetObserver = MyDataSetObserver()

    init {
        setHasStableIds(true)// 这个地方要注意一下，需要将表关联ID设置为true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(parent.inflate(R.layout.item_picture_picker_list))
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        if (!mDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (mCursor == null || !mCursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position $position")
        }
        bindView(holder, mCursor)
    }


    override fun getItemCount(): Int {
        return if (mDataValid && mCursor!=null){ mCursor!!.count}else{0}
    }

    override fun getItemId(position: Int): Long {
        return if (mDataValid && mCursor != null) {
            if (mCursor!!.moveToPosition(position)){
                mCursor!!.getLong(mRowIDColumn)
            }else {
                0
            }
        }else {
            0
        }
    }

    fun changeCursor(cursor: Cursor?) {
        var old:Cursor? = swapCursor(cursor)
        if (old!=null) {
            old.close()
        }
    }


    private fun swapCursor(newCursor: Cursor?): Cursor? {
        if (newCursor == mCursor) {
            return null
        }
        var old = mCursor
        if (old!=null) {
            old.unregisterContentObserver(mChangeObserver)
            old.unregisterDataSetObserver(mDataSetObserver)
        }
        mCursor = newCursor
        if (newCursor!=null) {
            newCursor.registerContentObserver(mChangeObserver)
            newCursor.registerDataSetObserver(mDataSetObserver)
            mRowIDColumn = newCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            mDataValid = true
            notifyDataSetChanged()
        }else {
            mRowIDColumn = -1
            mDataValid = false
            notifyDataSetChanged()
        }

        return old

    }

    abstract fun bindView(holder: FileViewHolder, cursor: Cursor?)


    inner class ChangeObserver: ContentObserver(android.os.Handler()){
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
        }

        override fun deliverSelfNotifications(): Boolean {
            return true
        }
    }

    inner class MyDataSetObserver: DataSetObserver() {
        override fun onChanged() {
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            mDataValid = false
            notifyDataSetChanged()
        }
    }
}