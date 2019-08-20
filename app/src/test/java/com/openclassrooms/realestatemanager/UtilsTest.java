package com.openclassrooms.realestatemanager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.models.Result;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;


public class UtilsTest {

    private Date date;
    private Context context;

    @Before
    public void setUp() {
        date = Mockito.mock(Date.class);
        context = Mockito.mock(Context.class);
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);

        NetworkInfo networkInfo = Mockito.mock(NetworkInfo.class);
        when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        when(networkInfo.isConnectedOrConnecting()).thenReturn(true);
    }

    @Test
    public void convertEuroToDollar() {

        int value = Utils.convertEuroToDollar(500);
        int expectedValue = 560;

        assertEquals(expectedValue, value);
    }


    @Test
    public void convertDollarToEuro() {

        int value = Utils.convertDollarToEuro(500);
        int expectedValue = 560;

        assertEquals(expectedValue, value);
    }

    @Test
    public void getTodayDate() {
        String value = Utils.getTodayDate(date);
        String expectedValue = "01/01/1970";

        assertEquals(expectedValue, value);
    }

    @Test
    public void checkInternetConnexion() {
        boolean value = Utils.isInternetAvailable(context);
        boolean expectedValue = true;

        assertEquals(expectedValue, value);
    }

    @Test
    public void setLocationString() {
        LatLng latLng = new LatLng(0.0, 0.0);
        String result = Utils.setLocationString(latLng);

        String expected = "0.0,0.0";
        assertEquals(expected, result);

    }

    @Test
    public void checkBeforeTodayWhenFalse() {

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, 4);
        cal.add(Calendar.DAY_OF_MONTH, 10);

        boolean isBefore = Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        assertFalse(isBefore);
    }

    @Test
    public void checkBeforeTodayWhenTrue() {

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MONTH, -1);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        boolean isBefore = Utils.checkBeforeToday(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        assertTrue(isBefore);
    }

    @Test
    public void checkIfResultTypeMatches_WhenFalse_ReturnEmptyList(){

        List<Result> resultList=new ArrayList<>();
        Result result=new Result();
        List<String> typesList=new ArrayList<>();

        typesList.add("car_wash");
        result.setTypes(typesList);
        resultList.add(result);

        List<Result> updatedList=PointOfInterestsMatcher.checkIfPointOfInterest(resultList);

        assertTrue(updatedList.isEmpty());
    }

    @Test
    public void checkIfResultTypeMatches_WhenTrue_ReturnUpdatedList(){

        List<Result> resultList=new ArrayList<>();
        Result result=new Result();
        Result result_two=new Result();
        List<String> typesList=new ArrayList<>();

        typesList.add("school");
        result.setTypes(typesList);
        resultList.add(result);

        typesList.add("doctor");
        typesList.add("bus_station");
        result_two.setTypes(typesList);
        resultList.add(result_two);

        List<Result> updatedList=PointOfInterestsMatcher.checkIfPointOfInterest(resultList);

        assertEquals(2, updatedList.size());

    }
}