package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.openclassrooms.realestatemanager.SearchManager
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.poi_details_layout.*
import kotlinx.android.synthetic.main.seekbar_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.*
import kotlin.collections.ArrayList
import android.R
import kotlinx.android.synthetic.main.seekbar_layout.view.*


class SearchActivity : AppCompatActivity() {

    private var viewModel: RealEstateViewModel? = null

    private var type = ""
    private var sold = false
    private var surfaceMin = "0"
    private var surfaceMax = "1000"
    private var priceMax = ""
    private var priceMin = ""

    private var updatedList = ArrayList<RealEstate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.openclassrooms.realestatemanager.R.layout.activity_search)
        initToolbar()
        initClickableItems()
        initSeekbars()
        initViewModel()
        checkIfTablet()
    }

    /**
     * Initiate buttons and toolbar and spinners
     */
    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }

    private fun initSeekbars() {

        val seekbarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                Log.v("searchA", "seekbar" + seekBar)

                when (seekBar) {
                    seekbar_max_surface -> surface_max_textview.text = "" + progress + " m2"
                    seekbar_min_surface -> surface_min_textview.text = "" + progress + " m2"

                    seekbar_max_price -> price_max_textview.text = "$ $progress"
                    seekbar_min_price -> price_min_textview.text = "$ $progress"

                    seekbar_max_rooms -> max_rooms_textview.text = "$progress"
                    seekbar_min_rooms -> min_rooms_textview.text = "$progress"

                    seekbar_max_bedrooms -> max_bedrooms_textview.text = "$progress"
                    seekbar_min_bedrooms -> min_bedrooms_textview.text = "$progress"

                    seekbar_max_bathrooms -> max_bathrooms_textview.text = "$progress"
                    seekbar_min_bathrooms -> min_bathrooms_textview.text = "$progress"

                    seekbar_max_photos -> max_photos_textview.text = "$progress"
                    seekbar_min_photos -> min_photos_textview.text = "$progress"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //logic
            }
        }

        //SURFACE SEEKBARS
        seekbar_max_surface.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_surface.setOnSeekBarChangeListener(seekbarListener)

        //PRICE SEEKBARS
        seekbar_max_price.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_price.setOnSeekBarChangeListener(seekbarListener)

        //ROOM SEEKBARS
        seekbar_max_rooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_rooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_max_bedrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_bedrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_max_bathrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_bathrooms.setOnSeekBarChangeListener(seekbarListener)

        //PHOTOS SEEKBARS
        seekbar_max_photos.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_photos.setOnSeekBarChangeListener(seekbarListener)

    }


    private fun initClickableItems() {
        //BUTTON SEARCH
        btn_search.setOnClickListener {
            getQueryFromUI()
        }

        //CHECKBOX SOLD
        check_sold.setOnClickListener {
            sold = check_sold.isChecked
        }

        //DATE PICKER
        sold_after_tv.setOnClickListener { startDatePicker(sold_after_tv) }
        sold_before_tv.setOnClickListener { startDatePicker(sold_before_tv) }

        start_date_after_tv.setOnClickListener { startDatePicker(start_date_after_tv) }
        start_date_before_tv.setOnClickListener { startDatePicker(start_date_before_tv) }
    }

    private fun checkIfTablet() {
        // WILL BE FALSE IF TABLET
        if (resources.getBoolean(com.openclassrooms.realestatemanager.R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun startDatePicker(editText: TextView) {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            if (Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))) {
                editText.text = Utils.getTodayDate(cal.time)
            } else {
                Toast.makeText(this, "You have to choose a date before today", Toast.LENGTH_SHORT).show()
            }
        }

        val dialog = DatePickerDialog(this@SearchActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel") { _, which ->
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                editText.text = ""
            }
        }
        dialog.show()
    }

    private fun getInfoFromCheckBox(): List<String> {
        val result = ArrayList<String>()
        val checkBoxList = ArrayList<CheckBox>()
        checkBoxList.add(cb_airport)
        checkBoxList.add(cb_bus_station)
        checkBoxList.add(cb_city_hall)
        checkBoxList.add(cb_doctor)
        checkBoxList.add(cb_hospital)
        checkBoxList.add(cb_parc)
        checkBoxList.add(cb_pharmacy)
        checkBoxList.add(cb_restaurant)
        checkBoxList.add(cb_school)
        checkBoxList.add(cb_subway)
        checkBoxList.add(cb_supermarket)
        checkBoxList.add(cb_train)
        for (box in checkBoxList) {
            if (box.isChecked) {
                result.add(box.text.toString().toLowerCase())
            }
        }
        return result
    }


    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    private fun getQueryFromUI() {
        val manageSearch = SearchManager()

        type = type_tv.text.toString()
        //  manageSearch.getQueryFromUI(agent_et, type, getInfoFromCheckBox(), city_tv, surfaceMin, surfaceMax, price_min, price_max,
        //        rooms_min, rooms_max, bedrooms_min, bedrooms_max, bathrooms_min, bathrooms_max, start_date_before, start_date_after, start_date_et,
        //      end_date_et, sold)

        Log.e("searchA", "query" + manageSearch.getQuery() + manageSearch.getArgs().toString())
        search(manageSearch.getQuery()!!, manageSearch.getArgs())
    }

    private fun search(query: String, arg: Array<String>) {
        viewModel!!.searchRealEstates(query, arg).observe(this, Observer<List<RealEstate>> {
            if (photos_min_et.text.isNotEmpty() || photos_max_et.text.isNotEmpty())
                this.searchOnPhotos(it)
            else {
                showResult(it)
            }
        })
    }

    private fun searchOnPhotos(list: List<RealEstate>) {
        updatedList = ArrayList()
        var isLast = false

        for (item in list) {
            viewModel!!.getPhotos(item.id!!).observe(this, Observer<List<Photo>> {
                if (item.id == list[list.size - 1].id) {
                    //THIS IS THE LAST ITEM
                    isLast = true
                }
                upDateListIfPhotos(it, item, isLast)
            })
        }
    }

    private fun upDateListIfPhotos(listPhoto: List<Photo>, realEstate: RealEstate, isLast: Boolean) {
        val max = if (photos_max_et.text.isNotEmpty()) Integer.parseInt(photos_max_et.text.toString()) else 20
        val min = if (photos_min_et.text.isNotEmpty()) Integer.parseInt(photos_min_et.text.toString()) else 0

        if (listPhoto.size in min..max) {
            updatedList.add(realEstate)
        }
        if (isLast) {
            showResult(updatedList)
        }
    }

    //START RESULT ACTIVITY WITH THE LIST OF REAL ESTATES
    private fun showResult(realEstateList: List<RealEstate>) {
        val gson = Gson()
        val jsonList = gson.toJson(realEstateList)

        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra(SearchResultActivity.EXTRA_TAG_RESULT, jsonList)
        startActivity(intent)
    }
}
