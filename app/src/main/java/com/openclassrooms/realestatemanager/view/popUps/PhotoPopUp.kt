package com.openclassrooms.realestatemanager.view.popUps

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.*
import com.openclassrooms.realestatemanager.controllers.fragments.BaseFragment
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.popup_photo.view.*
import java.util.*


/**
 * Pop up dialog showing the photo or video.
 * If the popup is opened in edit or create activity, the user can also delete the photo/video
 */

class PhotoPopUp(var context: Context) {

    fun popUp(uri: String, photos: ArrayList<Photo>, type: String, photoAdapter: PhotoAdapter, position: Int?) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialog)

        // set the custom layout
        val customLayout = LayoutInflater.from(context).inflate(R.layout.popup_photo, null)
        builder.setView(customLayout)
        val imageView = customLayout.findViewById<ImageView>(R.id.popup_image)
        customLayout.popup_edit_tv.visibility = View.INVISIBLE

        //IF PHOTO
        if (type == "photo") {
            Picasso.get().load(uri).into(imageView)
        }
        //IF VIDEO
        if (type == "video") {
            val videoUri = Uri.parse(uri)
            val videoView = customLayout.findViewById<VideoView>(R.id.popup_video)
            val mc = MediaController(context)
            mc.setAnchorView(videoView)
            mc.setMediaPlayer(videoView)

            videoView.setMediaController(mc)
            videoView.setVideoURI(videoUri)

            videoView.visibility = View.VISIBLE

            imageView.visibility = View.INVISIBLE
            videoView.start()
        }

        if (context is CreateActivity || context is EditActivity) {
            // Set up the buttons
            builder.setPositiveButton(context.getString(R.string.delete_photo)) { dialog, _ ->

                //When user validate
                photos[position!!].text = "Deleted"
                photoAdapter.updateData(photos, null)

                Toast.makeText(context, context.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        builder.setNegativeButton(context.getString(R.string.close)
        ) { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()

        dialog.show()

    }
}