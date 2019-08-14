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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.johnhiott.darkskyandroidlib.ForecastApi;
import com.johnhiott.darkskyandroidlib.RequestBuilder;
import com.johnhiott.darkskyandroidlib.models.Request;
import com.johnhiott.darkskyandroidlib.models.WeatherResponse;

import androidx.annotation.NonNull;
import com3001.jb01026.finalyearproject.WeatherCalculations;
import com3001.jb01026.finalyearproject.adapter.CreateWalkPointsAdapter;
import com3001.jb01026.finalyearproject.adapter.PlotsListAdapter;
import com3001.jb01026.finalyearproject.adapter.WalkListAdapter;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.Walk;
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
    List<String> plotIDs = new ArrayList<>();
    ListView plotList;
    PlotsListAdapter adapter;

    FirebaseFirestore firedb = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser fireUser = mAuth.getCurrentUser();

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
                String plotID = plotIDs.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("garden_plot", plot);

                GardenPlotFragment plotFragment = new GardenPlotFragment();
                plotFragment.setArguments(bundle);

                //getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.garden_fragment_layout, plotFragment, "garden_plot_fragment").addToBackStack(null)
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

        getListItems();

        return rootView;


    }

    public void getListItems() {

        plots = new ArrayList<>();
        plotIDs = new ArrayList<>();

        firedb.collection("users").document(fireUser.getUid()).collection("plots").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        adapter = new PlotsListAdapter(getContext(), (ArrayList)plots, (ArrayList)plotIDs, (GardenFragment) getActivity().getSupportFragmentManager().findFragmentByTag("garden"));
                        plotList.setAdapter(adapter);
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        GardenPlot plot = (document.toObject(GardenPlot.class));
                        plots.add(plot);
                        plotIDs.add(document.getId());

                        adapter = new PlotsListAdapter(getContext(), (ArrayList)plots, (ArrayList)plotIDs, (GardenFragment) getActivity().getSupportFragmentManager().findFragmentByTag("garden"));
                        plotList.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(),"Error getting plots.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
