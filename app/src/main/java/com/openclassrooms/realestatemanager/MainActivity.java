package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.openclassrooms.realestatemanager.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.fragments.MainFragment;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import androidx.appcompat.app.AppCompatActivity;

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


    private void setActionbar(){
        BottomAppBar bottomAppBar=findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        /**override fun onOptionsItemSelected(item: MenuItem?): Boolean {
            when (item!!.itemId) {
                R.id.app_bar_fav -> toast("Fav menu item is clicked!")
                R.id.app_bar_search -> toast("Search menu item is clicked!")
                R.id.app_bar_settings -> toast("Settings item is clicked!")
            }

            return true
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * override fun onCreateOptionsMenu(menu: Menu): Boolean {
         *     val inflater = menuInflater
         *     inflater.inflate(R.menu.bottomappbar_menu, menu)
         *     return true
         * }
         */
// Inflate the menu; the search view
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu, menu);
        return true;
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
        } else{
            setActionbar();
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
