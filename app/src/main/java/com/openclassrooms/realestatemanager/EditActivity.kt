package com.openclassrooms.realestatemanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.Result
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.CircularRevealAnimation
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.bottom_app_bar.*
import kotlinx.android.synthetic.main.information_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.ArrayList


class EditActivity : AppCompatActivity() {


    private var viewModel: RealEstateViewModel? = null
    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private lateinit var realEstateId: String

    // For creating real estate object
    private val photos_list = ArrayList<Photo>()
    private var type_info = ""
    private var price_info = ""
    private var description_info = ""
    private var surface_info = ""
    private var rooms_info = ""
    private var bathrooms_info = ""
    private var address_info = ""
    private val sold = false
    private var startDate_info = ""
    private val endDate_info = ""
    private var agent_info = ""
    private var pointsOfInterest: List<Result> = ArrayList()
    private var bedrooms_info: String? = null
    private var latitude: String = ""
    private var longitude: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initAnimation(savedInstanceState)
        initViewModel()
        initRecyclerView()
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            realEstateId = bundle.getString("RealEstateId")
            initRealEstate(realEstateId)
            getPhotos(realEstateId)
        }
    }

    // RecyclerView node initialized here
    private fun initRecyclerView() {
        recyclerview_photos_info.apply {
            layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
            // set the custom adapter to the RecyclerView
            adapter = photoAdapter
        }
    }

    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    private fun initRealEstate(id: String?) {
        this.viewModel?.getRealEstate(id)?.observe(this, Observer<RealEstate> {
            this.updateDetails(it)
        })
    }

    private fun updateDetails(realEstate: RealEstate) {
        agent.setText(realEstate.agent)
        type.setText(realEstate.type)
        surface.setText(realEstate.surface)
        price.setText(realEstate.price)

        var roomsNb: Int? = realEstate.rooms?.toInt()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bathrooms?.toInt()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bedrooms?.toInt()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        description.setText(realEstate.description)

    }

    //  Get all photos
    private fun getPhotos(realEstateId: String) {
        this.viewModel?.getPhotos(realEstateId)?.observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    private fun updatePhotoList(list: List<Photo>) {
        Log.e("edit act", "list of photos=" + list.size)
        this.photoAdapter.updateData(list)
    }

    private fun initAnimation(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

            root_layout.visibility = View.INVISIBLE

            val viewTreeObserver = root_layout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity()

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            root_layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        } else {
                            root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })
            }
        }
    }

    private fun circularRevealActivity() {
        CircularRevealAnimation.startAnimation(root_layout)
        initToolbar()
    }

    /**
     * Initiate buttons and toolbar and spinners
     */
    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v -> startActivity(Intent(this, MainActivity::class.java)) }
    }

    private fun initButtons() {
        btn_update.setOnClickListener { v -> updateRealEstate() }
        btn_delete.setOnClickListener { v -> deleteRealEstate() }
    }


    private fun updateRealEstate() {

        val realEstate = RealEstate(realEstateId, type_info, price_info, latitude, longitude, description_info, surface_info, bedrooms_info,
                rooms_info, bathrooms_info, address_info, false, startDate_info, null, agent_info
        )

        viewModel?.updateRealEstate(realEstate)

        for (photo in photos_list) {
            photo.realEstateId = realEstateId
            viewModel?.createPhoto(photo)
        }

        Toast.makeText(this, "Real estate updated succesfully", Toast.LENGTH_SHORT).show()
    }

    private fun deleteRealEstate() {
        viewModel?.deleteRealEstate(realEstateId)

    }

}