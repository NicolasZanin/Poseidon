package etu.poseidon;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Locale;

import etu.poseidon.fragments.LoginFragment;
import etu.poseidon.fragments.profile.ProfileFragment;
import etu.poseidon.fragments.weathercondition.WeatherConditionCreatorFragment;
import etu.poseidon.fragments.weathercondition.WeatherConditionUpdaterFragment;
import etu.poseidon.fragments.picture.IPictureActivity;
import etu.poseidon.fragments.picture.PictureFragment;
import etu.poseidon.fragments.profile.ProfileHistoryAdapter;
import etu.poseidon.models.Poi;
import etu.poseidon.temp.TempAlertExample;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        WeatherConditionUpdaterFragment.OnWeatherConditionDeletedListener,
        WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener,
        ProfileHistoryAdapter.OnLocateButtonClickedListener,
        IPictureActivity {
    private MapView map;
    private IMapController gestionnaireMap;

    private Fragment openedFragment;

    private final String TAG = "POSEIDON " + getClass().getSimpleName();
    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 1;
    public static final int PERMISSIONS_FINE_LOCATION = 99;
    public static final int PERMISSIONS_COARSE_LOCATION = 98;
    public boolean followUser = false;
    private Bitmap picture;

    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentRealLocation;

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
        locationRequest.setInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateCurrentLocation(locationResult.getLastLocation());
            }
        };

        findViewById(R.id.button_relocate).setOnClickListener( click -> {
            GeoPoint geoPointActuel = new GeoPoint(currentRealLocation.getLatitude(),
                    currentRealLocation.getLongitude());
            gestionnaireMap.setCenter(geoPointActuel);
        });

        findViewById(R.id.coMap).setOnClickListener( click -> {
            Button button = findViewById(R.id.coMap);
            if(followUser){
                stopLocationUpdates();
                followUser = false;
                button.setText("Exploration de la carte");
            }else{
                startLocationUpdates();
                followUser = true;
                button.setText("La carte suit votre position");
            }
        });

        updateGPS();


    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void startLocationUpdates() {
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
                    startLocationUpdates();
                    Log.d(TAG, "GPS UPDATED");

                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Need Permission");
                }
                break;
            case REQUEST_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast toast = Toast.makeText(this, "CAMERA Permission granted", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(this, "CAMERA Permission refused", Toast.LENGTH_SHORT);
                    toast.show();
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
        currentRealLocation = location;
        Button button = findViewById(R.id.coordinates);
        button.setText(currentRealLocation.getLatitude() + " " + currentRealLocation.getLongitude());
        GeoPoint newLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
        gestionnaireMap.setCenter(newLocation);
        gestionnaireMap.setZoom(20.0);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(GoogleSignIn.getLastSignedInAccount(this) != null)
            Log.d(TAG,"Logged account: " + GoogleSignIn.getLastSignedInAccount(this).getEmail());
        else
            Log.d(TAG,"No logged account");

        // Button to open weather condition creator
        Button buttonWeatherConditionCreator = findViewById(R.id.button_weather_condition_creator);
        buttonWeatherConditionCreator.setOnClickListener(v -> openWeatherConditionCreatorFragment());

        // Button to open profile
        Button buttonProfile = findViewById(R.id.button_profile);
        buttonProfile.setOnClickListener(v -> openProfileFragment());

        loadAllPOIs();

        // This is temporary - only for demonstration
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            // Trst Arnaud
            Button testArnaud = findViewById(R.id.test_arnaud);
            testArnaud.setOnClickListener(v -> TempAlertExample.run());
            // End temporary code
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                picture = (Bitmap) data.getExtras().get("data");
                PictureFragment pictureFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_picture);
                pictureFragment.setPicture(picture);
            } else if (resultCode == RESULT_CANCELED) {
                Toast toast = Toast.makeText(this, "No picture taken", Toast.LENGTH_SHORT);
                toast.show();
            }else {
                Toast toast = Toast.makeText(this, "Action failed", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                Log.e(TAG,"Error: " + t.getMessage());
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
        Drawable poiIcon = ResourcesCompat.getDrawable(getResources(), poiIconId, getTheme());
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
        if(GoogleSignIn.getLastSignedInAccount(this) != null){
            closeOpenedFragment();
            WeatherConditionCreatorFragment weatherConditionCreatorFragment = new WeatherConditionCreatorFragment();

            Bundle args = new Bundle();
            // Real location
            args.putParcelable("real_location_param", currentRealLocation);
            weatherConditionCreatorFragment.setArguments(args);

            // Map location
            GeoPoint location = new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
            args.putParcelable("map_location_param", location);
            weatherConditionCreatorFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) weatherConditionCreatorFragment).commit();
            openedFragment = weatherConditionCreatorFragment;
        } else {
            openLoginFragment();
        }
    }

    private void openProfileFragment(){
        if(GoogleSignIn.getLastSignedInAccount(this) != null){
            closeOpenedFragment();
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) profileFragment).commit();
            openedFragment = profileFragment;
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
    public void onWeatherConditionFinished() {
        removeAllPOIs();
        loadAllPOIs();
    }

    @Override
    public void onWeatherConditionCreated() {
        removeAllPOIs();
        loadAllPOIs();
    }

    @Override
    public void onLocateButtonClicked(Poi poi) {
        closeOpenedFragment();
        GeoPoint poiPosition = new GeoPoint(poi.getLatitude(), poi.getLongitude());
        gestionnaireMap.setCenter(poiPosition);
        gestionnaireMap.setZoom(20.0);
        openWeatherConditionUpdaterFragment(poi);
    }
}