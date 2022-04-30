package com.joyflick;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joyflick.Adapter.userAdapter;
import com.joyflick.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    public static final String TAG = "SearchUserActivity";
    protected List<ParseUser> users;
    SearchView idSearchuser;
    TextView idUserText;
    RecyclerView UserListResult;

    @Override
    protected void onCreate(Bundle savedInstance) {

        super.onCreate(savedInstance);
        setContentView(R.layout.activity_user_search);

        idSearchuser = findViewById(R.id.idSearchUsers);
        idUserText = findViewById(R.id.idUserText);
        UserListResult = findViewById(R.id.UsersListResult);

        idUserText.setVisibility(View.GONE);

        users = new ArrayList<ParseUser>();
        userAdapter adapater = new userAdapter(this, users);
        UserListResult.setAdapter(adapater);
        UserListResult.setLayoutManager(new LinearLayoutManager(this));
        users.clear();

        idSearchuser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                users.clear();
                ParseQuery<ParseUser> query= ParseUser.getQuery();
                query.whereEqualTo("username", s);
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
}
