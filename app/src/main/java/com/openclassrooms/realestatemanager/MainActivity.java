package com.openclassrooms.realestatemanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.fragments.MainFragment;
import com.openclassrooms.realestatemanager.view.RealEstateViewHolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity implements RealEstateViewHolder.OnItemClickedListener {

    private DetailFragment detailFragment;
    private String tag;
    private Boolean tablet = false;

    @BindView(R.id.fab)
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkIfTablet();
        this.showMainFragment();
        this.configureAndShowDetailFragment();

        if (findViewById(R.id.frame_layout_detail) != null) {
            tablet = true;
            setActionbarTablet();
        } else {
            setActionbarPhone();
        }
    }

    // 3 - Create a new item
    private void createRealEstate() {
        startActivity(new Intent(this, CreateActivity.class));
    }


    private void setActionbarPhone() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);

        fab.setImageResource(R.drawable.ic_action_add_dark);
        fab.setOnClickListener(v -> createRealEstate());
    }

    private void setActionbarTablet() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        fab.setImageResource(R.drawable.ic_action_edit_dark);
        fab.setOnClickListener(v -> edit());
    }

    private void edit() {
        if (tag != null) {
            Log.e("Detail", "edit clicked with tag " + tag);
            Intent i = new Intent(this, EditActivity.class);
            i.putExtra("RealEstateId", tag);
            startActivity(i);
        } else {
            Toast.makeText(this, "Choose an item to edit first", Toast.LENGTH_SHORT).show();
        }
    }


    private void checkIfTablet() {
        Log.e("main", "portrait only " + getResources().getBoolean(R.bool.portrait_only));
        // WILL BE FALSE IF TABLET
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showMainFragment() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_main);
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
    public void onItemClick(long id) {
        tag = String.valueOf(id);
        // Check if detail fragment is not created or if not visible, then open DetailActivity
        Log.e("Main", "Clicked" + id);
        if (detailFragment == null) {
            Log.e("Detail", "det frag null start act");
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_TAG, id);
            startActivity(i);
        } else {
            Log.e("Detail", "det frag ej null start act");
            detailFragment.updateDetails(id);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu, menu);

        // REMOVE CREATE ITEM FROM TOOLBAR IF TABLET MODE
        if (!tablet) {
            menu.removeItem(R.id.app_bar_add);
            supportInvalidateOptionsMenu();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.app_bar_search:
                println("Fav menu item is clicked!");
                return true;

            case R.id.app_bar_add:
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
