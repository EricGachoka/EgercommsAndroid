package com.example.egercomms.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Announcement implements Parcelable {
    private String title;
    private String message;
    private String deadline;
    private String attachments;
    private String updated;

    public Announcement(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", deadline='" + deadline + '\'' +
                ", attachments='" + attachments + '\'' +
                ", updated='" + updated + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.message);
        dest.writeString(this.deadline);
        dest.writeString(this.attachments);
        dest.writeString(this.updated);
    }

    public Announcement() {
    }

    protected Announcement(Parcel in) {
        this.title = in.readString();
        this.message = in.readString();
        this.deadline = in.readString();
        this.attachments = in.readString();
        this.updated = in.readString();
    }

    public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() {
        @Override
        public Announcement createFromParcel(Parcel source) {
            return new Announcement(source);
        }

        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };
}
