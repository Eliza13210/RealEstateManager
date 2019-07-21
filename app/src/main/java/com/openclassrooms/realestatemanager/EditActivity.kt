package com.openclassrooms.realestatemanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.information_layout.*
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS


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

        roomsNb = realEstate.bathrooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        roomsNb = realEstate.bedrooms?.toIntOrNull()
        if (roomsNb != null) spinner_rooms.setSelection(roomsNb) else spinner_rooms.setSelection(0)

        description_et.setText(realEstate.description)
        latitude = realEstate.latitude!!
        longitude = realEstate.longitude!!
        address = realEstate.address!!

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
        check_sold.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                startDatePicker()

            } else {
                sold = false
                endDate=""
                end_date.text=""
            }
        }
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
        endDate = end_date.text.toString()

        Log.e("check if", "is empty " + agent.isEmpty())

        if (agent.isNotEmpty()) {
            createRealEstate()
        } else {
            Toast.makeText(this, "You need to choose an agent and address for the object you want to create", Toast.LENGTH_SHORT).show()
        }
    }

    override fun createRealEstate() {
        Log.e("edit", "update clicket " + sold + endDate)
        val realEstate = RealEstate(realEstateId, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, sold, startDate, endDate, agent
        )

        viewModel?.updateRealEstate(realEstate)

        for (photo in photos) {
            photo.realEstateId = realEstateId
            viewModel?.createPhoto(photo)
        }
    }

    private fun startDatePicker() {
        sold = true

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
            end_date.text = sdf.format(cal.time)
        }
        DatePickerDialog(this@EditActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

    }
}