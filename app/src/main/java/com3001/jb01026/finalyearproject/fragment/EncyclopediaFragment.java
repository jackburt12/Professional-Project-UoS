package com3001.jb01026.finalyearproject.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;

import com.google.android.material.navigation.NavigationView;
import com.turingtechnologies.materialscrollbar.AlphabetIndicator;
import com.turingtechnologies.materialscrollbar.DragScrollBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.activity.MainActivity;
import com3001.jb01026.finalyearproject.adapter.CreateWalkPointsAdapter;
import com3001.jb01026.finalyearproject.adapter.EncyclopediaRecyclerAdapter;
import com3001.jb01026.finalyearproject.adapter.FilterListAdapter;
import com3001.jb01026.finalyearproject.adapter.PointsArrayAdapter;
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
public class EncyclopediaFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener  {
    private final static String PLANT_FRAGMENT = "plant";

    ArrayList<Plant> plantList = new ArrayList<Plant>();
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    ListView listView;
    RecyclerView recyclerView;
    SearchView searchView;
    ImageView filterButton;
    DrawerLayout filterDrawer;

    EncyclopediaRecyclerAdapter encyclopediaRecyclerAdapter;
    FilterListAdapter filterListAdapter;

    View rootView;

    Context context;
    MainActivity mainActivity;

    ExpandableListView expandableListView;

    String checkBoxFilterQuery;
    String searchQuery;


    private OnFragmentInteractionListener mListener;
    private View filterHeader;

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

        mainActivity = (MainActivity) getActivity();

        db = dbHelper.getWritableDatabase();
        plantList = dbHelper.getPlantList(db);



//        CustomListAdapter customLA = new CustomListAdapter((Activity) getContext(), plantList);
//        // Inflate the layout for this fragment
//        rootView =  inflater.inflate(R.layout.fragment_encyclopedia, container, false);
//
//        listView = rootView.findViewById(R.id.list_view);
//        listView.setAdapter(customLA);

        encyclopediaRecyclerAdapter = new EncyclopediaRecyclerAdapter(this, plantList);

        if(mainActivity.isCreatingWalk()) {
            rootView = inflater.inflate(R.layout.fragment_encyclopedia_create, container, false);

        } else {
            rootView = inflater.inflate(R.layout.fragment_encyclopedia, container, false);

        }

        searchView = rootView.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                UpdateFilter(searchQuery + "|" + checkBoxFilterQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                UpdateFilter(searchQuery + "|" + checkBoxFilterQuery);
                return false;
            }
        });

        DragScrollBar scrollBar = rootView.findViewById(R.id.dragScrollBar);
        scrollBar.setIndicator(new AlphabetIndicator(context), true);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(encyclopediaRecyclerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        filterDrawer = rootView.findViewById(R.id.filter_drawer);
        filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

        filterButton = rootView.findViewById(R.id.filter_icon);

        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
            }

        });

        filterHeader = rootView.findViewById(R.id.nav_view_header);

        filterHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            }
        });

        //TODO: Make drawer close if click outside drawer

        expandableListView = rootView.findViewById(R.id.filter_listview);

        HashMap<String, List<String>> item = new HashMap<>();

        ArrayList<String> category = new ArrayList<>();
        category.add("Fruit");
        category.add("Veg");
        category.add("Herb");
        item.put("Category", category);

        ArrayList<String> plotSize = new ArrayList<>();
        plotSize.add("Small");
        plotSize.add("Medium");
        plotSize.add("Large");
        item.put("Plot Size", plotSize);

        ArrayList<String> careFreq = new ArrayList<>();
        careFreq.add("Occasionally");
        careFreq.add("Monthly");
        careFreq.add("Fortnightly");
        careFreq.add("Weekly");
        careFreq.add("Daily");
        item.put("Frequency of Care", careFreq);

        ArrayList<String> expertise = new ArrayList<>();
        expertise.add("Beginner");
        expertise.add("Easy");
        expertise.add("Medium");
        expertise.add("Hard");
        expertise.add("Expert");
        item.put("Level of Expertise", expertise);


        filterListAdapter = new FilterListAdapter(item);
        expandableListView.setAdapter(filterListAdapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                filterListAdapter.toggleChecked(groupPosition, childPosition);
                return true;
            }
        });

        if(mainActivity.isCreatingWalk()) {
            Button confirm = rootView.findViewById(R.id.choose_points_confirm);

            List<Plant> pointsChosen = new ArrayList<>();
            confirm.setOnClickListener(v -> {
                for(int i = 0; i<encyclopediaRecyclerAdapter.getItemCount(); i++) {
                    if(encyclopediaRecyclerAdapter.getItem(i).getType()!=PlantType.DIVIDER) {
                        if(encyclopediaRecyclerAdapter.getItem(i).isChecked()) {
                            pointsChosen.add(encyclopediaRecyclerAdapter.getItem(i));
                        }
                    }
                }
                CreateWalkFragment createWalkFragment = (CreateWalkFragment) getActivity().getSupportFragmentManager().findFragmentByTag("create_walk");
                createWalkFragment.UpdateList(pointsChosen);
                getActivity().getSupportFragmentManager().popBackStack();

            });

        }

        getCheckboxFilters();

        Button applyFilters = rootView.findViewById(R.id.apply_filter_button);

        applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCheckboxFilters();
                UpdateFilter(searchQuery + "|" + checkBoxFilterQuery);
                filterDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
            }
        });

        return rootView;
    }

    public void getCheckboxFilters() {
        List<boolean[]> checkedArray = filterListAdapter.checkedArray;
        String s = "";
        for(boolean[] bool: checkedArray) {
            s = s + Arrays.toString(bool);
        }
        checkBoxFilterQuery = s;
    }

    public void UpdateFilter(String query) {
        encyclopediaRecyclerAdapter.getFilter().filter(query);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Plant p = encyclopediaRecyclerAdapter.getItem(position);
        if(p.getType()!=PlantType.DIVIDER) {
            if(mainActivity.isCreatingWalk()==false) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("plant", p);

                PlantFragment plantFragment = new PlantFragment();
                plantFragment.setArguments(bundle);

                //getActivity().getSupportFragmentManager().popBackStack();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.filter_drawer, plantFragment, PLANT_FRAGMENT).addToBackStack(null)
                        .commit();
            } else {
//                RadioButton radio = view.findViewById(R.id.radio_button);
//                radio.setChecked(!radio.isChecked());
            }
        }
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
