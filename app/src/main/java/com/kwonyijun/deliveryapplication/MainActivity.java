package com.kwonyijun.deliveryapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kwonyijun.deliveryapplication.Home.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INSTANTIATE FIREBASE
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Log.i("Haerin1", currentUser + "");

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_bar);
        navigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            if (itemId == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.navigation_search) {
                fragment = new SearchFragment();
            } else if (itemId == R.id.navigation_orders) {
                fragment = new OrdersFragment();
            } else if (itemId == R.id.navigation_profile) {
                if (currentUser == null) {
                    fragment = new HomeFragment();
                } else {
                    fragment = new ProfileFragment();
                }
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        });

        // Set the default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }
}