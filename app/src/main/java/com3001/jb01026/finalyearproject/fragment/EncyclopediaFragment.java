package com3001.jb01026.finalyearproject.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;
import com.turingtechnologies.materialscrollbar.TouchScrollBar;

import java.util.ArrayList;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.adapter.CustomRecyclerAdapter;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlantType;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EncyclopediaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EncyclopediaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncyclopediaFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Plant> plantList = new ArrayList<Plant>();
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    ListView listView;
    RecyclerView recyclerView;
    SearchView searchView;
    ImageView filterButton;
    DrawerLayout filterDrawer;


    View rootView;

    Context context;

    private OnFragmentInteractionListener mListener;

    public EncyclopediaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EncyclopediaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EncyclopediaFragment newInstance(String param1, String param2) {
        EncyclopediaFragment fragment = new EncyclopediaFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        CustomListAdapter customLA = new CustomListAdapter((Activity) getContext(), nameArr, typeArr);
//
//        listView = rootView.findViewById(R.id.list_view);
//        listView.setAdapter(customLA);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            db = dbHelper.getWritableDatabase();
            Log.d("DID GET WRITABLE DATABASE", "yay");

            Cursor c = db.rawQuery("SELECT Name, Type, Image FROM Plants", null);

            if(c!=null) {
                if(c.moveToFirst()) {
                    do {
                        String name = c.getString(c.getColumnIndex("Name"));
                        String type = c.getString(c.getColumnIndex("Type"));
                        String image = c.getString(c.getColumnIndex("Image"));


                        PlantType pType = null;

                        switch (type) {
                            case "Vegetable":
                                pType = PlantType.VEGETABLE;
                                break;
                            case "Fruit":
                                pType = PlantType.FRUIT;
                                break;
                            case "Herb":
                                pType = PlantType.HERB;
                                break;

                        }
                        Plant p = new Plant(name, pType, image);

                        plantList.add(p);
                    } while(c.moveToNext());
                }
            }

        } catch (SQLException se) {
            Log.d("DIDN'T GET WRITABLE DATABASE", "bollocks");

        } finally {
            if(db!=null) {
                db.execSQL("DELETE FROM Plants");
                db.close();
            }
        }


//        CustomListAdapter customLA = new CustomListAdapter((Activity) getContext(), plantList);
//        // Inflate the layout for this fragment
//        rootView =  inflater.inflate(R.layout.fragment_encyclopedia, container, false);
//
//        listView = rootView.findViewById(R.id.list_view);
//        listView.setAdapter(customLA);

        CustomRecyclerAdapter customRecyclerAdapter = new CustomRecyclerAdapter(plantList);

        rootView = inflater.inflate(R.layout.fragment_encyclopedia, container, false);

        searchView = rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                customRecyclerAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });

        ((DragScrollBar)rootView.findViewById(R.id.dragScrollBar)).setIndicator(new AlphabetIndicator(context), true);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(customRecyclerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        filterDrawer = rootView.findViewById(R.id.filter_drawer);
        filterButton = rootView.findViewById(R.id.filter_icon);
        filterButton.setOnClickListener(new View.OnClickListener() {

        boolean drawerOpen = false;

        @Override
        public void onClick(View v) {
            filterButton = rootView.findViewById(R.id.filter_icon);
            //filterDrawer.openDrawer(GravityCompat.END);
            if(!drawerOpen) {
                filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
            }else {
                filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            }
        }

        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        dbHelper = new DatabaseHelper(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.filter_drawer);
        Menu menu = navigationView.getMenu();
        menu.setGroupVisible(R.id.category_group, false);
        menu.setGroupVisible(R.id.plot_group, false);
        int id = item.getItemId();
        switch (id) {
            case R.id.category_group:
                menu.setGroupVisible(R.id.category_group, true);
                return true;
            case R.id.plot_group:
                menu.setGroupVisible(R.id.category_group, true);
                return true;

        }


        return true;
    }


}
