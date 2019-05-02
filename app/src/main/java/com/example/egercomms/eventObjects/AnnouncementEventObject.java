package com.example.egercomms.eventObjects;

import com.example.egercomms.models.Announcement;

import java.util.List;

public class AnnouncementEventObject {
    private List<Announcement> announcements;

    public AnnouncementEventObject(List<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}
