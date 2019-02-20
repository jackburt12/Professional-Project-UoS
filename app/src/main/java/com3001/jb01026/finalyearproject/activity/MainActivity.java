package com3001.jb01026.finalyearproject.activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.DatabaseHelper;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.fragment.EncyclopediaFragment;
import com3001.jb01026.finalyearproject.fragment.GardenFragment;
import com3001.jb01026.finalyearproject.fragment.WalksFragment;

public class MainActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(getApplicationContext());

        actionBar = getSupportActionBar();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_garden:
                                actionBar.setTitle(R.string.garden);
                                showView(new GardenFragment());
                                break;
                            case R.id.action_encyclopedia:
                                actionBar.setTitle(R.string.encyclopedia);
                                showView(new EncyclopediaFragment());
                                break;
                            case R.id.action_walks:
                                actionBar.setTitle(R.string.walks);
                                showView(new WalksFragment());
                                break;
                        }
                        return true;
                    }
                });


    }

    public void showView(Fragment view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, view)
                .commit();
    }
}
