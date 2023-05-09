package etu.poseidon;

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

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import etu.poseidon.models.Poi;
import etu.poseidon.webservices.pois.PoiApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private final String TAG = "POSEIDON " + getClass().getSimpleName();
    private TextView numberOfEventsTextView, noEventsTextView;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount loggedInAccount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this.requireContext(), gso);
        loggedInAccount = GoogleSignIn.getLastSignedInAccount(this.requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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

        // Photo de profil - Récupération depuis Google ou récupération de l'image de profil de base depuis les ressources si inexistante chez Google
        if(loggedInAccount.getPhotoUrl() == null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.profil, null);
            profileImage.setImageDrawable(drawable);
        } else {
            Picasso.get().load(loggedInAccount.getPhotoUrl()).into(profileImage);
        }

        profileNameTextView.setText(loggedInAccount.getDisplayName());
        updateNumberOfEvents(0);
        loadHistory(view.findViewById(R.id.history));

        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeFragment());
        return view;
    }
    private void closeFragment(){
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(requireContext(), "Déconnexion réussie, à bientôt !", Toast.LENGTH_SHORT).show();
                    closeFragment();
                }
            });
    }

    private void loadHistory(ListView container) {
        PoiApiClient.getInstance().getHistoryForUser(loggedInAccount.getEmail(), new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    List<Poi> poiList = response.body();
                    ProfileHistoryAdapter adapter = new ProfileHistoryAdapter(getContext(), poiList);
                    container.setAdapter(adapter);

                    updateNumberOfEvents(poiList.size());
                    toggleHistory(poiList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                Log.e(TAG,"Error: " + t.getMessage());
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


