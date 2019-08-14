package com3001.jb01026.finalyearproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.RouteData;

public class PointsArrayAdapter extends ArrayAdapter<Plot> {

    private Context mContext;
    private ArrayList<Plot> plotList;
    private RouteData[] routeData;

    private int limit = 7;

    public PointsArrayAdapter(@NonNull Context context, ArrayList<Plot> plots, RouteData[] data) {
        super(context,0, plots);
        mContext = context;
        plotList = plots;
        routeData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View point = convertView;

        if(point == null) {
            point = LayoutInflater.from(mContext).inflate(R.layout.point_of_interest, parent, false);
        }

        Plot plot = plotList.get(position);

        ImageView image = point.findViewById(R.id.point_of_interest_icon);
        String s = "img_" + plot.getPlant().getImage();
        image.setImageResource(mContext.getResources().getIdentifier(s, "drawable", mContext.getPackageName()));

        TextView name = point.findViewById(R.id.point_name);
        name.setText(plot.getPlant().getName());

        TextView distanceTime = point.findViewById(R.id.point_distance_time);

        int distance = 0;
        int time = 0;

        for(int i = 0; i<=position; ++i) {
            distance += routeData[i].getDistance();
            time += routeData[i].getTime();
        }

        String formattedTime = Integer.toString(time/60) + " min";
        DecimalFormat df = new DecimalFormat("0.00");
        double distanceKM = (double) distance /1000;
        String formattedDistance = df.format(distanceKM) + " km";

        String dtString = formattedTime + " - " + formattedDistance;

        distanceTime.setText(dtString);

        return point;
    }

    @Override
    public int getCount() {
        if (plotList != null) {
            return Math.min(plotList.size(), limit + 1);
        } else {
            return 0;
        }
    }
}
