package etu.poseidon;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import etu.poseidon.R;
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

    private final String TAG = "JULIAN "+getClass().getSimpleName();
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION= 99;
    public static final int PERMISSIONS_COARSE_LOCATION= 98;


    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_gps, sw_locationsupdates;

    boolean updateOn = false;

    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Les configurations par défaut de l'appareil
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);


        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);

        sw_gps = findViewById(R.id.sw_gps);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationsupdates.isChecked()) {
                    startLocationUpdates();
                }
                else {
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("LOCATION IS STOPPED");
        tv_lat.setText("NOT TRACKING LOCATION");
        tv_lon.setText("NOT TRACKING LOCATION");
        tv_speed.setText("NOT TRACKING LOCATION");
        tv_address.setText("NOT TRACKING LOCATION");
        tv_accuracy.setText("NOT TRACKING LOCATION");
        tv_altitude.setText("NOT TRACKING LOCATION");
        tv_sensor.setText("NOT TRACKING LOCATION");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        tv_updates.setText("LOCATION IS BEING TRACKED");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,null);
        updateGPS();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, String.valueOf(permissions.length));
        Log.d(TAG, "permission : " +permissions[0]);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                Log.d(TAG, "grantResults : " +grantResults[0]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "grantResults[0] = "+ PackageManager.PERMISSION_GRANTED);
                    updateGPS();
                    Log.d(TAG, "GPS UPDATED");

                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Need Permission");
                }
                break;
            default:
                Log.d(TAG, "Permission Refused");
                break;
        }

    }

    private void updateGPS(){


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission Granted");
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d(TAG, "Location is not null");
                        updateUIValues(location);
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if(location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not Available");
        }
        if(location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Not Available");
        }

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (IOException e) {
            tv_address.setText("Unable to get street address");
        }


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