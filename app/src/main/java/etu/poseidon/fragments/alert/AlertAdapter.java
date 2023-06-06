package etu.poseidon.fragments.alert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import etu.poseidon.R;
import etu.poseidon.models.Alert;

public class AlertAdapter extends BaseAdapter {

    private List<Alert> items;
    private LayoutInflater mInflater;

    public AlertAdapter(Context applicationContext, List<Alert> items) {
        this.items = items;
        mInflater = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(items.get(i).getId());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Alert item = items.get(i);
        View layoutItem = view == null ? mInflater.inflate (R.layout.alert_item, viewGroup, false) : view;
        TextView alertNameTextView = layoutItem.findViewById(R.id.title);
        Switch alertEnabledSwitch = layoutItem.findViewById(R.id.enabling_alert);
        alertNameTextView.setText(item.getName());
        alertEnabledSwitch.setChecked(item.getEnabled());// TODO: set the switch to the correct value
        alertEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // TODO: update database
                });
        layoutItem.setOnClickListener(v -> {
            EditAlert detailsFragment = new EditAlert(item);
            FragmentManager fragmentManager = ((AppCompatActivity) viewGroup.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return layoutItem;
    }
}
