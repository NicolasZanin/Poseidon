package etu.poseidon.fragments.historic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import etu.poseidon.R;

public class HistoricFragment extends Fragment {
    private ListView historyList;
    private View coordinatorLayout;
    private View range;
    private View historic;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historic, container, false);

        // Find views
        historyList = view.findViewById(R.id.history_list);
        coordinatorLayout = view.findViewById(R.id.coordinates_value);
        range = view.findViewById(R.id.range);
        historic = view.findViewById(R.id.history_list);

        TextView closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    private void closeFragment(){
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
