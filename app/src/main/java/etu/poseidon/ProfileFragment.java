package etu.poseidon;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Locale;
import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {

    private Button btnDeconnexion;
    private ImageView imgProfil;
    private TextView txtNomProfil, txtNombreEvenements;
    private ListView listHistorique;

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

    // Méthode appelée lors de la création du fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Récupération des vues du layout
        btnDeconnexion = view.findViewById(R.id.btnDeconnexion);
        imgProfil = view.findViewById(R.id.imgProfil);
        txtNomProfil = view.findViewById(R.id.txtNomProfil);
        txtNombreEvenements = view.findViewById(R.id.txtNombreEvenements);
        listHistorique = view.findViewById(R.id.listHistorique);

        // Ajout du listener sur le bouton de déconnexion
        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Photo de profil - Récupération depuis Google ou récupération de l'image de profil de base depuis les ressources si inexistante chez Google
        if(loggedInAccount.getPhotoUrl() == null) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.profil, null);
            imgProfil.setImageDrawable(drawable);
        } else {
            Picasso.get().load(loggedInAccount.getPhotoUrl()).into(imgProfil);
        }

        // Affichage du nom de profil
        txtNomProfil.setText(loggedInAccount.getDisplayName());

        // Affichage du nombre d'événements notifiés
        int nbEvenements = 0;
        String texteNbEvenements = String.format(Locale.getDefault(), "%d événements notifiés", nbEvenements);
        txtNombreEvenements.setText(texteNbEvenements);

        // TODO : Récupération de l'historique des événements notifiés et affichage dans la liste
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
}


