package etu.poseidon.factories;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


import etu.poseidon.models.weather.WeatherCondition;

/**
 * Factory de création de point
 */
public class PoiCreatorFactory extends AppCompatActivity {
    public PoiCreatorFactory(){ };
    public enum PoiCondition { SUN, CLOUD, THUNDERSTORM, WIND, STORM, RAIN, GLOBAL}

    /**
     * Permet de convertir un WeatherCondition en PoiCondition
     * @param weatherCondition weather Condition
     * @return Renvoie un PoiCondition
     */
    public static PoiCondition convertWeatherConditionPoiCondition(WeatherCondition weatherCondition) {
        switch (weatherCondition) {
            case SUN: return PoiCondition.SUN;
            case CLOUD: return PoiCondition.CLOUD;
            case THUNDERSTORM: return PoiCondition.THUNDERSTORM;
            case STORM: return PoiCondition.STORM;
            case WIND: return PoiCondition.WIND;
            case RAIN: return PoiCondition.RAIN;
            default: throw new IllegalArgumentException("Le temps ajouté en paramètre n'est pas le bon");
        }
    }

    /**
     * Fonction pour créer un Marker (== Point)
     * @param map la carte de l'application
     * @param conditionPoi l'enum du climat du point
     * @param poiPosition La position du point
     * @param context Le context de l'application
     * @return Le marker créer
     */
    public static Marker buildMarker(MapView map, PoiCondition conditionPoi, GeoPoint poiPosition, Context context) {
        // On crée une icone
        Marker poiMarker = new Marker(map);
        // On paramètre la position de l'icone avec le Geopoint créer auparavant
        poiMarker.setPosition(poiPosition);

        // Set resource icon dynamically with poi.getType()
        int poiIconId = 0;
        switch (conditionPoi) {
            case SUN :
                poiIconId = context.getResources().getIdentifier("ic_poi_sun", "drawable", context.getPackageName());
                break;
            case CLOUD:
                poiIconId = context.getResources().getIdentifier("ic_poi_cloud", "drawable", context.getPackageName());
                break;
            case RAIN:
                poiIconId = context.getResources().getIdentifier("ic_poi_rain", "drawable", context.getPackageName());
                break;
            case WIND:
                poiIconId = context.getResources().getIdentifier("ic_poi_wind", "drawable", context.getPackageName());
                break;
            case STORM:
                poiIconId = context.getResources().getIdentifier("ic_poi_storm", "drawable", context.getPackageName());
                break;
            case THUNDERSTORM:
                poiIconId = context.getResources().getIdentifier("ic_poi_thunderstorm", "drawable", context.getPackageName());
                break;
            case GLOBAL:
                poiIconId = context.getResources().getIdentifier("ic_poi_global", "drawable", context.getPackageName());
                poiMarker.setOnMarkerClickListener((marker, mapView) -> false);
                poiMarker.setId("global");
                break;
        }
        Drawable poiIcon = ResourcesCompat.getDrawable(context.getResources(), poiIconId, context.getTheme());
        poiMarker.setIcon(poiIcon);
        return poiMarker;
    }
}
