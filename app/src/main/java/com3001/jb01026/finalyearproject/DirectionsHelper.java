package com3001.jb01026.finalyearproject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com3001.jb01026.finalyearproject.model.Plot;

public class DirectionsHelper extends AsyncTask<String, String, String> {

    private LatLng start;

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    public String getMapsApiDirectionsUrl(ArrayList<Plot> plots) {

        String key = "AIzaSyC0ZgbFDEsvp56h5VUw8g1j2LldFv-2_F4";
        String origin = "origin=" + start.latitude + "," + start.longitude;
        String waypoints = "waypoints=optimize:true|";

        for(int i = 0; i < plots.size(); i++) {
            waypoints = waypoints + plots.get(i).getLocation().latitude + "," + plots.get(i).getLocation().longitude + "|";
        }
        String destination = "destination=" + start.latitude + "," + start.longitude;

        String sensor = "sensor=false";
        String params = origin + "&" + waypoints + "&"  + destination + "&" + sensor + "&travelMode=walking";
        String output = "json?";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + params + "&key=" + key;

        Log.v("URL:", url);
        return url;
    }


    public String getJSONFromUrl(String url) {
        InputStream is = null;
        JSONObject jObj = null;
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
                    Log.v("TEST", line
                    );
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
