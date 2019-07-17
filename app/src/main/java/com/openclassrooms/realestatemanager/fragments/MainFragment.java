package com.openclassrooms.realestatemanager.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
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

    // 1 - FOR DATA
    private RealEstateViewModel realEstateViewModel;

    @BindView(R.id.recyclerview_list_real_estates)
    RecyclerView recyclerView;

    //For recyclerview
    private RealEstateAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout of MainFragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        this.configureRecyclerview();
        this.configureViewModel();
        this.getItems();
        return view;

    }

    private void configureRecyclerview() {
        // Create adapter passing the list of news
        this.adapter = new RealEstateAdapter(Objects.requireNonNull(getContext()));
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


    // Configuring ViewModel
    private void configureViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(getActivity());
        this.realEstateViewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel.class);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureRecyclerview();
    }

    // - Get all real estates
    private void getItems() {
        this.realEstateViewModel.getAllItems().observe(this, this::updateItemsList);
    }

    private void updateItemsList(List<RealEstate> realEstateList) {
        this.adapter.updateData(realEstateList);
    }

    @Override
    public void onItemClick(String id) {

    }


    /**
     // 3 - Delete an item
     private void deleteItem(Item item){
     this.itemViewModel.deleteItem(item.getId());
     }

     // 3 - Update an item (selected or not)
     private void updateItem(Item item){
     item.setSelected(!item.getSelected());
     this.itemViewModel.updateItem(item);
     }
     */
}


