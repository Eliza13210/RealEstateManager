package com.openclassrooms.realestatemanager;

import android.app.Activity;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    private Activity activity;
    private GoogleMap map;

    public FetchUserLocation(Context context, EditText editText, Activity activity, GoogleMap map) {
        this.map = map;
        this.context = context;
        this.editText = editText;
        this.activity = activity;
    }

    public void checkLocationPermission() {
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
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getDeviceLocation() {
        /*
         * Get the best and most recent latLng of the device, which may be null in rare
         * cases when a latLng is not available.
         */
        try {
            Log.e("fetch", "try to get location");
            Task locationResult = fusedLocationProviderClient.getLastLocation();

            locationResult.addOnCompleteListener(activity, new OnCompleteListener() {
                Location mLastKnownLocation;

                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {

                        // Set the map's camera position to the current latLng of the device.
                        mLastKnownLocation = (Location) task.getResult();
//                        Log.e("fetch", mLastKnownLocation.toString());
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

                            if (editText != null) {
                                getAddress(mLatitude, mLongitude);
                            } else if (map != null) {
                                showUserOnMap(mLatitude, mLongitude);
                            }
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
        String userCity = "";

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                userAddress = address.getAddressLine(0);
                userCity = address.getLocality().toLowerCase().replace("-", " ");
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        //Save user address
        pref.edit().putString("CurrentAddress", userAddress).putString("CurrentCity", userCity).apply();

        updateUIWithAddress(userAddress);
    }

    private void updateUIWithAddress(String address) {
        editText.setText(address);
    }

    private void showUserOnMap(Double latitude, Double longitude) {
        MapManager manager = new MapManager(context, null, map);
        LatLng latlng = new LatLng(latitude, longitude);
        manager.showUser(latlng);
    }

}
