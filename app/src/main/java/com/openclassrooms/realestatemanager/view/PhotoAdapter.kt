package com.openclassrooms.realestatemanager.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_photo_detail.view.*

class PhotoAdapter() : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    var listOfPhotos = arrayListOf<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_photo_detail, parent, false))
    }

    override fun getItemCount() = this.listOfPhotos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val itemPhoto = listOfPhotos[position]
        holder.bindPhoto(itemPhoto)
    }

    class PhotoViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var photo: Photo? = null


        init {
            v.setOnClickListener(this)
        }


        override fun onClick(v: View) {
            Log.d("RecyclerView", "CLICK!")
        }

        companion object {
            private val PHOTO_KEY = "PHOTO"
        }

        fun bindPhoto(photo: Photo) {
            this.photo = photo
            Picasso.get().load(photo.url).into(view.photo_detail);
            view.text_detail.text = photo.text
        }
    }

    fun updateData(items: List<Photo>) {
        this.listOfPhotos = items as ArrayList<Photo>
        this.notifyDataSetChanged()
    }
}