package com3001.jb01026.finalyearproject.fragment.setup;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;

public class SetupStep1 extends Fragment {

    EditText emailEnter, emailConfirm, passwordEnter, passwordConfirm, nameEnter;
    GradientDrawable emailGrad, emailConfirmGrad, passwordGrad, passwordConfirmGrad, nameEnterGrad;

    //TODO: Figure out way to disallow swipe to progress when any fields are invalid
    Boolean allEntriesValid = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.setup_step_1, container, false);

        emailEnter = (EditText)view.findViewById(R.id.emailEntry);
        emailConfirm = (EditText)view.findViewById(R.id.emailConfirm);
        passwordEnter = (EditText)view.findViewById(R.id.passwordEntry);
        passwordConfirm = (EditText)view.findViewById(R.id.passwordConfirm);
        nameEnter = (EditText)view.findViewById(R.id.nameEntry);
        emailGrad = (GradientDrawable)emailEnter.getBackground();
        emailConfirmGrad = (GradientDrawable)emailConfirm.getBackground();
        passwordGrad = (GradientDrawable)passwordEnter.getBackground();
        passwordConfirmGrad = (GradientDrawable)passwordConfirm.getBackground();
        nameEnterGrad =(GradientDrawable)nameEnter.getBackground();

        nameEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals("")) {
                    nameEnterGrad.setStroke(0, Color.TRANSPARENT);
                } else {
                    nameEnterGrad.setStroke(5, getResources().getColor(R.color.colorAccent));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailEnter.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")) {
                    emailGrad.setStroke(0, Color.TRANSPARENT);
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    emailGrad.setStroke(5, getResources().getColor(R.color.colorAccent));
                } else {
                    emailGrad.setStroke(5, Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        emailConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals("")) {
                    emailConfirmGrad.setStroke(0, Color.TRANSPARENT);
                } else if(s.toString()!="") {
                    if(emailConfirm.getText().toString().equals(emailEnter.getText().toString())) {
                        emailConfirmGrad.setStroke(5, getResources().getColor(R.color.colorAccent));

                    } else {
                        emailConfirmGrad.setStroke(5, Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")) {
                    passwordGrad.setStroke(0, Color.TRANSPARENT);
                } else if(passwordValidation(charSequence.toString())) {
                    passwordGrad.setStroke(5, getResources().getColor(R.color.colorAccent));
                } else {
                    passwordGrad.setStroke(5, Color.RED);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals("")) {
                    passwordConfirmGrad.setStroke(0, Color.TRANSPARENT);
                } else if(s!="") {
                    if(passwordConfirm.getText().toString().equals(passwordEnter.getText().toString())) {
                        passwordConfirmGrad.setStroke(5, getResources().getColor(R.color.colorAccent));

                    } else {
                        passwordConfirmGrad.setStroke(5, Color.RED);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;

    }

    boolean passwordValidation(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        if(password.matches(pattern)) {
            return true;
        }
        return false;
    }
}
