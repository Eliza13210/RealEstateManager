package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.RealEstate
import com.openclassrooms.realestatemanager.view.RealEstateAdapter
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder
import kotlinx.android.synthetic.main.fragment_list_view.*


class SearchResultFragment : Fragment(),  RealEstateViewHolder.OnItemClickedListener  {
    private var realEstateAdapter: RealEstateAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.fragment_list_view, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.configureRecyclerView()
    }

    private fun configureRecyclerView() {
        realEstateAdapter= RealEstateAdapter(context!!)
        recyclerview_list_real_estates.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = realEstateAdapter
        }
    }

    fun updateItemsList(realEstateList: List<RealEstate>) {
        this.realEstateAdapter!!.updateData(realEstateList)
    }

    override fun onItemClick(id: Long) {}
}
