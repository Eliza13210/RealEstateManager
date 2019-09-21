package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.models.Photo;
import com.openclassrooms.realestatemanager.models.RealEstate;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)

public class RealEstateDaoTest {


    // DATA SET FOR TEST
    private static long REALESTATE_ID = 1;
    private static RealEstate REALESTATE_DEMO = new RealEstate(REALESTATE_ID, "house", 500000,"1.1","1.1", "House on the countryside",
            400, 4,10,4,
            "Rue des cigales", "Maussane ", "false", "01/09/2018", "", "Liz", "school");


    // FOR DATA
    private RealEstateDatabase database;

    private static Photo NEW_PHOTO = new Photo(REALESTATE_ID, 1,
            "https://images.victorianplumbing.co.uk/images/Legend-Traditional-Bathroom-Suite_P.jpg",
            "Kitchen", "video");

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                RealEstateDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void insertAndGetUser() throws InterruptedException {
        // BEFORE : Adding a new user
        this.database.mRealEstateDao().createRealEstate(REALESTATE_DEMO);
        // TEST
        RealEstate realEstate = LiveDataTestUtil.getValue(this.database.mRealEstateDao().getRealEstate(REALESTATE_ID));
        assertTrue(realEstate.getType().equals(REALESTATE_DEMO.getType()) && realEstate.getId() == REALESTATE_ID);
    }


    @Test
    public void getPhotosWhenNoPhotosInserted() throws InterruptedException {
        // TEST
        List<Photo> photos = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID));
        assertTrue(photos.isEmpty());
    }

    @Test
    public void insertAndGetItems() throws InterruptedException {
        // BEFORE : Adding demo real estate & demo photo

        this.database.mRealEstateDao().createRealEstate(REALESTATE_DEMO);
        this.database.mPhotoDao().insertPhoto(NEW_PHOTO);

        // TEST
        List<Photo> photos = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID));
        assertTrue(photos.size() == 1);
    }

    @Test
    public void insertAndUpdateItem() throws InterruptedException {
        // BEFORE : Adding demo real estate & demo photo. Next, update photo added & re-save it
        this.database.mRealEstateDao().createRealEstate(REALESTATE_DEMO);
        this.database.mPhotoDao().insertPhoto(NEW_PHOTO);
        Photo photoAdded = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID)).get(0);
        //photoAdded.setSelected(true);
        this.database.mPhotoDao().updatePhoto(photoAdded);

        //TEST
        List<Photo> photos = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID));
        assertTrue(photos.size() == 1 );
        //&& photos.get(0).getSelected()
    }

    @Test
    public void insertAndDeleteItem() throws InterruptedException {
        // BEFORE : Adding demo user & demo item. Next, get the item added & delete it.
        this.database.mRealEstateDao().createRealEstate(REALESTATE_DEMO);
        this.database.mPhotoDao().insertPhoto(NEW_PHOTO);
        Photo itemAdded = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID)).get(0);
        this.database.mPhotoDao().deletePhoto(itemAdded.getId());

        //TEST
        List<Photo> items = LiveDataTestUtil.getValue(this.database.mPhotoDao().getPhotos(REALESTATE_ID));
        assertTrue(items.isEmpty());
    }
}


