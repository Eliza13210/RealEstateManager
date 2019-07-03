package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {

    // 1 - FOR DATA
    private lateinit var viewModel: RealEstateViewModel
    val REALESTATE_ID: Long = 1

    private lateinit var adapter: PhotoAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(com.openclassrooms.realestatemanager.R.layout.fragment_detail, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initRecyclerView()
        this.configureViewModel()
        this.getItems()

    }

    // RecyclerView node initialized here
    fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = PhotoAdapter()
        }
    }

    // Configuring ViewModel
    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(activity)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
        this.viewModel.init(REALESTATE_ID)
    }

    // 3 - Get all items for a user
    private fun getItems() {
        this.viewModel.getPhotos(REALESTATE_ID).observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    fun updatePhotoList(list: List<Photo>) {
        this.adapter.updateData(list)
    }
}