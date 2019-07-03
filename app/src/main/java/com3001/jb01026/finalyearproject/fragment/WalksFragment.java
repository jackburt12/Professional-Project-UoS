package com3001.jb01026.finalyearproject.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.DirectionsHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.adapter.PointsArrayAdapter;
import com3001.jb01026.finalyearproject.model.CareFrequency;
import com3001.jb01026.finalyearproject.model.Expertise;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlantType;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.PlotSize;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class WalksFragment extends Fragment implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15; // 1 minute

    MapView mMapView;
    private GoogleMap googleMap;

    private MarkerOptions options = new MarkerOptions();


    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    Context context;

    private ArrayList<Plot> plots;
    private ArrayList<Plant> plantList;

    private Polyline route;

    private BottomSheetBehavior bottomSheetBehavior;

    private ToggleButton toggleButton;

    private ListView pointsOfInterest;
    private ArrayList<Plot> points = new ArrayList<>();

    private LocationManager locationManager;
    private LatLng latlng;
    private Location loc;
    private boolean isGPS = false;
    private boolean isNetwork = false;
    private boolean canGetLocation = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_walks, container, false);
        LinearLayout linearLayoutBSheet = rootView.findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);

        toggleButton = rootView.findViewById(R.id.toggle_button);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    toggleButton.setChecked(true);
                } else if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    toggleButton.setChecked(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        db = dbHelper.getWritableDatabase();

        plantList = dbHelper.getPlantList(db);
        generatePlots();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                googleMap.setMapType(1);

                googleMap.setMaxZoomPreference(18);
                googleMap.setMinZoomPreference(12);

                LatLng wisley = new LatLng(51.313392, -0.473622);
                //googleMap.addMarker(new MarkerOptions().position(wisley).title("Marker Title").snippet("Marker Description"));

                for (Plot plot: plots) {
                    options.position(plot.getLocation());
                    if(plot.getPlant()!=null) {
                        options.title(plot.getPlant().getName());
                        options.snippet(plot.getPlant().getType().toString());
                    }

                    googleMap.addMarker(options);
                }
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(wisley).zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    return;
                }else{
                    googleMap.setMyLocationEnabled(true);

                }

            }

        });

//        View slidingPane = rootView.findViewById(R.id.sliding_layout_pane);
//
//        slidingPane.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
//        slidingPane.setClipToOutline(true);

        locationManager = (LocationManager)this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();
//        String urlResult = getMapsApiDirectionsUrl();
//
//        drawPath(getJSONFromUrl(urlResult));

        for (int i = 0; i<5; i++) {
            points.add(plots.get(i));
        }

        PointsArrayAdapter pAdapter =  new PointsArrayAdapter(this.getContext(), points);
        pointsOfInterest = rootView.findViewById(R.id.points_of_interest);

        pointsOfInterest.setAdapter(pAdapter);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        dbHelper = new DatabaseHelper(context);

    }

    public void generatePlots() {

        LatLng wisley = new LatLng(51.313392, -0.473622);

        ArrayList<Plot> plotList = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            double upper = 0.002;
            double lower = -0.002;
            double random = (Math.random() * (upper - lower)) + lower;

            double lat = wisley.latitude + random;

            upper = 0.003;
            lower = -0.003;
            random = (Math.random() * (upper - lower)) + lower;

            double lng = wisley.longitude + random;
            plotList.add(new Plot(new LatLng(lat, lng)));
        }

        plots = plotList;

        plantPlots();

    }

    public void plantPlots() {
        for(Plot plot : plots) {
            double upper = plantList.size()-1;
            double lower = 0;
            int i = (int)Math.ceil((Math.random() * (upper - lower)) + lower);
            plot.setPlant(plantList.get(i));
        }
    }


    public void drawPath() {

        DirectionsHelper directionsHelper = new DirectionsHelper();

        String url = directionsHelper.getMapsApiDirectionsUrl(plots);
        String result = directionsHelper.getJSONFromUrl(url);


        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = directionsHelper.decodePoly(encodedString);

            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );

        } catch (JSONException e) {
            Log.v("FAILED",e.toString());
        }
    }


    private void getLocation() {
        try {
            if (canGetLocation) {
                if (isGPS) {
                    // from GPS
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    }
                } else {
//                    loc.setLatitude(0);
//                    loc.setLongitude(0);
//                    latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    // from Network Provider
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                    }
                }
            } else {
                //Can't get location
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
