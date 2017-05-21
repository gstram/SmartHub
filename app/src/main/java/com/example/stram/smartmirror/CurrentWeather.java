package com.example.stram.smartmirror;

/**
 * Created by stram on 2/13/2016.
 * grabs data from forecast.io and sets it to a variable
 */
public class CurrentWeather
{
    private String mIcon,mIconOne, mIconTwo, mIconThree, mIconFour, mIconFive;
    private double mTemperature, mHumidity, mPrecipitation;
    private String mSummary;

    public String getIcon() {
        return mIcon;
    }

    public void setmIcon(String Icon) {
        this.mIcon = Icon;
    }

    public String getmIconOne(){
        return mIconOne;
    }

    public String getmIconTwo(){
        return mIconTwo;
    }

    public String getmIconThree(){
        return mIconThree;
    }

    public String getmIconFour(){
    return mIconFour;
}

    public String getmIconFive(){
        return mIconFive;
    }

    public void setmIconOne(String Icon)
    {
        this.mIconOne = Icon;
    }

    public void setmIconTwo(String Icon)
    {
        this.mIconTwo = Icon;
    }

    public void setmIconThree(String Icon)
    {
        this.mIconThree = Icon;
    }

    public void setmIconFour(String Icon)
    {
        this.mIconFour = Icon;
    }

    public void setmIconFive(String Icon)
    {
        this.mIconFive = Icon;
    }

    //converts icon number to an icon
    public int getIconId(String icon)
    {
        //sets icon to a default in case of new icons in future
        int iconId = R.drawable.clear_day;

        // sets icons
        if (icon.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if (icon.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (icon.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (icon.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (icon.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (icon.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (icon.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (icon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (icon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (icon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }

        return iconId;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setmSummary(String Summary) {
        this.mSummary = Summary;
    }

    public int getPrecipitation() {
        double precipPercentage = mPrecipitation * 100;
        return (int)Math.round(precipPercentage);
    }

    public void setmPrecipitation(double Precipitation) {
        this.mPrecipitation = Precipitation;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setmHumidity(double Humidity) {
        this.mHumidity = Humidity;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setmTemperature(double Temperature) {
        this.mTemperature = Temperature;
    }

}
