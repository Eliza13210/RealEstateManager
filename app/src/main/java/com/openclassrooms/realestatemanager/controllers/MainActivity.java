package com.openclassrooms.realestatemanager.controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.MainFragment;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RealEstateViewHolder.OnItemClickedListener,
        NavigationView.OnNavigationItemSelectedListener {

    // Create static variable to identify Intent
    public static final String EXTRA_TAG = "com.openclassrooms.myfragmentapp.Controllers.Activities.DetailActivity.EXTRA_TAG";

    private DetailFragment detailFragment;
    private MainFragment mainFragment;
    private long tag = -1;
    private Boolean tablet = false;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_nav_view)
    NavigationView navigationView;

    @Override
    public void onResume() {
        super.onResume();
        // Call update method here because we are sure that DetailFragment is visible
        this.updateDetailFragmentWithIntentTag();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.configureAndShowDetailFragment();
        checkIfTablet();
        this.showMainFragment();
    }

    private void checkIfTablet() {
        // WILL BE FALSE IF TABLET
        //IF PHONE SHOW BOTTOM APP BAR WITH FAB
        if (getResources().getBoolean(R.bool.portrait_only)) {
            Log.e("main", "phone");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setActionbarPhone();
        }
        //IF TABLET, SHOW TOOLBAR
        if (findViewById(R.id.frame_layout_detail) != null) {
            Log.e("main", "tablet");
            tablet = true;
            setActionbarTablet();
        }
    }

    private void setActionbarPhone() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);
        configureDrawerLayout(bottomAppBar, null);

        fab.setImageResource(R.drawable.ic_action_add_dark);
        fab.setOnClickListener(v -> createRealEstate());
    }

    //  Start create activity when click on fab if phone
    private void createRealEstate() {
        startActivity(new Intent(this, CreateActivity.class));
    }

    private void setActionbarTablet() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        configureDrawerLayout(null, toolbar);

        fab.setImageResource(R.drawable.ic_action_edit_dark);
        fab.setOnClickListener(v -> edit());
    }

    //Start edit activity when click on fab if tablet
    private void edit() {
        if (tag != -1) {
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra(EXTRA_TAG, tag);

            Log.e("main start edit", "tag =" +tag);
            startActivity(i);
        } else {
            Toast.makeText(this, R.string.choose_item_to_edit, Toast.LENGTH_SHORT).show();
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

    //FOR TABLET ONLY - SHOW REAL ESTATE IN FRAGMENT IF STARTED FROM MAP ACTIVITY
    private void updateDetailFragmentWithIntentTag() {
        if (detailFragment != null) {
            // Get marker tag from intent if it comes from map activity
            Intent intent = getIntent();
            if (intent.hasExtra(EXTRA_TAG)) {
                long tagFromMap = getIntent().getLongExtra(EXTRA_TAG, 0);
                // Update DetailFragment
                detailFragment.updateDetails(tagFromMap);
            }
        }
    }

    // --------------
    // CallBack
    // Handle click in list view to show real estate in detail fragment
    // --------------
    @Override
    public void onItemClick(long id) {
        tag = id;
        Log.e("main list clicked", "tag =" +tag);
        // Check if phone, then open DetailActivity
        if (detailFragment == null) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_TAG, id);
            startActivity(i);
        } else {
            //If tablet
            Log.e("main update details", "tag =" + id);
            detailFragment.updateDetails(id);
        }
    }


    /**
     * Handle click in action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu, menu);

        // REMOVE CREATE ITEM FROM TOOLBAR IF PHONE
        if (!tablet) {
            menu.removeItem(R.id.app_bar_add);
            supportInvalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.app_bar_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return true;

            case R.id.app_bar_add:
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(BottomAppBar bottomAppBar, Toolbar toolbar) {
        ActionBarDrawerToggle toggle;
        //TABLET
        if (bottomAppBar == null) {
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        } else {
            //PHONE
            toggle = new ActionBarDrawerToggle(this, drawerLayout, bottomAppBar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
        }
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        initDrawerHeader();
    }

    private void initDrawerHeader() {
        //Inflate header layout
        navigationView.inflateHeaderView(R.layout.drawer_header);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_map:
                //Start Map view
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
            case R.id.action_euro:
                mainFragment.convertCurrency("euro");
                break;
            case R.id.action_dollar:
                mainFragment.convertCurrency("dollar");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
