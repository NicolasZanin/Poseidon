package etu.poseidon.webservices.pois;

import android.util.Log;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import etu.poseidon.models.Poi;
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

public class PoiApiClient {

    private static final String BASE_URL = "http://api.poseidon.alexismalosse.fr/";
    private static PoiApiClient instance;
    private PoiApiService poiApiService;

    private PoiApiClient() {
        // Temporaire pour afficher le JSON brut dans le logcat
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                String rawJson = response.body().string();
                Log.d("Raw JSON", rawJson); // afficher le JSON brut dans le logcat
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
        poiApiService = retrofit.create(PoiApiService.class);
    }

    public static synchronized PoiApiClient getInstance() {
        if (instance == null) {
            instance = new PoiApiClient();
        }
        return instance;
    }

    public void getPoiList(Callback<List<Poi>> callback) {
        Call<List<Poi>> call = poiApiService.getPoiList();
        call.enqueue(callback);
    }

    public void getHistoryForUser(String email, Callback<List<Poi>> callback) {
        Call<List<Poi>> call = poiApiService.getHistoryForUser(email);
        call.enqueue(callback);
    }

    public void createPoi(Poi poi, Callback<Poi> callback) {
        Call<Poi> call = poiApiService.createPoi(poi);
        call.enqueue(callback);
    }

    public void updatePoi(String poiId, Poi poi, Callback<Poi> callback) {
        Call<Poi> call = poiApiService.updatePoi(poiId, poi);
        call.enqueue(callback);
    }

    public void deletePoi(String poiId, Callback<Void> callback) {
        Call<Void> call = poiApiService.deletePoi(poiId);
        call.enqueue(callback);
    }
}
