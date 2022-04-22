package com.joyflick;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH = "https://api.rawg.io/api/games?key=8abe1aadb6a6459db418c8ac8239aa05&search=";
    public static final String TAG = "SearchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().hide();

        // Determine if search should include users
        Bundle bundle = getIntent().getExtras();
        boolean searchUsers = true;
        if(bundle != null){
            searchUsers = false;
        }
        Log.i(TAG, "Search users: " + searchUsers);

        // Get the intent, verify the action and get the query
        Intent I = getIntent();
        if(Intent.ACTION_SEARCH.equals(I.getAction())) {
            String query = I.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {

    }
}
