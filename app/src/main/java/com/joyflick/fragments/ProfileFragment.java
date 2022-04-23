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
import com.joyflick.LoginActivity;
import com.joyflick.R;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    public final static int PICK_PHOTO_CODE = 1046;
    private ImageView ivProfileImage;
    private TextView tvProfileName;
    private Button btnLogout;
    private Button btnProfilePhoto;
    private TextView tvNoUserReviews;
    private RecyclerView rvUserReviews;
    private String userId;
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
        btnLogout = view.findViewById(R.id.btnLogout);
        btnProfilePhoto = view.findViewById(R.id.btnProfilePhoto);
        rvUserReviews = view.findViewById(R.id.rvUserReviews);
        tvNoUserReviews = view.findViewById(R.id.tvNoUserReviews);
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

        // Change profile picture
        btnProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(view);
            }
        });

        if(!userId.equals(ParseUser.getCurrentUser().getObjectId())){
            // Viewing someone else's profile
            queryUser();
            btnLogout.setVisibility(View.GONE);
            btnProfilePhoto.setVisibility(View.GONE);
        }
        else{
            // Viewing logged in user's profile details
            ParseUser currentUser = ParseUser.getCurrentUser();
            btnLogout.setVisibility(View.VISIBLE);
            btnProfilePhoto.setVisibility(View.VISIBLE);
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

    // Trigger gallery selection for a photo
    public void onPickPhoto (View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            File selectedPhoto = getPhotoFileUri("avatar.jpg");
            ParseFile parseFile = new ParseFile(selectedPhoto);

            // Change profile photo of current user to selected image
            Log.i(TAG, "Attempting to change profile photo");
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("profilePicture", parseFile);
            currentUser.saveInBackground(e -> {
                if(e == null){
                    Log.i(TAG, "Profile photo update success");
                    Toast.makeText(getContext(), "Profile photo has been updated.", Toast.LENGTH_SHORT).show();
                    // Update profile picture displayed
                    ParseFile profilePicture = currentUser.getParseFile("profilePicture");
                    Glide.with(getContext()).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).into(ivProfileImage);
                } else {
                    Log.e(TAG, "Profile photo update failure: " + e);
                    // Toast.makeText(getContext(), "Error updating profile photo: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // Check Android version
            if(Build.VERSION.SDK_INT > 27){
                // Newer version detected - use new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // Use getBitmap for older Android versions
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if(!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "Failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
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
