package etu.poseidon.fragments.search;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import etu.poseidon.activities.main.MainActivity;
import etu.poseidon.R;
import etu.poseidon.fragments.weathercondition.components.WeatherConditionListSelectorFragment;
import etu.poseidon.models.weather.WeatherCondition;

/**
 * Fragment de la barre de recherche
 */
public class SearchFragment extends Fragment implements WeatherConditionListSelectorFragment.OnWeatherConditionSelectedListener{
    // Méthode vide
    public SearchFragment() {}

    // Communiquer l'interface avec l'activity Main
    public interface OnSearchFragmentListener {
        void relocateSearch(GeoPoint geoPoint);
        void filterMap(ArrayList<WeatherCondition> weatherConditionList, String searchText);
    }

    private OnSearchFragmentListener searchFragmentListener;
    private View rootView;
    private ArrayList<WeatherCondition> weatherSelected;
    private CharSequence searchText = "";

    // Créer une nouvelle instance du fragment
    public static SearchFragment newInstance(ArrayList<WeatherCondition> weatherSelected, CharSequence searchText) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(MainActivity.TAG_SEARCH_FRAGMENT, weatherSelected);
        args.putCharSequence(MainActivity.TAG_SEARCH_FRAGMENT + "2", searchText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        // Récupère les arguments
        if (getArguments() != null) {
            weatherSelected = getArguments().getParcelableArrayList(MainActivity.TAG_SEARCH_FRAGMENT);
            searchText = getArguments().getCharSequence(MainActivity.TAG_SEARCH_FRAGMENT + "2");
        }
        Log.d("TEST", weatherSelected.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        rootView = inflater.inflate(R.layout.fragment_recherche, container, false);

        // Avoid user miscliking on the activity holding when fragment open
        rootView.findViewById(R.id.recherche_fragment_layout).setOnClickListener(v -> {});

        // Initie le fragment pour sélectionner les filtres
        WeatherConditionListSelectorFragment weatherConditionListSelectorFragment = WeatherConditionListSelectorFragment.newInstance(true, weatherSelected);
        weatherConditionListSelectorFragment.setOnWeatherConditionListSelectedListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.selectClimat, weatherConditionListSelectorFragment).commit();

        // Récupérer l'edittext du Fragment
        EditText editText = rootView.findViewById(R.id.edit_text_Search_Fragment);

        // Ajoute l'ancien historique du texte
        if (!searchText.equals(""))
            editText.setText(searchText);
        editText.requestFocus();
        // Force l'affichage du clavier et l'écriture sur la barre de recherche
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

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
                        searchFragmentListener.filterMap(weatherSelected, text);
                        searchFragmentListener.relocateSearch(newGeoPoint);
                        closeFragment();
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

    // Récupère la liste des filtres
    @Override
    public void onWeatherConditionSelected(List<WeatherCondition> conditions) {
        weatherSelected = new ArrayList<>(conditions);
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
        EditText editText = rootView.findViewById(R.id.edit_text_Search_Fragment);
        editText.requestFocus();

        // Ferme le clavier
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.RESULT_UNCHANGED_SHOWN);

        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
