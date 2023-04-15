package etu.poseidon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements WeatherConditionUpdaterFragment.OnWeatherConditionDeletedListener, WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener {
    private MapView map;
    private IMapController gestionnaireMap;

    private Fragment openedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Les configurations par défaut de l'appareil
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);

        // On récupère le layout de la map dans le activity_main.xml
        map = findViewById(R.id.carte);

        // Récupère la map en ligne
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Active les zooms
        map.setBuiltInZoomControls(true);

        // Crée le point de départ de classe GeoPoint qu'on pourra personnalisé
        // par la suite (avantage du geoPoint) et doit être utilisé par le gestionnaire de la map
        GeoPoint startPoint = new GeoPoint(43.65020, 7.00517);

        // On récupère le gestionnaire de la map pour poser des points, centrer la position, le zoom, etc.
        gestionnaireMap = map.getController();

        // Pour centrer la map sur la coordonnées stating point (43.65020, 7.00517)
        // au démarrage de l'application
        gestionnaireMap.setCenter(startPoint);

        // Pour mettre le niveau de zoom à 20.0
        gestionnaireMap.setZoom(20.0);

        // Button to open weather condition creator
        Button buttonWeatherConditionCreator = findViewById(R.id.button_weather_condition_creator);
        buttonWeatherConditionCreator.setOnClickListener(v -> {
            openFragmentWeatherConditionCreator();
        });

        // Load all POIs on create
        loadAllPOIs();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    private void openFragmentWeatherConditionCreator() {
        closeOpenedFragment();
        WeatherConditionCreatorFragment weatherConditionCreatorFragment = new WeatherConditionCreatorFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) weatherConditionCreatorFragment).commit();
        // TODO : passer les coordonnées GPS actuelles de la map, pour l'instant les pois sont crées en (43.65020, 7.00517) (comme la map)
        openedFragment = weatherConditionCreatorFragment;
    }

    private void openFragmentWeatherConditionUpdater(Poi poi){
        closeOpenedFragment();
        WeatherConditionUpdaterFragment weatherConditionUpdaterFragment = new WeatherConditionUpdaterFragment();
        Bundle args = new Bundle();
        args.putParcelable("poi_param", poi);
        weatherConditionUpdaterFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) weatherConditionUpdaterFragment).commit();
        openedFragment = weatherConditionUpdaterFragment;
    }

    private void loadAllPOIs(){
        PoiApiClient.getInstance().getPoiList(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    List<Poi> poiList = response.body();
                    for (Poi poi : poiList) {
                        addPOI(poi);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }
        });
    }

    private void addPOI(Poi poi) {
        GeoPoint poiPosition = new GeoPoint(poi.getLatitude(), poi.getLongitude());
        Marker poiMarker = new Marker(map);
        poiMarker.setPosition(poiPosition);

        // Set resource icon dynamically with poi.getType()
        int poiIconId = getResources().getIdentifier("ic_poi_" + poi.getWeatherCondition().name().toLowerCase(Locale.ROOT), "drawable", getPackageName());
        Drawable poiIcon = getResources().getDrawable(poiIconId);
        poiMarker.setIcon(poiIcon);

        map.getOverlays().add(poiMarker);

        poiMarker.setOnMarkerClickListener((marker, mapView) -> {
            openFragmentWeatherConditionUpdater(poi);
            return false;
        });
    }

    private void removeAllPOIs(MapView map) {
        List<Marker> markers = new ArrayList<>();
        for (Overlay overlay : map.getOverlays()) {
            if (overlay instanceof Marker) {
                markers.add((Marker) overlay);
            }
        }
        map.getOverlays().removeAll(markers);
        map.invalidate();
    }


    private void closeOpenedFragment() {
        if (openedFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(openedFragment).commit();
            openedFragment = null;
        }
    }

    @Override
    public void onWeatherConditionDeleted() {
        removeAllPOIs(map);
        loadAllPOIs();
    }

    @Override
    public void onWeatherConditionCreated() {
        removeAllPOIs(map);
        loadAllPOIs();
    }
}