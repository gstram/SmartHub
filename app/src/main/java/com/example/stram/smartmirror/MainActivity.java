package com.example.stram.smartmirror;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //get location stuff
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 666;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private double homeLongitude;
    private double homeLatitude;

    // Create calendar for calender
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    // Create weather object for weather
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
    private CurrentTraffic mCurrentTraffic;

    // sets each icon to its variable
    @InjectView(R.id.temperatureLabel)
    TextView mTemperatureValue;
    @InjectView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @InjectView(R.id.todaysDateLabel)
    TextView mtodaysDateLabel;
    @InjectView(R.id.todaysDateLabel2)
    TextView mtodaysDateLabel2;
    @InjectView(R.id.iconImageView)
    ImageView mIconImageView;
    @InjectView(R.id.imageViewOne)
    ImageView imageViewOne;
    @InjectView(R.id.imageViewTwo)
    ImageView imageViewTwo;
    @InjectView(R.id.imageViewThree)
    ImageView imageViewThree;
    @InjectView(R.id.imageViewFour)
    ImageView imageViewFour;
    @InjectView(R.id.imageViewFive)
    ImageView imageViewFive;
    @InjectView(R.id.trafficLabel)
    TextView mTrafficLabel;
    @InjectView(R.id.trafficText)
    TextView mTrafficText;
    @InjectView(R.id.locationLabel)
    TextView mLocationLabel;

    private TextView lblLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

        // Determines if the user has the correct permissions
        // If the user has the correct permission, goes to else statement
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //Prompts the user to accept or deny the permission
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        else
        {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }

            mGoogleApiClient.connect();

            Log.d(TAG, "Main UI code is running!");
        }
    }

    // If the user is prompted to deny or accept the permission, this method is what happens next
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }

                    mGoogleApiClient.connect();

                    Log.d(TAG, "Main UI code is running!");
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    // Method that connects to forecast.io and google maps and grabs all the data
    private void getAllInfo()
    {
        // sets work or school coordinates
        // need to make customizable
        double workLatitude = 40.2800833;
        double workLongitude = -074.0052500;

        //start forecast implementation
        // Sets api key for forecast.io to variables
        String apiKey = "null";

        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey +
                "/" + homeLatitude + "," + homeLongitude;

        // sets api key for google maps
        String googleMapsAPI = "null";
        String googleMapsTrafficUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + homeLatitude + "," + homeLongitude +
                "&destination=" + workLatitude + "," + workLongitude + "&traffic_model=pessimistic&departure_time=now&key=" + googleMapsAPI;

        // checks for network connectivity
        if (isNetworkAvailable()) {

            // Starts a okhttp client
            OkHttpClient client = new OkHttpClient();

            // requests the forecasturl and google maps information
            Request request = new Request.Builder().url(forecastUrl).build();
            Request trafficRequest = new Request.Builder().url(googleMapsTrafficUrl).build();

            // get traffic data
            Call trafficCall = client.newCall(trafficRequest);
            trafficCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentTraffic = getTrafficDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateTrafficDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    }
                }
            });

            //calls for forecast
            // get weather data
            Call weatherCall = client.newCall(request);
            weatherCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT: ", e);
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    // Links all the values to the icons on display
    private void updateTrafficDisplay() {
        mTrafficLabel.setText(mCurrentTraffic.getTravelTime() + "");
        mTrafficText.setText(mCurrentTraffic.getmTrafficText() + "");
        mLocationLabel.setText(mCurrentTraffic.getmCity() + "");
    }

    private void updateDisplay() {
        // date
        //Finds out the Date
        int todaysDayNumber = 1;
        Calendar rightNow = Calendar.getInstance();
        String todaysDay = "";
        todaysDayNumber = Calendar.DAY_OF_WEEK;

        String currentDate = DateFormat.getDateInstance().format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        todaysDay = sdf.format(d);

        mtodaysDateLabel.setText(todaysDay);
        mtodaysDateLabel2.setText(currentDate);
        mTemperatureValue.setText(mCurrentWeather.getTemperature() + "");
        mSummaryLabel.setText(mCurrentWeather.getSummary() + "");

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getIcon()));
        mIconImageView.setImageDrawable(drawable);
        drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getmIconOne()));
        imageViewOne.setImageDrawable(drawable);
        drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getmIconTwo()));
        imageViewTwo.setImageDrawable(drawable);
        drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getmIconThree()));
        imageViewThree.setImageDrawable(drawable);
        drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getmIconFour()));
        imageViewFour.setImageDrawable(drawable);
        drawable = getResources().getDrawable(mCurrentWeather.getIconId(mCurrentWeather.getmIconFive()));
        imageViewFive.setImageDrawable(drawable);
    }

    // Gets current weather
    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        //gets current weather information
        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setmHumidity(currently.getDouble("humidity"));
        currentWeather.setmIcon(currently.getString("icon"));
        currentWeather.setmPrecipitation(currently.getDouble("precipProbability"));
        currentWeather.setmTemperature((currently.getDouble("temperature")));
        currentWeather.setmSummary(currently.getString("summary"));

        //gets weekly weather information
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");
        JSONObject array = data.getJSONObject(0);
        currentWeather.setmIconOne(array.getString("icon"));
        array = data.getJSONObject(1);
        currentWeather.setmIconTwo(array.getString("icon"));
        array = data.getJSONObject(2);
        currentWeather.setmIconThree(array.getString("icon"));
        array = data.getJSONObject(3);
        currentWeather.setmIconFour(array.getString("icon"));
        array = data.getJSONObject(4);
        currentWeather.setmIconFive(array.getString("icon"));

        Log.d(TAG, "From JSON: " + timezone);
        return currentWeather;
    }

    // Gets traffic information
    private CurrentTraffic getTrafficDetails(String jsonData) throws JSONException {

        //Navigate the JSON structure of google maps
        JSONObject traffic = new JSONObject(jsonData);
        JSONArray routes = traffic.getJSONArray("routes");
        JSONObject legs = routes.getJSONObject(0);
        JSONArray duration = legs.getJSONArray("legs");
        JSONObject other = duration.getJSONObject(0);
        JSONObject hopefully = other.getJSONObject("duration");
        int normal_time = hopefully.getInt("value") / 60;
        hopefully = other.getJSONObject("duration_in_traffic");
        String travel = hopefully.getString("text");
        int traffic_time = hopefully.getInt("value") / 60;

        //Get city and manipulate string
        String start = other.getString("start_address");
        start = start.substring(start.indexOf(',')+1, start.length()-1);
        start = start.substring(0, start.indexOf(',')+4);

        //set the time of travel to current traffic object
        CurrentTraffic currentTraffic = new CurrentTraffic();
        currentTraffic.setmTravelTime(travel);
        currentTraffic.setmCity(start);

        if (traffic_time - normal_time <= 5) {
            currentTraffic.setmTrafficText("No Traffic.");
        } else if (traffic_time - normal_time < 15) {
            currentTraffic.setmTrafficText("Light Traffic.");
        } else {
            currentTraffic.setmTrafficText("Heavy Traffic.");
        }

        return currentTraffic;
    }

    // checks if network is available
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.stram.smartmirror/http/host/path")
        );

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void displayLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            homeLatitude = mLastLocation.getLatitude();
            homeLongitude = mLastLocation.getLongitude();

        } else
        {

        }
        getAllInfo();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            homeLatitude = mLastLocation.getLatitude();
            homeLongitude = mLastLocation.getLongitude();

        } else {

        }
        getAllInfo();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        displayLocation();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }
}

