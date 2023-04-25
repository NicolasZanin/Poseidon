package etu.poseidon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView map;
    private IMapController gestionnaireMap;

    private void ajouterPoint(double x, double y) {
        List<Overlay> listPointMap = map.getOverlays();
        Drawable drawable = new DrawableContainer();
        GeoPoint pointAAdd = new GeoPoint(x, y);
        Marker marker = new Marker(map);
        marker.setPosition(pointAAdd);
        listPointMap.add(marker);
    }

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

        ajouterPoint(43.65020, 7.00517);
        ajouterPoint(43.65010, 7.00517);
        ajouterPoint(43.65000, 7.00517);
        ajouterPoint(43.64990, 7.00517);
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
}