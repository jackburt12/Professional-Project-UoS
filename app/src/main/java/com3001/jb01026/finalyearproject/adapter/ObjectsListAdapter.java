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
import com3001.jb01026.finalyearproject.model.ShadowObject;

public class ObjectsListAdapter extends ArrayAdapter<ShadowObject> {

    private Context mContext;
    private ArrayList<ShadowObject> objects = new ArrayList<>();

    public ObjectsListAdapter(@NonNull Context context, ArrayList<ShadowObject> objects) {
        super(context,0, objects);
        mContext = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View objectView = convertView;

        if(objectView == null) {
            objectView = LayoutInflater.from(mContext).inflate(R.layout.shadow_object_item, parent, false);
        }

        ShadowObject object = objects.get(position);

        TextView name = objectView.findViewById(R.id.object_name);
        TextView height = objectView.findViewById(R.id.object_height);
        TextView distance = objectView.findViewById(R.id.object_distance);
        TextView angle = objectView.findViewById(R.id.object_angle);

        name.setText(object.getName());
        height.setText(Double.toString(object.getHeight()));
        distance.setText(Double.toString(object.getDistanceFromPlot()));
        angle.setText(Double.toString(object.getAngleFromPlot()));

        ImageView delete = objectView.findViewById(R.id.trash);
        delete.setTag(position);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objects.remove((int)v.getTag());
                ObjectsListAdapter.this.notifyDataSetChanged();
            }
        });

        return objectView;
    }

    @Override
    public int getCount() {

        if(objects == null) {
            return 0;
        }
        return objects.size();
    }


}
