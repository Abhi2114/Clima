package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 145;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "3b91ce1c180ba0a20ee61e05844d4ad2";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    TextView mMinTemp;
    TextView mMaxTemp;
    TextView mHumidity;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        mMaxTemp = (TextView) findViewById(R.id.maxTempTV);
        mMinTemp = (TextView) findViewById(R.id.minTempTV);
        mHumidity = (TextView) findViewById(R.id.humidityTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // change the activity
                Intent myIntent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "onResume() called");

        // check if the city has changed
        // we do that by creating an intent
        Intent myIntent = getIntent();
        String myCity = myIntent.getStringExtra("city");

        // if city changed
        if (myCity != null){

            getWeatherForNewCity(myCity);
            return;
        }

        Log.d("Clima", "Getting weather for the current location");
        getWeatherForCurrentLocation();
    }

    private void getWeatherForNewCity(String city){

        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
    }

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("Clima", "onLocationChanged() callback received");
                // get the longitude and latitude of the device
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                Log.d("Clima", "longitude = " + longitude);
                Log.d("Clima", "latitude = " + latitude);

                // prepare the search query
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.d("Clima", "onProviderDisabled() callback received");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Clima", "onRequestPermissionsResult(): Permission Granted");
            }
            else{
                Log.d("Clima", "Permission Denied =(");
            }
        }
    }

    private void letsDoSomeNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();
        // perform an http request to get data from the server
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){

            // if GET request was successful
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){

                Log.d("Clima", "Success! JSON " + response.toString());
                // get the weather data and display it
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                // put the data on the views, i.e update the UI
                updateUI(weatherData);
            }

            // if GET request was unsuccessful
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response){

                Log.d("Clima", "Fail " + e.toString());
                Log.d("Clima", "Status Code " + statusCode);
                // show something to the user
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(WeatherDataModel weatherData){

        mCityLabel.setText(weatherData.getCity());
        mTemperatureLabel.setText(weatherData.getTemperature());
        // now, we update the image icon
        // we know the name of the file, but we do not know the resource id
        // get the resource id first
        int resourceID = getResources().getIdentifier(weatherData.getIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
        // update the max and min temperatures
        mMaxTemp.setText(weatherData.getMaxTemp());
        mMinTemp.setText(weatherData.getMinTemp());
        // update the humidity as well
        mHumidity.setText(weatherData.getHumidity());
    }

    @Override
    public void onPause(){

        super.onPause();
        // if the app is not in the foreground anymore, stop asking for location
        // updates
        if (mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);
    }

}
