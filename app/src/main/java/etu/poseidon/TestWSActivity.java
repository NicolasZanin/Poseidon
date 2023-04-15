package etu.poseidon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Button;

import java.util.List;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestWSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wsactivity);

        Button loadAllPoisBtn = findViewById(R.id.loadAll);
        loadAllPoisBtn.setOnClickListener(v -> {
            PoiApiClient.getInstance().getPoiList(new Callback<List<Poi>>() {
                @Override
                public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                    if (response.isSuccessful()) {
                        System.out.println("Success : " + response.body());
                        List<Poi> poiList = response.body();
                        for (Poi poi : poiList) {
                            System.out.println(poi);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Poi>> call, Throwable t) {
                    System.out.println("Error: " + t.getMessage());
                }
            });
        });

        Button openForm = findViewById(R.id.openForm);
        openForm.setOnClickListener(v -> {
            openFormFragment();
        });
    }

    private void openFormFragment() {
        FormPoiFragment formPoiFragment = new FormPoiFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentFormPoi, (Fragment) formPoiFragment) .commit();
    }
}