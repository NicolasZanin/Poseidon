package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;

import java.util.Observable;

public interface IWeatherConditionUpdaterModel {
    void updatePerimeter(int perimeter);
    boolean isDeletable();
    void setController(IWeatherConditionUpdaterController controller);
    void setPoiFinished(Context context);
    void deletePoiDefinilety(Context context);
}
