package etu.poseidon;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;

public class SearchFragment extends Fragment {
    // Méthode vide
    public SearchFragment() {}

    // Communiquer l'interface avec l'activity Main
    public interface OnSearchFragmentListener {
        void relocateSearch(GeoPoint geoPoint);
    }

    private OnSearchFragmentListener searchFragmentListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        View rootView = inflater.inflate(R.layout.fragment_recherche, container, false);
        // Récupérer l'edit text du Fragment
        EditText editText = rootView.findViewById(R.id.edit_text_Search_Fragment);

        // En cas de click sur la croix
        rootView.findViewById(R.id.close_button_Search).setOnClickListener(click -> {
            // Ferme le Fragment
            closeFragment();
        });

        // En cas de click sur le bouton de recherche
        rootView.findViewById(R.id.button_search_Fragment).setOnClickListener(click -> {
            // Récupère le texte de l'edit texte
            String text = editText.getText().toString();

            // Si le texte n'est pas vide
            if (text.length() != 0) {
                String[] coordonnees = text.split(" ");

                // Récupère les données de longitude et latitude
                if (coordonnees.length >= 2) {
                    try {
                        double longitude = Double.parseDouble(coordonnees[0]);
                        double latitude = Double.parseDouble(coordonnees[1]);

                        // Crée un Géopoint sur la position à chercher
                        GeoPoint newGeoPoint = new GeoPoint(longitude, latitude);
                        searchFragmentListener.relocateSearch(newGeoPoint);
                    }
                    // Crée une alerte en cas de mauvaise valeur entrée
                    catch (NumberFormatException nFE) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setTitle("Mauvaise valeur");
                        alertDialogBuilder.setMessage("Vous avez pas mis de valeur");
                        alertDialogBuilder.setNeutralButton("Ok", null);
                        alertDialogBuilder.show();
                    }
                }
            }
        });

        return rootView;
    }

    // Attache le fragment et donne à l'interface le contexte
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentListener) {
            searchFragmentListener = (OnSearchFragmentListener) context;
        }
        else {
            throw new RuntimeException();
        }
    }

    // Détache le fragment et met la valeur de l'interface à null
    @Override
    public void onDetach() {
        super.onDetach();
        searchFragmentListener = null;
    }

    // Ferme le fragment
    private void closeFragment(){
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
