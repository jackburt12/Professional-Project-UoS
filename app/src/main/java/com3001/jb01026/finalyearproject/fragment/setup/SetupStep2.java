package com3001.jb01026.finalyearproject.fragment.setup;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import java.util.Calendar;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.activity.SetupWizard;

public class SetupStep2 extends Fragment {

    private Place mPlace;
    private LatLng placeLatLng;
    private EditText city, country, county, postCode;
    private PlacesAutocompleteTextView placesAutocomplete;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setup_step_2, container, false);

        city = view.findViewById(R.id.input_city);
        country = view.findViewById(R.id.input_country);
        county = view.findViewById(R.id.input_county);
        postCode = view.findViewById(R.id.input_post_code);

        placesAutocomplete = view.findViewById(R.id.places_autocomplete);

        placesAutocomplete.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        // do something awesome with the selected place
                        mPlace = place;
                        placesAutocomplete.getDetailsFor(place, new DetailsCallback() {
                            @Override
                            public void onSuccess(PlaceDetails placeDetails) {

                                placeLatLng = new LatLng(placeDetails.geometry.location.lat, placeDetails.geometry.location.lng);

                                for (AddressComponent component : placeDetails.address_components) {
                                    for(AddressComponentType type : component.types) {
                                        switch(type) {
                                            case POSTAL_TOWN:
                                                city.setText(component.long_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_2:
                                                county.setText(component.long_name);
                                                break;
                                            case COUNTRY:
                                                country.setText(component.long_name);
                                                break;
                                            case POSTAL_CODE:
                                                postCode.setText(component.long_name);
                                                break;
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Throwable throwable) {

                            }
                        });



                    }
                }
        );

        Button nextButton = view.findViewById(R.id.next_button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    SetupWizard wizard = (SetupWizard)getActivity();
                    wizard.setPlace(placeLatLng);
                    wizard.nextPage();
                }
            }
        });

        Button backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SetupWizard)getActivity()).previousPage();
            }
        });


        return view;
    }

    private boolean validate() {
        boolean valid = true;

        if(mPlace==null) {
            placesAutocomplete.setError("Please select a valid place");
            valid = false;
        }

        return valid;
    }


}
