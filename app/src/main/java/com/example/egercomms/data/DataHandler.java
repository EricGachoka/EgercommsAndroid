package com.example.egercomms.data;

import com.example.egercomms.models.Announcement;
import com.example.egercomms.models.Jurisdiction;

import java.util.Arrays;
import java.util.List;

public class DataHandler {
    private static DataHandler uniqueInstance = new DataHandler();
    private String item = null;
    private List<Jurisdiction> jurisdictions;
    private List<Announcement> announcements;

    private DataHandler() {
    }

    public static DataHandler getInstance() {
        return uniqueInstance;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public List<Jurisdiction> getJurisdictions() {
        return jurisdictions;
    }

    public void setJurisdictions(List<Jurisdiction> jurisdictions) {
        this.jurisdictions = jurisdictions;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}
