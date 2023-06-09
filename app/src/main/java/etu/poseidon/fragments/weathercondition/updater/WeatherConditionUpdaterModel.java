package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.Observable;
import java.util.Optional;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherConditionUpdaterModel extends Observable implements IWeatherConditionUpdaterModel {

    public enum WeatherConditionUpdaterApiState {
        SAVING,
        SAVED
    }

    private final Poi poiToUpdate;

    private IWeatherConditionUpdaterController controller;

    public WeatherConditionUpdaterModel(Poi poiToUpdate) {
        this.poiToUpdate = poiToUpdate;
    }

    @Override
    public void updatePerimeter(int perimeter) {
        setChanged();
        notifyObservers(WeatherConditionUpdaterApiState.SAVING);
        poiToUpdate.setPerimeter(perimeter);
        PoiApiClient.getInstance().updatePoi(poiToUpdate.getId(), poiToUpdate, new Callback<Poi>() {
            @Override
            public void onResponse(@NonNull Call<Poi> call, @NonNull Response<Poi> response) {
                if (response.isSuccessful()) {
                    setChanged();
                    notifyObservers(WeatherConditionUpdaterApiState.SAVED);
                } else {
                    setChanged();
                    notifyObservers(WeatherConditionUpdaterApiState.SAVING);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Poi> call, @NonNull Throwable t) {
                setChanged();
                notifyObservers(WeatherConditionUpdaterApiState.SAVING);
            }
        });
    }

    @Override
    public boolean isDeletable() {
        return poiToUpdate.isFinished();
    }

    @Override
    public void setController(IWeatherConditionUpdaterController controller) {
        this.controller = controller;
    }

    // Delete :
    // If the poi is not finished and the delete button is pressed, the poi is set to finished, it will not be displayed anymore (only in creator history)
    // If the poi is finished and the delete button is pressed, the poi is deleted definilety

    public void deletePoiDefinilety(Context context){
        PoiApiClient.getInstance().deletePoi(poiToUpdate.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    int stringSuccess = context.getResources().getIdentifier("weather_condition_updater_succefully_deleted_definitely", "string", context.getPackageName());
                    Toast.makeText(context, context.getString(stringSuccess), Toast.LENGTH_SHORT).show();
                    controller.modelDeleted(Optional.of(poiToUpdate));
                } else {
                    int stringError = context.getResources().getIdentifier("global_error", "string", context.getPackageName());
                    Toast.makeText(context, context.getString(stringError), Toast.LENGTH_SHORT).show();
                    controller.modelDeleted(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                int stringError = context.getResources().getIdentifier("global_error", "string", context.getPackageName());
                Toast.makeText(context, context.getString(stringError), Toast.LENGTH_SHORT).show();
                controller.modelDeleted(Optional.empty());
            }
        });
    }

    public void setPoiFinished(Context context){
        poiToUpdate.setFinished(true);
        PoiApiClient.getInstance().updatePoi(poiToUpdate.getId(), poiToUpdate, new Callback<Poi>() {
            @Override
            public void onResponse(@NonNull Call<Poi> call, @NonNull Response<Poi> response) {
                if (response.isSuccessful()) {
                    int stringSuccess = context.getResources().getIdentifier("weather_condition_updater_succefully_deleted", "string", context.getPackageName());
                    Toast.makeText(context, context.getString(stringSuccess), Toast.LENGTH_SHORT).show();
                    controller.modelDeleted(Optional.of(poiToUpdate));
                } else {
                    int stringError = context.getResources().getIdentifier("global_error", "string", context.getPackageName());
                    Toast.makeText(context, context.getString(stringError), Toast.LENGTH_SHORT).show();
                    controller.modelDeleted(Optional.empty());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Poi> call, @NonNull Throwable t) {
                int stringError = context.getResources().getIdentifier("global_error", "string", context.getPackageName());
                Toast.makeText(context, context.getString(stringError), Toast.LENGTH_SHORT).show();
                controller.modelDeleted(Optional.empty());
            }
        });
    }

    public String getId() {
        return poiToUpdate.getId();
    }
}
