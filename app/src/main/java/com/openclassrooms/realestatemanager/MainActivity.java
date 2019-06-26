package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.openclassrooms.realestatemanager.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.fragments.MainFragment;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

public class MainActivity extends AppCompatActivity implements RealEstateViewHolder.OnItemClickedListener {

    private DetailFragment detailFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkIfTablet();
        this.showMainFragment();
        this.configureAndShowDetailFragment();
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
    public void onItemClick(View view) {
        // Check if detail fragment is not created or if not visible, then open DetailActivity
        Log.e("Main", "Clicked");
        if (detailFragment == null || !detailFragment.isVisible()) {
            startActivity(new Intent(this, DetailActivity.class));
        }
        //If tablet, show details in detail fragment
    }
}
