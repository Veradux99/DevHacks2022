package com.example.devhecks2022maps5;

import com.google.android.gms.maps.model.LatLng;

public class markerLocation {
    Integer id;
    String name;
    String mac;
    LatLng location;

    public markerLocation(Integer id, String name, String mac, LatLng location) {
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
