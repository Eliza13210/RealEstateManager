package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Photo
import kotlinx.android.synthetic.main.recyclerview_photo_detail.view.*

/**
 * Adapter and view holder used for showing photos in recycler view, in detail fragment, create and edit activity
 */
class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private var listOfPhotos = arrayListOf<Photo>()
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

    fun updateData(items: List<Photo>) {
        this.listOfPhotos = items as ArrayList<Photo>
        this.notifyDataSetChanged()
    }

    /**
     * View holder
     */
    class PhotoViewHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

        private var view: View = v
        private var photo: Photo? = null
        private var position: Int? = null

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

        fun bindPhoto(photo: Photo, context: Context, position: Int?) {
            this.position = position

            createCallbackToParentActivity(context)
            view.setOnClickListener(this)
            this.photo = photo

            Glide.with(context)
                    .asBitmap()
                    .load(photo.url)
                    .into(view.photo_detail)
            view.text_detail.text = photo.text
        }
    }
}

