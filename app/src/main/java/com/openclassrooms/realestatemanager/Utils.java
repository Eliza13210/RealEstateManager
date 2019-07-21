package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.openclassrooms.realestatemanager.models.NearbySearchObject;
import com.openclassrooms.realestatemanager.models.Result;
import com.openclassrooms.realestatemanager.network.NearbySearchStream;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros * 1.12);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @return
     */
    public static String getTodayDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     *
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    /**
     * Get latitude and longitude object from a latLng address
     *
     * @param context
     * @param strAddress
     * @return
     */

    public static LatLng getLatLngFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address.size() > 0) {
                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(context, "You must enter a valid address", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (IOException ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

        return p1;
    }

    /**
     * Get adress from a latitude and longitude object
     *
     * @param latLng
     * @return
     */

    public static String setLocationString(LatLng latLng) {
        //Build latLng string to fetch nearby restaurants
        StringBuilder builder = new StringBuilder();
        builder.append(latLng.latitude);
        builder.append(",");
        builder.append(latLng.longitude);

        return builder.toString();
    }
}
