package com.openclassrooms.realestatemanager

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
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

        fetchUserLocation = FetchUserLocation(this, address_tv)
        pref = this.getSharedPreferences("RealEstate", Context.MODE_PRIVATE)
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
            Log.e("create", "clicked img")
            getUserLocation()
        }

    }

    override fun getInfoFromUI() {
        agent = agent_et.text.toString()
        price = price_tv.text.toString()
        description = description_et.text.toString()
        surface = surface_tv.text.toString()
        startDate = Utils.getTodayDate(Calendar.getInstance().time)

        Log.e("check if", "is empty " + agent.isEmpty())

        if (agent.isNotEmpty()) {
            createRealEstate()
        } else {
            Toast.makeText(this, "You need to choose an agent for the object you want to create", Toast.LENGTH_SHORT).show()
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
                                //do what you wish
                                getPointsOfInterest()
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
        Log.e("Create", address)

        if (latLng != null) {
            val locationForSearch = Utils.setLocationString(latLng)

            disposable = NearbySearchStream.fetchNearbyPlacesStream(locationForSearch).subscribeWith(object : DisposableObserver<NearbySearchObject>() {
                override fun onNext(nearbySearchObject: NearbySearchObject) {
                    listPoi = Utils.checkIfPointOfInterest(nearbySearchObject.results)
                }

                override fun onError(e: Throwable) {
                    Log.e("Main", "Error fetching nearby places $e")
                }

                override fun onComplete() {

                }
            })
        } else {
            Toast.makeText(this, "Enter a valid address", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Get user latLng when clicking on geo latLng icon and update edit text with address
     */
    private fun getUserLocation() {
        Log.e("create", "check permission")
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

        val sb = StringBuilder()

        for (result in listPoi) {
            sb.append(result.name + ", " + result.types[0].replace('_', ' '))
            sb.append(System.getProperty("line.separator"))
        }
        val pointsOfInterest: String = sb.toString()

        Log.e("create", pointsOfInterest)

        val realEstate = RealEstate(null, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, false, startDate, null, agent, pointsOfInterest
        )

        realEstateId = viewModel?.createRealEstate(realEstate)!!

        for (photo in photos) {
            photo.realEstateId = realEstateId
            viewModel?.createPhoto(photo)
        }

        Toast.makeText(this, "Real estate added succesfully", Toast.LENGTH_SHORT).show()

    }

    private fun disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed) this.disposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeWhenDestroy()
    }


}