package com.joyflick;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.Adapter.userAdapter;
import com.joyflick.models.Game;
import com.joyflick.models.Post;
import com.joyflick.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    TextView Username;
    ImageView userImage;
    boolean searchUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if(getIntent().getExtras() != null) {
            searchUsers = getIntent().getExtras().getBoolean("searchUsers");
        }
        else{
            searchUsers = true;
        }
        Log.i(TAG, "Search users is " + searchUsers);

        idSearch = findViewById(R.id.idSearch);
        idGames = findViewById(R.id.idGames);
        idResult = findViewById(R.id.idResult);
        textView = findViewById(R.id.textView);
        Username = findViewById(R.id.UserName);
        userImage = findViewById(R.id.UserPicture);

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
                if(searchUsers) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", s);
                    Log.i(TAG, "Querying users for " + User.KEY_USERNAME + " equal to " + s);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> users, ParseException e) {
                            ParseUser user = users.get(0);
                            String name = user.getUsername();
                            ParseFile imageURL = user.getParseFile("profilePicture");
                            textView.setVisibility(View.VISIBLE);
                            if (imageURL != null) {
                                Log.i(TAG, "Attempting to load profile picture" + imageURL);
                                Glide.with(userImage.getContext()).load(imageURL.getUrl()).into(userImage);
                            }
                            Username.setText(user.getUsername());
                            Log.d("objects name" + name, TAG);

                        }
                    });
                }
                else{
                    // Extend games recycler view to whole screen for game selection
                    idResult.getLayoutParams().height = -1;
                }

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
