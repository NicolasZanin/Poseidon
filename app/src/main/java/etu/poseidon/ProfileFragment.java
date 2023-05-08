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

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private Button btnDeconnexion;
    private ImageView imgProfil;
    private TextView txtNomProfil, txtNombreEvenements;
    private ListView listHistorique;

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
                // TODO : Déconnexion de l'utilisateur
                Toast.makeText(getActivity(), "Déconnexion", Toast.LENGTH_SHORT).show();
            }
        });

        // Récupération de l'image de profil depuis les ressources
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.profil, null);
        imgProfil.setImageDrawable(drawable);

        // Affichage du nom de profil
        txtNomProfil.setText("Julian ZAZIN");

        // Affichage du nombre d'événements notifiés
        int nbEvenements = 10;
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
}


