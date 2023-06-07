package etu.poseidon.fragments.weathercondition.updater;

import java.util.Optional;

import etu.poseidon.models.Poi;

public interface IWeatherConditionUpdaterController {
    void userActionUpdatePerimeter(int perimeter);
    void userActionDeletePoi();
    void userActionCloseFragment();
    void userActionOpeningFragment();

    void modelDeleted(Optional<Poi> poi);
}
