package com.kwonyijun.deliveryapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMapSdk;

public class MapActivity extends AppCompatActivity {
    Boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // INITIALIZE NAVER MAP SDK
        // https://console.ncloud.com/naver-service/application
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("ocfg9oem75"));

        LinearLayout hiddenTips = findViewById(R.id.hidden_tips_LinearLayout);
        LinearLayout bottomLayout = findViewById(R.id.bottom_LinearLayout);
        MaterialButton mapsButton = findViewById(R.id.current_location_MaterialButton);
        EditText searchEditText = findViewById(R.id.search_EditText);
        ImageButton closeButton = findViewById(R.id.close_ImageButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSearching == false) {
                    onBackPressed();
                } else {
                    searchEditText.clearFocus();
                    hiddenTips.setVisibility(View.GONE);
                    mapsButton.setVisibility(View.VISIBLE);
                }
            }
        });

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus == true) {
                    hiddenTips.setVisibility(View.VISIBLE);
                    mapsButton.setVisibility(View.GONE);
                    closeButton.setImageResource(R.drawable.ic_arrow_back);
                    isSearching = true;
                } else {
                    closeButton.setImageResource(R.drawable.ic_close);
                    isSearching = false;
                }
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ACCESS NAVER MAP
                startActivity(new Intent(MapActivity.this, NaverMapActivity.class));
            }
        });
    }
}