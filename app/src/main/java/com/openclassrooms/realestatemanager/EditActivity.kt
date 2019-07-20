package com.openclassrooms.realestatemanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.information_layout.*


class EditActivity : BaseActivityUIInformation() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            realEstateId = bundle.getString("RealEstateId")
            initRealEstate(realEstateId)
            getPhotos(realEstateId)
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_edit
    }

    private fun initRealEstate(id: String?) {
        this.viewModel?.getRealEstate(id)?.observe(this, Observer<RealEstate> {
            this.updateDetails(it)
        })
    }


    private fun updateDetails(realEstate: RealEstate) {
        agent_et.setText(realEstate.agent)
        type_tv.setText(realEstate.type)
        surface_tv.setText(realEstate.surface)
        price_tv.setText(realEstate.price)

        var roomsNb: Int? = realEstate.rooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        /**  roomsNb = realEstate.bathrooms?.toInt()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bedrooms?.toInt()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)
         */
        description_et.setText(realEstate.description)

    }

    //  Get all photos
    private fun getPhotos(realEstateId: String) {
        this.viewModel?.getPhotos(realEstateId)?.observe(this, Observer<List<Photo>> { this.updatePhotoList(it) })
    }

    private fun updatePhotoList(list: List<Photo>) {
        Log.e("edit act", "list of photos=" + list.size)
        this.photoAdapter.updateData(list)
    }


    override fun initButtons() {
        btn_update.setOnClickListener { getInfoFromUI() }
        btn_delete.setOnClickListener { deleteRealEstate() }
    }


    private fun deleteRealEstate() {
        Log.e("edit", "delete clicket")
        viewModel?.deleteRealEstate(realEstateId)
        Toast.makeText(this, "Item successfully deleted", Toast.LENGTH_SHORT).show()
    }
    override fun getInfoFromUI() {

        Toast.makeText(this, "Real estate updated successfully", Toast.LENGTH_SHORT).show()
        agent = agent_et.text.toString()
        price = price_tv.text.toString()
        description = description_et.text.toString()
        surface = surface_tv.text.toString()
        sold=rbtn_sold.isChecked


        //TODO  END DATE LATITUDE LONGITUDE LATLNG ADDRESS RETRIEVE FROM OBJECT OR NOT UPDATE

        Log.e("check if", "is empty " + agent.isEmpty())

        if (agent.isNotEmpty()) {
            createRealEstate()
        } else {
            Toast.makeText(this, "You need to choose an agent and address for the object you want to create", Toast.LENGTH_SHORT).show()
        }
    }

    override fun createRealEstate() {
        Log.e("edit", "update clicket")
        val realEstate = RealEstate(realEstateId, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, false, startDate, null, agent
        )

        viewModel?.updateRealEstate(realEstate)

        for (photo in photos) {
            photo.realEstateId = realEstateId
            viewModel?.createPhoto(photo)
        }}
}