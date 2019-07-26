package com.openclassrooms.realestatemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.openclassrooms.realestatemanager.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.provider.ItemContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;


//@RunWith(AndroidJUnit4.class)
@RunWith(RobolectricTestRunner.class)
public class ItemContentProviderTest {

    // FOR DATA
    private ContentResolver mContentResolver;

    // DATA SET FOR TEST
    private static long ID = 1;

    @Before
    public void setUp() {
        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                RealEstateDatabase.class)
                .allowMainThreadQueries()
                .build();
        mContentResolver = InstrumentationRegistry.getInstrumentation().getTargetContext().getContentResolver();
    }

    @Test
    public void getItemsWhenNoItemInserted() {
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(ItemContentProvider.URI_REALESTATE, ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertEquals(cursor.getCount(), 0);
        cursor.close();
    }

    @Test
    public void insertAndGetItem() {
        // BEFORE : Adding demo item
        final Uri userUri = mContentResolver.insert(ItemContentProvider.URI_REALESTATE, generateItem());
        // TEST
        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(ItemContentProvider.URI_REALESTATE, ID),
                null, null, null, null);
        assertThat(cursor, notNullValue());
        assertEquals(cursor.getCount(),1);
        assertEquals(cursor.moveToFirst(), true);
        assertEquals(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("Visite cet endroit de rêve !"));
    }

    // ---

    private ContentValues generateItem(){
        final ContentValues values = new ContentValues();
        values.put("description", "Visite cet endroit de rêve !");
        values.put("address", "Killingevag 134");
        values.put("agent", "Theo");
        values.put("id", "1");
        return values;
    }
}