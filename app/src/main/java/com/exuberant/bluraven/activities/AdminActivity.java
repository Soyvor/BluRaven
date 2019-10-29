package com.exuberant.bluraven.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.exuberant.bluraven.R;
import com.exuberant.bluraven.fragments.MapFragment;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, mapFragment).commit();

    }
}
