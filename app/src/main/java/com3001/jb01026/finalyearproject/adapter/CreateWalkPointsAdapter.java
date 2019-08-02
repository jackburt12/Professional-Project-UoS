package com3001.jb01026.finalyearproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.model.Plant;

public class CreateWalkPointsAdapter extends ArrayAdapter {

    Context mContext;
    List<Plant> plants;

    public CreateWalkPointsAdapter(@NonNull Context context, @NonNull List plants) {
        super(context, 0, plants);
        this.mContext = context;
        this.plants = plants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.point_of_interest_create, parent, false);
        }

        TextView text = view.findViewById(R.id.point_create_text);

        text.setText(plants.get(position).getName());

        ImageView deleteButton = view.findViewById(R.id.trash);
        deleteButton.setTag(position);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("CLICKED:", Integer.toString((int)v.getTag()));
                plants.remove((int)v.getTag());
                CreateWalkPointsAdapter.this.notifyDataSetChanged();
            }
        });


        return view;
    }
}
