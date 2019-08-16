package com3001.jb01026.finalyearproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.Route;
import com3001.jb01026.finalyearproject.model.RouteData;


public class DirectionsHelper extends AsyncTask<Route, String, String> {

    public interface AsyncResponse {
        void processFinish(RouteData[] routeData, String[] waypointOrder, List<LatLng> decodedList, List<Plot> plots);
    }

    public AsyncResponse delegate = null;

    private LatLng start;
    private ArrayList<Plot> plots;
    private GoogleMap map;

    /**
     *   decodePoly() method lifted directly from a response on the following StackOverflow thread:
     *   https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
     *
     *   extremely similar methods can be found in many different locations, and it seems as if this is more or less
     *   template code that is almost always used when dealing with the Directions API
     *
     *   as explained in the report, much of this code can be attributed to Google's Directions API developer guide
     *   with parts taking inspiration from code found on stack overflow - primarily the question/answers from the thread linked above
     *
     **/

    public DirectionsHelper(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(Route... pRoute) {

        Route route = pRoute[0];
        this.start = route.getStart();
        this.plots = route.getPlots();

        String key = "AIzaSyC0ZgbFDEsvp56h5VUw8g1j2LldFv-2_F4";
        String origin = "origin=" + start.latitude + "," + start.longitude;
        String waypoints = "waypoints=optimize:true|";

        for(int i = 0; i < plots.size(); i++) {
            waypoints = waypoints + plots.get(i).getLocation().latitude + "," + plots.get(i).getLocation().longitude + "|";
        }
        String destination = "destination=" + start.latitude + "," + start.longitude;

        String sensor = "sensor=false";
        String params = origin + "&" + waypoints + "&"  + destination + "&" + sensor + "&mode=walking";
        String output = "json?";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + params + "&key=" + key;

        InputStream is;
        String json = "";

        try {

            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            }


        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        return json;

    }

    @Override
    protected void onPostExecute(String json) {

        try {
            //Tranform the string into a json object
            final JSONObject jsonObject = new JSONObject(json);
            JSONArray routeArray = jsonObject.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);


            JSONArray legsArray = routes.getJSONArray("legs");

            RouteData[] routeData = new RouteData[legsArray.length()];

            for(int i = 0; i< legsArray.length(); i++) {
                int distance = legsArray.getJSONObject(i).getJSONObject("distance").getInt("value");
                int time = legsArray.getJSONObject(i).getJSONObject("duration").getInt("value");

                routeData[i] = new RouteData(time, distance);
            }

            String waypointOrder = routes.getString("waypoint_order").split("[\\[\\]]")[1];
            String[] orderArray = waypointOrder.split(",");

            delegate.processFinish(routeData, orderArray, list, plots);

        } catch (JSONException e) {
            Log.v("FAILED",e.toString());
        }
    }


    public List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}

