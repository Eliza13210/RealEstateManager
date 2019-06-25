package com.openclassrooms.realestatemanager.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.RealEstate;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RealEstateViewHolder extends RecyclerView.ViewHolder {

    //VIEW
    @BindView(R.id.photo_realestate)
    ImageView photo;
    @BindView(R.id.item_type)
    TextView type;
    @BindView(R.id.item_location)
    TextView location;
    @BindView(R.id.item_price)
    TextView price;


    public RealEstateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(RealEstate realEstateItem, Context context) {
        String defaultImg = "https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg";
        try {
            Glide.with(context)
                    .load(defaultImg)
                    .into(photo);
        } catch (Exception e) {
            Glide.with(context)
                    .load(defaultImg)
                    .into(photo);
        }

        type.setText(realEstateItem.getType());
        location.setText(realEstateItem.getLocation());
        price.setText(realEstateItem.getPrice());
    }

}
