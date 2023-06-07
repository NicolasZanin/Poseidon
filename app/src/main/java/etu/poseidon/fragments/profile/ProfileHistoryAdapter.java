package etu.poseidon.fragments.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import etu.poseidon.R;
import etu.poseidon.models.Poi;

public class ProfileHistoryAdapter extends BaseAdapter {

    public interface OnProfileLocateButtonClickedListener {
        void onProfileLocateButtonClicked(Poi poi);
    }

    private List<Poi> items;
    private LayoutInflater mInflater;

    public ProfileHistoryAdapter (Context applicationContext, List<Poi> items) {
        this.items = items;
        mInflater = (LayoutInflater.from(applicationContext));
    }
    public int getCount() {
        return items.size();
    }
    public Object getItem(int position){
        return items.get(position);
    }
    public long getItemId(int position){
        return position;
    }

    public View getView (int position, View convertView, ViewGroup parent){
        Poi item = items.get(position);
        // (1) : Réutilisation des layouts
        View layoutItem = convertView == null ? mInflater.inflate (R.layout.profile_history_item, parent, false) : convertView;
        // (2) : Récupération des TextView de notre layout
        TextView longitudeTextView = layoutItem.findViewById(R.id.longitude);
        TextView latitudeTextView = layoutItem.findViewById(R.id.latitude);
        TextView durationTextView = layoutItem.findViewById(R.id.duration);
        TextView dateTextView = layoutItem.findViewById(R.id.date);
        TextView stateTextView = layoutItem.findViewById(R.id.state);
        ImageView conditionMeteoImageView = layoutItem.findViewById(R.id.condition_meteo_image);

        // (3) : Renseignement des valeurs
        longitudeTextView.setText(String.format(Locale.FRANCE, "%.6f", item.getLongitude()));
        latitudeTextView.setText(String.format(Locale.FRANCE, "%.6f", item.getLatitude()));

        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.FRANCE);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        outputFormat.setTimeZone(TimeZone.getDefault());

        // Duration
        try {
            Date dateCreated = inputFormat.parse(item.getCreatedAt());
            // The finished date is the last updatedAt date, if not finished, take the current date
            Date dateFinished = item.isFinished() ? inputFormat.parse(item.getUpdatedAt()) : new Date();
            assert dateCreated != null;
            assert dateFinished != null;
            long duration = dateFinished.getTime() - dateCreated.getTime();
            durationTextView.setText(getDurationFormatted(duration));
        } catch (ParseException e) {
            durationTextView.setText("-");
        }

        // Date created
        try {
            Date dateCreated = inputFormat.parse(item.getCreatedAt());
            assert dateCreated != null;
            dateTextView.setText(outputFormat.format(dateCreated));
        } catch (ParseException e) {
            dateTextView.setText(item.getCreatedAt());
        }

        if (!item.isFinished()){
            stateTextView.setText(R.string.profile_history_item_ongoing);
            stateTextView.setTextColor(layoutItem.getResources().getColor(R.color.green));
        } else {
            stateTextView.setText(R.string.profile_history_item_finished);
            stateTextView.setTextColor(layoutItem.getResources().getColor(R.color.grey));
        }

        // Weather condition image
        int iconId = layoutItem.getResources().getIdentifier("ic_weather_" + item.getWeatherCondition().name().toLowerCase(Locale.ROOT), "drawable", layoutItem.getContext().getPackageName());
        conditionMeteoImageView.setImageResource(iconId);

        // Locate button
        layoutItem.findViewById(R.id.locate).setOnClickListener(v -> {
            ProfileHistoryAdapter.OnProfileLocateButtonClickedListener activity = (OnProfileLocateButtonClickedListener) v.getContext();
            activity.onProfileLocateButtonClicked(item);
        });

        Animation animation = AnimationUtils.loadAnimation(mInflater.getContext(), R.anim.slide_in_left);
        animation.setDuration(300);
        layoutItem.startAnimation(animation);

        return layoutItem;
    }

    // Method that take a duration and return a string like X days or Y hours or Z minutes (only one of them)
    private String getDurationFormatted(long duration){
        long days = duration / (1000 * 60 * 60 * 24);
        long hours = duration / (1000 * 60 * 60);
        long minutes = duration / (1000 * 60);

        if (days > 0){
            return String.format(Locale.FRANCE, "%d jour" + (days > 1 ? "s" : ""), days);
        } else if (hours > 0){
            return String.format(Locale.FRANCE, "%d heure" + (hours > 1 ? "s" : ""), hours);
        } else {
            return String.format(Locale.FRANCE, "%d minute" + (minutes > 1 ? "s" : ""), minutes);
        }
    }
}
