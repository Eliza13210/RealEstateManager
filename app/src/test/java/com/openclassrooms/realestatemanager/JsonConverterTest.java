package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.models.Result;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JsonConverterTest {

    @Test
    public void convertResultToJson() {
        Result r = new Result();
        r.setName("Fleur du thym");

        List<String> types = new ArrayList<>();
        types.add("restaurant");
        r.setTypes(types);

        List<Result> listOfResult = new ArrayList<>();
        listOfResult.add(r);

        String actual = JsonConverter.convertToJson(listOfResult);

        assertEquals("[{\"name\":\"Fleur du thym\",\"types\":[\"restaurant\"]}]", actual);
    }

    @Test
    public void convertStringToList() {
        List<Result> resultList = JsonConverter.convertToList("[{\"name\":\"Fleur du thym\",\"types\":[\"restaurant\"]}]");
        assertEquals(resultList.get(0).getName(), "Fleur du thym");
    }

}