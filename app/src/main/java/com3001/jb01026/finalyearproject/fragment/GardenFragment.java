package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import com3001.jb01026.finalyearproject.WeatherCalculations;
import com3001.jb01026.finalyearproject.adapter.CreateWalkPointsAdapter;
import com3001.jb01026.finalyearproject.adapter.PlotsListAdapter;
import com3001.jb01026.finalyearproject.model.Plant;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.SunCalculations;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.ShadowObject;


public class GardenFragment extends Fragment {

    List<GardenPlot> plots = new ArrayList<>();
    ListView plotList;
    PlotsListAdapter adapter;

    public SunCalculations sunCalculations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_garden, container, false);

        plotList = rootView.findViewById(R.id.plot_list);
//        adapter = new PlotsListAdapter(getContext(), (ArrayList)plots, this);
//        plotList.setAdapter(adapter);



        /*
        sunCalculations = new SunCalculations();


        GardenPlot plot = new GardenPlot(6, 4);
        ShadowObject obj1 = new ShadowObject(10, 220, 7);
        ShadowObject obj2 = new ShadowObject(10,40,5);
        ShadowObject obj3 = new ShadowObject(14,98,8);

        List<ShadowObject> tempList = new ArrayList<>();
        tempList.add(obj1);
        tempList.add(obj2);
        tempList.add(obj3);

        plot.setShadowObjects(tempList);

        Date today = new Date();

        double sunPerDay = sunCalculations.calc_sun_exposure_day(plot, today);
        double sunPerMonth = sunCalculations.calc_sun_exposure_month(plot, 1);

        System.out.println(sunPerDay);
        System.out.println(sunPerMonth);

        WeatherCalculations weatherCalculations = new WeatherCalculations();
        weatherCalculations.calc_weather();

*/
        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePlotFragment createPlotFragment = new CreatePlotFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.garden_fragment_layout, createPlotFragment, "create_plot").addToBackStack(null)
                        .commit();

                //fab.setVisibility(View.GONE);

            }
        });

        return rootView;


    }

    public void AddToList(GardenPlot plot) {
        plots.add(plot);
        adapter = new PlotsListAdapter(getContext(), (ArrayList)plots, this);
        plotList.setAdapter(adapter);
    }
}
