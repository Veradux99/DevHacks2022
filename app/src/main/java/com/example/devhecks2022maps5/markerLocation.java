package com.example.devhecks2022maps5;

import com.google.android.gms.maps.model.LatLng;

public class markerLocation {
    String name;
    LatLng location;

    public markerLocation(String name, LatLng location) {
        this.name = name;
        this.location = location;
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
