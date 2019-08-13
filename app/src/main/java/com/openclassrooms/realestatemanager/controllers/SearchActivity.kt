package com.openclassrooms.realestatemanager.controllers

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.models.Result
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.room_details_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.type_details_layout.*
import java.util.ArrayList

class SearchActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


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
        initSpinner()
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

    private fun initSpinner() {
        //Rooms spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.rooms_array, R.layout.spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_rooms.adapter = adapter
        spinner_rooms.onItemSelectedListener = this

        //Bathrooms spinner
        val adapterBathroom = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item)

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_bathrooms.adapter = adapterBathroom
        spinner_bathrooms.onItemSelectedListener = this

        //Bedrooms spinner
        val adapterBedrooms = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item)

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_bedrooms.adapter = adapterBedrooms
        spinner_bedrooms.onItemSelectedListener = this

        //TODO PHOTO SPINNER
    }

    /**
     * Select item in spinner
     */

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {

        when (parent.id) {
            R.id.spinner_rooms -> {
                rooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", rooms)
            }
            R.id.spinner_bathrooms -> {
                bathrooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", bathrooms)
            }
            R.id.spinner_bedrooms -> {
                bedrooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", bedrooms)
            }
            else -> {
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        rooms = ""
        bathrooms = ""
        bedrooms = ""
        Log.e("Spinner", rooms + bathrooms)
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

        btn_search.setOnClickListener {
            getQueryFromUI()
            Log.e("search", "clicked button")
        }

        cb_sold.setOnClickListener {
            sold = cb_sold.isChecked
        }
    }

    private fun getType(tag: String) {
        type_tv.setText("")
        when (tag) {
            "tag_house" -> {
                type = "house"
                //CHANGE COLOR TO SHOW CLICKED
                house_tv.setBackgroundResource(R.drawable.rounded_corners)
                apartement_tv.setBackgroundResource(R.color.white)
                Log.e("Type ", type)
            }
            "tag_apartement" -> {
                type = "apartement"
                Log.e("Type ", type)
                house_tv.setBackgroundResource(R.color.white)
                apartement_tv.setBackgroundResource(R.drawable.rounded_corners)
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
        var sb: StringBuilder = java.lang.StringBuilder()

        sb.append("SELECT * FROM RealEstate WHERE ")
        sb.append("agent = ? AND ")
        sb.append("sold = ? AND ")
        sb.append("type =? ;")

        query = sb.toString()

        Log.e("search", query)

        //ARRAY
        var array: Array<String> = arrayOf(agent_et.text.toString(), sold.toString(), type)
        Log.e("search", array[0])

        search(query!!, array)
        //TODO GET INFO FROM UI

    }

    private fun search(query: String, arg: Array<String>) {
        viewModel!!.searchRealEstates(query, arg).observe(this, Observer<List<RealEstate>> { this.showResult(it) })
    }

    private fun showResult(realEstateList: List<RealEstate>) {
        println("result = " + realEstateList.size)
        if (realEstateList.size > 0) {
            println("result item = " + realEstateList[0].agent + realEstateList[0].sold + realEstateList[0].type)
        }
    }
}