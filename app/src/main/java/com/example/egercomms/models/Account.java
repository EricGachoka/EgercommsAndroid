package com.example.egercomms.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    private Jurisdiction jurisdiction;
    private Staff staff;

    public Account(Jurisdiction jurisdiction, Staff staff) {
        this.jurisdiction = jurisdiction;
        this.staff = staff;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    @Override
    public String toString() {
        return "Account{" +
                "jurisdiction=" + jurisdiction +
                ", staff=" + staff +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.jurisdiction, flags);
        dest.writeParcelable(this.staff, flags);
    }

    protected Account(Parcel in) {
        this.jurisdiction = in.readParcelable(Jurisdiction.class.getClassLoader());
        this.staff = in.readParcelable(Staff.class.getClassLoader());
    }

    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
