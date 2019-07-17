package com.openclassrooms.realestatemanager.view;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.RealEstate;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    //Declare callback
    private OnItemClickedListener callback;

    // Declare interface that will be implemented by any container activity
    public interface OnItemClickedListener {
        void onItemClick(String id);
    }

    // Create callback to parent activity
    private void createCallbackToParentActivity(Context context) {
        try {
            //Parent activity will automatically subscribe to callback
            callback = (OnItemClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() + " must implement OnItemClickedListener");
        }
    }

    public RealEstateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(RealEstate realEstateItem, Context context) {
        createCallbackToParentActivity(context);
        String defaultImg = "https://images.victorianplumbing.co.uk/images/Legend-Traditional-Bathroom-Suite_P.jpg";
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
        location.setText(realEstateItem.getAddress());
        price.setText(realEstateItem.getPrice());
        itemView.setOnClickListener(v -> {
            // Spread the click to the parent activity
            Log.e("holder", realEstateItem.getId());
            callback.onItemClick(realEstateItem.getId());
        });
    }
}
