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
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PictureFragment extends Fragment{
    private ImageView imageView;

    public PictureFragment() {}
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picture, container, false);
        imageView = rootView.findViewById(R.id.imageView);
        rootView.findViewById(R.id.buttonImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions( getActivity(),
                                                        new String[] {Manifest.permission.CAMERA},
                                                        IPuctureActivity.REQUEST_CAMERA);
                } else { //permission still GRANTED
                    takePicture();
                }
            }
        });

        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions( getActivity(),
                                                new String[] {Manifest.permission.CAMERA},
                                                IPuctureActivity.REQUEST_CAMERA);
        } else { //permission still GRANTED
            takePicture();
        }
        return rootView;

}
    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        getActivity().startActivityForResult(intent, IPuctureActivity.REQUEST_CAMERA);
    }

    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void setPicture(Bitmap picture) {
        imageView.setImageBitmap(picture);
    }
}