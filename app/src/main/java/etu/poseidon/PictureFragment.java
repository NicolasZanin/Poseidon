package etu.poseidon;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class PictureFragment extends Fragment{
    private ImageView imageView;
    private TextView photoTextIndicator;

    private Button buttonTakeImage;
    private Button buttonRetakeImage;
    private Button buttonRemoveImage;

    public PictureFragment() {}
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);

        imageView = rootView.findViewById(R.id.image_view);
        photoTextIndicator = rootView.findViewById(R.id.photo_text_indicator);

        buttonTakeImage = rootView.findViewById(R.id.button_take_image);
        buttonRetakeImage = rootView.findViewById(R.id.button_retake_image);
        buttonRemoveImage = rootView.findViewById(R.id.button_remove_image);

        buttonTakeImage.setOnClickListener(v -> takePicture());
        buttonRetakeImage.setOnClickListener(v -> takePicture());
        buttonRemoveImage.setOnClickListener(v -> removePicture());

        return rootView;
}
    void takePicture() {
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions( getActivity(),
                    new String[] {Manifest.permission.CAMERA},
                    IPuctureActivity.REQUEST_CAMERA);
        } else { //permission still GRANTED
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent, IPuctureActivity.REQUEST_CAMERA);
        }
    }

    public void setPicture(Bitmap picture) {
        // Modify the text indicator
        int stringPhoto = getResources().getIdentifier("fragment_picture_photo", "string", getContext().getPackageName());
        photoTextIndicator.setText(getString(stringPhoto));

        // Change the buttons
        buttonTakeImage.setVisibility(View.GONE);
        buttonRetakeImage.setVisibility(View.VISIBLE);
        buttonRemoveImage.setVisibility(View.VISIBLE);

        // Display the picture
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageBitmap(picture);
    }

    private void removePicture(){
        // Modify the text indicator
        int stringNoPhoto = getResources().getIdentifier("fragment_picture_photo_text_indicator", "string", getContext().getPackageName());
        photoTextIndicator.setText(getString(stringNoPhoto));

        // Change the buttons
        buttonTakeImage.setVisibility(View.VISIBLE);
        buttonRetakeImage.setVisibility(View.GONE);
        buttonRemoveImage.setVisibility(View.GONE);

        // Remove the picture
        imageView.setVisibility(View.GONE);
        imageView.setImageBitmap(null);
    }
}