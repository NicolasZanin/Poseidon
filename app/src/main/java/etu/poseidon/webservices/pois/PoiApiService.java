package etu.poseidon.webservices.pois;

import java.util.List;

import etu.poseidon.models.Poi;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PoiApiService {

    @GET("pois")
    Call<List<Poi>> getPoiList();

    @GET("pois/history/{email}")
    Call<List<Poi>> getHistoryForUser(@Path("email") String email);

    @POST("pois")
    Call<Poi> createPoi(@Body Poi poi);

    @PUT("pois/{id}")
    Call<Poi> updatePoi(@Path("id") String poiId, @Body Poi poi);

    @DELETE("pois/{id}")
    Call<Void> deletePoi(@Path("id") String poiId);
}
