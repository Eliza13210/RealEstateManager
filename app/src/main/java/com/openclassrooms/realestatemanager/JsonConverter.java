package com.openclassrooms.realestatemanager;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.realestatemanager.models.Result;

import java.lang.reflect.Type;
import java.util.List;

public class JsonConverter {

    public static String convertToJson(List<Result> listPoi) {
        Gson gson = new Gson();
        String json = gson.toJson(listPoi);
        return json;
    }

    public static List<Result> convertToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Result>>() {
        }.getType();
        return gson.fromJson(json, type);

    }
}
