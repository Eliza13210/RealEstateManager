package com.openclassrooms.realestatemanager.controllers

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.EditFragment
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.toolbar.*

class EditActivity : AppCompatActivity(), PhotoAdapter.PhotoViewHolder.OnItemClickedListener {

    private var isTablet = false
    private var editFragment: EditFragment? = null

    override fun onNewIntent(intent: Intent?) {
        if (intent != null)
            setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        editFragment!!.initRealEstate(intent.getLongExtra(DetailActivity.EXTRA_TAG, 0))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        checkIfTablet()
        showFragment()
    }

    private fun checkIfTablet() {
        // WILL BE FALSE IF TABLET
        if (resources.getBoolean(R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setActionbarPhone()
        } else {
            isTablet = true
            initToolbar()
        }
    }

    private fun initToolbar() {
        //Initiate toolbar to navigate back to main activity
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun setActionbarPhone() {
        this.setSupportActionBar(bottom_app_bar)
        bottom_app_bar?.setNavigationOnClickListener { startActivity(Intent(this@EditActivity, MainActivity::class.java)) }

        fab?.setImageResource(R.drawable.ic_action_done)
        fab?.setOnClickListener { editFragment!!.getInfoFromUI() }
    }

    private fun showFragment() {
        editFragment = supportFragmentManager?.findFragmentById(R.id.frame_layout_edit) as? EditFragment
        if (editFragment == null) {
            editFragment = EditFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.frame_layout_edit, editFragment!!)
                    .commit()
        }
    }


    /**
     * Handle click in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // REMOVE CREATE ITEM FROM TOOLBAR IF PHONE
        if (!isTablet) {
            menuInflater.inflate(R.menu.bottom_app_bar_menu_nav_back, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_home -> {
                startActivity(Intent(this@EditActivity, MainActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(id: Long?, position: Int?) {
    }
}
