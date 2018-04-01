package com.londonappbrewery.climapm;

import org.json.JSONException;
import org.json.JSONObject;

class WeatherDataModel {

    private String mCity;
    private String mIconName;
    private String mTemperature;
    private String mMaxTemp;
    private String mMinTemp;
    private String mhumidity;

    private WeatherDataModel(String city, String iconName, String temperature,
                             String maxTemp, String minTemp, String humidity){
        mCity = city;
        mIconName = iconName;
        mTemperature = temperature;
        mMaxTemp = maxTemp;
        mMinTemp = minTemp;
        mhumidity = humidity;
    }

    static WeatherDataModel fromJson(JSONObject jsonObject){

        String city = "";
        String iconName = "";
        String temperature = "";
        String minTemp = "";
        String maxTemp = "";
        String humidity = "";

        try {
            city = jsonObject.getString("name");
            int condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            iconName = updateWeatherIcon(condition);
            // convert temp from Kelvin to Celsius
            double temp = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundTemp = (int) Math.rint(temp);   // round the temp
            temperature = String.valueOf(roundTemp);
            // also get the min and max temps
            double min = jsonObject.getJSONObject("main").getDouble("temp_min") - 273.15;
            roundTemp = (int) Math.rint(min);
            minTemp = String.valueOf(roundTemp);
            double max = jsonObject.getJSONObject("main").getDouble("temp_max") - 273.15;
            roundTemp = (int) Math.rint(max);
            maxTemp = String.valueOf(roundTemp);
            // get the humidity in the same way
            int hum = jsonObject.getJSONObject("main").getInt("humidity");
            humidity = String.valueOf(hum);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new WeatherDataModel(city, iconName, temperature, maxTemp, minTemp, humidity);
    }

    private static String updateWeatherIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    String getCity() {
        return mCity;
    }

    String getIconName() {
        return mIconName;
    }

    String getTemperature() {
        return mTemperature + "°";
    }

    String getMaxTemp() {
        return mMaxTemp;
    }

    String getMinTemp() {
        return mMinTemp;
    }

    String getHumidity() { return mhumidity; }
}
