package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        // Create the DetailsFragment
        DetailsFragment detailsFragment = new DetailsFragment();

        // Get the bundle with relevant information
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            detailsFragment.setArguments(bundle);
        }

        // Replace the FrameLayout with the DetailsFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, detailsFragment)
                .commit();
    }
}
