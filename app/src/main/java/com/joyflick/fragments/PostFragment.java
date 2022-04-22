package com.joyflick.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.R;
import com.joyflick.SearchActivity;
import com.joyflick.models.Post;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

public class PostFragment extends Fragment {
    public static final String TAG = "PostFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivReviewGamePoster;
    private TextView tvReviewGameName;
    private RatingBar rbReviewRating;
    private EditText etReview;
    private String gameId;
    private ImageButton ibReviewPost;

    public PostFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {
            gameId = getArguments().getString("gId");
            Log.i(TAG, gameId);
        }
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivReviewGamePoster = view.findViewById(R.id.ivReviewGamePoster);
        tvReviewGameName = view.findViewById(R.id.tvReviewGameName);
        rbReviewRating = view.findViewById(R.id.rbReviewRating);
        etReview = view.findViewById(R.id.etReview);
        ibReviewPost = view.findViewById(R.id.ibReviewPost);
        setGameInfo();

        // Go to game search/selection when image is pressed
        ivReviewGamePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("searchUsers", false);
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // Post review when submit button is pressed
        ibReviewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String post = etReview.getText().toString();
                Double rating = Double.valueOf(rbReviewRating.getRating());
                if(gameId == null){
                    Toast.makeText(getContext(), "You must select a game to post a review", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(post.isEmpty()){
                    Toast.makeText(getContext(), "Your review must have text", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, rating.toString() + ": " + post);
                savePost(currentUser, post, rating, gameId);
            }
        });
    }

    private void setGameInfo() {
        if(gameId != null){
            queryGame();
            etReview.setFocusable(true);
            rbReviewRating.setIsIndicator(false);
            ibReviewPost.setClickable(true);
        }
        else{
            Glide.with(getView()).load(R.drawable.logo1).centerCrop().into(ivReviewGamePoster);
            tvReviewGameName.setText("Select a game");
            etReview.setFocusable(false);
            rbReviewRating.setIsIndicator(true);
            ibReviewPost.setClickable(false);
        }
    }

    private void queryGame() {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(TAG, String.format(GAME, gameId));
        client.get(String.format(GAME, gameId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                Log.d(TAG, "Async onSuccess");
                try {
                    tvReviewGameName.setText(jsonObject.get("name").toString());
                    String imageUrl = jsonObject.get("background_image").toString();
                    Glide.with(getView()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivReviewGamePoster);
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

    private void savePost(ParseUser currentUser, String review, Double rating, String idGame){
        Post post = new Post();
        post.setPost(review);
        post.setRating(rating);
        post.setGameId(idGame);
        ParseRelation uRel = post.getRelation("user");
        uRel.add(currentUser);
        post.setUser(uRel);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue while saving: " + e);
                    Toast.makeText(getContext(), "Error while saving review", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Review saved successfully!");
                Toast.makeText(getContext(), "Your review has been saved", Toast.LENGTH_SHORT).show();
                etReview.setText("");
                rbReviewRating.setRating(0);
                gameId = null;
                setGameInfo();
            }
        });
    }
}
