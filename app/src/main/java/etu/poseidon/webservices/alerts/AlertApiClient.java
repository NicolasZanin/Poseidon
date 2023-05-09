package etu.poseidon.webservices.alerts;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import etu.poseidon.models.Alert;
import etu.poseidon.models.weather.WeatherCondition;
import etu.poseidon.models.weather.WeatherConditionAdapter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlertApiClient {

    private static final String BASE_URL = "http://api.poseidon.alexismalosse.fr/";
    private static AlertApiClient instance;
    private AlertApiService alertApiService;

    private AlertApiClient() {
        // Temporaire pour afficher le JSON brut dans le logcat
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                String rawJson = response.body().string();
                //Log.d("Raw JSON Alert", rawJson); // afficher le JSON brut dans le logcat
                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), rawJson))
                        .build();
            }
        });
        OkHttpClient okHttpClient = builder.build();
        // Fin temporaire

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient) // temporaire
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .registerTypeAdapter(WeatherCondition.class, new WeatherConditionAdapter())
                        .create()))
                .build();
        alertApiService = retrofit.create(AlertApiService.class);
    }

    public static synchronized AlertApiClient getInstance() {
        if (instance == null) {
            instance = new AlertApiClient();
        }
        return instance;
    }

    public void getAlertsForUser(String email, Callback<List<Alert>> callback) {
        Call<List<Alert>> call = alertApiService.getAlertsForUser(email);
        call.enqueue(callback);
    }

    public void createAlert(Alert alert, Callback<Alert> callback) {
        Call<Alert> call = alertApiService.createAlert(alert);
        call.enqueue(callback);
    }

    public void updateAlert(String alertId, Alert alert, Callback<Alert> callback) {
        Call<Alert> call = alertApiService.updateAlert(alertId, alert);
        call.enqueue(callback);
    }

    public void deleteAlert(String alertId, Callback<Void> callback) {
        Call<Void> call = alertApiService.deleteAlert(alertId);
        call.enqueue(callback);
    }
}
