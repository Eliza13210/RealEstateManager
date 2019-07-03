package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.fragments.MainFragment;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends BaseActivity implements RealEstateViewHolder.OnItemClickedListener {

    private DetailFragment detailFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIfTablet();
        this.showMainFragment();
        this.configureAndShowDetailFragment();

        if (findViewById(R.id.frame_layout_detail) != null) {
            setActionbarTablet();
        }else {
            setActionbarPhone();
        }
    }


    private void setActionbarPhone() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_add_dark);
    }

    private void setActionbarTablet() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
    }


    private void checkIfTablet() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showMainFragment() {
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_main);
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_main, mainFragment)
                    .commit();
        }
    }

    private void configureAndShowDetailFragment() {
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        //only add DetailFragment in Tablet mode (If found frame_layout_detail)
        if (detailFragment == null && findViewById(R.id.frame_layout_detail) != null) {
            detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }

    }

    // --------------
    // CallBack
    // --------------
    @Override
    public void onItemClick(Long id) {

        // Check if detail fragment is not created or if not visible, then open DetailActivity
        Log.e("Main", "Clicked" + id);
        if (detailFragment == null || !detailFragment.isVisible()) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_TAG, id);
            startActivity(i);
        } else{
            detailFragment.updateDetails(id);
        }
    }
}
