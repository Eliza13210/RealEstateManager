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
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.popUps.MapPopUp
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.openclassrooms.realestatemanager.view.popUps.PhotoPopUp
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.*
import java.util.ArrayList


class DetailFragment : Fragment(), PhotoAdapter.PhotoViewHolder.OnItemClickedListener {

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

    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(activity)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    private fun getCurrentRealEstate(id: Long) {
        this.viewModel.getRealEstate(id).observe(this, Observer<RealEstate> { this.updateDetails(it) }
        )
    }

    private fun updateDetails(realEstate: RealEstate) {
        detail_description.text = realEstate.description
        detail_surface.text = realEstate.surface?.let { Utils.convertIntToStringAndDivide(it.toFloat()) }
        detail_rooms.text =  realEstate.rooms?.toString()
        detail_bathrooms.text =  realEstate.bathrooms?.toString()
        detail_bedrooms.text =  realEstate.bedrooms?.toString()
        detail_address.text = realEstate.address
        detail_poi_tv.text = realEstate.pointsOfInterest

        if (realEstate.sold == "true")
            isSold_tv.text = getString(R.string.sold) + " " + realEstate.endDate

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

    private fun getPhotos() {
        this.viewModel.getPhotos(realEstateId).observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    //Show photos in recycler view
    private fun updatePhotoList(list: List<Photo>) {
        this.photos = list
        this.photoAdapter.updateData(list,null)
    }

    // --------------
    // CallBack
    // Handle click in photo recycler view to show photo or video in popup
    // --------------
    override fun onItemClick(id: Long?, position: Int?) {
        val photoPopup = PhotoPopUp(context!!)
        var photo: Photo? = null
        if (id != null) {
            for (p in photos!!) {
                if (p.id == id)
                    photo = p
            }
        } else {
            photo = photos?.get(position!!)
        }
        photoPopup.popUp(photo!!.url, photos as ArrayList<Photo>, photo.type!!, photoAdapter, position)
    }
}