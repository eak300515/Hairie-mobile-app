package com.example.hairie;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.hairie.fragments.GalleryFragment;
import com.example.hairie.fragments.HomeFragment;
import com.example.hairie.fragments.ProfileFragment;
import com.example.hairie.fragments.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Load the default fragment (HomeFragment)
        loadFragment(new HomeFragment());

        // Handle window insets for system bars (if necessary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        // Switch between different fragments based on the menu item selected
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            fragment = new HomeFragment();
        } else if (itemId == R.id.menu_trend) {
            fragment = new TrendingFragment();
        } else if (itemId == R.id.menu_gallery) {
            fragment = new GalleryFragment();
        } else if (itemId == R.id.menu_profile) {
            fragment = new ProfileFragment();
        }

        // Load the selected fragment
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        // Replace the current fragment with the selected fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
