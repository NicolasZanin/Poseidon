package etu.poseidon;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.util.Locale;
import java.util.Map;

import etu.poseidon.models.Poi;
import etu.poseidon.models.weather.WeatherCondition;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherConditionCreatorFragment extends Fragment {

    public interface OnWeatherConditionCreatedListener {
        void onWeatherConditionCreated();
    }

    private static final String ARG_CURRENT_LOCATION = "current_location_param";
    private Location currentLocation;
    private final int BUTTONS_PER_ROW = 3;
    private final float BUTTONS_DP_SIZE = 90f;

    private Map<WeatherCondition, Button> weatherButtons;
    private int perimeter = 10;

    private WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener mListener;

    private GoogleSignInAccount account;

    public WeatherConditionCreatorFragment() {
        // Required empty public constructor
    }

    public static WeatherConditionCreatorFragment newInstance(Location location) {
        WeatherConditionCreatorFragment fragment = new WeatherConditionCreatorFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentLocation = getArguments().getParcelable(ARG_CURRENT_LOCATION);
        }

        account = GoogleSignIn.getLastSignedInAccount(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_condition_creator, container, false);

        addWeatherButtons(view);

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

        // Inflate the layout for this fragment
        return view;
    }

    private void addWeatherButtons(View view){
        weatherButtons = new ArrayMap<>();

        LinearLayout parentLayout = view.findViewById(R.id.conditions);
        LinearLayout currentLayout = new LinearLayout(getContext());
        currentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        currentLayout.setOrientation(LinearLayout.HORIZONTAL);
        currentLayout.setGravity(Gravity.CENTER);
        parentLayout.addView(currentLayout);

        // Calculate width
        int buttonCount = 0;

        for (WeatherCondition condition : WeatherCondition.values()) {
            buttonCount++;

            Button button = new Button(getContext());
            weatherButtons.put(condition, button);

            // Define button icon
            int iconId = getResources().getIdentifier("ic_weather_" + condition.name().toLowerCase(Locale.ROOT), "drawable", getContext().getPackageName());
            button.setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);

            // Define button text
            int stringId = getResources().getIdentifier("weather_" + condition.name().toLowerCase(Locale.ROOT), "string", getContext().getPackageName());
            button.setText(stringId);

            currentLayout.addView(button);

            float pixelValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BUTTONS_DP_SIZE, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) pixelValue, (int) pixelValue);
            button.setLayoutParams(params);
            // Set the first button to selected, the other not
            if (buttonCount == 1) {
                button.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.green, null));
                button.setTag("selected");
            } else
                button.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.light_grey, null));

            button.setOnClickListener(v -> handleWeatherButtonClicked(button));

            // Create a new line if the current one is full
            if (buttonCount%BUTTONS_PER_ROW == 0) {
                LinearLayout newLine = new LinearLayout(getContext());
                newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                newLine.setOrientation(LinearLayout.HORIZONTAL);
                newLine.setGravity(Gravity.CENTER);
                parentLayout.addView(newLine);
                currentLayout = newLine;
            }
        }
    }

    private void handleWeatherButtonClicked(Button button){
        for(Button b : weatherButtons.values()){
            if(b == button){
                b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.green, null));
                b.setTag("selected");
            } else {
                b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.light_grey, null));
                b.setTag(null);
            }
        }
    }

    private WeatherCondition getSelectedWeatherCondition(){
        for(Button b : weatherButtons.values()){
            if(b.getTag() != null && b.getTag().equals("selected"))
                return weatherButtons.keySet().stream().filter(key -> weatherButtons.get(key) == b).findFirst().get();
        }
        return null;
    }

    private void handleConfirmButton(){
        Poi newPoi = new Poi();
        newPoi.setLatitude(currentLocation.getLatitude());
        newPoi.setLongitude(currentLocation.getLongitude());
        newPoi.setWeatherCondition(getSelectedWeatherCondition());
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
}