package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.Adapter.PostAdapter;
import com.joyflick.R;
import com.joyflick.models.Game;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class GameDetailFragment extends Fragment {
    public static final String TAG = "GameDetailFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivPoster;
    private TextView tvName;
    private String gameId;
    private RatingBar rbGameAvgRating;
    private ImageButton ibAddReview;
    private RecyclerView rvGameRatings;
    private TextView tvNoReviews;
    private float avgRating;
    protected PostAdapter adapter;
    protected List<Post> ratings;

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
        ibAddReview = view.findViewById(R.id.ibAddReview);
        rbGameAvgRating = view.findViewById(R.id.rbGameAvgRating);
        rvGameRatings = view.findViewById(R.id.rvGameRatings);
        tvNoReviews = view.findViewById(R.id.tvNoReviews);
        ratings = new ArrayList<>();
        adapter = new PostAdapter(getContext(), ratings);
        rvGameRatings.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getContext()));
        rvGameRatings.setLayoutManager(linearLayoutManager);
        queryGame();
        queryPosts();

        // Add a review...
        ibAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to post fragment, passing the current game id
                Bundle bundle = new Bundle();
                bundle.putString("gId", gameId);
                PostFragment pFragment = new PostFragment();
                pFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, pFragment).addToBackStack(null).commit();
            }
        });
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

    protected void queryPosts(){
        ParseQuery<Post> query= ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_GAMEID, gameId);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.setLimit(5);
        Log.i(TAG, "Querying posts for " + Post.KEY_GAMEID + " equal to " + gameId);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting ratings for a game" + e);
                    return;
                }
                adapter.clear();

                // Update average rating of game
                for(Post post: posts){
                    avgRating += post.getRating();
                }
                avgRating /= posts.size();
                rbGameAvgRating.setRating(avgRating);

                adapter.addAll(posts);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0){
                    Log.i(TAG, "There are no reviews for the selected game");
                    tvNoReviews.setVisibility(View.VISIBLE);
                }
                else{
                    tvNoReviews.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // For hiding toolbar
    @Override
    public void onResume(){
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop(){
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
