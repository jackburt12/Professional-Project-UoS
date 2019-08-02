package com3001.jb01026.finalyearproject.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Route {

    LatLng start;
    ArrayList<Plot> plots;

    public Route(LatLng start, ArrayList<Plot> plots) {
        this.start = start;
        this.plots = plots;
    }

    public LatLng getStart() {
        return start;
    }

    public ArrayList<Plot> getPlots() {
        return plots;
    }
}
