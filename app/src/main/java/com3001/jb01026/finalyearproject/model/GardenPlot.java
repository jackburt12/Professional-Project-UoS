package com3001.jb01026.finalyearproject.model;

import java.io.Serializable;
import java.util.List;

public class GardenPlot implements Serializable {

    private String name;
    private double width;
    private double length;

    private List<ShadowObject> shadowObjects;

    public GardenPlot(double width, double length) {
        this.width = width;
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public double getLength() {
        return length;
    }

    public void setShadowObjects(List<ShadowObject> shadowObjects) {
        this.shadowObjects = shadowObjects;
    }

    public void addShadowObject(ShadowObject object) {
        this.shadowObjects.add(object);
    }

    public List<ShadowObject> getShadowObjects() {
        return shadowObjects;
    }

    public String getName() {
        if(name == null) {
            return "Plot";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int countObjects() {
        if(shadowObjects!=null) {
            return shadowObjects.size();
        } else {
            return 0;
        }
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setLength(double length) {
        this.length = length;
    }
}
