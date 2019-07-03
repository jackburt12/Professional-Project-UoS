package com3001.jb01026.finalyearproject.model;

import com.google.android.gms.maps.model.LatLng;

public class Plot {

    private LatLng location;
    private Plant plant;

    public Plot(LatLng location) {
        this.location = location;
    }

    public Plant getPlant() {
        return plant;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
