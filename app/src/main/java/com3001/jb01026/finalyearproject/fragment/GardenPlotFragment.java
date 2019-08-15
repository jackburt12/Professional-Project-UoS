package com3001.jb01026.finalyearproject.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.SunCalculations;
import com3001.jb01026.finalyearproject.WeatherCalculations;
import com3001.jb01026.finalyearproject.activity.MainActivity;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GardenPlotFragment extends Fragment {

    private GardenPlot plot;
    private SunCalculations sunCalculations = new SunCalculations();

    View loadingOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_garden_plot, container, false);

        loadingOverlay = view.findViewById(R.id.loading_screen);
        loadingOverlay.setVisibility(View.VISIBLE);

        Bundle bundle = getArguments();
        plot = (GardenPlot) bundle.getSerializable("garden_plot");

        TextView plotName = view.findViewById(R.id.plot_name);


        List<TextView> rainfallTextViews = new ArrayList<>();
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_1));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_2));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_3));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_4));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_5));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_6));
        rainfallTextViews.add(view.findViewById(R.id.rainfall_day_7));

        List<TextView> rainfallLabels = new ArrayList<>();
        rainfallLabels.add(view.findViewById(R.id.day_of_week_1_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_2_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_3_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_4_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_5_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_6_rain));
        rainfallLabels.add(view.findViewById(R.id.day_of_week_7_rain));


        List<TextView> temperatureTextViews = new ArrayList<>();
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_1));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_2));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_3));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_4));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_5));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_6));
        temperatureTextViews.add(view.findViewById(R.id.temperature_day_7));

        List<TextView> temperatureLabels = new ArrayList<>();
        temperatureLabels.add(view.findViewById(R.id.day_of_week_1_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_2_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_3_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_4_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_5_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_6_temp));
        temperatureLabels.add(view.findViewById(R.id.day_of_week_7_temp));

        TextView rainToday = view.findViewById(R.id.rainfall_today);
        TextView temperatureToday = view.findViewById(R.id.temperature_today);

        TextView shadeRating = view.findViewById(R.id.shade_rating);

        TextView sunlightDay = view.findViewById(R.id.avg_sun_day);
        TextView sunlightMonth = view.findViewById(R.id.avg_sun_month);
        TextView sunlightYear = view.findViewById(R.id.avg_sun_year);

        ImageView info = view.findViewById(R.id.shade_rating_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.app_name);
                builder.setMessage("The shade rating is a representation of how much sunlight a plot is exposed to over the course of the day.\n " +
                        "A score of 100 represents a plot that has no objects projecting shade over it, and is only limited by the length of each day.\n" +
                        "A shade rating of 0 represents a plot that is constantly being shaded by at least one object.\n");
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            });

        plotName.setText(plot.getName());

        int shadeRatingValue = sunCalculations.calc_shade_rating(plot);
        shadeRating.setText(Integer.toString(shadeRatingValue));
        setColour(shadeRating, 100, shadeRatingValue);

        sunlightDay.setText(formatHours((float)sunCalculations.calc_sun_exposure_day(plot,new Date())));
        setColour(sunlightDay, 100, shadeRatingValue);

        sunlightMonth.setText(formatHours((float)sunCalculations.calc_sun_exposure_month(plot, Calendar.getInstance().get(Calendar.MONTH)+1)/30));
        setColour(sunlightMonth, 100, shadeRatingValue);

        sunlightYear.setText(formatHours((float)(sunCalculations.calc_sun_exposure_year(plot)/365)));
        setColour(sunlightYear, 100, shadeRatingValue);

        Date today = new Date();

        double sunPerDay = sunCalculations.calc_sun_exposure_day(plot, today);
        double sunPerMonth = sunCalculations.calc_sun_exposure_month(plot, 1);

        System.out.println(sunPerDay);
        System.out.println(sunPerMonth);

        MainActivity activity = (MainActivity)getActivity();
        LatLng gardenLocation = activity.userLocation;

        ForecastApi.create("e5e6c785be3d23a995ee1ceb88afe9a0");

        Map<Long, Double> temperatureMap = new HashMap<>();
        Map<Long, Double> rainfallMap = new HashMap<>();


        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date todayDate = cal.getTime();

        long unixTimestamp = todayDate.getTime()/1000;
        //unixTimestamp = 1565395200 + 43000;


        RequestBuilder weatherToday = new RequestBuilder();

        Request todayRequest = new Request();
        todayRequest.setLat(Double.toString(gardenLocation.latitude));
        todayRequest.setLng(Double.toString(gardenLocation.longitude));
        todayRequest.setUnits(Request.Units.UK);
        todayRequest.setLanguage(Request.Language.ENGLISH);

        weatherToday.getWeather(todayRequest, new Callback<WeatherResponse>() {
            @Override
            public void success(WeatherResponse weatherResponse, Response response) {
                rainToday.setText(Double.toString(Double.parseDouble(weatherResponse.getDaily().getData().get(0).getPrecipProbability())*100).split("\\.")[0]+"%");
                temperatureToday.setText(Double.toString(weatherResponse.getDaily().getData().get(0).getTemperatureMax())+"\u00B0");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d("ERROR", "Error while calling: " + retrofitError.getUrl());
            }
        });





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

//                    temperatureList.add(weatherResponse.getDaily().getData().get(0).getTemperatureMax());
//
//                    double rainfall = Double.parseDouble(weatherResponse.getDaily().getData().get(0).getPrecipProbability());
//                    rainfallList.add(rainfall);


                    long time = weatherResponse.getDaily().getData().get(0).getTime();
                    temperatureMap.put(time,weatherResponse.getDaily().getData().get(0).getTemperatureMax());

                    double rainfall = Double.parseDouble(weatherResponse.getDaily().getData().get(0).getPrecipProbability());
                    rainfallMap.put(time, rainfall);

                    if(rainfallMap.size()==7) {

                        fillTextViews(rainfallMap, temperatureMap, temperatureTextViews, rainfallTextViews, temperatureLabels, rainfallLabels);

                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {
                    Log.d("ERROR", "Error while calling: " + retrofitError.getUrl());
                }
            });

        }
        //List<Map<Long, Double>> mapList = weatherCalculations.calc_weather(activity.userLocation);

        return view;
    }

    public void fillTextViews(Map<Long, Double> rainfallMap, Map<Long, Double> temperatureMap, List<TextView> temperatureTextViews, List<TextView> rainfallTextViews, List<TextView> temperatureLabels, List<TextView> rainfallLabels) {
        List sortedKeys = new ArrayList(rainfallMap.keySet());
        Collections.sort(sortedKeys);

        for(int i = 0; i < sortedKeys.size(); i++) {

            long time = (long)sortedKeys.get(i);

            Date date = new Date ();
            date.setTime((long)time*1000);
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            double rain = rainfallMap.get(sortedKeys.get(i))*100;
            double temp = temperatureMap.get(sortedKeys.get(i));

            temperatureLabels.get(i).setText(localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.UK));
            rainfallLabels.get(i).setText(localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.UK));

            rainfallTextViews.get(i).setText(Double.toString(Math.round(rain)).split("\\.")[0]+"%");
            temperatureTextViews.get(i).setText(Double.toString(temp)+"\u00B0");
            setColour(rainfallTextViews.get(i), 100, (int)rain);
            setColour(temperatureTextViews.get(i), 30, (int)temp);

            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void setColour(TextView view, int upperBound, int value) {

        double colourValue = ((double)value/upperBound)*100;

        int colour = Color.HSVToColor(new float[]{(float)(colourValue), 0.7f, 0.95f });

        view.setTextColor(colour);

    }

    private String formatHours(float f) {
        Log.v("FLOAT f", Float.toString(f));
        double rounded = Math.round(f/0.25)*0.25;
        Log.v("DOUBLE rounded", Double.toString(rounded));
        //double rounded = (Math.round((f*4)/4f))*60/100;

        String s = String.format("%.2f", rounded);
        System.out.println(s);
        String[] temp = s.split("\\.");
        return temp[0] + "h" + Double.toString(Double.parseDouble(temp[1])/100*60).split("\\.")[0] + "m";
    }


}
