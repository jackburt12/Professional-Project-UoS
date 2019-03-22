package com3001.jb01026.finalyearproject.fragment.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;

public class SetupComplete extends Fragment {

    CheckBox agreeBox;
    Button finishButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_complete, container, false);

        agreeBox = view.findViewById(R.id.agree_box);
        finishButton = view.findViewById(R.id.finish_button);

        agreeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(agreeBox.isChecked() == true) {
                    finishButton.setVisibility(View.VISIBLE);
                } else {
                    finishButton.setVisibility(View.GONE);
                }
            }
        });


        return view;
    }
}
