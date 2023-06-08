package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private IWeatherConditionUpdaterController controller;

    private LinearLayout editionSaveIngoing;
    private LinearLayout editionSaveCompleted;

    private View view;
    private Context context;

    public void setContentView(Poi poiToUpdate, View view, Context context){
        this.view = view;
        this.context = context;

        // Avoid user miscliking on the activity holding when fragment open
        view.findViewById(R.id.weather_condition_updator_fragment_layout).setOnClickListener(v -> {});

        // Say to the controller that we are opening the fragment to setup the POI picture
        controller.userActionOpeningFragment();

        // Setup information part
        setupInformationPart(poiToUpdate);

        // Setup buttons
        setupButtons(view);

        // Edition part
        setupEditionPart(poiToUpdate);

        // Deletion part
        setupDeletionPart(poiToUpdate);
    }


    @Override
    public void setController(IWeatherConditionUpdaterController controller) {
        this.controller = controller;
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable instanceof WeatherConditionUpdaterModel && o instanceof WeatherConditionUpdaterModel.WeatherConditionUpdaterApiState){
            if(o.equals(WeatherConditionUpdaterModel.WeatherConditionUpdaterApiState.SAVING)){
                editionSaveIngoing.setVisibility(View.VISIBLE);
                editionSaveCompleted.setVisibility(View.GONE);
            } else {
                editionSaveIngoing.setVisibility(View.GONE);
                editionSaveCompleted.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setDateTimeField(Poi poiToUpdate, TextView textView, DateFormat inputFormat, DateFormat outputFormat, String stringResource) {
        int stringId = context.getResources().getIdentifier(stringResource, "string", context.getPackageName());
        try {
            Date date = inputFormat.parse(poiToUpdate.getCreatedAt());
            String formattedDateTime = outputFormat.format(date);
            textView.setText(context.getString(stringId, formattedDateTime));
        } catch (ParseException e) {
            textView.setText(context.getString(stringId, poiToUpdate.getCreatedAt()));
        }
    }

    @Override
    public void setPicture(Bitmap picture){
        ImageView imageView = view.findViewById(R.id.image_view);
        if(picture != null) {
            imageView.setImageBitmap(picture);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void setupInformationPart(Poi poiToUpdate){
        // Title
        TextView title = view.findViewById(R.id.title);
        int stringTitle = context.getResources().getIdentifier("weather_condition_updater_title", "string", context.getPackageName());
        title.setText(context.getString(stringTitle, poiToUpdate.isFinished() ? " - Terminé" : " - En cours"));

        // Coordinates
        TextView coordinates = view.findViewById(R.id.coordinates);
        int stringCoordinates = context.getResources().getIdentifier("weather_condition_updater_coordinates", "string", context.getPackageName());
        coordinates.setText(context.getString(stringCoordinates, poiToUpdate.getLatitude(), poiToUpdate.getLongitude()));

        // Weather condition
        TextView weatherCondition = view.findViewById(R.id.condition_valeur);
        int stringWeatherCondition = context.getResources().getIdentifier("weather_" + poiToUpdate.getWeatherCondition().toString().toLowerCase(Locale.ROOT), "string", context.getPackageName());
        weatherCondition.setText(context.getString(stringWeatherCondition));

        // Perimeter
        TextView perimeter = view.findViewById(R.id.perimeter);
        int stringPerimeter = context.getResources().getIdentifier("weather_condition_updater_perimeter", "string", context.getPackageName());
        perimeter.setText(context.getString(stringPerimeter, (int) poiToUpdate.getPerimeter()));

        // Setup date formatter
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.FRANCE);
        outputFormat.setTimeZone(TimeZone.getDefault());

        // Created at - date field
        setDateTimeField(poiToUpdate, view.findViewById(R.id.created_at), inputFormat, outputFormat, "weather_condition_updater_created_at");

        // Updated at - date field
        setDateTimeField(poiToUpdate, view.findViewById(R.id.last_update), inputFormat, outputFormat, "weather_condition_updater_last_update");

        // Added by
        TextView addedBy = view.findViewById(R.id.added_by);
        int stringAddedBy = context.getResources().getIdentifier("weather_condition_updater_added_by", "string", context.getPackageName());
        addedBy.setText(context.getString(stringAddedBy, poiToUpdate.getCreatorFullname()));
    }

    private void setupButtons(View view){
        // Close icon
        view.findViewById(R.id.close_button).setOnClickListener(v -> controller.userActionCloseFragment());

        // Close button
        view.findViewById(R.id.close).setOnClickListener(v -> controller.userActionCloseFragment());

        // Finished button
        view.findViewById(R.id.finished).setOnClickListener(v -> controller.userActionDeletePoi());
    }

    private void setupDeletionPart(Poi poiToUpdate){
        TextView informationText = view.findViewById(R.id.click_if_is_finished);
        if(poiToUpdate.isFinished()){
            // Change information text
            int stringFinished = context.getResources().getIdentifier("weather_condition_updater_button_information_already_finished", "string", context.getPackageName());
            informationText.setText(context.getString(stringFinished));
            // Set "delete" button to red background
            view.findViewById(R.id.finished).setBackgroundColor(context.getResources().getColor(R.color.red));
        }
    }

    private void setupEditionPart(Poi poiToUpdate){
        if(poiToUpdate.isFinished()){
            // Hide edition_layout if the poi is finished
            view.findViewById(R.id.edition_layout).setVisibility(View.GONE);
        } else {
            // Default value for range text
            int stringRange = context.getResources().getIdentifier("weather_condition_updater_edition_perimeter", "string", context.getPackageName());
            TextView rangeValue = view.findViewById(R.id.edition_range_value);
            rangeValue.setText(context.getString(stringRange, (int) poiToUpdate.getPerimeter()));

            // Range
            SeekBar range = view.findViewById(R.id.edition_range);
            range.setProgress((int) poiToUpdate.getPerimeter());
            range.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rangeValue.setText(context.getString(stringRange, progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    controller.userActionUpdatePerimeter(seekBar.getProgress());
                }
            });


            // Retrieve layout to indicate saving status
            editionSaveIngoing = view.findViewById(R.id.edition_save_ingoing);
            editionSaveCompleted = view.findViewById(R.id.edition_save_completed);
        }
    }
}
