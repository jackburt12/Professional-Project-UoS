package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.adapter.ObjectsListAdapter;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Plot;
import com3001.jb01026.finalyearproject.model.ShadowObject;
import com3001.jb01026.finalyearproject.model.Walk;

public class CreatePlotFragment extends Fragment {

    Context context;
    GardenPlot plot;
    List<ShadowObject> objects = new ArrayList<>();

    GardenPlot editedPlot;
    String plotID;

    EditText nameET, widthET, lengthET;

    FirebaseFirestore firedb = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser fireUser = mAuth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_plot, container, false);
        context = getContext();

        nameET = rootView.findViewById(R.id.plot_name);
        widthET = rootView.findViewById(R.id.plot_width);
        lengthET = rootView.findViewById(R.id.plot_length);

        GardenFragment gardenFragment = (GardenFragment) getActivity().getSupportFragmentManager().findFragmentByTag("garden");

        Button deleteButton = rootView.findViewById(R.id.delete_plot_button);


        try {
            Bundle bundle = getArguments();
            plotID = bundle.getString("plot_id");
            editedPlot = (GardenPlot) bundle.getSerializable("PLOT");
            Log.d("PLOT ID IN CREATE", plotID);

        } catch (Exception e) {
            Log.v("ERROR", e.toString());
        }

        if(editedPlot!=null) {
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firedb.collection("users").document(fireUser.getUid()).collection("plots").document(plotID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Plot was deleted.",Toast.LENGTH_SHORT).show();
                            gardenFragment.getListItems();
                            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("create_plot");
                            if(fragment != null)
                                getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                    });
                }
            });


            nameET.setText(editedPlot.getName());
            widthET.setText(Double.toString(editedPlot.getWidth()));
            lengthET.setText(Double.toString(editedPlot.getLength()));
            if(editedPlot.getShadowObjects()!=null) {
                objects = editedPlot.getShadowObjects();
            }
        }


        //TODO: Gotta check if this plot exists - some online database should be setup


        ListView objectList = rootView.findViewById(R.id.object_list);
        ObjectsListAdapter adapter = new ObjectsListAdapter(context, (ArrayList<ShadowObject>) objects);
        objectList.setAdapter(adapter);

        Button addObjectButton = rootView.findViewById(R.id.add_object_button);
        addObjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View objectCreateView = inflater.inflate(R.layout.object_create_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Create an Object");

                EditText nameEditText = objectCreateView.findViewById(R.id.edit_name);
                EditText heightEditText = objectCreateView.findViewById(R.id.edit_height);
                EditText angleEditText = objectCreateView.findViewById(R.id.edit_angle);
                EditText distanceEditText = objectCreateView.findViewById(R.id.edit_distance);


                //alertDialog.setIcon("Icon id here");
                //alertDialog.setCancelable(false);
                //alertDialog.setMessage("Your Message Here");

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //getActivity().getSupportFragmentManager().popBackStack();
//                        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("walking");
//                        if(fragment != null)
//                            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        String name = nameEditText.getText().toString();
                        double height = Double.parseDouble(heightEditText.getText().toString());
                        double angle = Double.parseDouble(angleEditText.getText().toString());
                        double distance = Double.parseDouble(distanceEditText.getText().toString());

                        ShadowObject object = new ShadowObject(height, angle, distance);
                        object.setName(name);

                        //Add shadowObject to list
                        objects.add(object);
                        adapter.notifyDataSetChanged();

                        dialog.dismiss();


                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setView(objectCreateView);
                alertDialog.show();

            }
        });


        Button createPlotButton = rootView.findViewById(R.id.create_plot_button);

        if(editedPlot!=null) {
            createPlotButton.setText("CONFIRM CHANGES");
        }

        createPlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(widthET.getText().toString().trim().length() == 0 || lengthET.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(),"Please ensure width and length have been entered",Toast.LENGTH_SHORT).show();
                } else {
                    double width = Double.parseDouble(widthET.getText().toString());
                    double length = Double.parseDouble(lengthET.getText().toString());

                    String name = "Plot";
                    if(nameET.getText().toString().trim().length() !=0) {
                        name = nameET.getText().toString();
                    }

                    GardenPlot plot = new GardenPlot(width, length);
                    plot.setName(name);
                    if(objects!= null && !objects.isEmpty()) {
                        plot.setShadowObjects(objects);
                    }

//                    GardenFragment gardenFragment = (GardenFragment) getActivity().getSupportFragmentManager().findFragmentByTag("garden");
//
//                    if(editedPlot!= null) {
//                        editedPlot.setName(plot.getName());
//                        editedPlot.setShadowObjects(plot.getShadowObjects());
//                        editedPlot.setLength(plot.getLength());
//                        editedPlot.setWidth(plot.getWidth());
//                        gardenFragment.adapter.notifyDataSetChanged();
//                    } else {
//                        gardenFragment.AddToList(plot);
//                    }

                    //getActivity().getSupportFragmentManager().popBackStack();



                    if(editedPlot!=null) {
                        firedb.collection("users").document(fireUser.getUid()).collection("plots").document(plotID).set(plot).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(),"Plot was edited.",Toast.LENGTH_SHORT).show();
                                gardenFragment.getListItems();

                            }
                        });
                    } else {
                        firedb.collection("users").document(fireUser.getUid()).collection("plots").add(plot).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(),"Added plot.",Toast.LENGTH_SHORT).show();
                                gardenFragment.getListItems();
                            }
                        });
                    }

                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("create_plot");
                    if(fragment != null)
                        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        });

        return rootView;
    }
}
