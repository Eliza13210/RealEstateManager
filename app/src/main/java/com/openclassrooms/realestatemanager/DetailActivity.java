package com.openclassrooms.realestatemanager;

import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.fragments.DetailFragment;

public class DetailActivity extends BaseActivity {

    // 1 - Declare detail fragment
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 2 - Configure and show home fragment
        this.configureAndShowDetailFragment();
        setActionbar();
    }

    private void setActionbar(){
        BottomAppBar bottomAppBar=findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);
        bottomAppBar.getMenu().removeItem(R.id.app_bar_edit);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_action_edit_dark);
    }

    // --------------
    // FRAGMENTS
    // --------------

    private void configureAndShowDetailFragment() {
        // A - Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null) {
            // B - Create new fragment
            detailFragment = new DetailFragment();
            // C - Add it to FrameLayout container
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }
    }
}