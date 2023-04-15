package etu.poseidon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import etu.poseidon.models.weather.WeatherCondition;

public class FormPoiFragment extends Fragment {

    public FormPoiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_poi, container, false);

        LinearLayout parentLayout = view.findViewById(R.id.conditions);
        LinearLayout currentLayout = new LinearLayout(getContext());
        currentLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        currentLayout.setOrientation(LinearLayout.HORIZONTAL);
        parentLayout.addView(currentLayout);

        // Obtenez la largeur maximale du parent LinearLayout
        int parentWidth = parentLayout.getWidth();

        for (WeatherCondition condition : WeatherCondition.values()) {
            Button button = new Button(getContext());

            // Définir l'icône du bouton
            //int iconResId = getResources().getIdentifier(condition.name(), "drawable", getContext().getPackageName());
            //button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);

            // Définir le texte du bouton sur le nom de l'enum
            button.setText(condition.name());

            // Ajoutez le bouton au LinearLayout
            currentLayout.addView(button);

            // Définir les paramètres de mise en page pour le bouton
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            // Obtenez la largeur actuelle du LinearLayout
            int currentWidth = currentLayout.getWidth();

            // Si la largeur actuelle dépasse la largeur maximale du parent LinearLayout,
            // commencez une nouvelle ligne
            if (currentWidth > parentWidth) {
                LinearLayout newLine = new LinearLayout(getContext());
                newLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                newLine.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.addView(newLine);
                currentLayout = newLine;
            }
        }

        // Inflate the layout for this fragment
        return view;
    }
}