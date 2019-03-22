package com3001.jb01026.finalyearproject.fragment.setup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;

public class SetupStep2 extends Fragment {

    EditText dateOfBirth, height, weight;
    Calendar calendar;
    NumberPicker npUnit, npOne, npTwo;
    TextView tvOne, tvTwo;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setup_step_2, container, false);

        return view;
    }


}
