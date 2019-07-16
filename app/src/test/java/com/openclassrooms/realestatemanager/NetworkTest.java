package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.models.NearbySearchObject;
import com.openclassrooms.realestatemanager.network.NearbySearchStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class NetworkTest {

    @Before
    public void setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @AfterClass
    public static void tearDownClass() {
        RxAndroidPlugins.reset();
    }


    @Test
    public void getNearbySearchStream_shouldReturnObject() {
        //1 - Get the stream
        Observable<NearbySearchObject> observable = NearbySearchStream.fetchNearbyPlacesStream("-33.8670522,151.1957362"
        );
        //2 - Create a new TestObserver
        TestObserver<NearbySearchObject> testObserver = new TestObserver<>();
        //3 - Launch observable
        observable.subscribeWith(testObserver)
                .assertNoErrors() // 3.1 - Check if no errors
                .assertNoTimeout() // 3.2 - Check if no Timeout
                .awaitTerminalEvent(); // 3.3 - Await the stream terminated before continue

        // 4 - Get list of results fetched
        NearbySearchObject nearbySearchObject = testObserver.values().get(0);
        assertEquals("OK", nearbySearchObject.getStatus());
        assertTrue(nearbySearchObject.getResults() != null);
    }


}
