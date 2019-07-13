package com.openclassrooms.realestatemanager;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openclassrooms.realestatemanager.models.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateRealEstateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    //PERMISSIONS
    private static final int RC_IMAGE_PERMS = 123;
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    // Uri of image selected by user
    private Uri uriImageSelected;
    private static final int RC_CHOOSE_PHOTO = 200;

    String currentPhotoPath;

    @BindView(R.id.root_layout)
    FrameLayout rootLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.agent)
    EditText agent_tv;
    //TYPE
    @BindView(R.id.house)
    TextView house;
    @BindView(R.id.apartement)
    TextView apartement;
    @BindView(R.id.type)
    EditText type_tv;

    @BindView(R.id.surface)
    EditText surface_tv;
    @BindView(R.id.price)
    EditText price_tv;
    //SPINNERS
    @BindView(R.id.spinner_rooms)
    Spinner spinnerRooms;
    @BindView(R.id.spinner_bathrooms)
    Spinner spinnerBathrooms;

    @BindView(R.id.description)
    EditText description_tv;

    //LOCATION
    @BindView(R.id.geo_loc)
    ImageView geoLocation;
    @BindView(R.id.location)
    EditText location_tv;
    @BindView(R.id.address)
    EditText address_tv;

    //BUTTON
    @BindView(R.id.btn_send)
    Button btn_validate;

    @BindView(R.id.ic_camera)
    TextView camera_ic;
    @BindView(R.id.ic_gallery)
    TextView gallery_ic;

    // For creating real estate object
    private long realEstateId = 1;
    private List<Photo> photos = new ArrayList<>();
    private String type = "";
    private String price = "";
    private String location = "";
    private String description = "";
    private String surface = "";
    private String rooms = "";
    private String bathrooms = "";
    private String address = "";
    private boolean sold = false;
    private String startDate = "";
    private String endDate = "";
    private String agent = "";
    private String[] pointsOfInterest = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_create_real_estate);
        ButterKnife.bind(this);
        initAnimation(savedInstanceState);
        initToolbar();
        initSpinner();
        initClickableItems();
    }

    private void initAnimation(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        circularRevealActivity();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }
    }

    private void circularRevealActivity() {

        int cx = rootLayout.getWidth() / 2;
        int cy = rootLayout.getHeight() - 70;

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        }
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
        initToolbar();
    }

    protected void initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }


    private void initSpinner() {
        //Rooms spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rooms_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRooms.setAdapter(adapter);
        spinnerRooms.setOnItemSelectedListener(this);

        //Bathrooms spinner
        ArrayAdapter<CharSequence> adapterBathroom = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item);

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBathrooms.setAdapter(adapter);
        spinnerBathrooms.setOnItemSelectedListener(this);
    }

    private void initClickableItems() {
        //Button
        btn_validate.setOnClickListener(v -> getInfoFromUI());
        //Type
        house.setOnClickListener(v -> getType("tag_house"));
        apartement.setOnClickListener(v -> getType("tag_apartement"));

        //Edit text type
        type_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                house.setBackgroundResource(R.color.white);
                apartement.setBackgroundResource(R.color.white);

            }

            public void afterTextChanged(Editable s) {
                type = type_tv.getText().toString();
                Log.e("type", type);
            }
        });

        //Photo
        camera_ic.setOnClickListener(v -> takePhoto());

        //Geo location
        geoLocation.setOnClickListener(v -> getUserLocation());
    }

    private void getUserLocation() {
        //Fetch user location
        FetchUserLocation fetchUserLocation = new FetchUserLocation(this);
        fetchUserLocation.checkLocationPermission();
        SharedPreferences pref = this.getSharedPreferences("RealEstateManager", Context.MODE_PRIVATE);

        //Update UI
        String address = pref.getString("CurrentAddress", "Default");
        String locality = pref.getString("CurrentCity", "Default");
        address_tv.setText(address);
        location_tv.setText(locality);
    }


    /**
     * For picking photo from gallery
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                this.handleResponseTakePhoto(requestCode, resultCode, data);
                break;
            case RC_CHOOSE_PHOTO:
                this.handleResponse(requestCode, resultCode, data);
                break;
        }
    }

    // --------------------
    // ACTIONS
    // --------------------
    @OnClick(R.id.ic_gallery)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onClickAddFile() {
        this.chooseImageFromPhone();
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
        }
    }

    // --------------------
    // FILE MANAGEMENT
    // --------------------
    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(this, PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        // Launch an "Selection Image" Activity
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    // Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { //SUCCESS
            this.uriImageSelected = data.getData();
            Photo photo = new Photo(null, realEstateId, uriImageSelected.toString(), "");
            photos.add(photo);
        } else {
            Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Take new photo
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        getApplicationContext(), "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    protected void handleResponseTakePhoto(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //use imageUri here to access the image
            Bundle extras = data.getExtras();
            Log.e("URI", currentPhotoPath.toString());
            Photo photo = new Photo(null, realEstateId, currentPhotoPath, "");
            photos.add(photo);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
        }

    }

    /**
     * Select item in spinner
     */

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        switch (parent.getId()) {
            case R.id.spinner_rooms:
                rooms = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.spinner_bathrooms:
                bathrooms = parent.getItemAtPosition(pos).toString();
                break;
            default:
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        rooms = "";
        bathrooms = "";
    }

    private void getInfoFromUI() {
        agent = agent_tv.getText().toString();

        price = price_tv.getText().toString();
        location = location_tv.getText().toString();
        description = description_tv.getText().toString();
        surface = surface_tv.getText().toString();
        address = address_tv.getText().toString();
        startDate = Utils.getTodayDate(Calendar.getInstance().getTime());
        pointsOfInterest = Utils.getPointsOfInterest()
    }

    private void getType(String tag) {
        type_tv.setText("");
        switch (tag) {
            case "tag_house":
                type = "house";
                //CHANGE COLOR TO SHOW CLICKED
                house.setBackgroundResource(R.drawable.rounded_corners);
                apartement.setBackgroundResource(R.color.white);
                break;
            case "tag_apartement":
                type = "apartement";

                house.setBackgroundResource(R.color.white);
                apartement.setBackgroundResource(R.drawable.rounded_corners);
                break;
            default:
                break;

        }
    }
}

