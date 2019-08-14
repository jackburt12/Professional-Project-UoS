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
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.RouteData;
import com3001.jb01026.finalyearproject.model.ShadowObject;
import com3001.jb01026.finalyearproject.model.Walk;

public class PlotsListAdapter extends ArrayAdapter<GardenPlot> {

    private Context mContext;
    private ArrayList<GardenPlot> plotList;
    private GardenFragment fragment;
    private ArrayList<String> plotIDs;


    public PlotsListAdapter(@NonNull Context context, ArrayList<GardenPlot> plots, ArrayList<String> ids, GardenFragment fragment) {
        super(context,0, plots);
        mContext = context;
        plotList = plots;
        plotIDs = ids;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View objectView = convertView;
        if(objectView == null) {
            objectView = LayoutInflater.from(mContext).inflate(R.layout.garden_plot_item, parent, false);
        }

        GardenPlot plot = plotList.get(position);

        TextView name = objectView.findViewById(R.id.plot_name);
        TextView width = objectView.findViewById(R.id.plot_width);
        TextView length = objectView.findViewById(R.id.plot_length);
        TextView objects = objectView.findViewById(R.id.plot_objects);

        name.setText(plot.getName());
        width.setText(Double.toString(plot.getWidth()));
        length.setText(Double.toString(plot.getLength()));
        objects.setText(Integer.toString(plot.countObjects()));

        ImageView edit = objectView.findViewById(R.id.edit);
        edit.setTag(position);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //plotList.remove((int)v.getTag());
                //0PlotsListAdapter.this.notifyDataSetChanged();

                CreatePlotFragment createPlotFragment = new CreatePlotFragment();

                Bundle bundle = new Bundle();
                bundle.putSerializable("PLOT", plot);
                bundle.putString("plot_id", plotIDs.get(position));


                createPlotFragment.setArguments(bundle);

                fragment.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.garden_fragment_layout, createPlotFragment, "create_plot").addToBackStack(null)
                        .commit();
            }
        });

        return objectView;
    }

    @Override
    public int getCount() {
        return plotList.size();
    }
}
