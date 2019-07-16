package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.models.RealEstate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_realestate.view.*

class RealEstateViewHolderKotlin(private val view: View) : RecyclerView.ViewHolder(view) {

    //Declare callback

    fun updateWithItem(realEstateItem: RealEstate, context: Context) {
        val defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg"
        Picasso.get().load(defaultImg).into(view.photo_realestate)
        view.item_type.text = realEstateItem.type
        view.item_location.text = realEstateItem.address
        view.item_price.text = realEstateItem.price

    }
    }


    // Create callback to parent activity
  //  private fun createCallbackToParentActivity(context: Context) =context as OnItemClickedListener
            //Parent activity will automatically subscribe to callback



