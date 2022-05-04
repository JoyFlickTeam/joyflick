package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.Adapter.CommentsAdapter;
import com.joyflick.R;
import com.joyflick.models.Comments;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CommentsFragment extends Fragment {
    public static final String TAG = "CommentsFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivCommentsTopProfileImage;
    private TextView tvCommentsTopProfileName;
    private RatingBar rbRatingComments;
    private ImageView ivCommentsTopGameImage;
    private TextView tvCommentsTopGameName;
    private TextView tvCommentsTopReview;
    private TextView tvNoComments;
    private RecyclerView rvComments;
    private String objectId;
    protected List<Comments> comments;
    protected CommentsAdapter adapter;

    public CommentsFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Get review from ReviewDetailFragment
        objectId = getArguments().getString("oId");
        Log.i(TAG, objectId);
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCommentsTopProfileImage = view.findViewById(R.id.ivCommentsTopProfileImage);
        tvCommentsTopProfileName = view.findViewById(R.id.tvCommentsTopProfileName);
        rbRatingComments = view.findViewById(R.id.rbRatingComments);
        ivCommentsTopGameImage = view.findViewById(R.id.ivCommentsTopGameImage);
        tvCommentsTopGameName = view.findViewById(R.id.tvCommentsTopGameName);
        tvCommentsTopReview = view.findViewById(R.id.tvCommentsTopReview);
        tvNoComments = view.findViewById(R.id.tvNoComments);
        rvComments = view.findViewById(R.id.rvComments);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(getContext(), comments);
        rvComments.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getContext()));
        rvComments.setLayoutManager(linearLayoutManager);
        queryPost();
        queryComments();
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
                tvCommentsTopProfileName.setText(postUser.getUsername());
                ParseFile profilePicture = postUser.getParseFile("profilePicture");
                if(profilePicture != null){
                    Glide.with(getView()).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivCommentsTopProfileImage);
                }
                else{
                    Glide.with(getView()).load(R.drawable.logo1).centerCrop().into(ivCommentsTopProfileImage);
                }
                // Display post
                tvCommentsTopReview.setText(post.getPost());
                rbRatingComments.setRating(Float.valueOf(post.getRating().toString()));
                // Display game info
                queryGame(post.getGameId());
                // Navigate to user's profile page
                ivCommentsTopProfileImage.setOnClickListener(new View.OnClickListener() {
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
                    tvCommentsTopGameName.setText(jsonObject.get("name").toString());
                    String imageUrl = jsonObject.get("background_image").toString();
                    Glide.with(getView()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivCommentsTopGameImage);

                    // Go to GameDetailFragment for queried game when game image is pressed
                    ivCommentsTopGameImage.setOnClickListener(new View.OnClickListener() {
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

    protected void queryComments(){
        ParseQuery<Comments> query= ParseQuery.getQuery(Comments.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Comments.KEY_POSTID, objectId);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.setLimit(10);
        Log.i(TAG, "Querying comments for " + Comments.KEY_POSTID + " equal to " + objectId);

        query.findInBackground(new FindCallback<Comments>() {
            @Override
            public void done(List<Comments> comments, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting comments for a review" + e);
                    return;
                }
                adapter.clear();

                adapter.addAll(comments);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0){
                    Log.i(TAG, "There are no comments for this review");
                    tvNoComments.setVisibility(View.VISIBLE);
                }
                else{
                    tvNoComments.setVisibility(View.INVISIBLE);
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
