package com.joyflick.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.Adapter.ProfilePostAdapter;
import com.joyflick.ChatActivity;
import com.joyflick.LoginActivity;
import com.joyflick.R;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    private ImageView ivProfileImage;
    private TextView tvProfileName;
    private ImageButton ibSettings;
    private ImageButton ibFollow;
    private ImageButton ibUnfollow;
    private ImageButton ibMessage;
    private Button btnLogout;
    private TextView tvNoUserReviews;
    private RecyclerView rvUserReviews;
    private String userId;
    private boolean isFollowing;
    protected ProfilePostAdapter adapter;
    protected List<Post> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getArguments() != null) {
            userId = getArguments().getString("uId");
            Log.i(TAG, userId);
        }
        else{
            userId = ParseUser.getCurrentUser().getObjectId();
        }
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        ibSettings = view.findViewById(R.id.ibSettings);
        ibFollow = view.findViewById(R.id.ibFollow);
        ibUnfollow = view.findViewById(R.id.ibUnfollow);
        ibMessage = view.findViewById(R.id.ibMessage);
        btnLogout = view.findViewById(R.id.btnLogout);
        rvUserReviews = view.findViewById(R.id.rvUserReviews);
        tvNoUserReviews = view.findViewById(R.id.tvNoUserReviews);
        isFollowing = false;
        posts = new ArrayList<>();
        adapter = new ProfilePostAdapter(getContext(), posts);
        rvUserReviews.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((getContext()));
        rvUserReviews.setLayoutManager(linearLayoutManager);

        // Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        if(!userId.equals(ParseUser.getCurrentUser().getObjectId())){
            // Viewing someone else's profile
            Log.i(TAG, "Viewing someone else's profile, querying user...");
            queryUser();
            ibSettings.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);
        }
        else{
            // Viewing logged in user's profile details
            Log.i(TAG, "Viewing own profile, show profile edit options");
            ParseUser currentUser = ParseUser.getCurrentUser();
            ibSettings.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
            isFollowing = false;
            ibFollow.setVisibility(View.GONE);
            ibUnfollow.setVisibility(View.GONE);
            ibMessage.setVisibility(View.GONE);
            // Load profile image
            ParseFile profilePicture = currentUser.getParseFile("profilePicture");
            if(profilePicture != null){
                Log.i(TAG, "Attempting to load profile picture");
                Glide.with(getContext()).load(profilePicture.getUrl()).into(ivProfileImage);
            }
            else{
                Log.i(TAG, "Using default profile picture");
                Glide.with(getContext()).load(R.drawable.logo1).into(ivProfileImage);
            }
            tvProfileName.setText(currentUser.getString("username"));

            queryPosts(currentUser);
        }

        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to settings fragment
                SettingsFragment sFragment = new SettingsFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, sFragment).addToBackStack(null).commit();
            }
        });
        ibFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Attempting to follow user");
                updateFollowing(userId, true);
                // Display unfollow button
                ibUnfollow.setVisibility(View.VISIBLE);
                ibFollow.setVisibility(View.INVISIBLE);
            }
        });
        ibUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Attempting to unfollow user");
                updateFollowing(userId, false);
                // Display follow button
                ibFollow.setVisibility(View.VISIBLE);
                ibUnfollow.setVisibility(View.INVISIBLE);
            }
        });
        ibMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start chat activity with other user
                Log.i(TAG, "Navigating to chat activity");
                Bundle bundle = new Bundle();
                bundle.putString("oId", userId);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    protected void updateFollowing(String uId, boolean follow){
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> following = new ArrayList<>();
        // Get current following list if it exists
        if(currentUser.getList("following") != null) {
            following.addAll(currentUser.getList("following"));
        }
        // Determine if following or unfollowing user
        if (follow) {
            // Add user to following
            following.add(uId);
        } else {
            // Remove user from following
            following.remove(uId);
        }
        currentUser.put("following", following);
        currentUser.saveInBackground(e -> {
                if(e == null)
                {
                    Log.i(TAG, "Updated following list");
                }
                else
                {
                    Log.e(TAG,"Failed to update following list:" + e);
                }
        });
    }

    protected void queryUser(){
        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.setLimit(1);
        Log.i(TAG, "Querying users for " + Post.KEY_USER + " equal to " + userId);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting user" + e);
                    return;
                }
                // There should be only one user
                ParseUser profileUser = users.get(0);
                // Show user's profile picture, name, and posts
                ParseFile profilePicture = profileUser.getParseFile("profilePicture");
                if(profilePicture != null){
                    Log.i(TAG, "Attempting to load profile picture");
                    Glide.with(getContext()).load(profilePicture.getUrl()).into(ivProfileImage);
                }
                else{
                    Log.i(TAG, "Using default profile picture");
                    Glide.with(getContext()).load(R.drawable.logo1).into(ivProfileImage);
                }
                tvProfileName.setText(profileUser.getString("username"));
                queryPosts(profileUser);

                // Update isFollowing if current user is following fetched user
                ParseUser currentUser = ParseUser.getCurrentUser();
                List<String> following = currentUser.getList("following");
                if(following != null && following.contains(profileUser.getObjectId())){
                    Log.i(TAG, "Currently following user, show unfollow button");
                    isFollowing = true;
                    ibUnfollow.setVisibility(View.VISIBLE);
                    ibFollow.setVisibility(View.INVISIBLE);
                }
                else{
                    Log.i(TAG, "Not following user, show follow button");
                    isFollowing = false;
                    ibFollow.setVisibility(View.VISIBLE);
                    ibUnfollow.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    protected void queryPosts(ParseUser profileUser){
        ParseQuery<Post> query= ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, profileUser);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.setLimit(10);
        Log.i(TAG, "Querying posts for " + Post.KEY_USER + " equal to " + profileUser.getObjectId());

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting ratings for a game" + e);
                    return;
                }
                adapter.clear();

                adapter.addAll(posts);
                adapter.notifyDataSetChanged();

                if(adapter.getItemCount() == 0){
                    Log.i(TAG, "There are no reviews made by the selected user");
                    tvNoUserReviews.setVisibility(View.VISIBLE);
                }
                else{
                    tvNoUserReviews.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // For hiding toolbar
    @Override
    public void onResume(){
        super.onResume();
        if(!userId.equals(ParseUser.getCurrentUser().getObjectId())){
            // Viewing someone else's profile, hide toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }
    @Override
    public void onStop(){
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
