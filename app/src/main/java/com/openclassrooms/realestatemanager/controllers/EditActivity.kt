package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask
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

    private var startDatePicker = true


    override fun onNewIntent(intent: Intent?) {
        if (intent != null)
            setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        realEstateId = intent.getLongExtra(DetailActivity.EXTRA_TAG, 0)
        if (!updated) {
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
        agent_et.setText(realEstate.agent)
        type_tv.setText(realEstate.type)
        surface_tv.setText(realEstate.surface?.let { Utils.convertIntToStringAndDivide(it.toFloat()) })
        price_tv.setText(realEstate.price?.toString())
        poi_tv.setText(realEstate.pointsOfInterest)
        description_et.setText(realEstate.description)
        if (realEstate.sold == "true") startDatePicker = false
        check_sold.isChecked = (realEstate.sold == "true")
        end_date.text = if (realEstate.endDate.isNullOrEmpty()) " " else realEstate.endDate
        rooms_tv.setText(realEstate.rooms.toString())
        bedroom_tv.setText(realEstate.bedrooms.toString())
        bathroom_tv.setText(realEstate.bathrooms.toString())

        description_et.setText(realEstate.description)
        latitude = realEstate.latitude!!
        longitude = realEstate.longitude!!
        address = realEstate.address
        city = realEstate.city
    }

    //  Get all photos
    private fun getPhotos(realEstateId: Long) {
        this.viewModel?.getPhotos(realEstateId)?.observe(this, Observer<List<Photo>> {
            this.updatePhotoList(it)
        })
    }

    private fun updatePhotoList(list: List<Photo>) {
        photos.clear()
        photos.addAll(list)
        this.photoAdapter.updateData(list, null)
    }

    override fun initButtons() {
        btn_update.setOnClickListener { getInfoFromUI() }
        check_sold.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sold = "true"
                if (startDatePicker) startDatePicker()
            } else {
                sold = "false"
                endDate = ""
                end_date.text = ""
                startDatePicker = true
            }
        }
    }

    override fun getInfoFromUI() {
        //agent = agent_et.getText.toString()

        if (price_tv.text!!.isNotEmpty()) price = Integer.parseInt(price_tv.text.toString())
        description = description_et.text.toString()
        surface = Utils.convertToIntAndMultiply(surface_tv.text.toString())
        endDate = end_date.text.toString()

        //Check if end date is picked when object is sold before updating
        if (sold == "true") {
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

        val realEstate = RealEstate(realEstateId, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, city, sold, startDate, endDate, agent, poi_tv.text.toString())

        AsyncTask.execute {
            viewModel?.updateRealEstate(realEstate)
            for (photo in photos) {
                //If photo has been deleted, remove it from database
                if (photo.text.equals("Deleted") && photo.id != null) {
                    viewModel?.deletePhoto(photo.id)
                }
                //Id will be null if not yet added to database, that means that it is a new photo that needs to be added
                if (photo.id == null && !photo.text.equals("Deleted")) {
                    photo.realEstateId = realEstateId
                    viewModel?.createPhoto(photo)
                }
            }
        }
        Toast.makeText(this, "Real estate updated successfully", Toast.LENGTH_SHORT).show()
        updated = false
    }

    private fun startDatePicker() {
        sold = "true"

        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

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