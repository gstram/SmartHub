package com.example.stram.smartmirror;

/**
 * Created by stram on 5/20/2016.
 */
public class CurrentTraffic {
    private String mTravelTime;
    private String mTrafficText;
    private String mCity;


    public void setmTravelTime(String time) {

        mTravelTime = time;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmTrafficText(String text)
    {
        mTrafficText = text;
    }

    public String getTravelTime()
    {
        return mTravelTime;
    }

    public String getmTrafficText() {
        return mTrafficText;
    }
}
