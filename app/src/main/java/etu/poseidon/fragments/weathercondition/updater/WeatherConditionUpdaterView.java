package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;

import etu.poseidon.R;
import etu.poseidon.models.Poi;

public class WeatherConditionUpdaterView implements Observer, IWeatherConditionUpdaterView {

    IWeatherConditionUpdaterController controller;


    public void setContentView(Poi poiToUpdate, View view, Context context){
        TextView title = view.findViewById(R.id.title);
        int stringTitle = context.getResources().getIdentifier("weather_condition_updater_title", "string", context.getPackageName());
        title.setText(context.getString(stringTitle, poiToUpdate.isFinished() ? " - Terminé" : " - En cours"));

        TextView coordinates = view.findViewById(R.id.coordinates);
        int stringCoordinates = context.getResources().getIdentifier("weather_condition_updater_coordinates", "string", context.getPackageName());
        coordinates.setText(context.getString(stringCoordinates, poiToUpdate.getLatitude(), poiToUpdate.getLongitude()));

        TextView weatherCondition = view.findViewById(R.id.condition);
        int stringCurrentWeatherCondition = context.getResources().getIdentifier("weather_condition_updater_current_condition", "string", context.getPackageName());
        int stringWeatherCondition = context.getResources().getIdentifier("weather_" + poiToUpdate.getWeatherCondition().toString().toLowerCase(Locale.ROOT), "string", context.getPackageName());
        weatherCondition.setText(context.getString(stringCurrentWeatherCondition, context.getString(stringWeatherCondition)));

        TextView perimeter = view.findViewById(R.id.perimeter);
        int stringPerimeter = context.getResources().getIdentifier("weather_condition_updater_perimeter", "string", context.getPackageName());
        perimeter.setText(context.getString(stringPerimeter, (int) poiToUpdate.getPerimeter()));

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.FRANCE);
        outputFormat.setTimeZone(TimeZone.getDefault());

        TextView createdAt = view.findViewById(R.id.created_at);
        int stringCreatedAt = context.getResources().getIdentifier("weather_condition_updater_created_at", "string", context.getPackageName());
        try {
            Date dateCreatedAt = inputFormat.parse(poiToUpdate.getCreatedAt());
            createdAt.setText(context.getString(stringCreatedAt, outputFormat.format(dateCreatedAt)));
        } catch (ParseException e) {
            createdAt.setText(context.getString(stringCreatedAt, poiToUpdate.getCreatedAt()));
        }

        TextView lastUpdate = view.findViewById(R.id.last_update);
        int stringLastUpdate = context.getResources().getIdentifier("weather_condition_updater_last_update", "string", context.getPackageName());
        try {
            Date dateUpdatedAt = inputFormat.parse(poiToUpdate.getUpdatedAt());
            lastUpdate.setText(context.getString(stringLastUpdate, outputFormat.format(dateUpdatedAt)));
        } catch (ParseException e) {
            lastUpdate.setText(context.getString(stringLastUpdate, poiToUpdate.getUpdatedAt()));
        }

        TextView addedBy = view.findViewById(R.id.added_by);
        int stringAddedBy = context.getResources().getIdentifier("weather_condition_updater_added_by", "string", context.getPackageName());
        addedBy.setText(context.getString(stringAddedBy, poiToUpdate.getCreatorFullname()));


        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> controller.userActionCloseFragment());

        Button close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> controller.userActionCloseFragment());

        Button finished = view.findViewById(R.id.finished);
        finished.setOnClickListener(v -> controller.userActionDeletePoi());

        // Delete part
        TextView informationText = view.findViewById(R.id.click_if_is_finished);
        if(poiToUpdate.isFinished()){
            // Change information text
            int stringFinished = context.getResources().getIdentifier("weather_condition_updater_button_information_already_finished", "string", context.getPackageName());
            informationText.setText(context.getString(stringFinished));

            // Set "delete" button to red background
            finished.setBackgroundColor(context.getResources().getColor(R.color.red));
        }

        // Edition part
        if(poiToUpdate.isFinished()){
            // Hide edition_layout if the poi is finished
            view.findViewById(R.id.edition_layout).setVisibility(View.GONE);
        } else {
            // Default value for range text
            int stringRange = context.getResources().getIdentifier("weather_condition_updater_edition_perimeter", "string", context.getPackageName());
            TextView rangeValue = view.findViewById(R.id.edition_range_value);
            rangeValue.setText(context.getString(stringRange, (int) poiToUpdate.getPerimeter()));

            SeekBar range = view.findViewById(R.id.edition_range);
            range.setProgress((int) poiToUpdate.getPerimeter());
            range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rangeValue.setText(context.getString(stringRange, progress));
                    poiToUpdate.setPerimeter(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }
    }

    @Override
    public void setController(IWeatherConditionUpdaterController controller) {
        this.controller = controller;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
