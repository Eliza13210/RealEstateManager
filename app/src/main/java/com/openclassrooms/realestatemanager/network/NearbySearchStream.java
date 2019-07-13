package com.openclassrooms.realestatemanager.network;

import com.openclassrooms.realestatemanager.models.NearbySearchObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NearbySearchStream {
    public static Observable<NearbySearchObject> fetchNearbyPlacesStream(String location) {
        NearbySearchService service = NearbySearchService.retrofit.create(NearbySearchService.class);
        return service.getRestaurant(NearbySearchService.NEARBY, location, NearbySearchService.RADIUS,
                NearbySearchService.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(60, TimeUnit.SECONDS);
    }
}
