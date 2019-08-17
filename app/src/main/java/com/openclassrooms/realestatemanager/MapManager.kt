package com.openclassrooms.realestatemanager

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.controllers.DetailActivity
import com.openclassrooms.realestatemanager.controllers.MainActivity
import com.openclassrooms.realestatemanager.models.RealEstate

class MapManager(var context: Context, var list: List<RealEstate>? = null, private var map: GoogleMap) {

    fun showUser(userLatLng: LatLng) {
        //Add user marker on map
        map.addMarker(MarkerOptions()
                .position(userLatLng)
                .title("User"))
                .tag = 100

        //Move camera to show user location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
    }


    fun displayMarkersOnMap() {
        for (realEstate in list!!) {

            val description: String? = realEstate.description

            val lat = realEstate.latitude
            val lng = realEstate.longitude

            Log.e("manager ", lat +lng)

            val latLng = LatLng(java.lang.Double.parseDouble(lat!!), java.lang.Double.parseDouble(lng!!))

            //Add marker to map
            val marker = map.addMarker(MarkerOptions().position(latLng)
                    .title(description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
            marker.tag = realEstate.id
            Log.e("marker", "tag =" + realEstate.id)
        }
        showRealEstateWhenClickOnMarker()
    }

    private fun showRealEstateWhenClickOnMarker() {
        //User click on marker will start function that fetch details about restaurant and start new activity
        map.setOnMarkerClickListener { marker ->
            val i = marker.tag as Long
            Log.e("click", "get tag $i")
            when {
                //CLICKED ON USER MARKER
                i == 100L -> Log.e("Manager", "Clicked on user")
                context.resources.getBoolean(R.bool.portrait_only) -> {
                    //CLICKED ON MARKER AND PHONE WILL START DETAIL ACTIVITY

                    Log.e("click", "click phone get tag $i")
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_TAG, i)
                    context.startActivity(intent)
                }
                else -> {
                    //CLICKED MARKER AND TABLET WILL START MAIN ACTIVITY AND SHOW REAL ESTATE IN FRAGMENT
                    Log.e("click", "tablet get tag $i")
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_TAG, i)
                    context.startActivity(intent)
                }
            }
            false
        }
    }
}