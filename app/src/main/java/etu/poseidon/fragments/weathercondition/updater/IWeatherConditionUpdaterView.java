package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import etu.poseidon.models.Poi;

public interface IWeatherConditionUpdaterView {
    void setContentView(Poi poiToUpdate, View view, Context context);
    void setController(IWeatherConditionUpdaterController controller);
    void setPicture(Bitmap picture);
}
