package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.JsonConverter
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.MapPopUp
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {


    private lateinit var viewModel: RealEstateViewModel
    private var realEstateId: Long = 0
    private var photos: List<Photo>? = null
    private val photoAdapter: PhotoAdapter = PhotoAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_detail, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initRecyclerView()
        this.configureViewModel()
    }

    //Called when click on real estate in list to show the chosen item
    fun updateDetails(tag: Long) {
        this.realEstateId = tag
        getCurrentRealEstate(tag)
        getPhotos()
    }

    private fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            // set the custom adapter to the RecyclerView
            adapter = photoAdapter
        }
    }

    // Configuring ViewModel a
    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(activity)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    // Get Current real estate
    private fun getCurrentRealEstate(id: Long) {
        this.viewModel.getRealEstate(id).observe(this, Observer<RealEstate> { this.updateDetails(it) }
        )

    }

    private fun updateDetails(realEstate: RealEstate) {
        detail_description.text = realEstate.description
        detail_surface.text = realEstate.surface
        detail_rooms.text = realEstate.rooms
        detail_bathrooms.text = realEstate.bathrooms
        detail_bedrooms.text = realEstate.bedrooms
        detail_address.text = realEstate.address
        detail_poi_tv.text = getPoi(realEstate.pointsOfInterest)

        if (realEstate.sold.equals("true"))
            isSold_tv.text = getString(R.string.sold) + realEstate.endDate

        //Show map with location
        val sb: StringBuilder = java.lang.StringBuilder()
        sb.append("https://maps.googleapis.com/maps/api/staticmap?center=" +
                realEstate.latitude + "," + realEstate.longitude + "&zoom=15&size=680x680&maptype=roadmap&markers=color:blue%7Clabel:S%7C" +
                realEstate.latitude + "," + realEstate.longitude +
                "&key=" + BuildConfig.API_KEY)

        val mapUrl: String = sb.toString()
        Picasso.get().load(mapUrl).into(map_iv)

        //SHOW MAP IN DIALOG WHEN CLICKED
        map_iv.setOnClickListener {
            val mapPopUp = MapPopUp(mapUrl, context)
            mapPopUp.showPopUp()
        }
    }

    private fun getPoi(string: String?): String {
        var poi = JsonConverter.convertToList(string)

        val builder = StringBuilder()

        for (result in poi) {
            builder.append(result.name + ", " + result.types[0].replace('_', ' '))
            builder.append(System.getProperty("line.separator"))
        }
        return builder.toString()

    }

    //  Get all photos
    private fun getPhotos() {
        this.viewModel.getPhotos(realEstateId).observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    //Show photos in recyclerview
    private fun updatePhotoList(list: List<Photo>) {
        this.photos = list
        this.photoAdapter.updateData(list)
    }
}