package etu.poseidon.fragments.alert;

import android.content.Context;
import android.util.Log;
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
import etu.poseidon.webservices.alerts.AlertApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        alertEnabledSwitch.setChecked(item.getEnabled());

        alertEnabledSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isChecked = alertEnabledSwitch.isChecked();
                Alert alert = item;
                alert.setEnabled(isChecked);
                AlertApiClient.getInstance().updateAlert(alert.getId(), alert, new Callback<Alert>() {
                    @Override
                    public void onResponse(Call<Alert> call, Response<Alert> response) {
                        if (response.isSuccessful()) {
                            Log.d("Alert", response.toString());
                        } else {
                            Log.e("Alert", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<Alert> call, Throwable t) {
                        Log.e("Alert", "Alert 1 not updated ERROR");
                    }
                });
            }
        });

        layoutItem.setOnClickListener(v -> {
            // TODO : add location in args and pass it to the fragment
            EditAlert detailsFragment = new EditAlert(item);
            FragmentManager fragmentManager = ((AppCompatActivity) viewGroup.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        TextView deleteAlertTextView = layoutItem.findViewById(R.id.delete_alert);
        deleteAlertTextView.setOnClickListener(v -> {
            // TODO : delete alert from database
            AlertApiClient.getInstance().deleteAlert(item.getId(), new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("Alert", "Alert deleted");
                        items.remove(item);
                        notifyDataSetChanged();
                    } else {
                        Log.e("Alert", "Alert not deleted");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("Alert", "Alert not deleted");
                }
            });
        });

        return layoutItem;
    }
}
