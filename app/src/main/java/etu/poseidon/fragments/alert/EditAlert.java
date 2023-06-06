package etu.poseidon.fragments.alert;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import etu.poseidon.R;
import etu.poseidon.fragments.weathercondition.WeatherConditionCreatorFragment;
import etu.poseidon.fragments.weathercondition.components.WeatherConditionListSelectorFragment;
import etu.poseidon.models.Alert;
import etu.poseidon.models.weather.WeatherCondition;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditAlert#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditAlert extends Fragment  implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener{


    public interface OnConfirmEditAlertListener{
        void onAlertCreated(String type, Alert alert);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    // implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener
    private static final String ARG_REAL_LOCATION = "real_location_param";
    private static final String ARG_MAP_LOCATION = "map_location_param";

    private OnConfirmEditAlertListener onConfirmEditAlertListener;

    // TODO: Rename and change types of parameters
    private Location currentRealLocation;
    private GeoPoint currentMapLocation;

    private Alert alert;

    private String type;

    private RadioButton realLocationButton;
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
            currentRealLocation = getArguments().getParcelable(ARG_REAL_LOCATION);
            currentMapLocation = getArguments().getParcelable(ARG_MAP_LOCATION);

            Log.d("EDIT ALERT", "currentRealLocation: " + currentRealLocation);
            Log.d("EDIT ALERT", "currentMapLocation: " + currentMapLocation);
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

        SeekBar perimeter = view.findViewById(R.id.range);
        perimeter.setProgress((int)this.alert.getPerimeter());

        this.realLocationButton = view.findViewById(R.id.radio_gps);

        perimeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView perimeterValue = view.findViewById(R.id.range_value);
                String perimeterText = "Périmètre : " + String.valueOf(progress) + " milles marin";
                perimeterValue.setText(perimeterText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Ne rien faire ici
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Ne rien faire ici
            }
        });

        GoogleSignInAccount loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());
        if (loggedInAccount != null) {
            this.alert.setCreatorEmail(loggedInAccount.getEmail());
            this.alert.setCreatorFullname(loggedInAccount.getDisplayName());
        }

        Button save = view.findViewById(R.id.confirm);
        save.setOnClickListener(v -> {
            this.alert.setName(name.getText().toString());
            this.alert.setDescription(description.getText().toString());
            Log.d("Edit alert", this.alert.toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            this.alert.setUpdatedAt(sdf.format(new Date()));
            this.alert.setEnabled(true);
            this.alert.setPerimeter(perimeter.getProgress());
            // TODO : add case of currentRealLocation

            if(realLocationButton.isChecked()) {
                if(currentRealLocation == null) {
                    int stringError = getResources().getIdentifier("weather_condition_creator_no_gps", "string", getContext().getPackageName());
                    Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_LONG).show();
                    closeFragment();
                    return;
                }
                this.alert.setLatitude(currentRealLocation.getLatitude());
                this.alert.setLongitude(currentRealLocation.getLongitude());
            } else {
                this.alert.setLatitude(currentMapLocation.getLatitude());
                this.alert.setLongitude(currentMapLocation.getLongitude());
            }
            Log.d("Edit alert", this.alert.toString());
            // TODO: get location from map
            this.onConfirmEditAlertListener.onAlertCreated(this.type, this.alert);
            closeFragment(); // TODO: open alertMenuFragment if created or edited successfully
        });



        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onWeatherConditionSelected(List<WeatherCondition> conditions) {
        this.alert.setListWeatherCondition(conditions);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener) {
            this.onConfirmEditAlertListener = (OnConfirmEditAlertListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnWeatherConditionCreatedListener");
        }
    }
}