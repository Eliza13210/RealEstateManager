package com.openclassrooms.realestatemanager.controllers

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.SearchManager
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.poi_details_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.Gson
import com.openclassrooms.realestatemanager.R


class SearchActivity : AppCompatActivity() {

    private var viewModel: RealEstateViewModel? = null

    private var type = ""
    private var sold = false
    private var updatedList = ArrayList<RealEstate>()

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
        toolbar.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
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
        }

        //CHECKBOX SOLD
        cb_sold.setOnClickListener {
            sold = cb_sold.isChecked
        }

        //DATE PICKER
        start_date_et.setOnClickListener { startDatePicker(start_date_et) }
        end_date_et.setOnClickListener { startDatePicker(end_date_et) }
    }

    private fun startDatePicker(editText: TextView) {
        val cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            Log.e("Search", " " + cal.get(Calendar.DAY_OF_MONTH) + cal.get(Calendar.MONTH) + cal.get(Calendar.YEAR))

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
                }
            }
            "tag_apartement" -> {
                if (type == "flat") {
                    type = ""
                    apartement_tv.setBackgroundResource(R.color.white)
                } else {
                    type = "flat"
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
        manageSearch.getQueryFromUI(agent_et, type, getInfoFromCheckBox(), city_et, surface_min, surface_max, price_min, price_max,
                rooms_min, rooms_max, start_date_et,
                end_date_et, cb_sold)
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


    private fun showResult(realEstateList: List<RealEstate>) {

        println("result = " + realEstateList.size)

        //START RESULT ACTIVITY WITH THE LIST OF REAL ESTATES
        val gson = Gson()
        val jsonList = gson.toJson(realEstateList)
        Log.e("searchA", jsonList)
        val intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("SearchResultList", jsonList)
        startActivity(intent)
    }
}
