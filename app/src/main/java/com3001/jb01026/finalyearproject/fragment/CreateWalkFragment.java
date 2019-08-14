package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.activity.MainActivity;
import com3001.jb01026.finalyearproject.adapter.CreateWalkPointsAdapter;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.Walk;

public class CreateWalkFragment extends Fragment {

    private final static String POINTS_FRAGMENT = "walking";

    boolean dragging = false;
    RangeBar distanceRange;
    EditText minDistEditText, maxDistEditText;
    ListView pointsList;
    Button createWalkButton, choosePointsButton;

    String[] pinLabels = new String[]{"0", "0.5", "1", "2", "3", "4", "5", "7.5", "10", DecimalFormatSymbols.getInstance().getInfinity()};
    List<Plant> pointsofInterest = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    private ArrayList<Plant> plantList;

    private CreateWalkPointsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_create_walk, container, false);

        distanceRange = rootView.findViewById(R.id.distance_range);
        minDistEditText = rootView.findViewById(R.id.min_value);
        maxDistEditText = rootView.findViewById(R.id.max_value);


        distanceRange.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {

                if(dragging) {
                    minDistEditText.setText(pinLabels[leftPinIndex]);
                    maxDistEditText.setText(pinLabels[rightPinIndex]);
                }
            }

            @Override
            public void onTouchStarted(RangeBar rangeBar) {
                dragging = true;
            }

            @Override
            public void onTouchEnded(RangeBar rangeBar) {
                dragging = false;
            }
        });

        //TODO: Getting some weird bug when number goes to 7.5 where I can't manually type anymore

        minDistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()!=null && s.toString().length() > 0 && dragging == false) {
                    double textValue = Double.parseDouble(s.toString());

                    if(textValue > 10) {
                        distanceRange.setRangePinsByIndices(9,distanceRange.getRightIndex());
                    }
                    else {
                        int closestLabel = 0;
                        for(int i = 0; i<pinLabels.length-1; i++) {
                            double d = Double.parseDouble(pinLabels[i]);
                            double previousDistance = Math.abs(Double.parseDouble(pinLabels[closestLabel]) - textValue);
                            double newdistance = Math.abs(d - textValue);
                            if(newdistance < previousDistance) {
                                closestLabel = i;
                            }
                        }

                        distanceRange.setRangePinsByIndices(closestLabel,distanceRange.getRightIndex());
                        //getting a weird infinite loop I think if the following happens:
                        //minDistEditText.setText(pinLabels[distanceRange.getLeftIndex()]);
                    }
                    minDistEditText.removeTextChangedListener(this);
                    s.replace(0, s.length(), pinLabels[distanceRange.getLeftIndex()]);
                    minDistEditText.addTextChangedListener(this);
                }
            }
        });

        maxDistEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString()!=null && s.toString().length() > 0 && dragging == false) {
                    double textValue = Double.parseDouble(s.toString());

                    if(textValue > 10) {
                        distanceRange.setRangePinsByIndices(distanceRange.getLeftIndex(),9);
                    }
                    else {
                        int closestLabel = 0;
                        for(int i = 0; i<pinLabels.length-1; i++) {
                            double d = Double.parseDouble(pinLabels[i]);
                            double previousDistance = Math.abs(Double.parseDouble(pinLabels[closestLabel]) - textValue);
                            double newdistance = Math.abs(d - textValue);
                            if(newdistance < previousDistance) {
                                closestLabel = i;
                            }
                        }

                        distanceRange.setRangePinsByIndices(distanceRange.getLeftIndex(),closestLabel);
                        //getting a weird infinite loop I think if the following happens:
                        //minDistEditText.setText(pinLabels[distanceRange.getLeftIndex()]);
                    }
                    maxDistEditText.removeTextChangedListener(this);
                    s.replace(0, s.length(), pinLabels[distanceRange.getRightIndex()]);
                    maxDistEditText.addTextChangedListener(this);
                }
            }
        });

        distanceRange.setFormatter(value -> pinLabels[Integer.parseInt(value)] + "km");

        pointsList = rootView.findViewById(R.id.points_list);

        db = dbHelper.getWritableDatabase();
        plantList = dbHelper.getPlantList(db);

        choosePointsButton = rootView.findViewById(R.id.choose_points_button);

        choosePointsButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setCreatingWalk(true);

            EncyclopediaFragment encyclopediaFragment = new EncyclopediaFragment();

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.create_walk_layout, encyclopediaFragment, POINTS_FRAGMENT).addToBackStack(null)
                    .commit();
        });

        createWalkButton = rootView.findViewById(R.id.create_walk_button);
        createWalkButton.setOnClickListener(v ->  {

            if(!pointsofInterest.isEmpty()) {
                WalkFragment walkFragment = new WalkFragment();
                double min = Double.parseDouble(minDistEditText.getText().toString());
                double max;
                if(!maxDistEditText.getText().toString().equals("∞")) {
                    max =  Double.parseDouble(maxDistEditText.getText().toString());
                } else {
                    max = 999;
                }

                Walk walk = new Walk(min,max, pointsofInterest);

                Bundle bundle = new Bundle();
                bundle.putSerializable("WALK", walk);

                walkFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.create_walk_layout, walkFragment, POINTS_FRAGMENT)
                        .commit();

            } else {
                Toast.makeText(getContext(),"Please add some points of interest first!",Toast.LENGTH_SHORT).show();

            }

        });


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);

    }

    public void UpdateList(List<Plant> list) {
        pointsofInterest = list;
        adapter = new CreateWalkPointsAdapter(getContext(), list);
        pointsList.setAdapter(adapter);
    }
}
