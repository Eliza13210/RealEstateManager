package com.openclassrooms.realestatemanager.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("name")
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Result getGeometry() {
        return geometry;
    }

    public void setGeometry(Result geometry) {
        this.geometry = geometry;
    }

    public Result getLocation() {
        return location;
    }

    public void setLocation(Result location) {
        this.location = location;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @SerializedName("types")
    @Expose
    private List<String> types;

    //To get latLng
    @SerializedName("geometry")
    @Expose
    private Result geometry;

    @SerializedName("latLng")
    @Expose
    private Result location;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("lng")
    @Expose
    private Double lng;
}
