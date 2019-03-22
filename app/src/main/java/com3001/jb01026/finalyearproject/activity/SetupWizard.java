package com3001.jb01026.finalyearproject.activity;

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

import com.google.android.material.tabs.TabLayout;
import com.kofigyan.stateprogressbar.StateProgressBar;

public class SetupWizard extends AppCompatActivity {
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private StateProgressBar stateProgressBar;
    private TabLayout tabLayout;
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
                case 3:
                    SetupComplete setupComplete = new SetupComplete();
                    return setupComplete;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }
    }
}
