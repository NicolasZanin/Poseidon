package etu.poseidon.webservices.alerts;

import java.util.List;

import etu.poseidon.models.Alert;
import etu.poseidon.models.Poi;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AlertApiService {

    @GET("alerts/{email}")
    Call<List<Alert>> getAlertsForUser(@Path("email") String email);

    @POST("alerts")
    Call<Alert> createAlert(@Body Alert alert);

    @PUT("alerts/{id}")
    Call<Alert> updateAlert(@Path("id") String alertId, @Body Alert alert);

    @DELETE("alerts/{id}")
    Call<Void> deleteAlert(@Path("id") String alertId);
}
