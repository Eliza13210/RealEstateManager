package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controllers.BaseActivityUIInformation
import com.openclassrooms.realestatemanager.controllers.CreateActivity
import com.openclassrooms.realestatemanager.controllers.DetailActivity
import com.openclassrooms.realestatemanager.controllers.EditActivity
import com.openclassrooms.realestatemanager.models.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_photo_detail.view.*

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    var listOfPhotos = arrayListOf<Photo>()
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        context = parent.context
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_photo_detail, parent, false))
    }

    override fun getItemCount() = this.listOfPhotos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val itemPhoto = listOfPhotos[position]
        holder.bindPhoto(itemPhoto, context!!, position)
    }

    class PhotoViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var photo: Photo? = null
        private var position: Int? = null
        //Declare callback
        private var callback: OnItemClickedListener? = null

        // Declare interface that will be implemented by any container activity
        interface OnItemClickedListener {
            fun onItemClick(id: Long?, position: Int?)
        }

        // Create callback to parent activity
        private fun createCallbackToParentActivity(context: Context) {
            try {
                //Parent activity will automatically subscribe to callback
                callback = context as OnItemClickedListener
            } catch (e: ClassCastException) {
                throw ClassCastException("$e must implement OnItemClickedListener")
            }

        }

        override fun onClick(v: View) {
            callback!!.onItemClick(photo!!.id, position)
        }

        companion object {
            private val PHOTO_KEY = "PHOTO"
        }

        fun bindPhoto(photo: Photo, context: Context, position: Int?) {
            this.position = position
            if(context is BaseActivityUIInformation) {
                createCallbackToParentActivity(context)
                view.setOnClickListener(this)
            }
            this.photo = photo
            Picasso.get().load(photo.url).into(view.photo_detail)
            view.text_detail.text = photo.text
        }
    }

    fun updateData(items: List<Photo>) {
        this.listOfPhotos = items as ArrayList<Photo>
        this.notifyDataSetChanged()
    }
}