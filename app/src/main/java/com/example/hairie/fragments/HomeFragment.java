package com.example.hairie.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.hairie.MainActivity;
import com.example.hairie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final String SHARED_PREFS_KEY = "CapturedImages";
    private static final String IMAGES_KEY = "imagePaths";

    private CardView logoutCardView;
    private CardView unlockLookCard;
    private Uri imageUri;
    String currentPhotoPath;
    private ImageView imageView; // ImageView to display the detected faces

    // Permission request launcher
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(getActivity(), "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    // Camera result launcher
    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        processImageForFaceDetection(imageUri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the CardView for logout
        logoutCardView = view.findViewById(R.id.logout_cardview);
        // Initialize the CardView for unlocking look
        unlockLookCard = view.findViewById(R.id.unlock_look_card);
        // Initialize the ImageView for displaying the captured image
        imageView = view.findViewById(R.id.image_view); // Ensure this ID matches your layout XML

        // Set onClickListener for the logout card
        logoutCardView.setOnClickListener(v -> logOutUser());

        // Set onClickListener for unlock look card
        unlockLookCard.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else {
                // If permission is already granted, open the camera
                openCamera();
            }
        });

        return view;
    }

    // Log out the user from Firebase and redirect to login screen
    private void logOutUser() {
        // Sign out the user from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Show a toast message
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to MainActivity (Login screen)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the back stack
        startActivity(intent);
        getActivity().finish(); // Close the current activity
    }

    // Open the camera using ActivityResultLauncher
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        getActivity().getPackageName() + ".fileprovider", photoFile); // Ensure authority matches
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                imageUri = photoURI;
                cameraLauncher.launch(cameraIntent);
            }
        }
    }

    // Create a file to store the captured image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Process the image and detect faces
    private void processImageForFaceDetection(Uri imageUri) {
        try {
            InputImage image = InputImage.fromFilePath(getContext(), imageUri);

            // Configure face detector options
            FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();

            FaceDetector detector = FaceDetection.getClient(options);

            detector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (!faces.isEmpty()) {
                            handleDetectedFaces(faces);
                        } else {
                            Toast.makeText(getContext(), "No faces detected", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Face detection failed", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the detected faces
    private void handleDetectedFaces(List<Face> faces) {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            canvas.drawRect(bounds, paint);
        }

        imageView.setImageBitmap(mutableBitmap);
        Toast.makeText(getContext(), "Face(s) detected!", Toast.LENGTH_SHORT).show();

        // Ensure the image path is not null or empty

        // Save the image path to shared preferences
        saveImagePath(currentPhotoPath);

        // Navigate to the GalleryFragment
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", currentPhotoPath);
        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, galleryFragment) // Replace with your container ID
                .addToBackStack(null)
                .commit();

    }

    // Save the captured image path to SharedPreferences
    private void saveImagePath(String imagePath) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);

        // Retrieve the image paths set or create a new HashSet if null
        Set<String> imagePaths = sharedPreferences.getStringSet(IMAGES_KEY, new HashSet<>());

        // Make a copy of the set to avoid modifying the original directly
        Set<String> updatedImagePaths = new HashSet<>(imagePaths);
        updatedImagePaths.add(imagePath);

        // Save the updated set back to SharedPreferences
        sharedPreferences.edit().putStringSet(IMAGES_KEY, updatedImagePaths).apply();
    }

}

