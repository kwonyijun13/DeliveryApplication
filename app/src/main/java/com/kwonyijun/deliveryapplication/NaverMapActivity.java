package com.kwonyijun.deliveryapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationSource;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NaverMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource {
    private MapView mapView;
    private NaverMap naverMap;
    private Marker userMarker;
    private FusedLocationProviderClient fusedLocationClient;
    TextView nameTextView,addressTextView;
    Button setButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naver_map);

        ImageButton closeButton = findViewById(R.id.close_ImageButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        // Initialize the FusedLocationProviderClient (RETRIEVE LAST KNOWN LOCATION)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Set the OnMapReadyCallback
        mapView.getMapAsync(this);

        // BOTTOMAPPBAR
        nameTextView = findViewById(R.id.address_title_TextView);
        addressTextView = findViewById(R.id.address_TextView);
        setButton = findViewById(R.id.set_Button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // Enable location tracking
        naverMap.setLocationSource(this);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        // Check and request location permissions
        if (hasLocationPermission()) {
            // Permission is granted, move on to setting up the user marker
            setupUserMarker();
        } else {
            // Request location permission
            requestLocationPermission();
        }

        // Set a click listener on the map to move the marker
        naverMap.setOnMapClickListener((point, coord) -> {
            // Update the marker position when the map is clicked
            moveMarker(coord);
        });
    }

    // PERMISSIONS CHECK
    private boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, move on to setting up the user marker
            setupUserMarker();
        } else {
            // Handle the case when the user denies location permission
        }
    }
    // END OF PERMISSIONS CHECK

    private void moveMarker(LatLng location) {
        // Move the marker to the clicked location
        if (userMarker == null) {
            userMarker = new Marker();
            userMarker.setMap(naverMap);
        }
        userMarker.setPosition(location);
    }

    private void setupUserMarker() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                // Set the user marker at the last known location
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                userMarker = new Marker();
                userMarker.setPosition(userLocation);
                userMarker.setMap(naverMap);
            }
        });
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        // You can use onLocationChangedListener to provide location updates to the NaverMap
    }

    @Override
    public void deactivate() {
        // You can clean up location-related resources here
    }
}