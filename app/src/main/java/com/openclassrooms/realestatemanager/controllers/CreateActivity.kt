package com.openclassrooms.realestatemanager.controllers

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.FetchUserLocation
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.models.RealEstate
import kotlinx.android.synthetic.main.activity_create_real_estate.*
import kotlinx.android.synthetic.main.address_layout.*
import kotlinx.android.synthetic.main.information_layout.*
import kotlinx.android.synthetic.main.room_details_layout.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.*

class CreateActivity : BaseActivityUIInformation() {

    private var latLng: LatLng? = null

    override fun onResume() {
        super.onResume()
        if (!updated) resetUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        //Button reset
        btn_reset.setOnClickListener {
            resetUI()
        }

        //Geo locate user on click
        geo_loc.setOnClickListener {
            getUserLocation()
        }
    }


    /**
     * Get LatLng from the address the user typed
     */
    private fun checkAddress() {
        address = address_tv.text.toString()
        city = Utils.removeSpacesAndAccentLetters(city_tv.text.toString())

        loadingPanel?.visibility = View.VISIBLE
        val checkAddress = "$address $city"
        AsyncTask.execute {
            latLng = Utils.getLatLngFromAddress(this, checkAddress, loadingPanel)
        }

    }

    /**
     * Get user latLng when clicking on geo latLng icon and update edit text with address
     */
    private fun getUserLocation() {
        fetchUserLocation = FetchUserLocation(this, address_tv, city_tv, this, null)
        fetchUserLocation?.checkLocationPermission()
    }

    override fun getInfoFromUI() {
        checkAddress()
        agent = Utils.removeSpacesAndAccentLetters(agent_et.text.toString())
        if (price_tv.text!!.isNotEmpty()) {
            price = Integer.parseInt(price_tv.text.toString())
        }
        description = description_et.text.toString()
        surface = if (!surface_tv.text.toString().isNullOrEmpty()) {
            Integer.parseInt(surface_tv.text.toString())
        } else {
            null
        }
        startDate = Utils.getTodayDate(Calendar.getInstance().time)
        pointsOfInterest = poi_tv.text.toString()
        type = type_tv.text.toString().toLowerCase()

        rooms = if (!rooms_tv.text.isNullOrEmpty()) {
            Integer.parseInt(rooms_tv.text.toString())
        } else {
            null
        }
        bedrooms = if (bedroom_tv.text.isNullOrEmpty()) {
            null
        } else {
            Integer.parseInt(bedroom_tv.text.toString())
        }
        bathrooms = if (bedroom_tv.text.isNullOrEmpty()) {
            null
        } else {
            Integer.parseInt(bathroom_tv.text.toString())
        }

        if (agent.isNotEmpty()) {
            if (city_tv.text.isNullOrEmpty() || address_tv.text.isNullOrEmpty()) {
                Toast.makeText(this@CreateActivity, resources.getString(R.string.warning_add_object_without_address), Toast.LENGTH_SHORT).show()
                loadingPanel.visibility = View.INVISIBLE
            } else {
                timer?.cancel()
                timer = object : CountDownTimer(1500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {

                        if (latLng != null) {
                            createRealEstate()
                        } else {
                            Toast.makeText(this@CreateActivity, resources.getString(R.string.warning_add_object_without_address), Toast.LENGTH_SHORT).show()
                        }
                        loadingPanel.visibility = View.INVISIBLE
                    }
                }.start()
            }
        } else {
            loadingPanel.visibility = View.INVISIBLE
            Toast.makeText(this, getString(R.string.warning_add_item_without_agent), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Create a new real estate and add to the database
     */
    override fun createRealEstate() {


        latitude = latLng!!.latitude.toString()
        longitude = latLng!!.longitude.toString()
        Log.e("create", "create real estate " + latLng)
        val realEstate = RealEstate(null, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, city, "false", startDate, null, agent, pointsOfInterest
        )

        AsyncTask.execute {
            realEstateId = viewModel?.createRealEstate(realEstate)!!
            createNotification()

            for (photo in photos) {
                photo.realEstateId = realEstateId
                if (!photo.text.equals("Deleted"))
                    viewModel?.createPhoto(photo)
            }
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    /**
     * Reset all edit text
     */
    private fun resetUI() {
        photos = ArrayList()
        photoAdapter.updateData(photos, null)
        type = ""
        type_tv.setText("")
        price = null
        price_tv.setText("")
        description = ""
        description_et.setText("")
        surface = null
        surface_tv.setText("")
        rooms = null
        rooms_tv.setText("")
        bathrooms = null
        bedroom_tv.setText("")
        bedrooms = null
        bedroom_tv.setText("")
        address = ""
        address_tv.setText("")
        city = ""
        city_tv.setText("")
        sold = "false"
        startDate = ""
        agent = ""
        agent_et.setText("")
        listPoi = ArrayList()
        latLng = null
        latitude = ""
        longitude = ""
        poi_tv.setText("")
    }

    private fun createNotification() {
        var builder = NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Real Estate manager")
                .setContentText(getString(R.string.real_estate_added))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}