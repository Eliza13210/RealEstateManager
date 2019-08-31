package com.openclassrooms.realestatemanager.view.popUps

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.openclassrooms.realestatemanager.R
import java.util.*

/**
 * Popup dialog allowing user to add points of interests to real estate object. The points of interests will be shown in the text view
 * in the activity
 */

class AddPoiPopUp(var context: Context, private var listOfPoi: List<String>, private var poi_tv: TextView) {

    fun popUpDialog() {
        val builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)
        builder.setTitle(context.getString(R.string.points_of_interest))

        // set the custom layout
        val customLayout = LayoutInflater.from(context).inflate(R.layout.popup_poi, null)
        builder.setView(customLayout)

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.update)) { dialog, _ ->

            //When user validate
            listOfPoi = getInfoFromCheckBox(customLayout)
            val sb = StringBuilder()
            for (string in listOfPoi) {
                sb.append(string)
                sb.append(", ")
            }
            poi_tv.text = sb.toString()

            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        Objects.requireNonNull(dialog.window).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        dialog.show()
    }

    private fun getInfoFromCheckBox(customLayout: View): List<String> {

        val result = ArrayList<String>()
        val checkBoxList = ArrayList<CheckBox>()
        checkBoxList.add(customLayout.findViewById(R.id.cb_airport))
        checkBoxList.add(customLayout.findViewById(R.id.cb_bus_station))
        checkBoxList.add(customLayout.findViewById(R.id.cb_city_hall))
        checkBoxList.add(customLayout.findViewById(R.id.cb_doctor))
        checkBoxList.add(customLayout.findViewById(R.id.cb_hospital))
        checkBoxList.add(customLayout.findViewById(R.id.cb_parc))
        checkBoxList.add(customLayout.findViewById(R.id.cb_pharmacy))
        checkBoxList.add(customLayout.findViewById(R.id.cb_restaurant))
        checkBoxList.add(customLayout.findViewById(R.id.cb_school))
        checkBoxList.add(customLayout.findViewById(R.id.cb_subway))
        checkBoxList.add(customLayout.findViewById(R.id.cb_supermarket))
        checkBoxList.add(customLayout.findViewById(R.id.cb_train))
        for (box in checkBoxList) {
            if (box.isChecked) {
                result.add(box.text.toString())
            }
        }
        return result
    }
}