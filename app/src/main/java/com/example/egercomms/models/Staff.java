package com.example.egercomms.models;
import android.os.Parcel;
import android.os.Parcelable;

public class Staff implements Parcelable {
    private User user;
    private String photo;
    private String gender;

    public Staff(User user) {
        this.user = user;
    }

    public Staff(User user, String photo, String gender) {
        this.user = user;
        this.photo = photo;
        this.gender = gender;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "user=" + user +
                ", photo='" + photo + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.photo);
        dest.writeString(this.gender);
    }

    protected Staff(Parcel in) {
        this.user = in.readParcelable(User.class.getClassLoader());
        this.photo = in.readString();
        this.gender = in.readString();
    }

    public static final Parcelable.Creator<Staff> CREATOR = new Parcelable.Creator<Staff>() {
        @Override
        public Staff createFromParcel(Parcel source) {
            return new Staff(source);
        }

        @Override
        public Staff[] newArray(int size) {
            return new Staff[size];
        }
    };
}
