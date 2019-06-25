package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class UtilsTest {

    private Date date;
    private Context context;

    @Before
    public void setUp() {
        date = Mockito.mock(Date.class);
        context= Mockito.mock(Context.class);
        ConnectivityManager connectivityManager=Mockito.mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);

        NetworkInfo networkInfo=Mockito.mock(NetworkInfo.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
    }

    @Test
    public void convertEuroToDollar(){

        int value = Utils.convertEuroToDollar(500);
        int expectedValue = 560;

        assertEquals(expectedValue, value);
    }

    @Test
    public void getTodayDate(){
        String value = Utils.getTodayDate(date);
        String expectedValue="01/01/1970";

        assertEquals(expectedValue, value);
    }

    @Test
    public void checkInternetConnexion(){
        boolean value=Utils.isInternetAvailable(context);
        boolean expectedValue=true;

        assertEquals(expectedValue,value);
    }
}