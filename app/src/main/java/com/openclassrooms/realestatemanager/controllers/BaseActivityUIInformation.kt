package com.openclassrooms.realestatemanager.controllers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.FetchUserLocation
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.PhotoAdapter
import com.openclassrooms.realestatemanager.view.popUps.AddPhotoPopUp
import com.openclassrooms.realestatemanager.view.popUps.AddPoiPopUp
import com.openclassrooms.realestatemanager.view.popUps.PhotoPopUp
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.information_layout.*
import kotlinx.android.synthetic.main.room_details_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.type_details_layout.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivityUIInformation : AppCompatActivity(), AdapterView.OnItemSelectedListener, EasyPermissions.PermissionCallbacks,
        PhotoAdapter.PhotoViewHolder.OnItemClickedListener {

    // For latLng
    protected var fetchUserLocation: FetchUserLocation? = null
    protected var pref: SharedPreferences? = null

    //PERMISSIONS AND REQUESTS
    private val rcImagePerm = 100
    private val rcCaptureImageActivityPerm = 123
    private val rcChoosePhotoPerm = 400
    private val REQUEST_VIDEO_CAPTURE = 200

    // Uri of image selected by user
    private var uriImageSelected: Uri? = null
    private var currentPhotoPath: String? = null

    protected var timer: CountDownTimer? = null
    protected var viewModel: RealEstateViewModel? = null
    protected val photoAdapter: PhotoAdapter = PhotoAdapter()
    protected var realEstateId: Long = 0
    protected var realEstateExists = false

    // For creating real estate object
    protected var photos = ArrayList<Photo>()
    protected var type = ""
    protected var price = ""
    protected var description = ""
    protected var surface = ""
    protected var rooms = ""
    protected var bathrooms = ""
    protected var address = ""
    protected var city = ""
    protected var sold = "false"
    protected var startDate = ""
    protected var endDate = ""
    protected var agent = ""
    protected var listPoi: List<String> = ArrayList()
    protected var pointsOfInterest = ""
    protected var bedrooms = ""
    protected var latitude = ""
    protected var longitude = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())
        initAnimation(savedInstanceState)
        initToolbar()
        checkIfTablet()
        initRecyclerView()
        initViewModel()
        initButtons()
        initSpinner()
        initClickableItems()
    }

    abstract fun getLayoutView(): Int

    private fun checkIfTablet() {

        // WILL BE FALSE IF TABLET
        if (resources.getBoolean(R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun initAnimation(savedInstanceState: Bundle?) {
        val animate = Animation()
        animate.start(savedInstanceState, root_layout)
    }

    private fun initToolbar() {
        //Initiate toolbar to navigate back to main activity
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }

    private fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }
    }

    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    /**
     * Implemented in EditActivity and CreateActivity
     */
    abstract fun initButtons()

    private fun initSpinner() {
        //Rooms spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.rooms_array, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_rooms.adapter = adapter
        spinner_rooms.onItemSelectedListener = this

        //Bathrooms spinner
        val adapterBathroom = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item)

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_bathrooms.adapter = adapterBathroom
        spinner_bathrooms.onItemSelectedListener = this

        //Bedrooms spinner
        val adapterBedrooms = ArrayAdapter.createFromResource(this,
                R.array.bathrooms_array, android.R.layout.simple_spinner_item)

        adapterBathroom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_bedrooms.adapter = adapterBedrooms
        spinner_bedrooms.onItemSelectedListener = this
    }

    /**
     * Select item in spinner
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View?,
                                pos: Int, id: Long) {
        when (parent.id) {
            R.id.spinner_rooms -> {
                rooms = parent.getItemAtPosition(pos).toString()
            }
            R.id.spinner_bathrooms -> {
                bathrooms = parent.getItemAtPosition(pos).toString()
            }
            R.id.spinner_bedrooms -> {
                bedrooms = parent.getItemAtPosition(pos).toString()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        rooms = ""
        bathrooms = ""
        bedrooms = ""
    }

    private fun initClickableItems() {
        // CHOOSE TYPE
        house_tv.setOnClickListener { getType(getString(R.string.type_tag_house)) }
        apartement_tv.setOnClickListener { getType(getString(R.string.type_tag_appartement)) }

        //Edit text type
        type_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                house_tv.setBackgroundResource(R.color.white)
                apartement_tv.setBackgroundResource(R.color.white)
                type=type_tv.text.toString()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        //TAKE PHOTO
        ic_camera.setOnClickListener {
            if (hasCameraPermission()) {
                takePhoto()
            } else {
                // Ask for permission
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_camera_access),
                        rcCaptureImageActivityPerm,
                        Manifest.permission.CAMERA)
            }
        }
        //PICK IN GALLERY
        ic_gallery.setOnClickListener {
            if (hasReadExternalStoragePermission()) {
                pickPhoto()
            } else {
                // Ask for permission
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access),
                        rcImagePerm,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        //VIDEO
        ic_video.setOnClickListener {
            if (hasCameraPermission())
                takeVideo()
            else {
                // Ask for permission
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_camera_access),
                        REQUEST_VIDEO_CAPTURE,
                        Manifest.permission.CAMERA)
            }
        }
        //POINTS OF INTEREST
        ic_add_poi.setOnClickListener {
            addPoi()
        }
    }

    private fun addPoi() {
        val popUp = AddPoiPopUp(this, listPoi, poi_tv)
        popUp.popUpDialog()
    }

    private fun getType(tag: String) {
        type_tv.setText("")
        when (tag) {
            "tag_house" -> {
                if (type == "house") {
                    type = ""
                    house_tv.setBackgroundResource(R.color.white)
                    apartement_tv.setBackgroundResource(R.color.white)
                } else {
                    type = "house"
                    house_tv.setBackgroundResource(R.drawable.rounded_corners)
                    apartement_tv.setBackgroundResource(R.color.white)
                }
            }
            "tag_apartement" -> {
                if (type == "flat") {
                    type = ""
                    house_tv.setBackgroundResource(R.color.white)
                    apartement_tv.setBackgroundResource(R.color.white)
                } else {
                    type = "flat"
                    house_tv.setBackgroundResource(R.color.white)
                    apartement_tv.setBackgroundResource(R.drawable.rounded_corners)
                }
            }
        }
    }

    private fun pickPhoto() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, rcChoosePhotoPerm)
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(this, "Error $ex", Toast.LENGTH_SHORT).show()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                        applicationContext, "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, rcCaptureImageActivityPerm)
            }
        }
    }

    private fun takeVideo() {
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (takeVideoIntent.resolveActivity(packageManager) != null) {

            var videoFile: File? = null
            try {
                videoFile = createVideoFile()
            } catch (ex: IOException) {
                Toast.makeText(this, "Error $ex", Toast.LENGTH_SHORT).show()
            }
            // Continue only if the File was successfully created
            if (videoFile != null) {
                val videoURI = FileProvider.getUriForFile(
                        applicationContext, "com.openclassrooms.realestatemanager.fileprovider",
                        videoFile)
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }
    }

    /** Used in Take photo to create a file name to save the photo when camera intent launched */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val imageFileName = "MP4_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val video = File.createTempFile(
                imageFileName, /* prefix */
                ".mp4", /* suffix */
                storageDir      /* directory */
        )
        currentPhotoPath = video.absolutePath
        return video
    }


    /**PERMISSIONS*/
    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasReadExternalStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
        } else if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            rcCaptureImageActivityPerm -> {
                this.takePhoto()
            }
            rcImagePerm -> {
                this.pickPhoto()
            }
            FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                fetchUserLocation?.getDeviceLocation()
            }
            REQUEST_VIDEO_CAPTURE -> {
                this.takeVideo()
            }
        }
    }

    /**
     * Handle response after launching start activity for result intents
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            rcCaptureImageActivityPerm -> {
                this.handleResponseTakePhoto(resultCode)
            }
            rcChoosePhotoPerm -> {
                this.handleResponsePickPhoto(resultCode, data)
            }
            REQUEST_VIDEO_CAPTURE -> {
                this.handleResponseTakeVideo(resultCode)
            }
        }
    }

    /** Get the selected photo uri from gallery
     * */
    private fun handleResponsePickPhoto(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            this.uriImageSelected = data!!.data
            addPhotoToList(uriImageSelected.toString(), "photo")
        } else {
            Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
        }
    }

    /** get the uri for the photo the user took and save it as a photo in the list*/
    private fun handleResponseTakePhoto(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            //use imageUri here to access the image
            currentPhotoPath = "file:///$currentPhotoPath"
            addPhotoToList(currentPhotoPath!!, "photo")
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.photo_not_taken), Toast.LENGTH_SHORT).show()
        }
    }

    /** get the uri for the video */
    private fun handleResponseTakeVideo(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            currentPhotoPath = "file:///$currentPhotoPath"
            addPhotoToList(currentPhotoPath!!, "video")
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.no_video_saved), Toast.LENGTH_SHORT).show()
        }
    }

    /** Start pop up to add description to photo/video, and then add the photo/video object to list */
    private fun addPhotoToList(uri: String, type: String) {
        val photoPopUp = AddPhotoPopUp(this)
        photoPopUp.popUpDialog(uri, type, photos, photoAdapter)
    }

    /**
     * Handle click on photo in recycler view
     */
    override fun onItemClick(id: Long?, position: Int?) {
        val photoPopup = PhotoPopUp(this)
        var photo: Photo? = null
        if (id != null) {
            for (p in photos) {
                if (p.id == id)
                    photo = p
            }
        } else {
            photo = photos[position!!]
        }
        photoPopup.popUp(photo!!.url, photos, photo.type!!, photoAdapter, position)
    }

    abstract fun getInfoFromUI()

    abstract fun createRealEstate()
}