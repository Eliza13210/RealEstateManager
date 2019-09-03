package com.openclassrooms.realestatemanager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.openclassrooms.realestatemanager.database.RealEstateDatabase;
import com.openclassrooms.realestatemanager.provider.RealEstateContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class RealEstateContentProviderTest {

    // FOR DATA
    private ContentResolver mContentResolver;

    // DATA SET FOR TEST
    private static long ID = 1;
    private final Executor executor = Executors.newSingleThreadExecutor();

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
        executor.execute(() -> {
            final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_REALESTATE, ID),
                    null, null, null, null);

            assertThat(cursor, notNullValue());
            assertEquals(cursor.getCount(), 0);
            cursor.close();
        });
    }

    @Test
    public void insertAndGetItem() {
        // BEFORE : Adding demo item
        executor.execute(() -> {
            final Uri exUri = mContentResolver.insert(RealEstateContentProvider.URI_REALESTATE, generateItem());
            // TEST
            assert exUri != null;
            final Cursor cursor = mContentResolver.query(exUri,
                    null, null, null, null);

            assertThat(cursor, notNullValue());
            assertEquals(cursor.getCount(), 1);
            assertTrue(cursor.moveToFirst());
            assertEquals(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("Beautiful house!"));
        });
    }

    // ---

    private ContentValues generateItem() {
        final ContentValues values = new ContentValues();
        values.put("description", "Beautiful house!");
        values.put("address", "Killingevag 134");
        values.put("agent", "Theo");
        values.put("id", "1");
        return values;
    }
}