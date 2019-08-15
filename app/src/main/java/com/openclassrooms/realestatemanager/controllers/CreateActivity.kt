package com.openclassrooms.realestatemanager.controllers

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.*
import com.openclassrooms.realestatemanager.models.NearbySearchObject
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.network.NearbySearchStream
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_create_real_estate.*
import kotlinx.android.synthetic.main.information_layout.*
import java.lang.StringBuilder
import java.util.*

class CreateActivity : BaseActivityUIInformation() {

    private var latLng: LatLng? = null
    private lateinit var disposable: Disposable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAddressTextView()

        fetchUserLocation = FetchUserLocation(this, address_tv, this, null)
        pref = this.getSharedPreferences("RealEstateManager", Context.MODE_PRIVATE)
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_create_real_estate
    }

    override fun initButtons() {
        //Button to add
        btn_send.setOnClickListener {
            getInfoFromUI()
        }
        //Geo latLng
        geo_loc.setOnClickListener {
            getUserLocation()
        }

    }

    override fun getInfoFromUI() {
        agent = agent_et.text.toString()
        price = price_tv.text.toString()
        description = description_et.text.toString()
        surface = surface_tv.text.toString()
        startDate = Utils.getTodayDate(Calendar.getInstance().time)

        if (agent.isNotEmpty()) {
            if (address.isNotEmpty()) {
                createRealEstate()
            } else {
                Toast.makeText(this, getString(R.string.warning_add_object_without_address), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.warning_add_item_without_agent), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAddressTextView() {
        //Address edit text
        address_tv.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        //Give the user time to finish writing
                        timer?.cancel()
                        timer = object : CountDownTimer(1500, 1000) {
                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                //search points of interests
                                getPointsOfInterest()
                                Log.e("address", " fetch " + address_tv.text)
                            }
                        }.start()
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
    }


    fun getPointsOfInterest() {
        //Fetch nearby search results from the latLng, this will also check that the user has entered a valid address
        address = address_tv.text.toString()
        latLng = Utils.getLatLngFromAddress(this, address)

        if (latLng != null) {
//            fetchUserLocation!!.getAddress(latLng!!.latitude, latLng!!.longitude)
            val locationForSearch = Utils.setLocationString(latLng)

            disposable = NearbySearchStream.fetchNearbyPlacesStream(locationForSearch).subscribeWith(object : DisposableObserver<NearbySearchObject>() {
                override fun onNext(nearbySearchObject: NearbySearchObject) {
                    listPoi = PointOfInterestsMatcher.checkIfPointOfInterest(nearbySearchObject.results)
                }

                override fun onError(e: Throwable) {
                    Log.e("Main", "Error fetching nearby places $e")
                }

                override fun onComplete() {
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.warning_address_not_valid), Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Get user latLng when clicking on geo latLng icon and update edit text with address
     */
    private fun getUserLocation() {
        fetchUserLocation?.checkLocationPermission()
    }


    /**
     * Handling permissions to take photo, pick photo from gallery and geo locate user
     */

    override fun createRealEstate() {
        val lat = latLng?.latitude
        val latitude = lat.toString()
        val lon = latLng?.longitude
        val longitude = lon.toString()
        val pointsOfInterests = JsonConverter.convertToJson(listPoi)

        city = pref!!.getString("CurrentCity", "Unknown")
        Log.e("create ", city)

        Log.e("create", pointsOfInterests)

        val realEstate = RealEstate(null, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, city!!, "false", startDate, null, agent, pointsOfInterests
        )

        AsyncTask.execute {
            realEstateId = viewModel?.createRealEstate(realEstate)!!

            for (photo in photos) {
                photo.realEstateId = realEstateId
                if (!photo.text.equals("Deleted"))
                    viewModel?.createPhoto(photo)
            }
            Log.e("Create", "realEstateId=" + realEstateId)

        }
        Toast.makeText(this, "Real estate added succesfully", Toast.LENGTH_SHORT).show()
    }
}