package com.openclassrooms.realestatemanager;

import android.text.Editable;
import android.text.InputFilter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.annotations.NonNull;

import static org.junit.Assert.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class SearchManagerTest {

    @Mock
    EditText agent_et;
    @Mock
    EditText city_et;
    @Mock
    EditText surface_min;
    @Mock
    EditText surface_max;
    @Mock
    EditText price_min;
    @Mock
    EditText price_max;
    @Mock
    EditText rooms_min;
    @Mock
    EditText rooms_max;
    @Mock
    EditText bedrooms_min;
    @Mock
    EditText bedrooms_max;
    @Mock
    EditText bathrooms_min;
    @Mock
    EditText bathrooms_max;
    @Mock
    TextView start_date;
    @Mock
    TextView end_date;
    @Mock
    CheckBox cb_sold;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getQueryAndArgsFromUI() {
        SearchManager manager = new SearchManager();
        Mockito.when(agent_et.getText()).thenReturn(new MockEditable("Liz"));
        Mockito.when(city_et.getText()).thenReturn(new MockEditable("Avignon"));
        Mockito.when(surface_min.getText()).thenReturn(new MockEditable("100"));
        Mockito.when(surface_max.getText()).thenReturn(new MockEditable("200"));
        Mockito.when(price_min.getText()).thenReturn(new MockEditable("200000"));
        Mockito.when(price_max.getText()).thenReturn(new MockEditable("400000"));

        Mockito.when(rooms_min.getText()).thenReturn(new MockEditable("5"));
        Mockito.when(rooms_max.getText()).thenReturn(new MockEditable("10"));

        Mockito.when(bedrooms_min.getText()).thenReturn(new MockEditable(""));
        Mockito.when(bedrooms_max.getText()).thenReturn(new MockEditable("5"));

        Mockito.when(bathrooms_min.getText()).thenReturn(new MockEditable("1"));
        Mockito.when(bathrooms_max.getText()).thenReturn(new MockEditable(""));

        Mockito.when(start_date.getText()).thenReturn(new MockEditable("01/04/2019"));
        Mockito.when(end_date.getText()).thenReturn(new MockEditable(""));
        Mockito.when(cb_sold.isChecked()).thenReturn(false);


        List<String> listCheckBox = new ArrayList<>();
        listCheckBox.add("school");
        listCheckBox.add("bus station");

        manager.getQueryFromUI(agent_et, "house", listCheckBox, city_et, surface_min, surface_max, price_min, price_max, rooms_min, rooms_max,
                bedrooms_min, bedrooms_max, bathrooms_min, bathrooms_max, start_date, end_date, cb_sold);

        String query = manager.getQuery();

        String[] args = manager.getArgs();
        String[] expected = new String[]{"Liz", "house", "%school%", "%bus station%", "avignon", "10000", "20000", "200000", "400000", "5", "10", "5", "1",
                "01/04/2019", "false"};


        assertEquals("SELECT * FROM RealEstate WHERE agent = ? AND type =? AND pointsOfInterest LIKE ? AND pointsOfInterest LIKE ? " +
                "AND city LIKE ? AND surface BETWEEN ? AND ? " +
                "AND price BETWEEN ? AND ? AND rooms BETWEEN ? AND ? " +
                "AND bedrooms <= ? " +
                "AND bathrooms >= ? " +
                "AND startDate >= ? AND sold = ? ;", query);

        assertEquals(Arrays.toString(expected), Arrays.toString(args));
    }

    /**
     * A mocked Editable that allow us to mock user input
     */
    class MockEditable implements Editable {

        private String str;

        public MockEditable(String str) {
            this.str = str;
        }

        @Override
        @NonNull
        public String toString() {
            return str;
        }

        @Override
        public int length() {
            return str.length();
        }

        @Override
        public char charAt(int i) {
            return str.charAt(i);
        }

        @Override
        public CharSequence subSequence(int i, int i1) {
            return str.subSequence(i, i1);
        }

        @Override
        public Editable replace(int i, int i1, CharSequence charSequence, int i2, int i3) {
            return this;
        }

        @Override
        public Editable replace(int i, int i1, CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable insert(int i, CharSequence charSequence, int i1, int i2) {
            return this;
        }

        @Override
        public Editable insert(int i, CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable delete(int i, int i1) {
            return this;
        }

        @Override
        public Editable append(CharSequence charSequence) {
            return this;
        }

        @Override
        public Editable append(CharSequence charSequence, int i, int i1) {
            return this;
        }

        @Override
        public Editable append(char c) {
            return this;
        }

        @Override
        public void clear() {
        }

        @Override
        public void clearSpans() {
        }

        @Override
        public void setFilters(InputFilter[] inputFilters) {
        }

        @Override
        public InputFilter[] getFilters() {
            return new InputFilter[0];
        }

        @Override
        public void getChars(int i, int i1, char[] chars, int i2) {
        }

        @Override
        public void setSpan(Object o, int i, int i1, int i2) {
        }

        @Override
        public void removeSpan(Object o) {
        }

        @Override
        public <T> T[] getSpans(int i, int i1, Class<T> aClass) {
            return null;
        }

        @Override
        public int getSpanStart(Object o) {
            return 0;
        }

        @Override
        public int getSpanEnd(Object o) {
            return 0;
        }

        @Override
        public int getSpanFlags(Object o) {
            return 0;
        }

        @Override
        public int nextSpanTransition(int i, int i1, Class aClass) {
            return 0;
        }
    }
}

