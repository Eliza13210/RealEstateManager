package com.openclassrooms.realestatemanager

import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

/**
 * Creates Query and arguments for raw query search in activity
 */

class SearchManager {

    //RETURN VALUES
    private var query: String? = null
    private val bindArgs = arrayListOf<String>()

    fun getQueryFromUI(agent_et: EditText?, type: String?, checkbox: List<String>?, city_et: EditText?,
                       surface_min: EditText?, surface_max: EditText?,
                       price_min: EditText?, price_max: EditText?,
                       rooms_min: EditText?, rooms_max: EditText?,
                       bedrooms_min: EditText?, bedrooms_max: EditText?,
                       bathrooms_min: EditText?, bathrooms_max: EditText?,
                       start_date_before: TextView?, start_date_after: TextView?,
                       sold_date_from: TextView?, sold_date_before: TextView?,
                       cb_sold: CheckBox?) {

        val sb: StringBuilder = java.lang.StringBuilder()
        var sold = cb_sold!!.isChecked

        sb.append("SELECT * FROM RealEstate WHERE ")

        //AGENT
        if (agent_et!!.text.isNotEmpty()) {
            sb.append("agent = ? AND ")
            bindArgs.add(Utils.removeSpacesAndAccentLetters(agent_et.text.toString().toLowerCase()))
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
        if (city_et!!.text.isNotEmpty()) {
            sb.append("city LIKE ? AND ")
            bindArgs.add("%" + Utils.removeSpacesAndAccentLetters(city_et.text.toString().toLowerCase()) + "%")
        }

        val minimum = Utils.convertToIntAndMultiply(surface_min?.text.toString())
        val maximum = Utils.convertToIntAndMultiply(surface_max?.text.toString())

        //SURFACE
        if (surface_min!!.text.isNotEmpty() && surface_max!!.text.isNotEmpty()) {
            sb.append("surface BETWEEN ? AND ? AND ")
            bindArgs.add(minimum.toString())
            bindArgs.add(maximum.toString())
        } else if (surface_min.text.isNotEmpty()) {
            sb.append("surface >= ? AND ")
            bindArgs.add(minimum.toString())
        } else if (surface_max!!.text.isNotEmpty()) {
            sb.append("surface <= ? AND ")
            bindArgs.add(maximum.toString())
        }

        //PRICE
        if (price_min!!.text.isNotEmpty() && price_max!!.text.isNotEmpty()) {
            sb.append("price BETWEEN ? AND ? AND ")
            bindArgs.add(price_min.text.toString())
            bindArgs.add(price_max.text.toString())
        } else if (price_min.text.isNotEmpty()) {
            sb.append("price >= ? AND ")
            bindArgs.add(price_min.text.toString())
        } else if (price_max!!.text.isNotEmpty()) {
            sb.append("price <= ? AND ")
            bindArgs.add(price_max.text.toString())
        }

        //ROOMS
        if (rooms_min!!.text.isNotEmpty() && rooms_max!!.text.isNotEmpty()) {
            sb.append("rooms BETWEEN ? AND ? AND ")
            bindArgs.add(rooms_min.text.toString())
            bindArgs.add(rooms_max.text.toString())
        } else if (rooms_min.text.isNotEmpty()) {
            sb.append("rooms >= ? AND ")
            bindArgs.add(rooms_min.text.toString())
        } else if (rooms_max!!.text.isNotEmpty()) {
            sb.append("rooms <= ? AND ")
            bindArgs.add(rooms_max.text.toString())
        }

        //BEDROOMS
        if (bedrooms_min!!.text.isNotEmpty() && bedrooms_max!!.text.isNotEmpty()) {
            sb.append("bedrooms BETWEEN ? AND ? AND ")
            bindArgs.add(bedrooms_min.text.toString())
            bindArgs.add(bedrooms_max.text.toString())
        } else if (bedrooms_min.text.isNotEmpty()) {
            sb.append("bedrooms >= ? AND ")
            bindArgs.add(bedrooms_min.text.toString())
        } else if (bedrooms_max!!.text.isNotEmpty()) {
            sb.append("bedrooms <= ? AND ")
            bindArgs.add(bedrooms_max.text.toString())
        }

        //BATHROOMS
        if (bathrooms_min!!.text.isNotEmpty() && bathrooms_max!!.text.isNotEmpty()) {
            sb.append("bathrooms BETWEEN ? AND ? AND ")
            bindArgs.add(bathrooms_min.text.toString())
            bindArgs.add(bathrooms_max.text.toString())
        } else if (bathrooms_min.text.isNotEmpty()) {
            sb.append("bathrooms >= ? AND ")
            bindArgs.add(bathrooms_min.text.toString())
        } else if (bathrooms_max!!.text.isNotEmpty()) {
            sb.append("bathrooms <= ? AND ")
            bindArgs.add(bathrooms_max.text.toString())
        }

        //SOLD DATES
        //SOLD AFTER
        val soldDateFrom: String?

        if (sold_date_from!!.text == "Click to pick a date" || sold_date_from.text!!.isNotEmpty()) {
            soldDateFrom = ""
        } else {
            sold = true
            soldDateFrom = sold_date_from.text.toString()
        }

        //SOLD BEFORE
        val soldDateBefore: String?
        if (sold_date_before!!.text == "Click to pick a date" || sold_date_before.text!!.isNotEmpty()) {
            soldDateBefore = ""
        } else {
            sold = true
            soldDateBefore = sold_date_before.text.toString()
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
        val beforeDate = if (start_date_before!!.text == "Click to pick a date") {
            ""
        } else {
            start_date_before.text
        }
        val afterDate = if (start_date_after!!.text == "Click to pick a date") {
            ""
        } else {
            start_date_after.text
        }

        if (beforeDate.isNotEmpty() && afterDate.isNotEmpty()) {
            sb.append("startDate >= ? AND startDate <= ? AND ")
            bindArgs.add(beforeDate.toString())
            bindArgs.add(afterDate.toString())

        } else if (afterDate.isNotEmpty()) {
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