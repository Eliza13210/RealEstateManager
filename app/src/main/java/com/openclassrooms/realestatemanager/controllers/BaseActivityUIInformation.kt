package com.openclassrooms.realestatemanager.controllers

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
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
import com.openclassrooms.realestatemanager.models.Result
import com.openclassrooms.realestatemanager.realEstateList.RealEstateViewModel
import com.openclassrooms.realestatemanager.view.*
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.information_layout.*
import kotlinx.android.synthetic.main.toolbar.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseActivityUIInformation : AppCompatActivity(), AdapterView.OnItemSelectedListener, EasyPermissions.PermissionCallbacks,
        PhotoAdapter.PhotoViewHolder.OnItemClickedListener  {

    // For latLng
    protected var fetchUserLocation: FetchUserLocation? = null
    protected var pref: SharedPreferences? = null

    //PERMISSIONS AND REQUESTS
    private val RC_IMAGE_PERM = 100
    private val RC_CAPTURE_IMAGE_ACTIVITY_PERM = 123
    private val RC_CHOOSE_PHOTO_PERM = 400

    // Uri of image selected by user
    protected var uriImageSelected: Uri? = null
    private var currentPhotoPath: String? = null

    protected var timer: CountDownTimer? = null


    protected var viewModel: RealEstateViewModel? = null
    protected val photoAdapter: PhotoAdapter = PhotoAdapter()
    protected var realEstateId: Long = 0

    // For creating real estate object
    protected var photos = ArrayList<Photo>()
    protected var type = ""
    protected var price = ""
    protected var description = ""
    protected var surface = ""
    protected var rooms = ""
    protected var bathrooms = ""
    protected var address = ""
    protected var sold = false
    protected var startDate = ""
    protected var endDate = ""
    protected var agent = ""
    protected var listPoi: List<Result> = ArrayList()
    protected var bedrooms: String? = null
    protected var latitude: String = ""
    protected var longitude: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutView())

        initAnimation(savedInstanceState)
        initViewModel()
        initRecyclerView()
        initButtons()
        initSpinner()
        initClickableItems()

    }

    abstract fun getLayoutView(): Int

    private fun initAnimation(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {

            root_layout.visibility = View.INVISIBLE

            val viewTreeObserver = root_layout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        circularRevealActivity()

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            root_layout.viewTreeObserver.removeGlobalOnLayoutListener(this)
                        } else {
                            root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                })
            }
        }
    }

    private fun circularRevealActivity() {
        CircularRevealAnimation.startAnimation(root_layout)
        initToolbar()
    }

    /**
     * Initiate buttons and toolbar and spinners
     */
    private fun initToolbar() {
        //Initiate toolbar to navigate back
        this.setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v -> startActivity(Intent(this, MainActivity::class.java)) }
    }


    // RecyclerView node initialized here
    private fun initRecyclerView() {
        recyclerview_photos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            // set the custom adapter to the RecyclerView
            adapter = photoAdapter
        }
    }

    private fun initViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.viewModel = ViewModelProviders.of(this, mViewModelFactory).get(RealEstateViewModel::class.java)
    }

    abstract fun initButtons()

    private fun initSpinner() {
        //Rooms spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.rooms_array, R.layout.spinner_item)

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

    override fun onItemSelected(parent: AdapterView<*>, view: View,
                                pos: Int, id: Long) {

        when (parent.id) {
            R.id.spinner_rooms -> {
                rooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", rooms)
            }
            R.id.spinner_bathrooms -> {
                bathrooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", bathrooms)
            }
            R.id.spinner_bedrooms -> {
                bedrooms = parent.getItemAtPosition(pos).toString()
                Log.e("Spinner", bedrooms)
            }
            else -> {
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        rooms = ""
        bathrooms = ""
        bedrooms = ""
        Log.e("Spinner", rooms + bathrooms)
    }

    private fun initClickableItems() {

        // CHOOSE TYPE
        house_tv.setOnClickListener { getType("tag_house") }
        apartement_tv.setOnClickListener { getType("tag_apartement") }

        //Edit text type
        type_tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                house_tv.setBackgroundResource(R.color.white)
                apartement_tv.setBackgroundResource(R.color.white)
            }

            override fun afterTextChanged(s: Editable) {
                type = type_tv.text.toString()
                Log.e("type", type)
            }
        })

        //TAKE PHOTO OR CHOOSE FROM GALLERY
        ic_camera.setOnClickListener {
            if (hasCameraPermission()) {
                takePhoto()
            } else {
                // Ask for one permission
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_camera_access),
                        RC_CAPTURE_IMAGE_ACTIVITY_PERM,
                        Manifest.permission.CAMERA)
            }
        }
        ic_gallery.setOnClickListener {
            if (hasReadExternalStoragePermission()) {
                pickPhoto()
            } else {
                // Ask for permission

                Log.e("pick photo", "ask for permission")
                EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access),
                        RC_IMAGE_PERM,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun getType(tag: String) {
        type_tv.setText("")
        when (tag) {
            "tag_house" -> {
                type = "house"
                //CHANGE COLOR TO SHOW CLICKED
                house_tv.setBackgroundResource(R.drawable.rounded_corners)
                apartement_tv.setBackgroundResource(R.color.white)
                Log.e("Type ", type)
            }
            "tag_apartement" -> {
                type = "apartement"
                Log.e("Type ", type)
                house_tv.setBackgroundResource(R.color.white)
                apartement_tv.setBackgroundResource(R.drawable.rounded_corners)
            }
            else -> {
            }
        }
    }


    private fun pickPhoto() {
        // Launch an "Selection Image" Activity
        Log.e("pick photo", "has permission")
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RC_CHOOSE_PHOTO_PERM)
    }


    private fun takePhoto() {
        Log.e("Photo", "take photo void")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {

                Log.e("Photo", "ERROR $ex")
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {

                Log.e("Photo", "Photo file not null")
                val photoURI = FileProvider.getUriForFile(
                        applicationContext, "com.openclassrooms.realestatemanager.fileprovider",
                        photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, RC_CAPTURE_IMAGE_ACTIVITY_PERM)
            }
        }
    }


    /**PHOTO PERMISSIONS*/

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
    }

    private fun hasReadExternalStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions

        Log.e("Activity", "Permission to easyperm$requestCode")
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
        Log.e("Activity", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        when (requestCode) {
            RC_CAPTURE_IMAGE_ACTIVITY_PERM -> {
                Log.e("Photo", "capture requestcode")
                this.takePhoto()
            }
            RC_IMAGE_PERM -> {
                Log.e("on act result", "asked")
                this.pickPhoto()
            }
            FetchUserLocation.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                fetchUserLocation?.getDeviceLocation()
            }
        }
    }

    /**
     * Handle response after launching start activity for result intents
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("onActivityResult", "Permission to easyperm$requestCode")
        when (requestCode) {
            RC_CAPTURE_IMAGE_ACTIVITY_PERM -> {
                Log.e("Photo", "capture requestcode")
                this.handleResponseTakePhoto(resultCode)
            }
            RC_CHOOSE_PHOTO_PERM -> {
                Log.e("on act result", "asked")
                this.handleResponsePickPhoto(resultCode, data)
            }
        }
    }

    /** Get the selected photo uri from gallery
     * */

    private fun handleResponsePickPhoto(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            //SUCCESS

            Log.e("handle resp pick photo", "result code ok")
            this.uriImageSelected = data!!.data
            addPhotoToList(uriImageSelected.toString())
        } else {
            Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show()
        }
    }

    /** Start pop up to add description to photo, and then add the photo object to list */
    private fun addPhotoToList(uri: String) {
        //Show pop up to get description of photo
        val photoPopUp = AddPhotoPopUp(this)
        photoPopUp.popUpDialog(uri, photos, photoAdapter)
 }


    /** get the uri for the photo the user took and save it as a photo in the list*/
    private fun handleResponseTakePhoto(resultCode: Int) {

        Log.e("Photo", "resuult code = $resultCode")
        if (resultCode == Activity.RESULT_OK) {
            //use imageUri here to access the image
            currentPhotoPath = "file:///$currentPhotoPath"
            addPhotoToList(currentPhotoPath!!)

            Log.e("URI", currentPhotoPath)
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show()
        }

    }

    /** Used in Take photo to create a file name to save the photo when camera intent launched */

    @Throws(IOException::class)
    private fun createImageFile(): File {

        Log.e("Photo", "Create image file")
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath

        Log.e("Photo", currentPhotoPath)
        return image
    }

    /**
     * Handle click on photo in recyclerview
     */

    override fun onItemClick(id: Long?, position: Int?) {

       val photoPopup=PhotoPopUp(this)
        var photo: Photo?=null
        if(id!=null) {
            for (p in photos) {
                if (p.id == id)
                    photo = p
            }
        }else {
            photo= photos[position!!]
        }
        photoPopup.popUp(photo!!.url, photos, photo, photoAdapter, position)
    }
    /**
     * Select item in spinner
     */
    abstract fun getInfoFromUI()

    abstract fun createRealEstate()
}