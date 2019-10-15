package com.openclassrooms.realestatemanager

import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText

/**
 * Creates Query and arguments for raw query search in activity
 */

class SearchManager {

    //RETURN VALUES
    private var query: String? = null
    private val bindArgs = arrayListOf<String>()

    fun getQueryFromUI(agent_et: TextInputEditText?, type: String?, checkbox: List<String>?, city_et: EditText?,
                       surface_min: String?, surface_max: String?,
                       price_min: String?, price_max: String?,
                       rooms_min: String?, rooms_max: String?,
                       bedrooms_min: String?, bedrooms_max: String?,
                       bathrooms_min: String?, bathrooms_max: String?,
                       start_date_before: TextInputEditText?, start_date_after: TextInputEditText?,
                       sold_date_from: TextInputEditText?, sold_date_before: TextInputEditText?,
                       isSold: Boolean?) {

        val sb: StringBuilder = java.lang.StringBuilder()
        var sold = isSold

        sb.append("SELECT * FROM RealEstate WHERE ")

        //AGENT
        if (agent_et!!.text!!.isNotEmpty()) {
            sb.append("agent LIKE ? AND ")
            bindArgs.add("%" + Utils.removeSpacesAndAccentLetters(agent_et.text.toString())+ "%")
        }

        //TYPE
        if (type!!.isNotEmpty()) {
            sb.append("type LIKE ? AND ")
            bindArgs.add("%" + Utils.removeSpacesAndAccentLetters(type.toLowerCase()) + "%")
        }

        //POI
        if (checkbox!!.isNotEmpty()) {
            for (box in checkbox) {
                sb.append("pointsOfInterest LIKE ? AND ")
                bindArgs.add("%$box%")
            }
        }

        //CITY
        if (!city_et?.text.isNullOrEmpty()) {
            sb.append("city LIKE ? AND ")
            bindArgs.add("%" + Utils.removeSpacesAndAccentLetters(city_et?.text.toString().toLowerCase()) + "%")
        }

        //SURFACE
        if (surface_min!! != "0" && surface_max!! !="1000") {
            sb.append("surface BETWEEN ? AND ? AND ")
            bindArgs.add(surface_min)
            bindArgs.add(surface_max)
        } else if (surface_min != "0") {
            sb.append("surface >= ? AND ")
            bindArgs.add(surface_min)
        } else if (surface_max!! != "1000") {
            sb.append("surface <= ? AND ")
            bindArgs.add(surface_max)
        }

        //PRICE
        if (price_min!! != "0" && price_max!! != "10000000") {
            sb.append("price BETWEEN ? AND ? AND ")
            bindArgs.add(price_min)
            bindArgs.add(price_max)
        } else if (price_min != "0") {
            sb.append("price >= ? AND ")
            bindArgs.add(price_min)
        } else if (price_max!! != "10000000") {
            sb.append("price <= ? AND ")
            bindArgs.add(price_max)
        }

        //ROOMS
        if (rooms_min!! != "0" && rooms_max!! != "50") {
            sb.append("rooms BETWEEN ? AND ? AND ")
            bindArgs.add(rooms_min)
            bindArgs.add(rooms_max)
        } else if (rooms_min != "0") {
            sb.append("rooms >= ? AND ")
            bindArgs.add(rooms_min)
        } else if (rooms_max!! != "50") {
            sb.append("rooms <= ? AND ")
            bindArgs.add(rooms_max)
        }

        //BEDROOMS
        if (bedrooms_min!! != "0" && bedrooms_max!! != "20") {
            sb.append("bedrooms BETWEEN ? AND ? AND ")
            bindArgs.add(bedrooms_min)
            bindArgs.add(bedrooms_max)
        } else if (bedrooms_min != "0") {
            sb.append("bedrooms >= ? AND ")
            bindArgs.add(bedrooms_min)
        } else if (bedrooms_max!! != "20") {
            sb.append("bedrooms <= ? AND ")
            bindArgs.add(bedrooms_max)
        }

        //BATHROOMS
        if (bathrooms_min!! != "0" && bathrooms_max!! != "20") {
            sb.append("bathrooms BETWEEN ? AND ? AND ")
            bindArgs.add(bathrooms_min)
            bindArgs.add(bathrooms_max)
        } else if (bathrooms_min != "0") {
            sb.append("bathrooms >= ? AND ")
            bindArgs.add(bathrooms_min)
        } else if (bathrooms_max!! != "20") {
            sb.append("bathrooms <= ? AND ")
            bindArgs.add(bathrooms_max)
        }

        //SOLD DATES

        //SOLD AFTER
        val soldDateFrom: String?

        if (sold_date_from?.text.isNullOrEmpty()) {
            soldDateFrom = ""
        } else {
            sold = true
            soldDateFrom = sold_date_from?.text.toString()
        }

        //SOLD BEFORE
        val soldDateBefore: String?
        if (sold_date_before?.text.isNullOrEmpty()) {
            soldDateBefore = ""
        } else {
            sold = true
            soldDateBefore = sold_date_before?.text.toString()
        }

        if (soldDateFrom.isNotEmpty() && soldDateBefore.isNotEmpty()) {
            sb.append("endDate >= ? AND endDate <= ? AND ")
            bindArgs.add(soldDateFrom.toString())
            bindArgs.add(soldDateBefore.toString())

        } else if (soldDateFrom.isNotEmpty()) {
            sb.append("endDate >= ? AND ")
            bindArgs.add(soldDateFrom.toString())
        } else if (soldDateBefore.isNotEmpty()) {
            sb.append("endDate <= ? AND ")
            bindArgs.add(soldDateBefore.toString())
        }

        //START DATES
        val beforeDate = if (start_date_before?.text.isNullOrEmpty()) {
            ""
        } else {
            start_date_before?.text
        }

        val afterDate = if (start_date_after?.text.isNullOrEmpty()) {
            ""
        } else {
            start_date_after?.text
        }

        if (beforeDate!!.isNotEmpty() && afterDate!!.isNotEmpty()) {
            sb.append("startDate >= ? AND startDate <= ? AND ")
            bindArgs.add(afterDate.toString())
            bindArgs.add(beforeDate.toString())

        } else if (afterDate!!.isNotEmpty()) {
            sb.append("startDate >= ? AND ")
            bindArgs.add(afterDate.toString())
        } else if (beforeDate.isNotEmpty()) {
            sb.append("startDate <= ? AND ")
            bindArgs.add(beforeDate.toString())
        }

        sb.append("sold = ? ")
        bindArgs.add(sold.toString())

        //END STRING
        sb.append(";")

        query = sb.toString()

    }

    fun getQuery(): String? {
        return query
    }

    fun getArgs(): Array<String> {
        return bindArgs.toTypedArray()
    }
}