package com3001.jb01026.finalyearproject.model;

public class ShadowObject {

    private double height;
    private double angleFromPlot;
    private double distanceFromPlot;

    private String name;
    private double[] azimuths;

    public ShadowObject(double height, double angleFromPlot, double distanceFromPlot) {
        this.height = height;
        this.angleFromPlot = angleFromPlot;
        this.distanceFromPlot = distanceFromPlot;
    }

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

    public double[] getAzimuths() {
        return azimuths;
    }

    public void setAzimuths(double[] azimuths) {
        this.azimuths = azimuths;
    }
}
