package com.openclassrooms.realestatemanager.controllers.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel;
import com.openclassrooms.realestatemanager.view.RealEstateAdapter;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements RealEstateViewHolder.OnItemClickedListener {

    private RealEstateAdapter adapter;
    private RealEstateViewModel realEstateViewModel;

    @BindView(R.id.recyclerview_list_real_estates)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerView();
        this.configureViewModel();
        this.getItems();
        return view;
    }

    private void configureRecyclerView() {
        this.adapter = new RealEstateAdapter(Objects.requireNonNull(getContext()));
        this.recyclerView.setAdapter(this.adapter);
        //Check if portrait orientation
        int orientation = getResources().getConfiguration().orientation;
        if (!getResources().getBoolean(R.bool.portrait_only) && orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Set layout manager to position the items horizontally if portrait in tablet
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        } else {
            // Set layout manager to position the items
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(getActivity());
        this.realEstateViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel.class);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureRecyclerView();
    }

    // - Get all real estates
    private void getItems() {
        this.realEstateViewModel.fetchAllRealEstates().observe(this, this::updateItemsList);
    }

    private void updateItemsList(List<RealEstate> realEstateList) {
        this.adapter.updateData(realEstateList);
    }

    public void convertCurrency(String currency) {
        this.adapter.convertCurrency(currency);
    }

    @Override
    public void onItemClick(long id) {
    }
}


