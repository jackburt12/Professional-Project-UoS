package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.SunCalculations;
import com3001.jb01026.finalyearproject.adapter.PlotsListAdapter;
import com3001.jb01026.finalyearproject.adapter.WalkListAdapter;
import com3001.jb01026.finalyearproject.model.GardenPlot;
import com3001.jb01026.finalyearproject.model.Walk;

public class WalksFragment extends Fragment {

    FirebaseFirestore firedb = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser fireUser = mAuth.getCurrentUser();

    List<Walk> walks = new ArrayList<>();
    List<String> walkIDs;
    ListView walkListView;
    WalkListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_walks, container, false);

        walkListView = rootView.findViewById(R.id.walk_list);

        walkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Walk walk = adapter.getItem(position);

                Bundle bundle = new Bundle();
                bundle.putSerializable("WALK_fav", walk);

                WalkFragment walkFragment = new WalkFragment();
                walkFragment.setArguments(bundle);

                //getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.walks_fragment_layout, walkFragment, "walking").addToBackStack(null)
                        .commit();

            }
        });

        FloatingActionButton fab = rootView.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateWalkFragment createWalkFragment = new CreateWalkFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.walks_fragment_layout, createWalkFragment, "create_walk").addToBackStack(null)
                        .commit();

                //fab.setVisibility(View.GONE);

            }
        });

        getListItems();

        return rootView;
    }

    public void AddToList(Walk walk) {
        walks.add(walk);
        adapter = new WalkListAdapter(getContext(), (ArrayList)walks, this);
        walkListView.setAdapter(adapter);
    }

    public void getListItems() {

        walks = new ArrayList<>();
        walkIDs = new ArrayList<>();

        firedb.collection("users").document(fireUser.getUid()).collection("walks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        adapter = new WalkListAdapter(getContext(), (ArrayList)walks, (WalksFragment) getActivity().getSupportFragmentManager().findFragmentByTag("walks"));
                        walkListView.setAdapter(adapter);
                    }
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Walk walk = (document.toObject(Walk.class));
                        walks.add(walk);
                        walkIDs.add(document.getId());

                        adapter = new WalkListAdapter(getContext(), (ArrayList)walks, (WalksFragment) getActivity().getSupportFragmentManager().findFragmentByTag("walks"));
                        walkListView.setAdapter(adapter);
                    }
                } else {
                    Toast.makeText(getContext(),"Error getting walks.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void removeItem(int position) {

        firedb.collection("users").document(fireUser.getUid()).collection("walks").document(walkIDs.get(position)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Walk was deleted.",Toast.LENGTH_SHORT).show();
            }
        });

        getListItems();

    }

}
