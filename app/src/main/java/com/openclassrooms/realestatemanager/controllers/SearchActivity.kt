package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.SearchManager
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.Result
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.poi_details_layout.*
import kotlinx.android.synthetic.main.room_details_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.*

class SearchActivity : AppCompatActivity() {


    private var viewModel: RealEstateViewModel? = null
    private var query: String? = null

    // For creating real estate object
    protected var photos = ArrayList<Photo>()
    protected var type = ""
    protected var price = ""
    protected var description = ""
    protected var surface = ""
    protected var rooms = ""
    protected var bathrooms = ""
    protected var address = ""
    protected var sold = false
    protected var startDate = ""
    protected var endDate = ""
    protected var agent = ""
    protected var listPoi: List<Result> = ArrayList()
    protected var bedrooms: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initToolbar()
        initClickableItems()
        initViewModel()
    }

    /**
     * Initiate buttons and toolbar and spinners
     */
    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v -> startActivity(Intent(this, MainActivity::class.java)) }
    }

    private fun initClickableItems() {

        // CHOOSE TYPE
        house_tv.setOnClickListener { getType("tag_house") }
        apartement_tv.setOnClickListener { getType("tag_apartement") }

        //Edit text type
        type_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                house_tv.setBackgroundResource(R.color.white)
                apartement_tv.setBackgroundResource(R.color.white)
            }

            override fun afterTextChanged(s: Editable) {
                type = type_tv.text.toString()
                Log.e("type", type)
            }
        })


        //BUTTON SEARCH
        btn_search.setOnClickListener {
            getQueryFromUI()
            Log.e("search", "clicked button")
        }

        //CHECKBOX SOLD
        cb_sold.setOnClickListener {
            sold = cb_sold.isChecked
        }

        //DATE PICKER
        start_date_et.setOnClickListener { startDatePicker(start_date_et) }
        end_date_et.setOnClickListener { startDatePicker(end_date_et) }
    }

    private fun startDatePicker(editText: EditText) {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            Log.e("Search", " " + cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.MONTH) + cal.get(Calendar.YEAR))

            if (Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))) {
                editText.setText(Utils.getTodayDate(cal.time))
            } else {
                Toast.makeText(this, "You have to choose a date before today", Toast.LENGTH_SHORT).show()
            }
        }

        DatePickerDialog(this@SearchActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
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

    private fun getType(tag: String) {
        type_tv.setText("")
        when (tag) {
            "tag_house" -> {
                if (type == "house") {
                    type = ""
                    house_tv.setBackgroundResource(R.color.white)
                } else {
                    type = "house"
                    //CHANGE COLOR TO SHOW CLICKED
                    house_tv.setBackgroundResource(R.drawable.rounded_corners)
                    apartement_tv.setBackgroundResource(R.color.white)
                    Log.e("Type ", type)
                }
            }
            "tag_apartement" -> {
                if (type == "apartement") {
                    type = ""
                    apartement_tv.setBackgroundResource(R.color.white)
                } else {
                    type = "apartement"
                    Log.e("Type ", type)
                    house_tv.setBackgroundResource(R.color.white)
                    apartement_tv.setBackgroundResource(R.drawable.rounded_corners)
                }
            }
            else -> {
            }
        }
    }


    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    private fun getQueryFromUI() {
        val manageSearch = SearchManager()
        manageSearch.getQueryFromUI(agent_et, type, getInfoFromCheckBox(), city_et, surface_min, surface_max, price_min, price_max, rooms_min, rooms_max, start_date_et,
                end_date_et, photos_min_et, photos_max_et, cb_sold)
        search(manageSearch.getQuery()!!, manageSearch.getArgs())
    }

    private fun search(query: String, arg: Array<String>) {
        viewModel!!.searchRealEstates(query, arg).observe(this, Observer<List<RealEstate>> { this.showResult(it) })
    }

    private fun showResult(realEstateList: List<RealEstate>) {
        println("result = " + realEstateList.size)
        if (realEstateList.isNotEmpty()) {
            println("result item = " + realEstateList[0].agent + realEstateList[0].sold + realEstateList[0].type)
        }
    }
}