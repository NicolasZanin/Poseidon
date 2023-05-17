package etu.poseidon.fragments.alert;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

import etu.poseidon.R;
import etu.poseidon.models.Alert;
import etu.poseidon.webservices.alerts.AlertApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlertsMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertsMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String TAG = "POSEIDON " + getClass().getSimpleName();

    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount loggedInAccount;
    public AlertsMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlertsMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static AlertsMenu newInstance(String param1, String param2) {
        AlertsMenu fragment = new AlertsMenu();
        Bundle args = new Bundle();
        return fragment;
    }


    private void loadAlerts(ListView container){
        AlertApiClient.getInstance().getAlertsForUser(loggedInAccount.getEmail(), new Callback<List<Alert>>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                Log.e(TAG,"email: " + loggedInAccount.getEmail());
                if (response.isSuccessful()) {
                    Log.d("Alert", "Alerts for "+loggedInAccount.getEmail()+" retrieved");
                    List<Alert> alerts = response.body();
                    container.setAdapter(new AlertAdapter(getContext(), alerts));
                } else {
                    Log.e("Alert", "Alerts for user not retrieved");
                }


            }



            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                Log.e(TAG,"Error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), gso);
        loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(etu.poseidon.R.layout.fragment_alerts_menu, container, false);
        ListView listView = view.findViewById(R.id.alerts_list);
        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            closeFragment();
        });
        loadAlerts(listView);
        return view;
    }

    private void closeFragment(){
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}

