package com.example.egercomms.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Jurisdiction implements Parcelable {
    private String name;

    public Jurisdiction(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Jurisdiction{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public Jurisdiction() {
    }

    protected Jurisdiction(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Jurisdiction> CREATOR = new Parcelable.Creator<Jurisdiction>() {
        @Override
        public Jurisdiction createFromParcel(Parcel source) {
            return new Jurisdiction(source);
        }

        @Override
        public Jurisdiction[] newArray(int size) {
            return new Jurisdiction[size];
        }
    };
}
