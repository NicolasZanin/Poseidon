package etu.poseidon.fragments.alert;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import etu.poseidon.R;
import etu.poseidon.fragments.weathercondition.components.WeatherConditionListSelectorFragment;
import etu.poseidon.models.Alert;
import etu.poseidon.models.weather.WeatherCondition;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAlert#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAlert extends Fragment  implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener{


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Alert alert;

    private String type;

    private WeatherConditionListSelectorFragment weatherConditionListSelectorFragment;

    public EditAlert() {
        // Required empty public constructor
        this.alert = new Alert();
        this.type = "create";
    }

    public EditAlert(Alert alert){
        this.alert = alert;
        this.type = "edit";
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditAlert.
     */
    // TODO: Rename and change types and number of parameters
    public static EditAlert newInstance(String param1, String param2) {
        EditAlert fragment = new EditAlert();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void closeFragment(){
        Log.d("POSEIDON", "closeFragment: " + this);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(etu.poseidon.R.layout.fragment_edit_alert, container, false);

        TextView close = view.findViewById(R.id.close_edit_alert);
        close.setOnClickListener(v -> {
            closeFragment();
        });

        //Weather condition list
        weatherConditionListSelectorFragment = WeatherConditionListSelectorFragment.newInstance(true, this.alert.getListWeatherCondition());
        weatherConditionListSelectorFragment.setOnWeatherConditionListSelectedListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_weather_condition_list, weatherConditionListSelectorFragment).commit();

        EditText name = view.findViewById(R.id.alert_name_input);
        name.setText(this.alert.getName());

        EditText description = view.findViewById(R.id.alert_description_input);
        description.setText(this.alert.getDescription());




        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onWeatherConditionSelected(List<WeatherCondition> conditions) {

    }
}