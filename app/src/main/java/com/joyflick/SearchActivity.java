package com.joyflick;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.models.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchActivity extends AppCompatActivity {
    public static final String SEARCH = "https://api.rawg.io/api/games?key=8abe1aadb6a6459db418c8ac8239aa05&search=";
    public static final String TAG = "SearchActivity";
    protected List<Game> games;
    SearchView idSearch;
    TextView idGames;
    RecyclerView idResult;
    TextView textView;
    RecyclerView iduserResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        idSearch = findViewById(R.id.idSearch);
        idGames = findViewById(R.id.idGames);
        idResult = findViewById(R.id.idResult);
        textView = findViewById(R.id.textView);
        iduserResult = findViewById(R.id.iduserResult);

        idGames.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        // putting the game into the adapter
        games = new ArrayList<>();
        GameAdapter gameAdapter = new GameAdapter(this, games);
        idResult.setAdapter(gameAdapter);
        idResult.setLayoutManager(new LinearLayoutManager(this));
        games.clear();

        // doing the query search

        idSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                AsyncHttpClient client = new AsyncHttpClient();
                client.get(SEARCH + s, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                        Log.d("Success and the search:" + s, TAG);
                        JSONObject jsonObject = json.jsonObject;
                        try{
                            idGames.setVisibility(View.VISIBLE);
                            games.clear();
                            JSONArray results = jsonObject.getJSONArray("results");
                            games.addAll(Game.fromJsonArray(results));
                            Log.i(TAG, "Games: " + games.size());
                            gameAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Hit JSON exception", e);
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d("Failure", TAG);
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });



    }// on create instance


}// search activity
