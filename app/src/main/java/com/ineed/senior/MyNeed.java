package com.ineed.senior;

import android.os.Parcel;
import android.os.Parcelable;

public class MyNeed implements Parcelable {

    private String desc, email, location, type;
    private double latitude;
    private double longitude;
    private boolean status;

    public MyNeed() {
    }

    public MyNeed(String desc, String email, String location, String type, double latitude, double longitude, boolean status) {
        this.desc = desc;
        this.email = email;
        this.location = location;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    protected MyNeed(Parcel in) {
        desc = in.readString();
        email = in.readString();
        location = in.readString();
        type = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        status = in.readByte() != 0;
    }

    public static final Creator<MyNeed> CREATOR = new Creator<MyNeed>() {
        @Override
        public MyNeed createFromParcel(Parcel in) {
            return new MyNeed(in);
        }

        @Override
        public MyNeed[] newArray(int size) {
            return new MyNeed[size];
        }
    };

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.desc,
                this.email,
                this.location,
                this.type, this.latitude+"", this.longitude+"", this.status+""
        });
    }

    @Override
    public String toString() {
        return "MyNeed{" +
                "desc='" + desc + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                '}';
    }
}
