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

    // FOR DATA
    private lateinit var viewModel: RealEstateViewModel
    private var realEstateId: Long = 1

    private val photoAdapter: PhotoAdapter = PhotoAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(com.openclassrooms.realestatemanager.R.layout.fragment_detail, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initRecyclerView()
        this.configureViewModel()
        this.getCurrentRealEstate(realEstateId)
        this.getPhotos()
    }

    fun updateDetails(tag: Long) {
        this.realEstateId = tag
        getCurrentRealEstate(realEstateId)
        getPhotos()
    }

    // RecyclerView node initialized here
    private fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = photoAdapter
        }
    }

    // Configuring ViewModel
    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(activity)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
        this.viewModel.init(realEstateId)
    }

    // 3 - Get Current User
    private fun getCurrentRealEstate(id: Long) {
        this.viewModel.getRealEstate(id).observe(this, Observer<RealEstate> { this.updateDetails(it) })
    }

    private fun updateDetails(realEstate: RealEstate) {
        detail_description.text = realEstate.description
        detail_surface.text = realEstate.surface
        detail_rooms.text = realEstate.rooms
        detail_bathrooms.text = realEstate.bathrooms
        detail_bedrooms.text = realEstate.bedrooms

        detail_address.text = realEstate.address
    }

    //  Get all photos
    private fun getPhotos() {
        this.viewModel.getPhotos(realEstateId).observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    private fun updatePhotoList(list: List<Photo>) {
        this.photoAdapter.updateData(list)
    }
}