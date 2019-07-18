package com.openclassrooms.realestatemanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.models.NearbySearchObject;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;
import com.openclassrooms.realestatemanager.models.Result;
import com.openclassrooms.realestatemanager.network.NearbySearchStream;
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel;
import com.openclassrooms.realestatemanager.view.CircularRevealAnimation;
import com.openclassrooms.realestatemanager.view.PhotoPopUp;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CreateRealEstateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CountDownTimer timer = null;

    //PERMISSIONS AND REQUESTS
    private static final int RC_IMAGE_PERMS = 111;
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;

    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    // Uri of image selected by user
    private Uri uriImageSelected;
    private String currentPhotoPath;

    // For latLng
    private FetchUserLocation fetchUserLocation;

    //VIEW
    @BindView(R.id.photo_layout)
    RelativeLayout photo_layout;

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
    @BindView(R.id.spinner_bedrooms)
    Spinner spinnerBedrooms;

    @BindView(R.id.description)
    EditText description_tv;

    //LOCATION
    @BindView(R.id.geo_loc)
    ImageView geoLocation;
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
    private String realEstateId = "temporary";
    private List<Photo> photos = new ArrayList<>();
    private String type = "";
    private String price = "";
    private String description = "";
    private String surface = "";
    private String rooms = "";
    private String bathrooms = "";
    private String address = "";
    private boolean sold = false;
    private String startDate = "";
    private String endDate = "";
    private String agent = "";
    private List<Result> pointsOfInterest = new ArrayList<>();
    private String bedrooms;
    private LatLng latLng;

    //FOR SAVING
    private SharedPreferences pref;
    private RealEstateViewModel viewModel;

    private Disposable disposable;

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
        configViewModel();
        fetchUserLocation =
                new FetchUserLocation(this, address_tv);
        pref = this.getSharedPreferences("RealEstate", Context.MODE_PRIVATE);
    }

    // Configuring ViewModel
    private void configViewModel() {
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel.class);
    }

    /**
     * Animation that starts when user click ob Fab button in Main Activity
     *
     * @param savedInstanceState
     */

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
        CircularRevealAnimation.startAnimation(rootLayout);
        initToolbar();
    }

    /**
     * Initiate buttons and toolbar and spinners
     */
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
        spinnerBathrooms.setAdapter(adapterBathroom);
        spinnerBathrooms.setOnItemSelectedListener(this);

        //Bedrooms spinner
        ArrayAdapter<CharSequence> adapterBedrooms = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item);

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBedrooms.setAdapter(adapterBedrooms);
        spinnerBedrooms.setOnItemSelectedListener(this);
    }

    private void initClickableItems() {
        //Button to add
        btn_validate.setOnClickListener(v -> getInfoFromUI());
        //User chose which type of object
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

        //Address edit text
        address_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Give the user time to finish writing
                if (timer != null) {
                    timer.cancel();
                }
                timer = new CountDownTimer(1500, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        //do what you wish
                        getPointsOfInterest();
                    }
                }.start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Photo
        camera_ic.setOnClickListener(v -> takePhoto());

        //Geo latLng
        geoLocation.setOnClickListener(v -> getUserLocation());
    }

    /**
     * Get user latLng when clicking on geo latLng icon and update edit text with address
     */
    private void getUserLocation() {
        fetchUserLocation.checkLocationPermission();
    }


    /**
     * Handling permissions to take photo, pick photo from gallery and geo locate user
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions

        Log.e("Activity", "Permission to easyperm" + requestCode);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("onActivityResult", "Permission to easyperm" + requestCode);
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                Log.e("Photo", "capture requestcode");
                this.handleResponseTakePhoto(resultCode);
                break;
            case RC_CHOOSE_PHOTO:
                this.handleResponse(resultCode, data);
                break;
            case FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    Log.e("MainActivity", "Permission Granted");
                    fetchUserLocation.getDeviceLocation();
                } else {
                    Toast.makeText(this, "You need to grant permission to access your latLng", Toast.LENGTH_LONG).show();
                }
                fetchUserLocation.getDeviceLocation();
            }
            break;
        }
    }

    /**
     * Handle response after asking permission
     */
    private void handleResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) { //SUCCESS
            this.uriImageSelected = data.getData();
            addPhotoToList(uriImageSelected.toString());

        } else {
            Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
        }
    }

    private void addPhotoToList(String uri) {
        //Show pop up to get description of photo
        PhotoPopUp photoPopUp = new PhotoPopUp(this);
        photoPopUp.popUpDialog(uri);

        String text = pref.getString("Photo text", "");
//TODO ALIGN PHOTOS!
        //Save photo in list
        Photo photo = new Photo(null, realEstateId, uri, text);
        photos.add(photo);

        //Add imageview to show thumbnail
        ImageView imageView = new ImageView(this);
        imageView.setMaxWidth(50);
        imageView.setMaxHeight(50);
        imageView.setTag(uri);

        Picasso.get().load(uri).into(imageView);

        //Delete photo on click
        photo_layout.addView(imageView);
        List<Photo> remove = new ArrayList<>();

        imageView.setOnClickListener(v -> {
            for (Photo p : photos) {
                if (p.getUrl() == imageView.getTag()) {
                    Log.e("remove", "removed= " + photos.size() + imageView.getTag() + p.getUrl());
                    remove.add(p);
                }
            }
            photos.removeAll(remove);

            photo_layout.removeView(imageView);
            Log.e("remove", "removed= " + photos.size());
        });

        Log.e("Photo", photo.getUrl() + photo.getText() + text);
        Log.e("Photo", "photo list size" + photos.size());
        Toast.makeText(this, "Photo added", Toast.LENGTH_SHORT).show();
    }

    protected void handleResponseTakePhoto(int resultCode) {

        Log.e("Photo", "resuult code = " + resultCode);
        if (resultCode == RESULT_OK) {
            //use imageUri here to access the image
            currentPhotoPath = "file:///" + currentPhotoPath;
            addPhotoToList(currentPhotoPath);

            Log.e("URI", currentPhotoPath);
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
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


    /**
     * User clicking on camera icon to take new photo
     */
    private void takePhoto() {

        Log.e("Photo", "take photo void");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

                Log.e("Photo", "ERROR " + ex);
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {


                Log.e("Photo", "Photo file not null");
                Uri photoURI = FileProvider.getUriForFile(
                        getApplicationContext(), "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {


        Log.e("Photo", "Create image file");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        Log.e("Photo", currentPhotoPath);
        return image;
    }


    /**
     * Select item in spinner
     */

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        switch (parent.getId()) {
            case R.id.spinner_rooms:
                rooms = parent.getItemAtPosition(pos).toString();
                Log.e("Spinner", rooms);
                break;
            case R.id.spinner_bathrooms:
                bathrooms = parent.getItemAtPosition(pos).toString();
                Log.e("Spinner", bathrooms);
                break;
            case R.id.spinner_bedrooms:
                bedrooms = parent.getItemAtPosition(pos).toString();
                Log.e("Spinner", bedrooms);
                break;
            default:
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        rooms = "";
        bathrooms = "";
        bedrooms = "";
        Log.e("Spinner", rooms + bathrooms);
    }

    private void getType(String tag) {
        type_tv.setText("");
        switch (tag) {
            case "tag_house":
                type = "house";
                //CHANGE COLOR TO SHOW CLICKED
                house.setBackgroundResource(R.drawable.rounded_corners);
                apartement.setBackgroundResource(R.color.white);
                Log.e("Type ", type);
                break;
            case "tag_apartement":
                type = "apartement";
                Log.e("Type ", type);
                house.setBackgroundResource(R.color.white);
                apartement.setBackgroundResource(R.drawable.rounded_corners);
                break;
            default:
                break;

        }
    }

    /**
     * Collects all info about Real estate object when user click on validate to register the object in database
     */

    private void getInfoFromUI() {
        agent = agent_tv.getText().toString();
        price = price_tv.getText().toString();
        description = description_tv.getText().toString();
        surface = surface_tv.getText().toString();
        startDate = Utils.getTodayDate(Calendar.getInstance().getTime());

        Log.e("check if", "is empty " + agent.isEmpty());

        if (!agent.isEmpty() && !address.isEmpty()) {
            createRealEstate();
        } else {
            Toast.makeText(this, "You need to choose an agent and address for the object you want to create", Toast.LENGTH_SHORT).show();
        }
    }

    private void createRealEstate() {
        Double lat = latLng.latitude;
        String latitude = lat.toString();
        Double lon = latLng.longitude;
        String longitude = lon.toString();
        realEstateId = address + "_" + startDate;

        RealEstate realEstate = new RealEstate(realEstateId, type, price, latitude, longitude, description, surface, bedrooms,
                rooms, bathrooms, address, false, startDate, null, agent
        );

        viewModel.createRealEstate(realEstate);

        for (Photo photo : photos) {
            photo.setRealEstateId(realEstateId);
            viewModel.createPhoto(photo);
        }

        Toast.makeText(this, "Real estate added succesfully", Toast.LENGTH_SHORT).show();

    }

    public void getPointsOfInterest() {

        //Fetch nearby search results from the latLng, this will also check that the user has entered a valid address

        address = address_tv.getText().toString();
        latLng = Utils.getLatLngFromAddress(this, address);
        Log.e("Create", address);

        if (latLng != null) {
            String locationForSearch = Utils.setLocationString(latLng);

            disposable = NearbySearchStream.fetchNearbyPlacesStream(locationForSearch).subscribeWith(new DisposableObserver<NearbySearchObject>() {
                @Override
                public void onNext(NearbySearchObject nearbySearchObject) {
                    pointsOfInterest = nearbySearchObject.getResults();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Main", "Error fetching nearby places " + e);
                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            Toast.makeText(this, "Enter a valid address", Toast.LENGTH_SHORT).show();
        }
    }


    public void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }
}

