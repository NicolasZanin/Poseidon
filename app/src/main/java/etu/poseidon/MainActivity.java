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

import android.app.AppComponentFactory;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements WeatherConditionUpdaterFragment.OnWeatherConditionDeletedListener, WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener, LoginFragment.LoginListener {
    private MapView map;
    private IMapController gestionnaireMap;

    private Fragment openedFragment;

    GoogleSignInAccount loggedAccount;

    private final String TAG = "JULIAN " + getClass().getSimpleName();
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 5;
    public static final int PERMISSIONS_FINE_LOCATION = 99;
    public static final int PERMISSIONS_COARSE_LOCATION = 98;


    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_gps, sw_locationsupdates;
    boolean updateOn = false;
    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

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
        map.getZoomController().activate();
        CustomZoomButtonsDisplay custom = map.getZoomController().getDisplay();
        custom.setPositions(false,
                CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                CustomZoomButtonsDisplay.VerticalPosition.CENTER);

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
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /*startLocationUpdates();*/
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateCurrentLocation(locationResult.getLastLocation());
            }
        };
        updateGPS();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
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
                        Log.d(TAG, "Location is not null : " + location.getLatitude() + " " + location.getLongitude());
                        updateCurrentLocation(location);
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

    private void updateCurrentLocation(Location location) {
        currentLocation = location;
        GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        gestionnaireMap.setCenter(newLocation);
        gestionnaireMap.setZoom(20.0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loggedAccount = GoogleSignIn.getLastSignedInAccount(this);
        System.out.println("Logged account: " + loggedAccount.getEmail());

        // Button to open weather condition creator
        Button buttonWeatherConditionCreator = findViewById(R.id.button_weather_condition_creator);
        buttonWeatherConditionCreator.setOnClickListener(v -> openWeatherConditionCreatorFragment());

        // Button to open profile
        Button buttonProfile = findViewById(R.id.button_profile);
        buttonProfile.setOnClickListener(v -> openProfileFragment());
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

    /**
     * Méthode pour ajouter des points
     * @param poi Point of interest, le point à ajouter
     */
    private void addPOI(Poi poi) {
        // On crée un GeoPoint qui contient une latitude et une longitude
        GeoPoint poiPosition = new GeoPoint(poi.getLatitude(), poi.getLongitude());
        // On crée une icone
        Marker poiMarker = new Marker(map);
        // On paramètre la position de l'icone avec le Geopoint créer auparavant
        poiMarker.setPosition(poiPosition);

        // Set resource icon dynamically with poi.getType()
        int poiIconId = getResources().getIdentifier("ic_poi_" + poi.getWeatherCondition().name().toLowerCase(Locale.ROOT), "drawable", getPackageName());
        Drawable poiIcon = getResources().getDrawable(poiIconId);
        poiMarker.setIcon(poiIcon);

        // On récupère la liste des points de la map et on ajoute le point sur cette liste pour
        // ajouter sur la map
        map.getOverlays().add(poiMarker);

        // On ajoute un événement quand on clique sur l'icone ajouté
        poiMarker.setOnMarkerClickListener((marker, mapView) -> {
            openWeatherConditionUpdaterFragment(poi);
            return false;
        });
    }
    /**
     * Supprime tous les points de la carte
     */
    private void removeAllPOIs() {
        // Vide la liste des points de la map
        map.getOverlays().clear();
        map.invalidate();
    }

    private void openWeatherConditionUpdaterFragment(Poi poi){
        closeOpenedFragment();
        WeatherConditionUpdaterFragment weatherConditionUpdaterFragment = new WeatherConditionUpdaterFragment();
        Bundle args = new Bundle();
        args.putParcelable("poi_param", poi);
        weatherConditionUpdaterFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) weatherConditionUpdaterFragment).commit();
        openedFragment = weatherConditionUpdaterFragment;
    }

    private void openWeatherConditionCreatorFragment(){
        if(loggedAccount != null){
            closeOpenedFragment();
            WeatherConditionCreatorFragment weatherConditionCreatorFragment = new WeatherConditionCreatorFragment();
            Bundle args = new Bundle();
            args.putParcelable("current_location_param", currentLocation);
            weatherConditionCreatorFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) weatherConditionCreatorFragment).commit();
            openedFragment = weatherConditionCreatorFragment;
        } else {
            openLoginFragment();
        }
    }

    private void openProfileFragment(){
        if(loggedAccount != null){
            // TODO : Julian ouvrir le fragment de profil
            System.out.println("TODO : Julian ouvrir le fragment de profil");
            //closeOpenedFragment();
            //ProfileFragment profileFragment = new ProfileFragment();
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) profileFragment).commit();
            //openedFragment = profileFragment;
        } else {
            openLoginFragment();
        }
    }

    private void openLoginFragment(){
        closeOpenedFragment();
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) loginFragment).commit();
        openedFragment = loginFragment;
    }

    private void closeOpenedFragment() {
        if (openedFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(openedFragment).commit();
            openedFragment = null;
        }
    }

    @Override
    public void onWeatherConditionDeleted() {
        removeAllPOIs();
        loadAllPOIs();
    }

    @Override
    public void onWeatherConditionCreated() {
        removeAllPOIs();
        loadAllPOIs();
    }

    @Override
    public void onLogIn(GoogleSignInAccount account) {
        loggedAccount = account;
        closeOpenedFragment();
    }
}