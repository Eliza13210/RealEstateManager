package com.openclassrooms.realestatemanager.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.view.PhotoAdapter;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements PhotoAdapter.PhotoViewHolder.OnItemClickedListener {

    // Declare detail fragment
    private DetailFragment detailFragment;

    // Create static variable to identify Intent
    public static final String EXTRA_TAG = "com.openclassrooms.realestatemanager.controllers.DetailActivity.EXTRA_TAG";
    private long tag;

    @Override
    public void onResume() {
        super.onResume();
        // Call update method here because we are sure that DetailFragment is visible
        this.updateDetailFragmentWithIntentTag();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        this.configureAndShowDetailFragment();
        setActionbar();
    }

    private void setActionbar() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        this.setSupportActionBar(bottomAppBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        bottomAppBar.setNavigationOnClickListener(view -> startActivity(new Intent(DetailActivity.this, MainActivity.class)));
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_edit_white);
        fab.setOnClickListener(v -> edit());
    }

    private void edit() {
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra(EXTRA_TAG, tag);
        startActivity(i);
    }

    // --------------
    // FRAGMENT
    // --------------
    private void configureAndShowDetailFragment() {
        // Get FragmentManager (Support) and Try to find existing instance of fragment in FrameLayout container
        detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.frame_layout_detail);

        if (detailFragment == null) {
            detailFragment = new DetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout_detail, detailFragment)
                    .commit();
        }
    }

    // --------------
    // UPDATE UI
    // --------------
    private void updateDetailFragmentWithIntentTag() {
        // Get tag from intent
        tag = getIntent().getLongExtra(EXTRA_TAG, 0);
        detailFragment.updateDetails(tag);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.app_bar_search:
                startActivity(new Intent(DetailActivity.this, SearchActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(@Nullable Long id, @Nullable Integer position) {
        detailFragment.onItemClick(id, position);
    }
}