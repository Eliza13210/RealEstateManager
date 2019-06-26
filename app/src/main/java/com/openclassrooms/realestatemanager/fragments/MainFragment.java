package com.openclassrooms.realestatemanager.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.view.RealEstateAdapter;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements RealEstateViewHolder.OnItemClickedListener{

    @BindView(R.id.recyclerview_list_real_estates)
    RecyclerView recyclerView;

    //For recyclerview
    private RealEstateAdapter adapter;
    private List<RealEstate> listOfRealEstates;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout of MainFragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        //Set onClickListener to button "SHOW ME DETAILS"
        //  result.findViewById(R.id.fragment_main_button).setOnClickListener(this);
        configureRecyclerview();
        return view;

    }

    private void configureRecyclerview() {

        // Reset list
        this.listOfRealEstates = new ArrayList<>();

        //TEST
        RealEstate realEstate = new RealEstate
                ("https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg", "Flat", "Center", "21000");
        listOfRealEstates.add(realEstate);

        RealEstate realEstate2 = new RealEstate
                ("https://s3.amazonaws.com/images.seroundtable.com/google-restraurant-menus-1499686091.jpg", "Flat", "Center", "21000");

        listOfRealEstates.add(realEstate2);


        // Create adapter passing the list of news
        this.adapter = new RealEstateAdapter(this.listOfRealEstates, getContext());
        // Attach the adapter to the recycler view to populate items
        this.recyclerView.setAdapter(this.adapter);
        //Check if portrait orientation
        int orientation = getResources().getConfiguration().orientation;
        if (!getResources().getBoolean(R.bool.portrait_only) && orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Set layout manager to position the items horizontally if portrait
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            // Set layout manager to position the items
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureRecyclerview();
    }

    @Override
    public void onItemClick(View view) {

    }
}


