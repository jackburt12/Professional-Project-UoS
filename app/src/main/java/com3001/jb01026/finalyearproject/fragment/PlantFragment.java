package com3001.jb01026.finalyearproject.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlotSize;

public class PlantFragment extends Fragment {

    Plant plant;

    ImageView plantImageIV;
    TextView plantTitleTV, plantDescriptionTV, plotFieldTV, careFieldTV, expertiseFieldTV, plantMonth;


    public PlantFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_plant, container, false);

        plantImageIV = view.findViewById(R.id.plant_image);
        plantTitleTV = view.findViewById(R.id.plant_title);
        plantDescriptionTV = view.findViewById(R.id.plant_description);

        View monthByMonth = view.findViewById(R.id.month_by_month);

        Bundle bundle = getArguments();
        plant = (Plant) bundle.getSerializable("plant");

        plantTitleTV.setText(plant.getName());
        plantDescriptionTV.setText(plant.getDescription());

        String s = "img_" + plant.getImageID();
        plantImageIV.setImageResource(getContext().getResources().getIdentifier(s, "drawable", getContext().getPackageName()));

        plotFieldTV = view.findViewById(R.id.plot_field);
        careFieldTV = view.findViewById(R.id.care_field);
        expertiseFieldTV = view.findViewById(R.id.expertise_field);

        Log.v("PLOTSIZE: ", plant.getName().toString());

        plotFieldTV.setText(plant.getPlotSize().toString());
        careFieldTV.setText(plant.getCareFrequency().toString());
        expertiseFieldTV.setText(plant.getExpertise().toString());

        View v =  view.findViewById(R.id.months);

      //  ((ViewGroup)v).getChildAt(1).setBackgroundResource(R.drawable.both_circle);


//        for(int index=0; index<((ViewGroup)v).getChildCount(); ++index) {
//            View nextChild = ((ViewGroup)v).getChildAt(index);
//        }



        String[] monSplit = plant.getMonthByMonth().split("/");
        char[] plantMonthsChar = monSplit[0].toCharArray();
        char[] harvestMonthsChar = monSplit[1].toCharArray();

        Log.v("plantMonthsChar", Arrays.toString(plantMonthsChar));
        Log.v("harvestMonthsChar", Arrays.toString(harvestMonthsChar));


        int[] plantMonths = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
        int[] harvestMonths = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};



        for(int i = 0; i < plantMonthsChar.length; i++){
            plantMonths[Integer.parseInt(Character.toString(plantMonthsChar[i]), 16)-1] = 1;
        }

        for(int i = 0; i< harvestMonthsChar.length; i++){
            harvestMonths[Integer.parseInt(Character.toString(harvestMonthsChar[i]), 16)-1] = 1;
        }

        Log.v("plantMonths", Arrays.toString(plantMonths));
        Log.v("harvestMonths", Arrays.toString(harvestMonths));

        for(int i = 0; i<12; i++) {
            View circleToChange = ((ViewGroup)((ViewGroup)monthByMonth).getChildAt(i)).getChildAt(1);

            if(plantMonths[i] == 1) {
                if(harvestMonths[i] == 1) {
                    circleToChange.setBackgroundResource(R.drawable.both_circle);
                } else {
                    circleToChange.setBackgroundResource(R.drawable.plant_circle);

                }
            } else if(harvestMonths[i] == 1) {
                circleToChange.setBackgroundResource(R.drawable.harvest_circle);
            }
        }






        return view;
    }
}
