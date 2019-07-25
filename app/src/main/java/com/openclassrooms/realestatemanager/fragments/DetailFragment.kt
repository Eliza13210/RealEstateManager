package com.openclassrooms.realestatemanager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.MapPopUp
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {

    // FOR DATA
    private lateinit var viewModel: RealEstateViewModel
    private var realEstateId: String = ""

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    var isImageFitToScreen: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(com.openclassrooms.realestatemanager.R.layout.fragment_detail, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initRecyclerView()
        this.configureViewModel()
    }


    fun updateDetails(tag: String) {
        this.realEstateId = tag
        Log.e("fragment", tag)
        getCurrentRealEstate(tag)
        getPhotos()
    }

    // RecyclerView node initialized here
    private fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            // set the custom adapter to the RecyclerView
            adapter = photoAdapter
        }
    }

    // Configuring ViewModel
    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(activity)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
        // this.viewModel.init(realEstateId)
    }

    // 3 - Get Current User
    private fun getCurrentRealEstate(id: String) {
        this.viewModel.getRealEstate(id).observe(this, Observer<RealEstate> { this.updateDetails(it) }
        )

    }

    private fun updateDetails(realEstate: RealEstate) {
        Log.e("det fr", realEstate.agent)
        detail_description.text = realEstate.description
        detail_surface.text = realEstate.surface
        detail_rooms.text = realEstate.rooms
        detail_bathrooms.text = realEstate.bathrooms
        detail_bedrooms.text = realEstate.bedrooms
        detail_address.text = realEstate.address

        var sb: StringBuilder = java.lang.StringBuilder()
        sb.append("https://maps.googleapis.com/maps/api/staticmap?center=" +
                realEstate.latitude + "," + realEstate.longitude + "&zoom=15&size=680x680&maptype=roadmap&markers=color:blue%7Clabel:S%7C" +
                realEstate.latitude + "," + realEstate.longitude +
                "&key=" + BuildConfig.API_KEY)

        val mapUrl: String = sb.toString()
        Log.e("detail", "Map " + mapUrl)
        Picasso.get().load(mapUrl).into(map_iv)

        //SHOW MAP IN DIALOG WHEN CLICKED
        map_iv.setOnClickListener {
            val mapPopUp = MapPopUp(mapUrl, context)
            mapPopUp.showPopUp()
        }
    }

    //  Get all photos
    private fun getPhotos() {
        this.viewModel.getPhotos(realEstateId).observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })

    }

    private fun updatePhotoList(list: List<Photo>) {
        Log.e("det fr", "list of photos=" + list.size)
        this.photoAdapter.updateData(list)
    }
}