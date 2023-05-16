package etu.poseidon;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import etu.poseidon.models.weather.WeatherCondition;

public class WeatherConditionListSelectorFragment extends Fragment {

    public interface OnWeatherConditionSelectedListener {
        void onWeatherConditionSelected(List<WeatherCondition> conditions);
    }

    private OnWeatherConditionSelectedListener mListener;

    private final int BUTTONS_PER_ROW = 3;
    private final float BUTTONS_DP_SIZE = 90f;

    private Map<WeatherCondition, Button> weatherButtons;

    private static final String MULTI_SELECT_PARAM = "multi_select_param";
    private static final String DEFAULT_SELECTED_CONDITIONS_PARAM = "default_selected_conditions_param";

    private boolean multiSelect;
    private List<WeatherCondition> defaultSelectedConditions;

    public WeatherConditionListSelectorFragment() {
        // Required empty public constructor
    }

    public static WeatherConditionListSelectorFragment newInstance(Boolean multiSelect, List<WeatherCondition> defaultSelectedConditions) {
        WeatherConditionListSelectorFragment fragment = new WeatherConditionListSelectorFragment();
        Bundle args = new Bundle();
        args.putBoolean(MULTI_SELECT_PARAM, multiSelect);
        args.putSerializable(DEFAULT_SELECTED_CONDITIONS_PARAM, (java.io.Serializable) defaultSelectedConditions);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnWeatherConditionListSelectedListener(OnWeatherConditionSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            multiSelect = getArguments().getBoolean(MULTI_SELECT_PARAM);
            defaultSelectedConditions = (List<WeatherCondition>) getArguments().getSerializable(DEFAULT_SELECTED_CONDITIONS_PARAM);
        } else {
            multiSelect = false;
            defaultSelectedConditions = List.of(WeatherCondition.SUN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather_condition_list_selector, container, false);

        addWeatherButtons(view);

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

            // Set button to not selected
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

        // Set default selected conditions
        if(defaultSelectedConditions != null){
            for(WeatherCondition condition : defaultSelectedConditions){
                Objects.requireNonNull(weatherButtons.get(condition)).setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.green, null));
                Objects.requireNonNull(weatherButtons.get(condition)).setTag("selected");
            }
        }
    }

    private void handleWeatherButtonClicked(Button button){
        for(Button b : weatherButtons.values()){
            if(!multiSelect){
                if(b == button){
                    b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.green, null));
                    b.setTag("selected");
                } else {
                    b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.light_grey, null));
                    b.setTag(null);
                }
            } else {
                if(b == button){
                    if(b.getTag() == null){
                        b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.green, null));
                        b.setTag("selected");
                    } else {
                        b.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.light_grey, null));
                        b.setTag(null);
                    }
                }
            }
        }

        mListener.onWeatherConditionSelected(getSelectedWeatherCondition());
    }

    private List<WeatherCondition> getSelectedWeatherCondition(){
        return weatherButtons.entrySet().stream()
            .filter(entry -> entry.getValue().getTag() != null)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}