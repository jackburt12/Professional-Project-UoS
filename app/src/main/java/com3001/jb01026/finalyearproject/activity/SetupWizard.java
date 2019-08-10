package com3001.jb01026.finalyearproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.fragment.setup.SetupComplete;
import com3001.jb01026.finalyearproject.fragment.setup.SetupStep1;
import com3001.jb01026.finalyearproject.fragment.setup.SetupStep2;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.seatgeek.placesautocomplete.model.Place;

import java.util.HashMap;
import java.util.Map;

public class SetupWizard extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private StateProgressBar stateProgressBar;
    private TabLayout tabLayout;

    private String name, email, password;
    private LatLng place;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_wizard);

        stateProgressBar = (StateProgressBar) findViewById(R.id.state_progress_bar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem());
                return true;
            }
        });

        tabLayout = new TabLayout(this);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                StateProgressBar.StateNumber stNumber = StateProgressBar.StateNumber.ONE;
                switch(tabLayout.getSelectedTabPosition()) {
                    case 0:
                        stNumber = StateProgressBar.StateNumber.ONE;
                        break;
                    case 1:
                        stNumber = StateProgressBar.StateNumber.TWO;
                        break;
                    case 2:
                        stNumber = StateProgressBar.StateNumber.THREE;
                        break;
                    default:
                        stNumber = StateProgressBar.StateNumber.ONE;


                }
                stateProgressBar.setCurrentStateNumber(stNumber);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position) {
                case 0:
                    SetupStep1 step1 = new SetupStep1();
                    return step1;
                case 1:
                    SetupStep2 step2 = new SetupStep2();
                    return step2;
                case 2:
                    SetupComplete setupComplete = new SetupComplete();
                    return setupComplete;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public void createAccount() {

        Map<String, Object> user = new HashMap<>();
        GeoPoint point = new GeoPoint(place.latitude, place.longitude);

        user.put("name", name);
        user.put("location", point);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser fireUser = mAuth.getCurrentUser();
                            db.collection("users").document(fireUser.getUid()).set(user);
                            Toast.makeText(SetupWizard.this, "Account creation successful, login to continue.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SetupWizard.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    public void nextPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1, true);
    }

    public void previousPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1, true);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlace(LatLng place) {
        this.place = place;
    }
}
