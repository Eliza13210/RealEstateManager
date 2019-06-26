package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstate

class RealEstateViewHolderKotlin(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //VIEW
    @BindView(R.id.photo_realestate)
    internal var photo: ImageView? = null
    @BindView(R.id.item_type)
    internal var type: TextView? = null
    @BindView(R.id.item_location)
    internal var location: TextView? = null
    @BindView(R.id.item_price)
    internal var price: TextView? = null

    fun updateWithItem(realEstateItem: RealEstate, context: Context) {

        val defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg"
        try {
            Glide.with(context)
                    .load(defaultImg)
                    //.into(ImageView.photo_realestate)
        } catch (e: Exception) {
            Glide.with(context)
                    .load(defaultImg)
                    //.into(photo)
        }

       // type.setText(realEstateItem.type)
       // location.setText(realEstateItem.location)
        //price.setText(realEstateItem.price)
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}