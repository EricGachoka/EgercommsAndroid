package com.example.egercomms.data;

import com.example.egercomms.models.Account;
import com.example.egercomms.models.Announcement;
import com.example.egercomms.models.Jurisdiction;

import java.util.List;

public class DataHandler {
    private static DataHandler uniqueInstance = new DataHandler();
    private String item;
    private List<Account> accounts;
    private List<Announcement> announcements;
    private boolean permissionsGranted;
    private List<Jurisdiction> jurisdictions;

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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public boolean isPermissionsGranted() {
        return permissionsGranted;
    }

    public void setPermissionsGranted(boolean permissionsGranted) {
        this.permissionsGranted = permissionsGranted;
    }

    public List<Jurisdiction> getJurisdictions() {
        return jurisdictions;
    }

    public void setJurisdictions(List<Jurisdiction> jurisdictions) {
        this.jurisdictions = jurisdictions;
    }
}
