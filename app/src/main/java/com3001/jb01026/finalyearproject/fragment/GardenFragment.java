package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_garden, container, false);

        plotList = rootView.findViewById(R.id.plot_list);

        plotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GardenPlot plot = adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable("garden_plot", plot);

                GardenPlotFragment plotFragment = new GardenPlotFragment();
                plotFragment.setArguments(bundle);

                //getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.filter_drawer, plotFragment, "garden_plot_fragment").addToBackStack(null)
                        .commit();

            }
        });

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
