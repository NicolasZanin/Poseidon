package etu.poseidon.fragments.weathercondition.updater;

import java.util.Optional;

import etu.poseidon.models.Poi;

public class WeatherConditionUpdaterController implements IWeatherConditionUpdaterController {
    private IWeatherConditionUpdaterModel model;
    private IWeatherConditionUpdaterView view;
    private WeatherConditionUpdaterFragment fragment;

    public WeatherConditionUpdaterController(IWeatherConditionUpdaterModel model, IWeatherConditionUpdaterView view, WeatherConditionUpdaterFragment fragment) {
        this.model = model;
        this.view = view;
        this.fragment = fragment;
    }

    @Override
    public void userActionUpdatePerimeter(int perimeter) {
        model.updatePerimeter(perimeter);
    }

    @Override
    public void modelDeleted(Optional<Poi> poi) {
        poi.ifPresent(value -> fragment.getActivityListener().onWeatherConditionFinished(value.getLatitude(), value.getLongitude()));
        closeFragment();
    }

    @Override
    public void userActionDeletePoi() {
        if(model.isDeletable()) {
            model.deletePoiDefinilety(fragment.getContext());
        } else {
            model.setPoiFinished(fragment.getContext());
        }
    }

    @Override
    public void userActionCloseFragment() {
        closeFragment();
    }

    private void closeFragment(){
        fragment.requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
}
