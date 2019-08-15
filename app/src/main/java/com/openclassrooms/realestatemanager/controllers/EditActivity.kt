package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.information_layout.*
import kotlinx.android.synthetic.main.room_details_layout.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.*


class EditActivity : BaseActivityUIInformation() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            realEstateId = bundle.getLong("RealEstateId", 0)
            initRealEstate(realEstateId)
            getPhotos(realEstateId)
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_edit
    }

    private fun initRealEstate(id: Long) {

        this.viewModel?.getRealEstate(id)?.observe(this, Observer<RealEstate> {
            if (it != null)
                this.updateDetails(it)
        })
    }


    private fun updateDetails(realEstate: RealEstate) {
        Log.e("edit", realEstate.agent)
        agent_et.setText(realEstate.agent)
        type_tv.setText(realEstate.type)
        surface_tv.setText(realEstate.surface)
        price_tv.setText(realEstate.price)

        var roomsNb: Int? = realEstate.rooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bathrooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bedrooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        description_et.setText(realEstate.description)
        latitude = realEstate.latitude!!
        longitude = realEstate.longitude!!
        address = realEstate.address!!
        city = realEstate.city
    }

    //  Get all photos
    private fun getPhotos(realEstateId: Long) {
        this.viewModel?.getPhotos(realEstateId)?.observe(this, Observer<List<Photo>> {
            this.updatePhotoList(it)
        })
    }

    private fun updatePhotoList(list: List<Photo>) {
        Log.e("edit act", "list of photos=" + list.size)
        photos.addAll(list)
        this.photoAdapter.updateData(list)
    }


    override fun initButtons() {
        btn_update.setOnClickListener { getInfoFromUI() }
        check_sold.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                sold = "true"
                startDatePicker()
            } else {
                sold = "false"
                endDate = ""
                end_date.text = ""
            }
        }
    }


    override fun getInfoFromUI() {

        Toast.makeText(this, "Real estate updated successfully", Toast.LENGTH_SHORT).show()
        agent = agent_et.text.toString()
        price = price_tv.text.toString()
        description = description_et.text.toString()
        surface = surface_tv.text.toString()
        endDate = end_date.text.toString()

        Log.e("check if", "is sold " + sold + " " + endDate)

        //Check if end date is picked when object is sold before updating
        if (sold.equals("true")) {
            if (endDate.isNotEmpty()) {
                createRealEstate()
            } else {
                Toast.makeText(this, "You need to choose a date if the object is sold", Toast.LENGTH_SHORT).show()
            }
        } else {
            createRealEstate()
        }

    }

    override fun createRealEstate() {
        Log.e("edit", "update clicket " + sold + endDate)
        val realEstate = RealEstate(realEstateId, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, city!!, sold, startDate, endDate, agent
        )

        viewModel?.updateRealEstate(realEstate)

        for (photo in photos) {

            //If photo has been deleted, remove it from database
            if (photo.text.equals("Deleted") && photo.id != null) {
                viewModel?.deletePhoto(photo.id!!)
                photos.remove(photo)
            } else if (photo.text.equals("Deleted")) {
                photos.remove(photo)
            }

            //Id will be null if not yet added to database, that means that it is a new photo that needs to be added
            if (photo.id == null && !photo.text.equals("Deleted")) {
                photo.realEstateId = realEstateId
                viewModel?.createPhoto(photo)
            }
        }
    }

    private fun startDatePicker() {
        sold = "true"

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            Log.e("Edit", " " + cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.MONTH) + cal.get(Calendar.YEAR))

            if (Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))) {
                end_date.text = Utils.getTodayDate(cal.time)
            } else {
                sold = "false"
                check_sold.isChecked = false
                Toast.makeText(this, "You have to choose a date before today", Toast.LENGTH_SHORT).show()
            }
        }

        DatePickerDialog(this@EditActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()


    }

}