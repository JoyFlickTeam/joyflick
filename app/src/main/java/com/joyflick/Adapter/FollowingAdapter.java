package com.joyflick.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.joyflick.R;
import com.joyflick.fragments.ReviewDetailFragment;
import com.joyflick.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder>{
    public static final String TAG = "FollowingAdapter";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    Context context;
    List<Post> followingPosts;

    public FollowingAdapter(Context context, List<Post> posts){
        this.context = context;
        this.followingPosts = posts;
    }

    @NonNull
    @Override
    public FollowingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View ratingView = LayoutInflater.from(context).inflate(R.layout.item_following_review,parent, false);
        return new FollowingAdapter.ViewHolder(ratingView);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder" + position);
        // get the game position
        Post post = followingPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return followingPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout container;
        ImageView ivFollowingProfileImage;
        TextView tvFollowingProfileName;
        RatingBar rbRatingFollowing;
        ImageView ivFollowingGameImage;
        TextView tvFollowingGameName;
        TextView tvFollowingReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFollowingProfileImage = itemView.findViewById(R.id.ivFollowingProfileImage);
            tvFollowingProfileName = itemView.findViewById(R.id.tvFollowingProfileName);
            rbRatingFollowing = itemView.findViewById(R.id.rbRatingFollowing);
            ivFollowingGameImage = itemView.findViewById(R.id.ivFollowingGameImage);
            tvFollowingGameName = itemView.findViewById(R.id.tvFollowingGameName);
            tvFollowingReview = itemView.findViewById(R.id.tvFollowingReview);
            container = itemView.findViewById(R.id.followingContainer);
        }

        public void bind(Post post){
            // Display user info
            ParseUser postUser = post.getUser();
            tvFollowingProfileName.setText(postUser.getUsername());
            rbRatingFollowing.setRating(Float.valueOf(post.getRating().toString()));
            tvFollowingReview.setText(post.getPost());
            ParseFile profilePicture = postUser.getParseFile("profilePicture");
            if(profilePicture != null){
                Glide.with(context).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivFollowingProfileImage);
            }
            else{
                Glide.with(context).load(R.drawable.logo1).centerCrop().into(ivFollowingProfileImage);
            }
            // Display game info
            queryGame(post.getGameId());

            // Go to detailed review screen
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Pass review info to detailed review fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("oId", post.getObjectId());
                    Log.i(TAG, "Sending oId " + post.getObjectId() + " to ReviewDetailFragment");
                    ReviewDetailFragment rdFragment = new ReviewDetailFragment();
                    rdFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, rdFragment).addToBackStack(null).commit();
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
                        tvFollowingGameName.setText(jsonObject.get("name").toString());
                        String imageUrl = jsonObject.get("background_image").toString();
                        Glide.with(context).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivFollowingGameImage);
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

    public void clear(){
        followingPosts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> list){
        followingPosts.addAll(list);
        notifyDataSetChanged();
    }
}
