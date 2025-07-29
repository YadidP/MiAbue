package com.miabue.app.models;

import java.io.Serializable;
import java.util.Date;

public class Treatment implements Serializable {
    private int id;
    private String name;
    private String dose;
    private int frequencyHours;
    private String startTime;
    private Date createdDate;
    private boolean isActive;
    private Date lastTaken;

    public Treatment() {
        this.createdDate = new Date();
        this.isActive = true;
    }

    public Treatment(String name, String dose, int frequencyHours, String startTime) {
        this();
        this.name = name;
        this.dose = dose;
        this.frequencyHours = frequencyHours;
        this.startTime = startTime;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public int getFrequencyHours() {
        return frequencyHours;
    }

    public void setFrequencyHours(int frequencyHours) {
        this.frequencyHours = frequencyHours;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getLastTaken() {
        return lastTaken;
    }

    public void setLastTaken(Date lastTaken) {
        this.lastTaken = lastTaken;
    }

    @Override
    public String toString() {
        return name + " - " + dose + " (cada " + frequencyHours + "h)";
    }
}