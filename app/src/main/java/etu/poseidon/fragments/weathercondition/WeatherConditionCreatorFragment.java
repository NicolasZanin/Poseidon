package etu.poseidon.fragments.weathercondition;

import android.content.Context;
import android.location.Location;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import etu.poseidon.R;
import etu.poseidon.fragments.picture.PictureFragment;
import etu.poseidon.fragments.weathercondition.components.WeatherConditionListSelectorFragment;
import etu.poseidon.models.Poi;
import etu.poseidon.models.weather.WeatherCondition;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherConditionCreatorFragment extends Fragment implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener {

    public interface OnWeatherConditionCreatedListener {
        void onWeatherConditionCreated();
    }

    private static final String ARG_REAL_LOCATION = "real_location_param";
    private static final String ARG_MAP_LOCATION = "map_location_param";
    private Location currentRealLocation;
    private GeoPoint currentMapLocation;
    private int perimeter = 10;

    private WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener mListener;

    private GoogleSignInAccount account;
    private Bitmap picture;
    private PictureFragment pictureFragment;
    private WeatherConditionListSelectorFragment weatherConditionListSelectorFragment;
    private WeatherCondition weatherConditionSelected;

    private RadioButton realLocationButton;

    public WeatherConditionCreatorFragment() {
        // Required empty public constructor
    }

    public static WeatherConditionCreatorFragment newInstance(Location currentRealLocation, GeoPoint currentMapLocation) {
        WeatherConditionCreatorFragment fragment = new WeatherConditionCreatorFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_REAL_LOCATION, currentRealLocation);
        args.putParcelable(ARG_MAP_LOCATION, currentMapLocation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentRealLocation = getArguments().getParcelable(ARG_REAL_LOCATION);
            currentMapLocation = getArguments().getParcelable(ARG_MAP_LOCATION);
        }

        account = GoogleSignIn.getLastSignedInAccount(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_condition_creator, container, false);

        SeekBar range = view.findViewById(R.id.range);
        TextView rangeValue = view.findViewById(R.id.range_value);

        // Default value for range text
        int stringRange = getResources().getIdentifier("weather_condition_creator_perimeter", "string", getContext().getPackageName());
        rangeValue.setText(getString(stringRange, 10));

        range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangeValue.setText(getString(stringRange, progress));
                perimeter = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Button confirmButton = view.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(v -> handleConfirmButton());

        Button cancelButton = view.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(v -> closeFragment());

        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeFragment());

        // Picture fragment
        pictureFragment = new PictureFragment();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_picture, pictureFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Weather condition list
        weatherConditionListSelectorFragment = WeatherConditionListSelectorFragment.newInstance(false, List.of(WeatherCondition.SUN));
        weatherConditionListSelectorFragment.setOnWeatherConditionListSelectedListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_weather_condition_list, weatherConditionListSelectorFragment).commit();

        realLocationButton = view.findViewById(R.id.radio_gps);

        // Inflate the layout for this fragment
        return view;
    }

    private void handleConfirmButton(){
        Poi newPoi = new Poi();
        if(realLocationButton.isChecked()) {
            if(currentRealLocation == null) {
                int stringError = getResources().getIdentifier("weather_condition_creator_no_gps", "string", getContext().getPackageName());
                Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_LONG).show();
                closeFragment();
                return;
            }
            newPoi.setLatitude(currentRealLocation.getLatitude());
            newPoi.setLongitude(currentRealLocation.getLongitude());
        } else {
            newPoi.setLatitude(currentMapLocation.getLatitude());
            newPoi.setLongitude(currentMapLocation.getLongitude());
        }
        newPoi.setWeatherCondition(weatherConditionSelected);
        newPoi.setPerimeter(perimeter);
        newPoi.setCreatorEmail(account.getEmail());
        newPoi.setCreatorFullname(account.getDisplayName());

        PoiApiClient.getInstance().createPoi(newPoi, new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    int stringSuccess = getResources().getIdentifier("weather_condition_creator_weather_sent", "string", getContext().getPackageName());
                    Toast.makeText(getContext(), getString(stringSuccess), Toast.LENGTH_SHORT).show();
                    mListener.onWeatherConditionCreated();
                    closeFragment();
                } else {
                    int stringError = getResources().getIdentifier("global_error", "string", getContext().getPackageName());
                    Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_SHORT).show();
                    Log.e("POSEIDON", "Error: " + response.code() + " " + response.message());
                    closeFragment();
                }
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                int stringError = getResources().getIdentifier("global_error", "string", getContext().getPackageName());
                Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_SHORT).show();
                Log.e("POSEIDON", "Error: " + t.getMessage());
                closeFragment();
            }
        });
    }

    private void closeFragment(){
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener) {
            mListener = (WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnWeatherConditionCreatedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onWeatherConditionSelected(List<WeatherCondition> conditions) {
        // We know that only one weather condition is selected (we are not in multiSelect)
        weatherConditionSelected = conditions.get(0);
    }
}