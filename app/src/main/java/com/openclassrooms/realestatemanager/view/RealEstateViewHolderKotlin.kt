package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.models.RealEstate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_realestate.view.*

class RealEstateViewHolderKotlin(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    private var view: View = v

    fun updateWithItem(realEstateItem: RealEstate, context: Context) {
        val defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg"
        Picasso.get().load(defaultImg).into(view.photo_realestate)
        view.item_type.text = realEstateItem.type
        view.item_location.text = realEstateItem.location
        view.item_price.text = realEstateItem.price
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}