package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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


class SearchActivity : AppCompatActivity() {

    private var isTablet: Boolean = false

    //FOR SEEKBAR
    var MIN = 0
    var MAX = 1000
    var STEP = 100
    var textView: TextView? = null

    private var viewModel: RealEstateViewModel? = null

    private var type = ""
    private var sold = false
    private var surfaceMin = "0"
    private var surfaceMax = "1000"
    private var priceMax = ""
    private var priceMin = ""
    private var roomsMax = ""
    private var roomsMin = ""
    private var bedroomsMax = ""
    private var bedroomsMin = ""
    private var bathroomsMax = ""
    private var bathroomsMin = ""

    private var updatedList = ArrayList<RealEstate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.openclassrooms.realestatemanager.R.layout.activity_search)
        checkIfTablet()
        initClickableItems()
        initSeekbars()
        initViewModel()
    }


    private fun checkIfTablet() {
        // WILL BE FALSE IF TABLET
        if (resources.getBoolean(com.openclassrooms.realestatemanager.R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setActionbarPhone()
        } else {
            isTablet = true
            initToolbar()
        }
    }

    private fun setActionbarPhone() {
        this.setSupportActionBar(bottom_app_bar)
        bottom_app_bar?.setNavigationOnClickListener { startActivity(Intent(this@SearchActivity, MainActivity::class.java)) }

        fab?.setImageResource(com.openclassrooms.realestatemanager.R.drawable.ic_action_done)
        fab?.setOnClickListener { getQueryFromUI() }
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
                    seekbar_max_rooms -> {
                        textView = max_rooms_textview
                        MAX = 50
                        STEP = 1
                    }
                    seekbar_min_rooms -> {
                        textView = min_rooms_textview
                        MAX = 50
                        STEP = 1
                    }

                    seekbar_max_bedrooms -> {
                        textView = max_bedrooms_textview
                        MAX = 20
                        STEP = 1
                    }
                    seekbar_min_bedrooms -> {
                        textView = min_bedrooms_textview
                        MAX = 20
                        STEP = 1
                    }

                    seekbar_max_bathrooms -> {
                        textView = max_bathrooms_textview
                        MAX = 20
                        STEP = 1
                    }
                    seekbar_min_bathrooms -> {
                        textView = min_bathrooms_textview
                        MAX = 20
                        STEP = 1
                    }
                }
                seekBar.max = (MAX - MIN) / STEP
                val progressStep = MIN + (progress * STEP)

                textView!!.text = progressStep.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        }

        //ROOM SEEKBARS
        seekbar_max_rooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_rooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_max_bedrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_bedrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_max_bathrooms.setOnSeekBarChangeListener(seekbarListener)
        seekbar_min_bathrooms.setOnSeekBarChangeListener(seekbarListener)

    }


    private fun initClickableItems() {

        //BUTTON SEARCH in TABLET LAYOUT
        btn_search?.setOnClickListener {
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


    private fun startDatePicker(editText: TextView) {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            if (Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))) {
                editText.text = Utils.formateDateForDatabase(cal.time)
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
        surfaceMin = surface_min.text.toString()
        surfaceMax = surface_max.text.toString()
        priceMin = price_min.text.toString()
        priceMax = price_max.text.toString()
        roomsMin = min_rooms_textview.text.toString()
        roomsMax = max_rooms_textview.text.toString()
        bedroomsMax = max_bedrooms_textview.text.toString()
        bedroomsMin = min_bedrooms_textview.text.toString()
        bathroomsMax = max_bathrooms_textview.text.toString()
        bathroomsMin = min_bathrooms_textview.text.toString()

        manageSearch.getQueryFromUI(agent_et, type, getInfoFromCheckBox(), city_tv, surfaceMin, surfaceMax, priceMin, priceMax,
                roomsMin, roomsMax, bedroomsMin, bedroomsMax, bathroomsMin, bathroomsMax, start_date_before_tv, start_date_after_tv, sold_after_tv,
                sold_before_tv, sold)

        Log.e("searchA", "query" + manageSearch.getQuery() + manageSearch.getArgs().asList().toString())
        search(manageSearch.getQuery()!!, manageSearch.getArgs())
    }

    private fun search(query: String, arg: Array<String>) {
        viewModel!!.searchRealEstates(query, arg).observe(this, Observer<List<RealEstate>> {
            if (photos_min.text.toString().isNotEmpty() || photos_max.text.toString().isNotEmpty() ) {
                this.searchOnPhotos(it)
            } else {
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
        val max = if (photos_max.text.toString().toInt() > 0) photos_max.text.toString().toInt() else 20
        val min = if (photos_min.text.toString().toInt() > 0) photos_min.text.toString().toInt() else 0

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

    /**
     * Handle click in action bar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // REMOVE CREATE ITEM FROM TOOLBAR IF PHONE
        if (!isTablet) {
            menuInflater.inflate(com.openclassrooms.realestatemanager.R.menu.bottom_app_bar_menu_nav_back, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.openclassrooms.realestatemanager.R.id.app_bar_home -> {
                startActivity(Intent(this@SearchActivity, MainActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
