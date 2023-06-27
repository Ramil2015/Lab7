package com.example.lab7;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private FrameLayout frameLayout;
    private CharacterAdapter characterAdapter;
    private List<String> characterNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        // Execute the AsyncTask to fetch character data
        new FetchCharacterDataTask().execute();

        // Set an OnItemClickListener to the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected item's information
                String item = characterNames.get(position);

                // Determine the device layout
                frameLayout = findViewById(R.id.frameLayout);
                if (frameLayout == null) {
                    // Phone layout
                    // Create a bundle with the relevant information
                    Bundle bundle = new Bundle();
                    bundle.putString("item", item);

                    // Start the EmptyActivity and pass the bundle as an extra
                    Intent intent = new Intent(MainActivity.this, EmptyActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    // Tablet layout
                    // Create the DetailsFragment
                    DetailsFragment detailsFragment = new DetailsFragment();

                    // Create a bundle with the relevant information
                    Bundle bundle = new Bundle();
                    bundle.putString("item", item);

                    // Set the bundle as arguments for the fragment
                    detailsFragment.setArguments(bundle);

                    // Replace the FrameLayout with the DetailsFragment
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout, detailsFragment);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    private class FetchCharacterDataTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            characterNames = new ArrayList<>();

            try {
                // Query the Star Wars API for character data
                URL url = new URL("https://swapi.dev/api/people/?format=json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse the JSON response to retrieve character names
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray results = jsonResponse.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject character = results.getJSONObject(i);
                        String name = character.getString("name");
                        characterNames.add(name);
                    }
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return characterNames;
        }

        @Override
        protected void onPostExecute(List<String> characterNames) {
            // Populate the ListView with character names
            characterAdapter = new CharacterAdapter(MainActivity.this, characterNames);
            listView.setAdapter(characterAdapter);
        }
    }
}
