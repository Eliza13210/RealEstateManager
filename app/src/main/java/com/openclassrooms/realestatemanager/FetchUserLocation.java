package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.openclassrooms.realestatemanager.controllers.CreateActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FetchUserLocation {

    //For user latLng
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 147;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Context context;
    private EditText editText;
    private SharedPreferences pref;

    public FetchUserLocation(Context context, EditText editText) {
        this.context = context;
        this.editText = editText;
    }

    public void checkLocationPermission() {

        Log.e("fetch", "check permission");

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        /*
         * Request latLng permission, so that we can get the latLng of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this).context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getDeviceLocation();
            Log.e("Permission", "Granted");
        } else {
            ActivityCompat.requestPermissions((CreateActivity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Log.e("Permission", "Request permission");
        }
    }

    public void getDeviceLocation() {
        /*
         * Get the best and most recent latLng of the device, which may be null in rare
         * cases when a latLng is not available.
         */
        try {
            Task locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener((CreateActivity) context, new OnCompleteListener() {
                Location mLastKnownLocation;

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        // Set the map's camera position to the current latLng of the device.
                        mLastKnownLocation = (Location) task.getResult();

                        double mLatitude;
                        double mLongitude;

                        if (mLastKnownLocation != null) {
                            //Get the latitude and longitude
                            mLatitude = mLastKnownLocation.getLatitude();
                            mLongitude = mLastKnownLocation.getLongitude();

                            //Save latitude and longitude
                            pref = context.getSharedPreferences("RealEstateManager", Context.MODE_PRIVATE);
                            pref.edit().putString("CurrentLatitude", Double.toString(mLatitude)).apply();
                            pref.edit().putString("CurrentLongitude", Double.toString(mLongitude)).apply();

                            Log.e("fetchLoc", Double.toString(mLatitude) + " " + Double.toString(mLongitude));

                            getAddress(mLatitude, mLongitude);
                        } else {
                            Toast.makeText(context, "Error defining latLng", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

        } catch (
                SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getAddress(double latitude, double longitude) {

        String userAddress = "";

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                userAddress = address.getAddressLine(0);
            } else {

                Log.e("Geocoder adress ", "address.size less than 0");
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        //Save user address
        pref.edit().putString("CurrentAddress", userAddress).apply();
        Log.e("get loc", userAddress);

        updateUI(userAddress);
    }

    private void updateUI(String address) {
        editText.setText(address);
    }

}
