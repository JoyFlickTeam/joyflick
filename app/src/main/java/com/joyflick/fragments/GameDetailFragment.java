package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.R;
import com.joyflick.models.Game;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Headers;

public class GameDetailFragment extends Fragment {
    public static final String TAG = "GameDetailFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivPoster;
    private TextView tvName;
    private String gameId;

    public GameDetailFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gameId = getArguments().getString("gId");
        Log.i(TAG, gameId);
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivPoster = view.findViewById(R.id.ivGamePoster);
        tvName = view.findViewById(R.id.tvGameName);
        queryGame();
    }// end of the onView Created

    private void queryGame() {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(TAG, String.format(GAME, gameId));
        client.get(String.format(GAME, gameId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                Log.d(TAG, "Async onSuccess");
                try {
                    tvName.setText(jsonObject.get("name").toString());
                    String imageUrl = jsonObject.get("background_image").toString();
                    Glide.with(getView()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivPoster);
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
}
