package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.R;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class ReviewDetailFragment extends Fragment {
    public static final String TAG = "ReviewDetailFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivDetailGamePicture;
    private TextView tvDetailGameName;
    private ImageView ivDetailUserPicture;
    private TextView tvDetailUserName;
    private RatingBar rbDetailRating;
    private TextView tvReview;
    private Button btnAddComment;
    private Button btnViewComments;
    private String objectId;

    public ReviewDetailFragment(){
        // Empty public constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        objectId = getArguments().getString("oId");
        Log.i(TAG, objectId);
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivDetailGamePicture = view.findViewById(R.id.ivDetailGamePicture);
        tvDetailGameName = view.findViewById(R.id.tvDetailGameName);
        ivDetailUserPicture = view.findViewById(R.id.ivDetailUserPicture);
        tvDetailUserName = view.findViewById(R.id.tvDetailUserName);
        rbDetailRating = view.findViewById(R.id.rbDetailRating);
        tvReview = view.findViewById(R.id.tvReview);
        btnAddComment = view.findViewById(R.id.btnAddComment);
        btnViewComments = view.findViewById(R.id.btnViewComments);
        queryPost();

        // Add a comment to this review
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass review info to PostCommentFragment
                Bundle bundle = new Bundle();
                bundle.putString("oId", objectId);
                Log.i(TAG, "Sending oId " + objectId + " to PostCommentFragment");
                PostCommentFragment pcFragment = new PostCommentFragment();
                pcFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, pcFragment).addToBackStack(null).commit();
            }
        });
        // View comments for this review
        btnViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass review info to CommentsFragment
                Bundle bundle = new Bundle();
                bundle.putString("oId", objectId);
                Log.i(TAG, "Sending oId " + objectId + " to CommentsFragment");
                CommentsFragment cFragment = new CommentsFragment();
                cFragment.setArguments(bundle);
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, cFragment).addToBackStack(null).commit();
            }
        });
    }

    protected void queryPost(){
        ParseQuery<Post> query= ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECTID, objectId);
        query.setLimit(1);

        Log.i(TAG, "Querying for a post with " + Post.KEY_OBJECTID + " equal to " + objectId);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting ratings for a game" + e);
                    return;
                }
                // Should only be one post with this objectId
                Post post = objects.get(0);
                ParseUser postUser = post.getUser();
                // Display user info
                tvDetailUserName.setText(postUser.getUsername());
                ParseFile profilePicture = postUser.getParseFile("profilePicture");
                if(profilePicture != null){
                    Glide.with(getView()).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivDetailUserPicture);
                }
                else{
                    Glide.with(getView()).load(R.drawable.logo1).centerCrop().into(ivDetailUserPicture);
                }
                // Display post
                rbDetailRating.setRating(Float.valueOf(post.getRating().toString()));
                tvReview.setText(post.getPost());
                // Display game info
                queryGame(post.getGameId());
                // Navigate to user's profile page
                ivDetailUserPicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("uId", postUser.getObjectId());
                        ProfileFragment pFragment = new ProfileFragment();
                        pFragment.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, pFragment).addToBackStack(null).commit();
                    }
                });
            }
        });
    }

    private void queryGame(String gameId) {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i(TAG, String.format(GAME, gameId));
        client.get(String.format(GAME, gameId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                Log.d(TAG, "Async onSuccess");
                try {
                    tvDetailGameName.setText(jsonObject.get("name").toString());
                    String imageUrl = jsonObject.get("background_image").toString();
                    Glide.with(getView()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivDetailGamePicture);

                    // Go to GameDetailFragment for queried game when game image is pressed
                    ivDetailGamePicture.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("gId", gameId);
                            GameDetailFragment gdFragment = new GameDetailFragment();
                            gdFragment.setArguments(bundle);
                            AppCompatActivity activity = (AppCompatActivity) v.getContext();
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, gdFragment).addToBackStack(null).commit();
                        }
                    });
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
