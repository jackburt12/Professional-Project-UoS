package com3001.jb01026.finalyearproject.model;

import java.io.Serializable;

public class Plant implements Serializable {

    private String name;
    private PlantType type;
    private String image;
    private String description;
    private PlotSize plotSize;
    private CareFrequency careFrequency;
    private Expertise expertise;
    private String monthByMonth;

    private boolean isChecked;

    public Plant() {}

    public Plant(String name, PlantType type, String image, String description, PlotSize plotSize, CareFrequency careFrequency, Expertise expertise, String monthByMonth) {
        this.name = name;
        this.type = type;
        this.image = image;
        this.description = description;
        this.plotSize = plotSize;
        this.careFrequency = careFrequency;
        this.expertise = expertise;
        this.monthByMonth = monthByMonth;
    }

    public Plant (String name, PlantType type, String image) {
        this.name = name;
        this.type = type;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public PlantType getType() {
        return type;
    }

    public String getImage() {
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

    public String getMonthByMonth() {
        return monthByMonth;
    }

    public boolean compareTo(Plant b){
        if(this.getName().charAt(0) == b.getName().charAt(0)) return false;
        else return true;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(PlantType type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlotSize(PlotSize plotSize) {
        this.plotSize = plotSize;
    }

    public void setCareFrequency(CareFrequency careFrequency) {
        this.careFrequency = careFrequency;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public void setMonthByMonth(String monthByMonth) {
        this.monthByMonth = monthByMonth;
    }


}
