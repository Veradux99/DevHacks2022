package com.example.devhecks2022maps5;

import com.google.android.gms.maps.model.LatLng;

public class poliLocation {
    LatLng start;
    LatLng end;

    public poliLocation(LatLng start, LatLng end) {
        this.start = start;
        this.end = end;
    }

    public LatLng getStart() {
        return start;
    }

    public void setStart(LatLng start) {
        this.start = start;
    }

    public LatLng getEnd() {
        return end;
    }

    public void setEnd(LatLng end) {
        this.end = end;
    }
}
