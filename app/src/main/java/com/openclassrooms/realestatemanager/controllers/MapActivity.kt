package com.openclassrooms.realestatemanager.controllers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.FetchUserLocation
import com.openclassrooms.realestatemanager.MapManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import kotlinx.android.synthetic.main.toolbar.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, EasyPermissions.PermissionCallbacks {

    //For google maps
    private var mMap: GoogleMap? = null
    private var location: FetchUserLocation? = null

    //For markers
    private var mapManager: MapManager? = null
    private var list: List<RealEstate>? = null

    //ViewModel and database
    private var viewModel: RealEstateViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        checkInternetConnexion()
        initToolbar()
        //Show the google map
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }

    private fun checkInternetConnexion() {
        if (!Utils.isInternetAvailable(this)) {
            Toast.makeText(this, "No internet available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserLocation() {
        location = FetchUserLocation(this, null, this, mMap)
        location!!.checkLocationPermission()
    }


    override fun onMapReady(p0: GoogleMap?) {
        mMap = p0

        getUserLocation()
        Log.e("mapA", "map ready")
        try {   //Check permission to show user location
            if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED) {
                //If ok, initialize map to show user location and buttons to zoom user

                Log.e("mapA", "access gps ok")
                mMap!!.isMyLocationEnabled = true
                mMap!!.uiSettings.isMyLocationButtonEnabled = true
                mMap!!.setOnMyLocationButtonClickListener(this)

                //Set map type and zoom
                mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                mMap!!.uiSettings.isZoomControlsEnabled = true

                initViewModel()
            } else {
                //If no permission, do not show user location, only map
                mMap!!.isMyLocationEnabled = false
                mMap!!.uiSettings.isMyLocationButtonEnabled = false
                Log.e("Update Map", "permission not yet granted")
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        val prefs = getSharedPreferences("Go4Lunch", Context.MODE_PRIVATE)

        val mLatitude = java.lang.Double.parseDouble(Objects.requireNonNull(prefs.getString("CurrentLatitude", "1")))
        val mLongitude = java.lang.Double.parseDouble(Objects.requireNonNull(prefs.getString("CurrentLongitude", "1")))
        val latLng = LatLng(mLatitude,
                mLongitude)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f))

        Log.e("mapA", "my loc btn clicked")
        return false
    }


    // Configuring ViewModel
    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
        this.viewModel!!.fetchAllRealEstates().observe(this, Observer<List<RealEstate>> {
            list = it
            //Adding markers to map if the list is not empty
            mapManager = MapManager(this, list, mMap!!)
            if (!list.isNullOrEmpty()) {
                mapManager!!.displayMarkersOnMap()
            }
        })
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions

        Log.e("Activity", "Permission to easyperm$requestCode")
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            Toast.makeText(this, "You need to grant permission to access your location", Toast.LENGTH_SHORT).show()
        } else if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.e("Activity", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        when (requestCode) {
            FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                location?.getDeviceLocation()
            }
        }
    }
}
