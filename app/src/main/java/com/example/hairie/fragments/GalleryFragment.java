package com.example.hairie.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hairie.R;

public class GalleryFragment extends Fragment {

    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_item, container, false);

        imageView = view.findViewById(R.id.image_view);

        // Check if the fragment received arguments
        String currentPhotoPath = null;
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString("imagePath");
        } else if (getArguments() != null) {
            currentPhotoPath = getArguments().getString("imagePath");
        }

        if (currentPhotoPath != null) {
            displayCapturedImage(currentPhotoPath);
        } else {
            Toast.makeText(getContext(), "No image path found", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the currentPhotoPath if available
        if (getArguments() != null) {
            outState.putString("imagePath", getArguments().getString("imagePath"));
        }
    }

    // Method to display the captured image in the ImageView
    private void displayCapturedImage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);
    }
}
