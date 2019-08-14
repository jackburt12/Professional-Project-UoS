package com3001.jb01026.finalyearproject.adapter;

import android.content.Context;
import android.os.Bundle;
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
import com3001.jb01026.finalyearproject.fragment.CreatePlotFragment;
import com3001.jb01026.finalyearproject.fragment.GardenFragment;
import com3001.jb01026.finalyearproject.fragment.WalksFragment;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.Walk;

public class WalkListAdapter extends ArrayAdapter<Walk> {

    private Context mContext;
    private ArrayList<Walk> walkList;
    private WalksFragment fragment;


    public WalkListAdapter(@NonNull Context context, ArrayList<Walk> walks, WalksFragment fragment) {
        super(context,0, walks);
        mContext = context;
        walkList = walks;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View walkView = convertView;
        if(walkView == null) {
            walkView = LayoutInflater.from(mContext).inflate(R.layout.walk_list_item, parent, false);
        }

        Walk walk = walkList.get(position);

        TextView name = walkView.findViewById(R.id.walk_name);
        TextView pointList = walkView.findViewById(R.id.walk_point_list);

        name.setText(walk.getPointList().size() + " points");
        String points = walk.getPointList().get(0).getName();
        for(int i = 1; i < walk.getPointList().size(); i++) {
            points += ", " + walk.getPointList().get(i).getName();
        }
        pointList.setText(points);

        ImageView delete = walkView.findViewById(R.id.trash);
        delete.setTag(position);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.removeItem((int)v.getTag());
            }
        });

        return walkView;
    }

    @Override
    public int getCount() {
        return walkList.size();
    }

}
