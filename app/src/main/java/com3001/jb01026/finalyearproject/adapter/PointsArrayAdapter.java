package com3001.jb01026.finalyearproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.model.Plot;

public class PointsArrayAdapter extends ArrayAdapter<Plot> {

    private Context mContext;
    private ArrayList<Plot> plotList = new ArrayList<>();

    private int limit = 7;
    private boolean cutOff = false;

    public PointsArrayAdapter(@NonNull Context context, ArrayList<Plot> plots) {
        super(context,0, plots);
        mContext = context;
        plotList = plots;

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
        String s = "img_" + plot.getPlant().getImageID();
        image.setImageResource(mContext.getResources().getIdentifier(s, "drawable", mContext.getPackageName()));

        TextView name = point.findViewById(R.id.point_name);
        name.setText(plot.getPlant().getName());

        //return super.getView(position, convertView, parent);
//        if(!cutOff) {
//            if(position > limit) {
//                TextView tv = new TextView(mContext);
//                String temp = new String("And " + (plotList.size()-limit) + " more...");
//                tv.setText(temp);
//                cutOff = false;
//                return null;
//            }
//        } else {
//            return null;
//        }

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
