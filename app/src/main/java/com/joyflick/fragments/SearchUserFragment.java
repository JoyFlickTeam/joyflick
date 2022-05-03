package com.joyflick.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joyflick.Adapter.userAdapter;
import com.joyflick.R;
import com.joyflick.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchUserFragment extends Fragment {
    public static final String TAG = "SearchUserActivity";
    protected List<ParseUser> users;
    SearchView idSearchuser;
    TextView idUserText;
    RecyclerView UserListResult;

    public SearchUserFragment(){
        // Empty constructor required
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idSearchuser = view.findViewById(R.id.idSearchUsers);
        idUserText = view.findViewById(R.id.idUserText);
        UserListResult = view.findViewById(R.id.UsersListResult);

        idUserText.setVisibility(View.GONE);

        users = new ArrayList<ParseUser>();
        userAdapter adapater = new userAdapter(getContext(), users);
        UserListResult.setAdapter(adapater);
        UserListResult.setLayoutManager(new LinearLayoutManager(getContext()));
        users.clear();

        idSearchuser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                users.clear();
                ParseQuery<ParseUser> query= ParseUser.getQuery();
                query.whereContains("username", s);
                Log.i(TAG, "Querying users for " + User.KEY_USERNAME + " equal to " + s);
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> object, ParseException e)
                    {
                        idUserText.setVisibility(View.VISIBLE);
                        users.addAll(object);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });


    }// end of the onCreate

    // Hide top bar
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop(){
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
