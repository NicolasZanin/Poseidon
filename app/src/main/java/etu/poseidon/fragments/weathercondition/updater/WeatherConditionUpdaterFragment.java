package etu.poseidon.fragments.weathercondition.updater;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etu.poseidon.R;
import etu.poseidon.models.Poi;

public class WeatherConditionUpdaterFragment extends Fragment {

    /**
     * Ce fragment implémente le pattern MVC, mais nous avons du l'adapter au fragment.
     * En effet, le fragment est normalement complètement indépendant, mais dans notre application
     * nous n'avons qu'une seule Activity et que des Fragments pour les autres fonctionnalités.
     * Nous avons donc décidé d'implémenter le pattern MVC sur la fonctionnalité de "mise à jour
     * des conditions météorologiques" et donc d'adapter le MVC au fragment...
     *
     * Nous avons essayé de respecter au maximum le pattern MVC, mais nous avons du faire quelques compromis.
     *
     * Nous avons :
     * - Le Model :
     *      - Il contient les données (ici un poi) et peut gérer les modifications directes
     *          sur les données (modification de l'objet et appel à l'API ("CRUD"))
     * - La View :
     *      - Gère les éléments graphiques et les listeners
     * - Le Controller :
     *      - Gère la communication de la View au le Model
     */

    private WeatherConditionUpdaterController controller;
    private WeatherConditionUpdaterModel model;
    private WeatherConditionUpdaterView view;

    private static final String ARG_POI = "poi_param";
    private Poi poiToUpdate;

    public interface OnWeatherConditionFinishedListener {
        void onWeatherConditionFinished(double latitude, double longitude);
    }
    private OnWeatherConditionFinishedListener mListener;

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
        view = new WeatherConditionUpdaterView();
        model = new WeatherConditionUpdaterModel(poiToUpdate);
        controller = new WeatherConditionUpdaterController(model, view, this);
        view.setController(controller);
        model.setController(controller);
        model.addObserver(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_weather_condition_updater, container, false);

        view.setContentView((Poi) poiToUpdate, layoutView, getContext());

        return layoutView;
    }

    public void loadPicture(){
        controller.loadPictureFromStorage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherConditionFinishedListener) {
            mListener = (OnWeatherConditionFinishedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnWeatherConditionDeletedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public OnWeatherConditionFinishedListener getActivityListener() {
        return mListener;
    }
}