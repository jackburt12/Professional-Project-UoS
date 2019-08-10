package com3001.jb01026.finalyearproject.fragment.setup;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.activity.SetupWizard;

public class SetupStep1 extends Fragment {

    EditText emailEnter, emailConfirm, passwordEnter, passwordConfirm, nameEnter;

    Boolean allEntriesValid = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.setup_step_1, container, false);

        emailEnter = (EditText)view.findViewById(R.id.input_email);
        emailConfirm = (EditText)view.findViewById(R.id.input_email_confirm);
        passwordEnter = (EditText)view.findViewById(R.id.input_password);
        passwordConfirm = (EditText)view.findViewById(R.id.input_password_confirm);
        nameEnter = (EditText)view.findViewById(R.id.input_name);

        Button nextButton = view.findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    SetupWizard wizard = (SetupWizard)getActivity();
                    wizard.setEmail(emailEnter.getText().toString());
                    wizard.setName(nameEnter.getText().toString());
                    wizard.setPassword(passwordEnter.getText().toString());
                    wizard.nextPage();
                }
            }
        });


        return view;

    }

    boolean validate() {
        boolean valid = true;

        if(nameEnter.getText().toString().isEmpty()) {
            nameEnter.setError("Must enter a name");
            valid = false;
        }

        if (emailEnter.getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailEnter.getText().toString()).matches()) {
            emailEnter.setError("Enter a valid email address");
            valid = false;
        }

        if (emailConfirm.getText().toString().isEmpty() || !emailConfirm.getText().toString().equals(emailEnter.getText().toString())) {
            emailConfirm.setError("Must match email address");
            valid = false;
        }

        String passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,16}";
        if(passwordEnter.getText().toString().isEmpty() || !passwordEnter.getText().toString().matches(passwordPattern)) {
            passwordEnter.setError("Must be between 4 and 16 characters, and include at least one letter and one number");
            valid = false;
        }

        if(passwordConfirm.getText().toString().isEmpty() || !passwordConfirm.getText().toString().equals(passwordEnter.getText().toString())) {
            passwordConfirm.setError("Must match password");
            valid = false;
        }

        return valid;
    }


}
