package com.example.egercomms.eventObjects;

import com.example.egercomms.models.Jurisdiction;

import java.util.List;

public class JurisdictionEventObject {
    private List<Jurisdiction> jurisdictions;

    public JurisdictionEventObject(List<Jurisdiction> jurisdictions) {
        this.jurisdictions = jurisdictions;
    }

    public List<Jurisdiction> getJurisdictions() {
        return jurisdictions;
    }

    public void setJurisdictions(List<Jurisdiction> jurisdictions) {
        this.jurisdictions = jurisdictions;
    }
}
