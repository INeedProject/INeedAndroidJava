package com.ineed.senior;

import java.io.Serializable;

public class Need implements Serializable {
    private String mTitle;
    private  String mUser;
    private String mCoordinates;
    private int mIndex;
    private String mCtite;
    private  String mDistrict;
    private String mUserId;


    public Need(){

    }

    public Need(String mTitle, String mUser, String mCoordinates, int mIndex, String mCitie, String mDistrict, String mUserId) {
        this.mTitle = mTitle;
        this.mUser = mUser;
        this.mCoordinates = mCoordinates;
        this.mIndex = mIndex;
        this.mCtite = mCitie;
        this.mDistrict = mDistrict;

        this.mUserId = mUserId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmUser() {
        return mUser;
    }

    public String getmCoordinates() {
        return mCoordinates;
    }

    public int getmIndex() {
        return mIndex;
    }

    public String getmCitie() {
        return mCtite;
    }

    public String getmDistrict() {
        return mDistrict;
    }

    public String getmUserId() {
        return mUserId;
    }
}
