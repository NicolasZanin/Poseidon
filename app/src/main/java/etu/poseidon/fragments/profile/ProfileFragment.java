package etu.poseidon.fragments.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

import etu.poseidon.R;
import etu.poseidon.models.Account;
import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private final String TAG = "POSEIDON " + getClass().getSimpleName();
    private TextView numberOfEventsTextView, noEventsTextView;

    private GoogleSignInClient mGoogleSignInClient;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Avoid user miscliking on the activity holding when fragment open
        view.findViewById(R.id.profile_fragment_layout).setOnClickListener(v -> {});

        Button logoutButton = view.findViewById(R.id.btnDeconnexion);
        ImageView profileImage = view.findViewById(R.id.imgProfil);
        TextView profileNameTextView = view.findViewById(R.id.txtNomProfil);
        numberOfEventsTextView = view.findViewById(R.id.txtNombreEvenements);
        noEventsTextView = view.findViewById(R.id.history_empty);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Photo de profil - image de profil de base depuis les ressources
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.profil, null);
        profileImage.setImageDrawable(drawable);

        profileNameTextView.setText(Account.getDisplayName());
        updateNumberOfEvents(0);
        loadHistory(view.findViewById(R.id.history));

        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeFragment());
        return view;
    }
    private void closeFragment(){
        System.out.println("close fragment profile");
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void signOut() {
        Account.logOut(mGoogleSignInClient);
        Toast.makeText(requireContext(), "Déconnexion réussie, à bientôt !", Toast.LENGTH_SHORT).show();
        closeFragment();
    }

    private void loadHistory(ListView container) {
        PoiApiClient.getInstance().getHistoryForUser(Account.getEmail(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Poi>> call, @NonNull Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    List<Poi> poiList = response.body();
                    // The player can have leave the fragment before we get the response, so we need to check if the context is still available and if not return
                    if (getContext() == null) return;
                    ProfileHistoryAdapter adapter = new ProfileHistoryAdapter(getContext(), poiList);
                    container.setAdapter(adapter);

                    assert poiList != null;
                    updateNumberOfEvents(poiList.size());
                    toggleHistory(poiList.size());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Poi>> call, @NonNull Throwable t) {
                Log.e(TAG, "Error: " + t.getMessage());
            }
        });
    }

    private void updateNumberOfEvents(int nbEvents) {
        int stringNbEvenements;
        if(nbEvents > 1)
            stringNbEvenements = getResources().getIdentifier("fragment_profile_nb_events_notifies_plural", "string", getContext().getPackageName());
        else
            stringNbEvenements = getResources().getIdentifier("fragment_profile_nb_events_notifies", "string", getContext().getPackageName());
        numberOfEventsTextView.setText(getString(stringNbEvenements, nbEvents));
    }

    private void toggleHistory(int numberOfItems){
        if(numberOfItems == 0){
            noEventsTextView.setVisibility(View.VISIBLE);
        } else {
            noEventsTextView.setVisibility(View.GONE);
        }
    }
}


