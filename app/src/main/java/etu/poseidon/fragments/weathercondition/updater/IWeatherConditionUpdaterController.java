package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;

import java.util.Optional;

import etu.poseidon.models.Poi;

public interface IWeatherConditionUpdaterController {
    void userActionUpdatePerimeter(int perimeter);
    void userActionDeletePoi();
    void userActionCloseFragment();

    void modelDeleted(Optional<Poi> poi);

}
