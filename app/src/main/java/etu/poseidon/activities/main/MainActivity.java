package etu.poseidon.activities.main;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.preference.PreferenceManager;

import android.graphics.Bitmap;
import android.widget.Button;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsDisplay;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import etu.poseidon.Message;
import etu.poseidon.activities.main.tools.FilterPoi;
import etu.poseidon.activities.main.tools.MainActivityFragmentManager;
import etu.poseidon.factories.PoiCreatorFactory;
import etu.poseidon.R;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import etu.poseidon.fragments.alert.EditAlert;
import etu.poseidon.fragments.search.SearchFragment;
import etu.poseidon.fragments.weathercondition.WeatherConditionCreatorFragment;
import etu.poseidon.fragments.weathercondition.updater.WeatherConditionUpdaterFragment;
import etu.poseidon.activities.main.tools.MainActivityPermissions;
import etu.poseidon.fragments.picture.PictureFragment;
import etu.poseidon.fragments.profile.ProfileHistoryAdapter;
import etu.poseidon.models.Account;
import etu.poseidon.models.Alert;
import etu.poseidon.models.Poi;
import etu.poseidon.models.weather.WeatherCondition;
import etu.poseidon.webservices.alerts.AlertApiClient;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements Observer,
        WeatherConditionUpdaterFragment.OnWeatherConditionFinishedListener,
        WeatherConditionCreatorFragment.OnWeatherConditionCreatedListener,
        ProfileHistoryAdapter.OnProfileLocateButtonClickedListener,
        SearchFragment.OnSearchFragmentListener,
        EditAlert.OnConfirmEditAlertListener
{

    private MapView map;
    private IMapController gestionnaireMap;

    private MainActivityFragmentManager fragmentManager;

    private String tokenFireBase;

    private final String TAG = "POSEIDON " + getClass().getSimpleName();
    public static final String TAG_SEARCH_FRAGMENT = "POSEIDON" + "SEARCH";
    public static final int FAST_UPDATE_INTERVAL = 1;
    private boolean followUser = true, isPositionOnMapSet = false, isUserMovingMap = false;
    private Bitmap picture;
    private ArrayList<WeatherCondition> weatherSelected = new ArrayList<>();
    private String searchText = "";
    private Marker markerCenter = null;

    LocationRequest locationRequest;
    LocationCallback locationCallBack;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentRealLocation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Les configurations par défaut de l'appareil
        Configuration.getInstance().load(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_main);

        fragmentManager = new MainActivityFragmentManager(this);

        map = findViewById(R.id.carte);
        // Récupère la map en ligne
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Active les zooms
        map.getZoomController().activate();
        CustomZoomButtonsDisplay custom = map.getZoomController().getDisplay();
        custom.setPositions(false,
                CustomZoomButtonsDisplay.HorizontalPosition.RIGHT,
                CustomZoomButtonsDisplay.VerticalPosition.CENTER);

        // Touch listener to set if the user screen is following his real GPS position or not (used when GPS updates to move the map)
        map.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                isUserMovingMap = true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                followUser = false;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                isUserMovingMap = false;
                updateCoordinatesText(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
            }
            return false;
        });

        // On récupère le gestionnaire de la map pour poser des points, centrer la position, le zoom, etc.
        gestionnaireMap = map.getController();

        // Setup location services
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

        // Location updates
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        startLocationUpdates();

        Message.getInstance().addObserver(this);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                System.out.println("Fetching FCM registration token failed");

                return;
            }
            this.tokenFireBase = task.getResult();
            // Log and toast
            Log.d("TOKEN FIREBASE", task.getResult());
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivityPermissions.REQUEST_FINE_LOCATION);
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MainActivityPermissions.REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Permission de localisation refusée, vous ne pourrez pas utiliser notre application convenablement", Toast.LENGTH_LONG).show();
                }
                break;
            case MainActivityPermissions.REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission d'utilisation de la caméra accordée", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission d'utilisation de la caméra refusée, vous ne pourrez pas utiliser notre application convenablement", Toast.LENGTH_LONG).show();
                }
                break;
            case MainActivityPermissions.REQUEST_MEDIA_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(fragmentManager.getOpenedFragment() instanceof WeatherConditionCreatorFragment){
                        ((WeatherConditionCreatorFragment) fragmentManager.getOpenedFragment()).savePicture();
                    } else {
                        Toast.makeText(this, "Permission d'écriture sur le stockage accordée", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(fragmentManager.getOpenedFragment() instanceof WeatherConditionCreatorFragment){
                        ((WeatherConditionCreatorFragment) fragmentManager.getOpenedFragment()).closeFragmentWithUnsavedPicture();
                    } else {
                        Toast.makeText(this, "Permission d'écriture sur le stockage refusée, vous ne pourrez pas utiliser notre application convenablement", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case MainActivityPermissions.REQUEST_MEDIA_READ:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission de lecture sur le stockage accordée", Toast.LENGTH_SHORT).show();
                    if(fragmentManager.getOpenedFragment() instanceof WeatherConditionUpdaterFragment){
                        ((WeatherConditionUpdaterFragment) fragmentManager.getOpenedFragment()).loadPicture();
                    }
                } else {
                    Toast.makeText(this, "Permission de lecture sur le stockage refusée, vous ne pourrez pas utiliser notre application convenablement", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Log.d(TAG, "Permission refusée");
                break;
        }

    }

    private void updateGPS(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) updateCurrentLocation(location);
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivityPermissions.REQUEST_FINE_LOCATION);
            }
        }
    }

    private void updateCurrentLocation(Location location) {
        currentRealLocation = location;

        // Update blue dot marker
        if (markerCenter != null)
            markerCenter.setPosition(new GeoPoint(location));
        else {
            GeoPoint geoPointActuel = new GeoPoint(location);
            markerCenter = PoiCreatorFactory.buildMarker(map, PoiCreatorFactory.PoiCondition.GLOBAL, geoPointActuel, this);
            map.getOverlays().add(markerCenter);
        }

        // Update map
        if(followUser && !isUserMovingMap) {
            // isPositionOnMapSet is used to set the zoom and center of the map only when the app starts
            if(isPositionOnMapSet) gestionnaireMap.animateTo(new GeoPoint(location.getLatitude(), location.getLongitude()));
            else {
                gestionnaireMap.setZoom(15.0);
                gestionnaireMap.setCenter(new GeoPoint(location.getLatitude(), location.getLongitude()));
                isPositionOnMapSet = true;
            }

            // Update coordinates
            updateCoordinatesText(location.getLatitude(), location.getLongitude());
        }
    }

    void updateCoordinatesText(double latitude, double longitude){
        DecimalFormat decimalFormat = new DecimalFormat("#.#######");
        String formattedLatitude = decimalFormat.format(latitude);
        String formattedLongitude = decimalFormat.format(longitude);
        TextView currentCoordinates = findViewById(R.id.coordinates);
        int stringCoordinates = getResources().getIdentifier("activity_main_coordonnees", "string", getPackageName());
        currentCoordinates.setText(getString(stringCoordinates,formattedLatitude, formattedLongitude));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Button to open weather condition creator
        Button buttonWeatherConditionCreator = findViewById(R.id.button_weather_condition_creator);
        buttonWeatherConditionCreator.setOnClickListener(v -> fragmentManager.openWeatherConditionCreatorFragment());

        // Button to open profile
        Button buttonProfile = findViewById(R.id.button_profile);
        buttonProfile.setOnClickListener(v -> fragmentManager.openProfileFragment());

        // Button to open alert
        Button buttonAlert = findViewById(R.id.button_alert_menu);
        buttonAlert.setOnClickListener(v -> fragmentManager.openAlertFragment());

        // Button to relocate the map to the user's GPS position
        findViewById(R.id.button_relocate).setOnClickListener(click -> {
            GeoPoint geoPointActuel = new GeoPoint(currentRealLocation.getLatitude(), currentRealLocation.getLongitude());
            gestionnaireMap.animateTo(geoPointActuel);
            followUser = true;
        });

        // Button to open the search fragment
        findViewById(R.id.button_search).setOnClickListener( click -> {
            fragmentManager.openSearchFragment(weatherSelected, searchText);
        });

        // Button to reload all
        findViewById(R.id.button_refresh).setOnClickListener(click -> {
            removeAllPOIs();
            loadAllPOIs(true);
            updateGPS();
        });

        // Get the last signed in account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) Account.logIn(account);

        // Load all POIs on the map (weather condition indicator)
        loadAllPOIs(false);
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
        if (requestCode == MainActivityPermissions.REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                picture = (Bitmap) data.getExtras().get("data");
                PictureFragment pictureFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_picture);
                assert pictureFragment != null;
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

    private void loadAllPOIs(boolean filter){
        PoiApiClient.getInstance().getPoiList(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Poi>> call, @NonNull Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    List<Poi> poiList = response.body();
                    if (filter) {
                        FilterPoi filterPoi = new FilterPoi(poiList);
                        poiList = filterPoi.filtrePois(weatherSelected);
                    }
                    assert poiList != null;
                    for (Poi poi : poiList) {
                        addPOI(poi);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Poi>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    /**
     * Méthode pour ajouter des points
     * @param poi Point of interest, le point à ajouter
     */
    private void addPOI(Poi poi) {
        GeoPoint poiPosition = new GeoPoint(poi.getLatitude(), poi.getLongitude());

        Marker poiMarker = PoiCreatorFactory.buildMarker(map, PoiCreatorFactory.convertWeatherConditionPoiCondition(poi.getWeatherCondition()),
                poiPosition, this);

        // On récupère la liste des points de la map et on ajoute le point sur cette liste pour ajouter sur la map
        map.getOverlays().add(poiMarker);

        // On ajoute un événement quand on clique sur l'icone ajouté
        poiMarker.setOnMarkerClickListener((marker, mapView) -> {
            fragmentManager.openWeatherConditionUpdaterFragment(poi);
            return false;
        });
    }
    /**
     * Supprime tous les points de la carte
     */
    private void removeAllPOIs() {
        Overlay blueDotMarker = null;
        for(Overlay o : map.getOverlays()){
            if(o instanceof Marker){
                Marker marker = (Marker) o;
                if(marker.getId() != null && marker.getId().equals("global")) blueDotMarker = o;
            }
        }
        map.getOverlays().clear();
        map.invalidate();
        assert blueDotMarker != null;
        map.getOverlays().add(blueDotMarker);
    }

    private void removePoi(double latitude, double longitude){
        for (Overlay overlay : map.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker marker = (Marker) overlay;
                if (marker.getPosition().getLatitude() == latitude && marker.getPosition().getLongitude() == longitude && marker.getId() == null) {
                    map.getOverlays().remove(marker);
                    map.invalidate();
                    break;
                }
            }
        }
    }

    @Override
    public void onWeatherConditionFinished(double latitude, double longitude) {
        removePoi(latitude, longitude);
    }

    @Override
    public void onWeatherConditionCreated(Poi newPoi) {
        addPOI(newPoi);
    }

    @Override
    public void onProfileLocateButtonClicked(Poi poi) {
        fragmentManager.closeOpenedFragment();
        GeoPoint poiPosition = new GeoPoint(poi.getLatitude(), poi.getLongitude());
        gestionnaireMap.setCenter(poiPosition);
        gestionnaireMap.setZoom(15.0);
        fragmentManager.openWeatherConditionUpdaterFragment(poi);
    }

    // Centre la map avec le GeoPoint donner dans la barre de recherche
    @Override
    public void relocateSearch(GeoPoint geoPoint) {
        followUser = false;
        updateCoordinatesText(geoPoint.getLatitude(), geoPoint.getLongitude());
        gestionnaireMap.animateTo(geoPoint);
    }

    @Override
    public void filterMap(ArrayList<WeatherCondition> weatherConditionList, String searchText) {
        removeAllPOIs();
        weatherSelected = weatherConditionList;
        this.searchText = searchText;
        loadAllPOIs(true);
    }
    @Override
    public void onAlertCreated(String type, Alert alert) {
        Log.d("Alert", alert.toString());
        alert.setFireBaseToken(this.tokenFireBase);
        if(type.equals("create")){
            AlertApiClient.getInstance().createAlert(alert, new Callback<>() {
                @Override
                public void onResponse(Call<Alert> call, Response<Alert> response) {
                    if (response.isSuccessful()) {
                        Log.d("Alert", response.toString());
                        Toast.makeText(getApplicationContext(), "Alerte créée", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Alert", response.toString());
                    }
                }

                @Override
                public void onFailure(Call<Alert> call, Throwable t) {
                    Log.e("Alert", "Alert 1 not created ERROR");
                }
            });
        }
        else if(type.equals("edit")){
            AlertApiClient.getInstance().updateAlert(alert.getId(), alert, new Callback<Alert>() {
                @Override
                public void onResponse(Call<Alert> call, Response<Alert> response) {
                    if (response.isSuccessful()) {
                        Log.d("Alert", response.toString());
                        Toast.makeText(getApplicationContext(), "Alerte mise à jour", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Alert", response.toString());
                    }
                }

                @Override
                public void onFailure(Call<Alert> call, Throwable t) {
                    Log.e("Alert", "Alert 1 not updated ERROR");
                }
            });
        }
    }

    public Location getCurrentRealLocation() {
        return currentRealLocation;
    }

    public MapView getMap() {
        return map;
    }

    @Override
    public void update(Observable observable, Object o) {
        RemoteMessage message = (RemoteMessage) o;
        System.out.println(message.getNotification().getTitle());
    }
}