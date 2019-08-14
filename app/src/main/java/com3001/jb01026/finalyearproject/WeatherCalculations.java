package com3001.jb01026.finalyearproject;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WeatherCalculations {

    int count = 0;

    double[] rainfall = new double[7];
    double[] temperature = new double[7];

    Map<Long, Double> temperatureMap = new HashMap<>();
    Map<Long, Double> rainfallMap = new HashMap<>();
    List<Map<Long,Double>> list = new ArrayList<>();

    boolean mReturn = false;

    public List<Map<Long, Double>> calc_weather(LatLng gardenLocation) {

        ForecastApi.create("e5e6c785be3d23a995ee1ceb88afe9a0");


        //request.addExcludeBlock(Request.Block.CURRENTLY);
        Map<Long, Double> temperatureMap = new HashMap<>();
        Map<Long, Double> rainfallMap = new HashMap<>();
        List<Map<Long,Double>> list = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date today = cal.getTime();

        long unixTimestamp = today.getTime()/1000;
        //unixTimestamp = 1565395200 + 43000;


        for(int i = 0; i<7;i++) {

            RequestBuilder weather = new RequestBuilder();

            Request request = new Request();
            request.setLat(Double.toString(gardenLocation.latitude));
            request.setLng(Double.toString(gardenLocation.longitude));
            request.setUnits(Request.Units.UK);
            request.setLanguage(Request.Language.ENGLISH);
            unixTimestamp += 86400;
            Log.d("Unix Time Stamp", ""+unixTimestamp);
            request.setTime(Long.toString(unixTimestamp));


            weather.getWeather(request, new Callback<WeatherResponse>() {
                @Override
                public void success(WeatherResponse weatherResponse, Response response) {
                    //Do something
                    long time = weatherResponse.getDaily().getData().get(0).getTime();

                    temperatureMap.put(time,weatherResponse.getDaily().getData().get(0).getTemperatureMax());

                    double rainfall = Double.parseDouble(weatherResponse.getDaily().getData().get(0).getPrecipProbability());
                    rainfallMap.put(time, rainfall);

                    count++;

                    Log.v("COUNT", ""+count);
                    if(count==7) {
                        list.add(temperatureMap);
                        list.add(rainfallMap);

                        mReturn = true;
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.d("ERROR", "Error while calling: " + retrofitError.getUrl());
                }
            });

        }

        return list;

    }

}
