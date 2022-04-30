package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joyflick.Adapter.FollowingAdapter;
import com.joyflick.Adapter.GameAdapter;
import com.joyflick.R;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {
    public static final String GAME = "https://api.rawg.io/api/games?key=8abe1aadb6a6459db418c8ac8239aa05";
    public static final String TAG = "FollowingFragment";
    private RecyclerView rvFollowingReviews;
    private TextView tvNoFollowing;
    protected List<Post> followingPosts;
    protected FollowingAdapter adapter;

    public FollowingFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFollowingReviews = view.findViewById(R.id.rvFollowingReviews);
        tvNoFollowing = view.findViewById(R.id.tvNoFollowing);
        followingPosts = new ArrayList<>();
        adapter = new FollowingAdapter(getContext(), followingPosts);
        rvFollowingReviews.setAdapter(adapter);
        LinearLayoutManager LinearLayoutManager = new LinearLayoutManager(getContext());
        rvFollowingReviews.setLayoutManager(LinearLayoutManager);

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> following = currentUser.getList("following");
        if(following != null && following.size() > 0){
            queryFollowingPosts(following);
        }
        else{
            // Not following any users
            tvNoFollowing.setVisibility(View.VISIBLE);
        }

    }

    protected void queryFollowingPosts(List<String> followingUsers) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.setLimit(10);
        Log.i(TAG, "Querying 10 latest posts");

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting latest posts" + e);
                    return;
                }
                adapter.clear();

                // Only add posts that user is following
                List<Post> followingPosts = new ArrayList<>();
                for(Post post: posts){
                    // If post is in list of following users, add to displayed posts list
                    if(followingUsers.contains(post.getUser().getObjectId())){
                        followingPosts.add(post);
                    }
                }
                adapter.addAll(followingPosts);
                adapter.notifyDataSetChanged();

                if (adapter.getItemCount() == 0) {
                    Log.i(TAG, "There are no latest reviews made by following users");
                    tvNoFollowing.setVisibility(View.VISIBLE);
                } else {
                    tvNoFollowing.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
