package com3001.jb01026.finalyearproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.SunCalculations;
import com3001.jb01026.finalyearproject.WeatherCalculations;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.ShadowObject;

public class GardenPlotFragment extends Fragment {

    private GardenPlot plot;
    private SunCalculations sunCalculations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_garden_plot, container, false);

        Bundle bundle = getArguments();
        plot = (GardenPlot) bundle.getSerializable("garden_plot");

        sunCalculations = new SunCalculations();

        Date today = new Date();

        double sunPerDay = sunCalculations.calc_sun_exposure_day(plot, today);
        double sunPerMonth = sunCalculations.calc_sun_exposure_month(plot, 1);

        System.out.println(sunPerDay);
        System.out.println(sunPerMonth);

        WeatherCalculations weatherCalculations = new WeatherCalculations();
        weatherCalculations.calc_weather();



        return view;
    }


}
