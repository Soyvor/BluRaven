package com.exuberant.ah2019.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.exuberant.ah2019.R;
import com.exuberant.ah2019.fragments.MapFragment;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, mapFragment).commit();

    }
}
