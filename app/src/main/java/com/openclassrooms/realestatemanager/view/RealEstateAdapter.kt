package com.openclassrooms.realestatemanager.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstate

class RealEstateAdapter(val realEstateList: List<RealEstate>, val context: Context) : RecyclerView.Adapter<RealEstateViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RealEstateViewHolder {
        return RealEstateViewHolder(LayoutInflater.from(context).inflate(R.layout.recyclerview_item_realestate, p0, false))
    }

    override fun getItemCount() = this.realEstateList.size

    override fun onBindViewHolder(realEstateViewHolder: RealEstateViewHolder, i: Int) {

        realEstateViewHolder?.updateWithItem(this.realEstateList[i], context)
    }
}