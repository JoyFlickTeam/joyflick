package com.joyflick.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FeedFragment<current_date> extends Fragment {

    public static final String GAME = "https://api.rawg.io/api/games?key=8abe1aadb6a6459db418c8ac8239aa05";
    public static final String TAG = "FeedFragment";
    protected List<Game> games;
    private RecyclerView rvPost;
    protected GameAdapter adapter;
    String current_date = String.valueOf(LocalDate.now());

    public FeedFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPost = view.findViewById(R.id.rvPosts);
        games = new ArrayList<>();
        adapter = new GameAdapter(getContext(), games);
        rvPost.setAdapter(adapter);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        rvPost.setLayoutManager(LinearLayoutManager);
        queryGames();
    }// end of the onView Created

    private void queryGames() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GAME + "&dates=2000-01-01,"+ current_date + "&ordering=-released", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                Log.d(TAG, "Async onSuccess");
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    games.addAll(Game.fromJsonArray(results));
                    Log.i(TAG, "Games" + games.size());
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Hit JSON exception", e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Async onFaliure");
            }
        });

    }



}// end of the class

