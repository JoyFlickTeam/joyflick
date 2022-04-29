package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.joyflick.models.Comments;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import okhttp3.Headers;

public class PostCommentFragment extends Fragment {
    public static final String TAG = "PostCommentFragment";
    public static final String GAME = "https://api.rawg.io/api/games/%s?key=8abe1aadb6a6459db418c8ac8239aa05";
    private ImageView ivCommentProfileImage;
    private ImageView ivCommenterGameImage;
    private TextView tvCommenterGameName;
    private TextView tvCommenterReview;
    private TextView tvCommenterProfileName;
    private EditText etComment;
    private ImageButton ibCommentPost;
    private String objectId;

    public PostCommentFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Get review id from ReviewDetailFragment
        objectId = getArguments().getString("oId");
        Log.i(TAG, objectId);
        return inflater.inflate(R.layout.fragment_post_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCommentProfileImage = view.findViewById(R.id.ivCommentProfileImage);
        tvCommenterProfileName = view.findViewById(R.id.tvCommenterProfileName);
        ivCommenterGameImage = view.findViewById(R.id.ivCommenterGameImage);
        tvCommenterGameName = view.findViewById(R.id.tvCommenterGameName);
        tvCommenterReview = view.findViewById(R.id.tvCommenterReview);
        etComment = view.findViewById(R.id.etComment);
        ibCommentPost = view.findViewById(R.id.ibCommentPost);
        queryPost();


        // Post comment when post button is pressed
        ibCommentPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String comment = etComment.getText().toString();
                if(comment.isEmpty()){
                    Toast.makeText(getContext(), "Your comment must have text", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveComment(currentUser, comment, objectId);
            }
        });
    }

    private void saveComment(ParseUser currentUser, String commentBody, String postId){
        Comments comment = new Comments();
        comment.setComment(commentBody);
        comment.setPostId(postId);
        comment.setUser(currentUser);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue while saving: " + e);
                    Toast.makeText(getContext(), "Error while saving comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Comment saved successfully!");
                Toast.makeText(getContext(), "Your comment has been saved", Toast.LENGTH_SHORT).show();
                etComment.setText("");
            }
        });
    }

    protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_OBJECTID, objectId);
        query.setLimit(1);

        Log.i(TAG, "Querying for a post with " + Post.KEY_OBJECTID + " equal to " + objectId);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting ratings for a game" + e);
                    return;
                }
                // Should only be one post with this objectId
                Post post = objects.get(0);
                ParseUser postUser = post.getUser();
                // Display user info
                tvCommenterProfileName.setText(postUser.getUsername());
                ParseFile profilePicture = postUser.getParseFile("profilePicture");
                if (profilePicture != null) {
                    Glide.with(getView()).load(profilePicture.getUrl()).placeholder(R.drawable.logo1).centerCrop().into(ivCommentProfileImage);
                } else {
                    Glide.with(getView()).load(R.drawable.logo1).centerCrop().into(ivCommentProfileImage);
                }
                // Display post
                tvCommenterReview.setText(post.getPost());
                // Display game info
                queryGame(post.getGameId());
                // Navigate to user's profile page
                ivCommentProfileImage.setOnClickListener(new View.OnClickListener() {
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
                    tvCommenterGameName.setText(jsonObject.get("name").toString());
                    String imageUrl = jsonObject.get("background_image").toString();
                    Glide.with(getView()).load(imageUrl).centerCrop().placeholder(R.drawable.logo1).into(ivCommenterGameImage);

                    // Go to GameDetailFragment for queried game when game image is pressed
                    ivCommenterGameImage.setOnClickListener(new View.OnClickListener() {
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
