package com.openclassrooms.realestatemanager.controllers

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment
import com.openclassrooms.realestatemanager.controllers.fragments.SearchResultFragment
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder
import kotlinx.android.synthetic.main.toolbar.*

class SearchResultActivity : AppCompatActivity(), RealEstateViewHolder.OnItemClickedListener, PhotoAdapter.PhotoViewHolder.OnItemClickedListener {

    companion object {
        const val EXTRA_TAG_RESULT = "com.openclassrooms.myfragmentapp.Controllers.Activities.SearchResultActivity.EXTRA_TAG_RESULT"
    }

    private var listFragment: SearchResultFragment? = null
    private var detailFragment: DetailFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        initToolbar()
        showListFragment()
        configureAndShowDetailFragment()
    }

    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { startActivity(Intent(this, SearchActivity::class.java)) }
    }

    private fun showListFragment() {
        listFragment = supportFragmentManager?.findFragmentById(R.id.frame_layout_result) as? SearchResultFragment
        if (listFragment == null) {
            listFragment = SearchResultFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.frame_layout_result, listFragment!!)
                    .commit()
        }
    }

    private fun configureAndShowDetailFragment() {
        detailFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_detail) as DetailFragment?

        //only add DetailFragment in Tablet mode (If found frame_layout_detail)
        if (detailFragment == null && findViewById<View>(R.id.frame_layout_detail) != null) {
            detailFragment = DetailFragment()
            supportFragmentManager.beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment!!)
                    .commit()
        }
    }

    /**
     * Update with search result when on resume to be sure that the fragment has been created
     */
    override fun onResume() {
        super.onResume()
        //GET THE LIST WITH RESULT FROM INTENT
        val resultFromActivity = intent.getStringExtra(EXTRA_TAG_RESULT)
        //RECONVERT TO LIST
        val gson = Gson()
        val itemType = object : TypeToken<List<RealEstate>>() {}.type
        val listOfResult =
                gson.fromJson<List<RealEstate>>(resultFromActivity, itemType)
        //UPDATE FRAGMENT
        listFragment?.updateItemsList(listOfResult)

        if (listOfResult.isNotEmpty()) detailFragment?.updateDetails(listOfResult[0].id!!)
    }


    /**
     * Handle click in list view
     */
    override fun onItemClick(id: Long) {
        // Check if phone, then open DetailActivity
        if (detailFragment == null) {
            val i = Intent(this, DetailActivity::class.java)
            i.putExtra(DetailActivity.EXTRA_TAG, id)
            startActivity(i)
        } else {
            //If tablet
            detailFragment!!.updateDetails(id)
        }
    }

    /**
     * Handle click on photos in detail fragment*
     */
    override fun onItemClick(id: Long?, position: Int?) {
        detailFragment!!.onItemClick(id, position)
    }
}
