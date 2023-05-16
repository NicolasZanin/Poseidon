package etu.poseidon.temp;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import etu.poseidon.models.Alert;
import etu.poseidon.models.weather.WeatherCondition;
import etu.poseidon.webservices.alerts.AlertApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TempAlertExample {
    // DELETE THIS CLASS WHEN DONE

    private static Alert alert1, alert2, alert3;

    private static void createThreeAlerts(){
        alert1 = new Alert();
        alert1.setName("Alert 1");
        alert1.setDescription("Description 1");
        alert1.setLatitude(35.382912);
        alert1.setLongitude(-120.343);
        alert1.setListWeatherCondition(Arrays.asList(WeatherCondition.SUN, WeatherCondition.CLOUD));
        // Pour les données de l'utilisateur, les prendre depuis google :
        // GoogleSignInAccount loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());
        // loggedInAccount.getEmail();
        // loggedInAccount.getDisplayName();
        alert1.setCreatorEmail("malosse10@gmail.com");
        alert1.setCreatorFullname("Alexis Malosse");

        alert2 = new Alert();
        alert2.setName("Alert 2");
        alert2.setDescription("Description 2");
        alert2.setLatitude(135.382912);
        alert2.setLongitude(-20.343);
        alert2.setListWeatherCondition(Arrays.asList(WeatherCondition.CLOUD));
        // Pour les données de l'utilisateur, les prendre depuis google :
        // GoogleSignInAccount loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());
        // loggedInAccount.getEmail();
        // loggedInAccount.getDisplayName();
        alert2.setCreatorEmail("malosse10@gmail.com");
        alert2.setCreatorFullname("Alexis Malosse");

        alert3 = new Alert();
        alert3.setName("Alert 3");
        alert3.setDescription("Description 3");
        alert3.setLatitude(5.382912);
        alert3.setLongitude(-2.343);
        alert3.setListWeatherCondition(Arrays.asList(WeatherCondition.SUN, WeatherCondition.CLOUD, WeatherCondition.THUNDERSTORM, WeatherCondition.WIND, WeatherCondition.STORM, WeatherCondition.RAIN));
        // Pour les données de l'utilisateur, les prendre depuis google :
        // GoogleSignInAccount loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());
        // loggedInAccount.getEmail();
        // loggedInAccount.getDisplayName();
        alert3.setCreatorEmail("test@gmail.com");
        alert3.setCreatorFullname("Test Test");

        AlertApiClient.getInstance().createAlert(alert1, new Callback<Alert>() {
            @Override
            public void onResponse(Call<Alert> call, Response<Alert> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alert 1 created");
                } else {
                    Log.e("Alert", "Alert 1 not created");
                }
                createSuite();
            }

            @Override
            public void onFailure(Call<Alert> call, Throwable t) {
                Log.e("Alert", "Alert 1 not created ERROR");
            }
        });
    }
    private static void createSuite(){
        AlertApiClient.getInstance().createAlert(alert2, new Callback<Alert>() {
            @Override
            public void onResponse(Call<Alert> call, Response<Alert> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alert 2 created");
                } else {
                    Log.e("Alert", "Alert 2 not created");
                }
                createSuite2();
            }

            @Override
            public void onFailure(Call<Alert> call, Throwable t) {
                Log.e("Alert", "Alert 2 not created ERROR");
            }
        });
    }
    private static void createSuite2(){
        AlertApiClient.getInstance().createAlert(alert3, new Callback<Alert>() {
            @Override
            public void onResponse(Call<Alert> call, Response<Alert> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alert 3 created");
                } else {
                    Log.e("Alert", "Alert 3 not created");
                }
                getAlertsForUser();
            }

            @Override
            public void onFailure(Call<Alert> call, Throwable t) {
                Log.e("Alert", "Alert 3 not created ERROR");
            }
        });
    }









    private static void getAlertsForUser(){
        AlertApiClient.getInstance().getAlertsForUser("malosse10@gmail.com", new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alerts for user malosse10@gmail.com retrieved");
                    for (Alert alert : response.body()) {
                        // -----
                        // Not useful, just to set id to after delete it for example
                        updateAlerts(alert);
                        // ------
                        Log.d("Alert", alert.toString());
                    }
                } else {
                    Log.e("Alert", "Alerts for user not retrieved");
                }
                getAlertsForUser2();
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.e("Alert", "Alerts for user not retrieved ERROR");
            }
        });
    }
    private static void getAlertsForUser2(){
        AlertApiClient.getInstance().getAlertsForUser("test@gmail.com", new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alerts for user test@gmail.com retrieved");
                    for (Alert alert : response.body()) {
                        // -----
                        // Not useful, just to set id to after delete it for example
                        updateAlerts(alert);
                        // ------
                        Log.d("Alert", alert.toString());
                    }
                } else {
                    Log.e("Alert", "Alerts for user not retrieved");
                }
                updateOneAlert();
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.e("Alert", "Alerts for user not retrieved ERROR");
            }
        });
    }









    private static void updateOneAlert(){
        alert2.setName("Alert 2 UPDATED");
        alert2.setDescription("Description 2 UPDATED");
        AlertApiClient.getInstance().updateAlert(alert2.getId(), alert2, new Callback<Alert>() {
            @Override
            public void onResponse(Call<Alert> call, Response<Alert> response) {
                if (response.isSuccessful()) {
                    Alert result = response.body();
                    Log.d("Alert", "Alert 2 updated " + result);
                } else {
                    Log.e("Alert", "Alert 2 not updated");
                }
                deleteFiveAlert();
            }

            @Override
            public void onFailure(Call<Alert> call, Throwable t) {
                Log.e("Alert", "Alert 2 not updated ERROR");
            }
        });
    }








    private static void deleteFiveAlert(){
        AlertApiClient.getInstance().deleteAlert(alert1.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("Alert", "Alert 1 deleted");
                } else {
                    Log.e("Alert", "Alert 1 not deleted");
                }
                deleteSuite();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Alert", "Alert 1 not deleted ERROR");
            }
        });
    }
    private static void deleteSuite(){
        AlertApiClient.getInstance().deleteAlert(alert2.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("Alert", "Alert 2 deleted");
                } else {
                    Log.e("Alert", "Alert 2 not deleted");
                }
                deleteSuite2();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Alert", "Alert 2 not deleted ERROR");
            }
        });
    }

    private static void deleteSuite2(){
        AlertApiClient.getInstance().deleteAlert(alert3.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("Alert", "Alert 3 deleted");
                } else {
                    Log.e("Alert", "Alert 3 not deleted");
                }
                deleteSuite3();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Alert", "Alert 3 not deleted ERROR");
            }
        });
    }
    private static void deleteSuite3(){
// There is no alerts now (for malosse10)
        AlertApiClient.getInstance().getAlertsForUser("malosse10@gmail.com", new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alerts for user retrieved");
                    for (Alert alert : response.body()) {
                        Log.d("Alert", alert.toString());
                    }
                } else {
                    Log.e("Alert", "Alerts for user not retrieved");
                }
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.e("Alert", "Alerts for user not retrieved ERROR");
            }
        });

        // For test@gmail.com
        AlertApiClient.getInstance().getAlertsForUser("test@gmail.com", new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alerts for user retrieved");
                    for (Alert alert : response.body()) {
                        Log.d("Alert", alert.toString());
                    }
                } else {
                    Log.e("Alert", "Alerts for user not retrieved");
                }
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.e("Alert", "Alerts for user not retrieved ERROR");
            }
        });
    }

















    // Not useful for API calls

    private static void updateAlerts(Alert alert){
        if(alert.getName().equals("Alert 1")){
            alert1.setId(alert.getId());
            return;
        }
        if(alert.getName().equals("Alert 2")){
            alert2.setId(alert.getId());
            return;
        }
        if(alert.getName().equals("Alert 3")){
            alert3.setId(alert.getId());
        }
    }

    public static void run(){
        createThreeAlerts();
    }
}
