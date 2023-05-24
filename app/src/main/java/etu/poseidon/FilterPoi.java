package etu.poseidon;

import java.util.ArrayList;
import java.util.List;

import etu.poseidon.models.Poi;
import etu.poseidon.models.weather.WeatherCondition;

/**
 * Classe pour filtrer les Poi en fonctions des conditions météos choisi
 */
public class FilterPoi {
    private List<Poi> poiList;
    public FilterPoi(List<Poi> pois) {
        poiList = pois;
    }

    /**
     * Filtre les Poi en fonctions des conditions météos choisi
     * @param listWeatherCondition la liste des filtres choisi
     * @return Retourne la liste des poi filtrer
     */
    public List<Poi> filtrePois(List<WeatherCondition> listWeatherCondition) {
        List<Poi> listPoiFilter = new ArrayList<>();
        if (listWeatherCondition.size() != 0) {
            for (Poi poi : poiList)
                if (listWeatherCondition.contains(poi.getWeatherCondition()))
                    listPoiFilter.add(poi);
        }
        else {
            return poiList;
        }
        return listPoiFilter;
    }
}
