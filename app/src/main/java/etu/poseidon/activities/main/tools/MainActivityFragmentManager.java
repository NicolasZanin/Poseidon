package etu.poseidon.activities.main.tools;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import etu.poseidon.R;
import etu.poseidon.activities.main.MainActivity;
import etu.poseidon.fragments.login.LoginFragment;
import etu.poseidon.fragments.alert.AlertsMenu;
import etu.poseidon.fragments.profile.ProfileFragment;
import etu.poseidon.fragments.search.SearchFragment;
import etu.poseidon.fragments.weathercondition.WeatherConditionCreatorFragment;
import etu.poseidon.fragments.weathercondition.updater.WeatherConditionUpdaterFragment;
import etu.poseidon.models.Account;
import etu.poseidon.models.Poi;
import etu.poseidon.models.weather.WeatherCondition;

public class MainActivityFragmentManager {
    private MainActivity mainActivity;
    private Fragment openedFragment;

    public MainActivityFragmentManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void openWeatherConditionUpdaterFragment(Poi poi){
        closeOpenedFragment();
        openedFragment = WeatherConditionUpdaterFragment.newInstance(poi);
        mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, openedFragment).commit();
    }

    public void openWeatherConditionCreatorFragment(){
        if(Account.isLoggedIn()){
            closeOpenedFragment();
            openedFragment = WeatherConditionCreatorFragment.newInstance(mainActivity.getCurrentRealLocation(), new GeoPoint(mainActivity.getMap().getMapCenter().getLatitude(), mainActivity.getMap().getMapCenter().getLongitude()));
            mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, openedFragment).commit();
        } else {
            openLoginFragment();
        }
    }

    public void openProfileFragment(){
        if(Account.isLoggedIn()){
            closeOpenedFragment();
            ProfileFragment profileFragment = new ProfileFragment();
            mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, profileFragment).commit();
            openedFragment = profileFragment;
        } else {
            openLoginFragment();
        }
    }

    public void openAlertFragment(){
        if(Account.isLoggedIn()){
            closeOpenedFragment();

            AlertsMenu alertFragment = new AlertsMenu();

            Bundle args = new Bundle();
            // Real location
            args.putParcelable("real_location_param", mainActivity.getCurrentRealLocation());
            alertFragment.setArguments(args);

            // Map location
            GeoPoint location = new GeoPoint(mainActivity.getMap().getMapCenter().getLatitude(), mainActivity.getMap().getMapCenter().getLongitude());
            args.putParcelable("map_location_param", location);
            alertFragment.setArguments(args);

            mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, (Fragment) alertFragment).commit();
            openedFragment = alertFragment;
        } else {
            openLoginFragment();
        }
    }

    public void openLoginFragment(){
        closeOpenedFragment();
        LoginFragment loginFragment = new LoginFragment();
        mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.fragment, loginFragment).commit();
        openedFragment = loginFragment;
    }

    public void openSearchFragment(ArrayList<WeatherCondition> weatherSelected, String searchText){
        closeOpenedFragment();
        SearchFragment searchFragment = SearchFragment.newInstance(weatherSelected, searchText);
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, searchFragment).commit();
        openedFragment = searchFragment;
    }

    public void closeOpenedFragment() {
        if (openedFragment != null) {
            mainActivity.getSupportFragmentManager().beginTransaction().remove(openedFragment).commit();
            openedFragment = null;
        }
    }

    public Fragment getOpenedFragment() {
        return openedFragment;
    }
}
