package net.muliba.fancyfilepickerlibrary.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import net.muliba.fancyfilepickerlibrary.R

class ImageClipActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_clip)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_clip, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_clip -> saveClipImage()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveClipImage(): Boolean {

        return true
    }
}
