package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Photo
import com.squareup.picasso.Picasso
import java.util.*

class PhotoPopUp(var context: Context) {

    fun popUp(uri: String, photos: ArrayList<Photo>, photo: Photo, photoAdapter: PhotoAdapter, position: Int?) {
        val builder = AlertDialog.Builder(context, R.style.Theme_AppCompat_Light_Dialog_Alert)

        // set the custom layout
        val customLayout = LayoutInflater.from(context).inflate(R.layout.popup_map, null)
        builder.setView(customLayout)
        val image = customLayout.findViewById<ImageView>(R.id.popup_map_image)
        Picasso.get().load(uri).into(image)

        Log.e("uri", uri)

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.delete_photo)) { dialog, i ->

            //When user validate
            photos[position!!].text="Deleted"

            photoAdapter.updateData(photos)

            Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()

            dialog.dismiss()

        }

        builder.setNegativeButton(context.getString(R.string.close)
        ) { dialog, i -> dialog.cancel() }
        val dialog = builder.create()
        Objects.requireNonNull<Window>(dialog.window).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }
}