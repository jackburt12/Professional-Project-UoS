package com3001.jb01026.finalyearproject.model;

import java.io.Serializable;
import java.util.List;

public class Walk implements Serializable {
    private double minDistance, maxDistance;
    private List<Plant> pointList;

    public Walk() {}

    public Walk(double minDistance, double maxDistance, List<Plant> pointList) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.pointList = pointList;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public List<Plant> getPointList() {
        return pointList;
    }
}
