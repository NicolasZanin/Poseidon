package etu.poseidon.fragments.weathercondition.updater;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Optional;

import etu.poseidon.activities.main.tools.MainActivityPermissions;
import etu.poseidon.models.Poi;

public class WeatherConditionUpdaterController implements IWeatherConditionUpdaterController {
    private final IWeatherConditionUpdaterModel model;
    private final IWeatherConditionUpdaterView view;
    private final WeatherConditionUpdaterFragment fragment;

    public WeatherConditionUpdaterController(IWeatherConditionUpdaterModel model, IWeatherConditionUpdaterView view, WeatherConditionUpdaterFragment fragment) {
        this.model = model;
        this.view = view;
        this.fragment = fragment;
    }

    @Override
    public void userActionUpdatePerimeter(int perimeter) {
        model.updatePerimeter(perimeter);
    }

    @Override
    public void modelDeleted(Optional<Poi> poi) {
        poi.ifPresent(value -> fragment.getActivityListener().onWeatherConditionFinished(value.getLatitude(), value.getLongitude()));
        closeFragment();
    }

    @Override
    public void userActionDeletePoi() {
        if(model.isDeletable()) {
            model.deletePoiDefinilety(fragment.getContext());
        } else {
            model.setPoiFinished(fragment.getContext());
        }
    }

    @Override
    public void userActionCloseFragment() {
        closeFragment();
    }

    @Override
    public void userActionOpeningFragment() {
        // When opening the fragment, we need to setup the image, so we load it
        loadPictureFromStorage();
    }

    private void closeFragment(){
        fragment.requireActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void loadPictureFromStorage(){
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) fragment.requireContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivityPermissions.REQUEST_MEDIA_READ);
        } else {
            try {
                String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
                String[] selectionArgs = new String[]{model.getId() + ".jpg"};
                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                Cursor cursor = fragment.requireContext().getContentResolver().query(queryUri, null, selection, selectionArgs, null);

                // If we found the picture, we load it
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null) {
                        view.setPicture(bitmap);
                    } else {
                        Log.e("loadPictureFromStorage", "Failed to decode bitmap");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
