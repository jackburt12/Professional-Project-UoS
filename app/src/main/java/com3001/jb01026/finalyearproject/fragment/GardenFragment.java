package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.SunCalculations;

public class GardenFragment extends Fragment {

    private LatLng gardenLocation = new LatLng(51.242560, -0.571569); //this is my address as a temp location, should be user entered obvs
    public SunCalculations sunCalculations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sunCalculations = new SunCalculations();




        return inflater.inflate(R.layout.fragment_garden, container, false);


    }
}
