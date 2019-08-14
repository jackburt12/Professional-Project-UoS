package com3001.jb01026.finalyearproject.model;

import java.util.List;

public class ShadowObject {

    private double height;
    private double angleFromPlot;
    private double distanceFromPlot;

    private String name;
    private List<Double> azimuths;

    public ShadowObject(double height, double angleFromPlot, double distanceFromPlot) {
        this.height = height;
        this.angleFromPlot = angleFromPlot;
        this.distanceFromPlot = distanceFromPlot;
    }

    public ShadowObject() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getHeight() {
        return height;
    }

    public double getAngleFromPlot() {
        return angleFromPlot;
    }

    public double getDistanceFromPlot() {
        return distanceFromPlot;
    }

    public List<Double> getAzimuths() {
        return azimuths;
    }

    public void setAzimuths(List<Double> azimuths) {
        this.azimuths = azimuths;
    }
}
