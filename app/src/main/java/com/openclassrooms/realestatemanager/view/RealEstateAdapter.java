package com.openclassrooms.realestatemanager.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.List;

public class RealEstateAdapter extends RecyclerView.Adapter<RealEstateViewHolder> {

    //FOR DATA
    private List<RealEstate> realEstateList;
    private Context context;

    public RealEstateAdapter(List<RealEstate> realEstateList) {
        this.realEstateList = realEstateList;
    }

    @NonNull
    @Override
    public RealEstateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_realestate, viewGroup, false);
        return new RealEstateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateViewHolder realEstateViewHolder, int i) {
        realEstateViewHolder.updateWithItem(this.realEstateList.get(i), context);
    }

    @Override
    public int getItemCount() {
        return this.realEstateList.size();
    }
}
