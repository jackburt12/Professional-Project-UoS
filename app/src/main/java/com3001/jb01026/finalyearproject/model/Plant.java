package com3001.jb01026.finalyearproject.model;

public class Plant {

    private String name;
    private PlantType type;
    private String image;

    private String description;

    private PlotSize plotSize;
    private CareFrequency careFrequency;
    private Expertise expertise;

    private int monthByMonth;

    public Plant(String name, PlantType type, String imageID, String description, PlotSize plotSize, CareFrequency careFrequency, Expertise expertise, int monthByMonth) {
        this.name = name;
        this.type = type;
        this.image = imageID;
        this.description = description;
        this.plotSize = plotSize;
        this.careFrequency = careFrequency;
        this.expertise = expertise;
        this.monthByMonth = monthByMonth;
    }

    public Plant (String name, PlantType type, String imageID) {
        this.name = name;
        this.type = type;
        this.image = imageID;
    }

    public String getName() {
        return name;
    }

    public PlantType getType() {
        return type;
    }

    public String getImageID() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public PlotSize getPlotSize() {
        return plotSize;
    }

    public CareFrequency getCareFrequency() {
        return careFrequency;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public int getMonthByMonth() {
        return monthByMonth;
    }

    public boolean compareTo(Plant b){
        if(this.getName().charAt(0) == b.getName().charAt(0)) return false;
        else return true;
    }
}
