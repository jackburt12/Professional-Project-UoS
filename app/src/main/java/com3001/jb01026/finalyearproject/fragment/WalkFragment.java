package com3001.jb01026.finalyearproject.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.DirectionsHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.RouteNestedLoop;
import com3001.jb01026.finalyearproject.adapter.PointsArrayAdapter;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.Route;
import com3001.jb01026.finalyearproject.model.RouteData;
import com3001.jb01026.finalyearproject.model.Walk;

public class WalkFragment extends Fragment implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15; // 1 minute

    View loadingOverlay;

    private Walk walk;
    private Button endWalk, favouriteWalk;
    MapView mMapView;
    private GoogleMap googleMap;

    private MarkerOptions options = new MarkerOptions();


    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ArrayList<Plant> plantList;
    private ArrayList<Plot> plots;


    Context context;

    private BottomSheetBehavior bottomSheetBehavior;

    private ToggleButton toggleButton;

    private TextView walkRemaining;

    private ListView pointsOfInterest;
    private ArrayList<Plot> points = new ArrayList<>();

    private LocationManager locationManager;
    private LatLng latlng = new LatLng(51.313392, -0.473622);
    private Location loc;
    private boolean isGPS = false;
    private boolean isNetwork = false;
    private boolean canGetLocation = true;

    private DirectionsHelper directionsHelper;

    private DirectionsHelper.AsyncResponse asyncResponse;
    LatLng entrance = new LatLng(51.242481, -0.571469);

    List<LatLng> bestRoute;
    private int bestJourneyDistance;
    private int bestJourneyTime;
    private String[] bestJourneyOrder;
    private RouteData[] bestRouteData;
    private List<Plot> bestPlots;

    private int tasksFinished = 0;
    private int taskCount = 0;

    FirebaseFirestore firedb = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser fireUser = mAuth.getCurrentUser();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_walk, container, false);

        LinearLayout linearLayoutBSheet = rootView.findViewById(R.id.bottom_sheet);
        loadingOverlay = rootView.findViewById(R.id.loading_screen);
        loadingOverlay.setVisibility(View.VISIBLE);

        bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        toggleButton = rootView.findViewById(R.id.toggle_button);
        endWalk = rootView.findViewById(R.id.end_walk_button);
        favouriteWalk = rootView.findViewById(R.id.favourite_walk_button);
        walkRemaining = rootView.findViewById(R.id.time_distance);

        Bundle bundle = getArguments();

        walk = (Walk) bundle.getSerializable("WALK_fav");
        if(walk!=null) {
            favouriteWalk.setVisibility(View.GONE);
        } else {
            walk = (Walk) bundle.getSerializable("WALK");
        }

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

        favouriteWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firedb.collection("users").document(fireUser.getUid()).collection("walks").add(walk).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(),"Added walk to favourites.",Toast.LENGTH_SHORT).show();
                        WalksFragment fragment = (WalksFragment)getActivity().getSupportFragmentManager().findFragmentByTag("walks");
                        fragment.getListItems();
                        favouriteWalk.setVisibility(View.GONE);

                    }
                });
            }
        });

        endWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Are you sure you want to end the walk?");
                //builder.setIcon(R.drawable.ic_launcher);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //getActivity().getSupportFragmentManager().popBackStack();
                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("walking");
                        Fragment fragment2 = getActivity().getSupportFragmentManager().findFragmentByTag("create_walk");
                        if(fragment2!=null) {
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment2).commit();
                        }
                        if(fragment != null)
                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                }
        });

        db = dbHelper.getWritableDatabase();
        plantList = dbHelper.getPlantList(db);

        if(plots==null) {
            generatePlots();
        }


        CalculateBestRoute();



        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        pointsOfInterest = rootView.findViewById(R.id.points_of_interest);


        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            googleMap.setMapType(1);

            googleMap.setMaxZoomPreference(18);
            googleMap.setMinZoomPreference(12);

            CameraPosition cameraPosition = new CameraPosition.Builder().target(entrance).zoom(16).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }else{
                googleMap.setMyLocationEnabled(true);

            }


        });
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

    /*
        Generating plots randomly within certain LatLng bounds of a starting position
        Number of plots dictated by numberOfPlots variable
        Just used my house as entrance for proof of concept purposes, made it easy for testing walks
     */
    public void generatePlots() {

        int numberOfPlots = 40;

        LatLng entrance = new LatLng(51.242481, -0.571469);
        ArrayList<Plot> plotList = new ArrayList<>();

        for(int i = 0; i < numberOfPlots; i++) {
            double upper = 0.00;
            double lower = -0.012;
            double random = (Math.random() * (upper - lower)) + lower;

            double lat = entrance.latitude + random;

            upper = 0.01;
            lower = -0.01;
            random = (Math.random() * (upper - lower)) + lower;

            double lng = entrance.longitude + random;
            plotList.add(new Plot(new LatLng(lat, lng)));
        }

        plots = plotList;

        plantPlots();

    }

    /*
    Weird way of doing things, but good enough for the purpose of a demo/proof of concept
    Randomly assigning two copies of every plant to a plot (assuming list size is 40, if list size is
    different this doesn't work, won't cause a crash but will use a limited list of the plants if smaller,
    or probably do something weird if bigger. This doesn't matter though, as in practice, this function wouldn't
    be used, and a hardcoded list of all the plots around Wisley/whichever garden would instead.
     */
    public void plantPlots() {
        Random r = new Random();
        for (int i = 0; i<plots.size(); i++ ) {
            Plant p = plantList.get((int)Math.floor(i/2));

            int upper = plots.size();
            int lower = 0;
            int rand=r.nextInt(upper-lower) + lower;
            while (plots.get(rand).getPlant() != null) {
                rand = r.nextInt(upper-lower) + lower;
            }
            plots.get(rand).setPlant(p);

        }
    }

    public void CalculateBestRoute()  {

        asyncResponse = (routeData, order, list, plots) -> {

            double minimum = walk.getMinDistance()*1000;
            double maximum = walk.getMaxDistance()*1000;

            int totalTime = 0;
            int totalDistance = 0;

            for (RouteData d : routeData) {
                totalTime += d.getTime();
                totalDistance += d.getDistance();
            }

            if(totalDistance > maximum || totalDistance < minimum) {
                //do nothing
            } else {
                if(bestRoute == null) {
                    bestRoute = list;
                    bestJourneyDistance = totalDistance;
                    bestJourneyTime = totalTime;
                    bestJourneyOrder = order;
                    bestRouteData = routeData;
                    bestPlots = plots;
                } else if(bestJourneyDistance > totalDistance) {
                    bestRoute = list;
                    bestJourneyDistance = totalDistance;
                    bestJourneyTime = totalTime;
                    bestJourneyOrder = order;
                    bestRouteData = routeData;
                    bestPlots = plots;
                } else {
                    //do nothing
                }

            }


            tasksFinished++;

            if(tasksFinished == taskCount) {
                if(bestRoute == null) {
                    Toast.makeText(getContext(),"No route that satisfies requirements! Please modify points or distance restrictions.",Toast.LENGTH_SHORT).show();
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("walking");
                    if(fragment != null)
                        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();

                } else {
                    PopulatePointsList(googleMap, bestRoute);
                }
            }


        };

        List<List<Integer>> indexes = new ArrayList<>();

        List<Plant> pointsOfInterest = walk.getPointList();

        taskCount = 0;
        tasksFinished = 0;

        for(int i = 0; i<pointsOfInterest.size(); i++) {
            List<Integer> temp = new ArrayList<>();
            for(int j = 0; j<plots.size(); j++) {
                if(plots.get(j).getPlant().getName().equals(pointsOfInterest.get(i).getName())) {
                    temp.add(j);
                    taskCount++;
                }
            }
            indexes.add(temp);
        }

        directionsHelper = new DirectionsHelper(asyncResponse);

        int numPoints = indexes.size();
        int count = 2;

        taskCount = (int)Math.pow(count, numPoints);

        for (int i = 0; i < numPoints + 1; i++) {
            final int depth = i;
            RouteNestedLoop.IAction testAction = indices -> {
                if (depth == numPoints) {
                    ArrayList<Plot> tempRoute = new ArrayList<>();
                    int temp = 0;
                    for (int i1 : indices) {
                        System.out.print(" " + i1);
                        tempRoute.add( plots.get(indexes.get(temp).get(i1)));
                        temp++;
                    }

                    Route r = new Route(entrance, tempRoute);

                    new DirectionsHelper(asyncResponse).execute(r);
                }

            };
            RouteNestedLoop nf = new RouteNestedLoop(0, count, testAction);
            nf.nFor(depth);
        }

    }

    /**
     * Ended up not implementing any user movement functions due to lack of time
     * this method works for getting location but is unused
     */

//    private void getLocation() {
//        try {
//            if (canGetLocation) {
//                if (isGPS) {
//                    // from GPS
//                    locationManager.requestLocationUpdates(
//                            LocationManager.GPS_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//                    if (locationManager != null) {
//                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                        if (loc != null)
//                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
//                    }
//                } else if (isNetwork) {
//                    // from Network Provider
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//                    if (locationManager != null) {
//                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (loc != null)
//                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
//                    }
//                } else {
//                    // from Network Provider
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//
//                    if (locationManager != null) {
//                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (loc != null)
//                            latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
//                    }
//                }
//            } else {
//                //Can't get location
//            }
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }



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

    public Bitmap createCustomMarker(Context context, Plant plant) {

        String s = "img_" + plant.getImage();

        int expectedHeight = 150;
        int expectedWidth = 100;

        Bitmap original = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(s, "drawable", context.getPackageName()));
        Bitmap mask = getBitmap(R.drawable.custom_marker_shape);

        original = getResizedBitmap(original, dipToPixels(context, expectedHeight), dipToPixels(context, expectedWidth));

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        int widthMask = mask.getWidth();
        int heightMask = mask.getHeight()+50;

        float centerX = (widthMask - original.getWidth()) * 0.5f;
        float centerY = (heightMask - original.getHeight()) * 0.5f;

        mCanvas.drawBitmap(original, centerX, centerY, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        return result;
    }

    public Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, float newHeight, float newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }


    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


    public void PopulatePointsList(GoogleMap googleMap, List<LatLng> list) {
        for(int i = 0; i<walk.getPointList().size(); i++) {
            options.position(bestPlots.get(Integer.parseInt(bestJourneyOrder[i])).getLocation());
            Plant p = walk.getPointList().get(Integer.parseInt(bestJourneyOrder[i]));
            if(p!=null) {
                options.title(p.getName());
                options.snippet(p.getType().toString());
                options.icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(context, p)));
            }
            googleMap.addMarker(options);
        }

        String formattedTime = Integer.toString(bestJourneyTime/60) + " min";
        DecimalFormat df = new DecimalFormat("0.00");
        double distanceKM = new Double(bestJourneyDistance)/1000;
        String formattedDistance = df.format(distanceKM) + " km";

        walkRemaining.setText(formattedTime + " - " + formattedDistance);

        for (int i = 0; i<bestJourneyOrder.length; i++) {
            points.add(bestPlots.get(Integer.parseInt(bestJourneyOrder[i])));
        }

        PointsArrayAdapter pAdapter =  new PointsArrayAdapter(getContext(), points, bestRouteData);

        pointsOfInterest.setAdapter(pAdapter);

        Polyline line = googleMap.addPolyline(new PolylineOptions()
                .addAll(list)
                .width(12)
                .color(Color.parseColor("#05b1fb"))//Google maps blue color
                .geodesic(true)
        );

        loadingOverlay.setVisibility(View.GONE);
    }
}
