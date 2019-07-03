package com.openclassrooms.realestatemanager;

import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.fragments.DetailFragment;

public class DetailActivity extends BaseActivity {

    // 1 - Declare detail fragment
    private DetailFragment detailFragment;

    // Create static variable to identify Intent
    public static final String EXTRA_TAG = "com.openclassrooms.myfragmentapp.Controllers.Activities.DetailActivity.EXTRA_TAG";


    @Override
    public void onResume() {
        super.onResume();
        // 3 - Call update method here because we are sure that DetailFragment is visible
        this.updateDetailFragmentWithIntentTag();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 2 - Configure and show home fragment
        this.configureAndShowDetailFragment();
        setActionbar();
    }

    private void setActionbar() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
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

    // --------------
    // UPDATE UI
    // --------------

    // 2 - Update DetailFragment with tag passed from Intent
    private void updateDetailFragmentWithIntentTag(){
        // Get button's tag from intent
        long tag = getIntent().getIntExtra(EXTRA_TAG, 0);
        // Update DetailFragment's TextView
        detailFragment.updateDetails(tag);
    }
}