package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.R;
import com.joyflick.models.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SearchFragment extends Fragment {
    public static final String SEARCH = "https://api.rawg.io/api/games?key=8abe1aadb6a6459db418c8ac8239aa05&search=";
    public static final String TAG = "SearchActivity";
    protected List<Game> games;
    SearchView idSearch;
    TextView idGames;
    RecyclerView idResult;

    public SearchFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idSearch = view.findViewById(R.id.idSearchUsers);
        idGames = view.findViewById(R.id.idGames);
        idResult = view.findViewById(R.id.UsersListResult);

        idGames.setVisibility(View.GONE);
        // putting the game into the adapter
        games = new ArrayList<>();
        GameAdapter gameAdapter = new GameAdapter(getContext(), games);
        idResult.setAdapter(gameAdapter);
        idResult.setLayoutManager(new LinearLayoutManager(getContext()));
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


/*
                ParseQuery<ParseUser> query= ParseUser.getQuery();
                query.whereEqualTo("username", s);
                Log.i(TAG, "Querying users for " + User.KEY_USERNAME + " equal to " + s);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> users, ParseException e) {
                        ParseUser user = users.get(0);
                        String name = user.getUsername();
                        ParseFile imageURL = user.getParseFile("profilePicture");
                        textView.setVisibility(View.VISIBLE);
                        if(imageURL != null){
                            Log.i(TAG, "Attempting to load profile picture" + imageURL);
                            Glide.with(userImage.getContext()).load(imageURL.getUrl()).into(userImage);
                        }
                        if(Username != null) {
                            Username.setText(user.getUsername());
                            Log.d("objects name" + name, TAG);
                        }

                    }
                });
*/
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });



    }// on create instance

    // Hide top bar
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop(){
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }


}// search activity
