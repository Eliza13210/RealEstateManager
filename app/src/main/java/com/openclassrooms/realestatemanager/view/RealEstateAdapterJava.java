package com.openclassrooms.realestatemanager.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.RealEstate;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RealEstateAdapterJava extends RecyclerView.Adapter<RealEstateViewHolderKotlin> {

    //FOR DATA
    private List<RealEstate> realEstateList;
    private Context context;

    public RealEstateAdapterJava(List<RealEstate> realEstateList) {
        this.realEstateList = realEstateList;
    }

    @NonNull
    @Override
    public RealEstateViewHolderKotlin onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // CREATE VIEW HOLDER AND INFLATING ITS XML LAYOUT
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_item_realestate, viewGroup, false);
        return new RealEstateViewHolderKotlin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RealEstateViewHolderKotlin realEstateViewHolder, int i) {
        realEstateViewHolder.updateWithItem(this.realEstateList.get(i), context);
    }

    @Override
    public int getItemCount() {
        return this.realEstateList.size();
    }
}
