package etu.poseidon;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherConditionUpdaterFragment extends Fragment {

    public interface OnWeatherConditionDeletedListener {
        void onWeatherConditionDeleted();
    }

    private static final String ARG_POI = "poi_param";
    private Poi poiToUpdate;

    private OnWeatherConditionDeletedListener mListener;

    public WeatherConditionUpdaterFragment() {
        // Required empty public constructor
    }

    public static WeatherConditionUpdaterFragment newInstance(Poi poi) {
        WeatherConditionUpdaterFragment fragment = new WeatherConditionUpdaterFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POI, poi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poiToUpdate = getArguments().getParcelable(ARG_POI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_condition_updater, container, false);

        TextView title = view.findViewById(R.id.title);
        int stringTitle = getResources().getIdentifier("weather_condition_updater_title", "string", getContext().getPackageName());
        title.setText(getString(stringTitle, poiToUpdate.isFinished() ? "" : " - En cours"));

        TextView coordinates = view.findViewById(R.id.coordinates);
        int stringCoordinates = getResources().getIdentifier("weather_condition_updater_coordinates", "string", getContext().getPackageName());
        coordinates.setText(getString(stringCoordinates, poiToUpdate.getLatitude(), poiToUpdate.getLongitude()));

        TextView weatherCondition = view.findViewById(R.id.condition);
        int stringCurrentWeatherCondition = getResources().getIdentifier("weather_condition_updater_current_condition", "string", getContext().getPackageName());
        int stringWeatherCondition = getResources().getIdentifier("weather_" + poiToUpdate.getWeatherCondition().toString().toLowerCase(Locale.ROOT), "string", getContext().getPackageName());
        weatherCondition.setText(getString(stringCurrentWeatherCondition, getString(stringWeatherCondition)));

        TextView perimeter = view.findViewById(R.id.perimeter);
        int stringPerimeter = getResources().getIdentifier("weather_condition_updater_perimeter", "string", getContext().getPackageName());
        perimeter.setText(getString(stringPerimeter, (int) poiToUpdate.getPerimeter()));

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH'h'mm");

        TextView createdAt = view.findViewById(R.id.created_at);
        int stringCreatedAt = getResources().getIdentifier("weather_condition_updater_created_at", "string", getContext().getPackageName());
        try {
            Date dateCreatedAt = inputFormat.parse(poiToUpdate.getCreatedAt());
            createdAt.setText(getString(stringCreatedAt, outputFormat.format(dateCreatedAt)));
        } catch (ParseException e) {
            createdAt.setText(getString(stringCreatedAt, poiToUpdate.getCreatedAt()));
        }

        TextView lastUpdate = view.findViewById(R.id.last_update);
        int stringLastUpdate = getResources().getIdentifier("weather_condition_updater_last_update", "string", getContext().getPackageName());
        try {
            Date dateUpdatedAt = inputFormat.parse(poiToUpdate.getUpdatedAt());
            lastUpdate.setText(getString(stringLastUpdate, outputFormat.format(dateUpdatedAt)));
        } catch (ParseException e) {
            lastUpdate.setText(getString(stringLastUpdate, poiToUpdate.getUpdatedAt()));
        }

        TextView addedBy = view.findViewById(R.id.added_by);
        int stringAddedBy = getResources().getIdentifier("weather_condition_updater_added_by", "string", getContext().getPackageName());
        addedBy.setText(getString(stringAddedBy, poiToUpdate.getCreatorFullname()));


        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeFragment());

        Button close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> closeFragment());

        Button finished = view.findViewById(R.id.finished);
        finished.setOnClickListener(v -> handleFinishButton());

        return view;
    }

    private void handleFinishButton(){
        PoiApiClient.getInstance().deletePoi(poiToUpdate.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    int stringSuccess = getResources().getIdentifier("weather_condition_updater_succefully_deleted", "string", getContext().getPackageName());
                    Toast.makeText(getContext(), getString(stringSuccess), Toast.LENGTH_SHORT).show();
                    mListener.onWeatherConditionDeleted();
                    closeFragment();
                } else {
                    int stringError = getResources().getIdentifier("global_error", "string", getContext().getPackageName());
                    Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_SHORT).show();
                    closeFragment();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                int stringError = getResources().getIdentifier("global_error", "string", getContext().getPackageName());
                Toast.makeText(getContext(), getString(stringError), Toast.LENGTH_SHORT).show();
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
        if (context instanceof OnWeatherConditionDeletedListener) {
            mListener = (OnWeatherConditionDeletedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnWeatherConditionDeletedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}