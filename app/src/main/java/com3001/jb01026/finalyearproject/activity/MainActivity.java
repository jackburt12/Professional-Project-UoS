package com3001.jb01026.finalyearproject.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.fragment.CreateWalkFragment;
import com3001.jb01026.finalyearproject.fragment.EncyclopediaFragment;
import com3001.jb01026.finalyearproject.fragment.GardenFragment;
import com3001.jb01026.finalyearproject.fragment.WalksFragment;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    public boolean creatingWalk = false;
    private boolean inEncyclopedia = false;
    private ActionBar actionBar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(getApplicationContext());

        actionBar = getSupportActionBar();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        FragmentManager manager = this.getSupportFragmentManager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_garden:
                    actionBar.setTitle(R.string.garden);
                    //showView(new GardenFragment());
                    if(manager.findFragmentByTag("garden")!=null) {
                        manager.beginTransaction().show(Objects.requireNonNull(manager.findFragmentByTag("garden"))).commit();
                    } else {
                        manager.beginTransaction().add(R.id.main_container, new GardenFragment(), "garden").commit();
                    }

                    if(manager.findFragmentByTag("encyclopedia")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("encyclopedia"))).commit();
                    }
                    if(manager.findFragmentByTag("walks")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("walks"))).commit();
                    }

                    inEncyclopedia = false;
                    break;
                case R.id.action_encyclopedia:
                    creatingWalk = false;
                    actionBar.setTitle(R.string.encyclopedia);
                    //showView(new EncyclopediaFragment());
                    if(manager.findFragmentByTag("encyclopedia")!=null) {
                        manager.beginTransaction().show(Objects.requireNonNull(manager.findFragmentByTag("encyclopedia"))).commit();
                    } else {
                        manager.beginTransaction().add(R.id.main_container, new EncyclopediaFragment(), "encyclopedia").commit();
                    }

                    if(manager.findFragmentByTag("garden")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("garden"))).commit();
                    }
                    if(manager.findFragmentByTag("walks")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("walks"))).commit();
                    }

                    getSupportFragmentManager().addOnBackStackChangedListener(this);
                    inEncyclopedia = true;


                    break;
                case R.id.action_walks:
                    actionBar.setTitle(R.string.walks);
                    //showView(new CreateWalkFragment());
                    if(manager.findFragmentByTag("walks")!=null) {
                        manager.beginTransaction().show(Objects.requireNonNull(manager.findFragmentByTag("walks"))).commit();
                    } else {
                        manager.beginTransaction().add(R.id.main_container, new CreateWalkFragment(), "walks").commit();
                    }

                    if(manager.findFragmentByTag("encyclopedia")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("encyclopedia"))).commit();
                    }
                    if(manager.findFragmentByTag("garden")!=null) {
                        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentByTag("garden"))).commit();
                    }

                    inEncyclopedia = false;

                    break;
            }
            if(!inEncyclopedia) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;
                getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
            }
            return true;
        });


    }

    public void showView(Fragment view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, view)
                .commit();
    }

    public boolean isCreatingWalk() {
        return creatingWalk;
    }

    public void setCreatingWalk(boolean creatingWalk) {
        this.creatingWalk = creatingWalk;
    }

    @Override
    public void onBackStackChanged() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount()>0;
        Log.v("BACK STACK" , "CHANGED");
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:

                FragmentManager manager = getSupportFragmentManager();
                if (manager.getBackStackEntryCount() > 0) {
                    //fm.popBackStack();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("plant");
                    if(fragment != null)
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    Log.v("NUMBER OF FRAGS ON BACKSTACK", Integer.toString(manager.getBackStackEntryCount()));

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
