package com3001.jb01026.finalyearproject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.time.Instant;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WeatherCalculations {

    private LatLng gardenLocation = new LatLng(51.242560, -0.571569); //this is my address as a temp location, should be user entered obvs


    public void calc_weather() {

        ForecastApi.create("e5e6c785be3d23a995ee1ceb88afe9a0");

        RequestBuilder weather = new RequestBuilder();

        Request request = new Request();
        request.setLat(Double.toString(gardenLocation.latitude));
        request.setLng(Double.toString(gardenLocation.longitude));
        request.setUnits(Request.Units.UK);
        request.setLanguage(Request.Language.ENGLISH);
        //request.addExcludeBlock(Request.Block.CURRENTLY);

        long unixTimestamp = Instant.now().getEpochSecond() - 1000;
        unixTimestamp = 1565395200 + 43000;

        request.setTime(Long.toString(unixTimestamp));

        weather.getWeather(request, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                //Do something
                Log.d("SUCCESS", "+" + weatherResponse.getDaily().getSummary());
                Log.d("SUCCESS", "+" + weatherResponse.getCurrently().getSummary());
                Log.d("SUCCESS", "+" + weatherResponse.getCurrently().getCloudClover());
                Log.d("SUCCESS", "+" + weatherResponse.getCurrently().getTemperature());
                Log.d("SUCCESS", "+" + weatherResponse.getCurrently().getHumidity());


            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("ERROR", "Error while calling: " + retrofitError.getUrl());
            }
        });

    }
}
