package com.joyflick;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joyflick.Adapter.ChatAdapter;
import com.joyflick.models.Message;
import com.joyflick.models.Post;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = "ChatActivity";
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final long POLL_INTERVAL = TimeUnit.SECONDS.toMillis(3);
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    Handler myHandler = new android.os.Handler();
    EditText etMessage;
    ImageButton ibSend;
    RecyclerView rvChat;
    List<Message> mMessages;
    boolean mFirstLoad;
    ChatAdapter mAdapter;
    String userId;
    String otherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        // Get other user's id
        if(getIntent().getExtras() != null) {
            otherId = getIntent().getExtras().getString("oId");
            Log.i(TAG, "Starting chat with " + otherId);
        }
        else{
            Log.e(TAG, "Entering chat activity without other user id... this shouldn't happen!");
            otherId = "";
        }

        // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            userId = ParseUser.getCurrentUser().getObjectId();
            Log.i(TAG, "Logging into chat as " + userId);
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            Log.e(TAG, "Entering chat activity as anonymous user... this shouldn't happen!");
            userId = "";
            login();
        }
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Get the userId from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();
    }

    // Set up button event handler which posts the entered message to Parse
    void setupMessagePosting() {
        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        ibSend = (ImageButton) findViewById(R.id.ibSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, otherId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecyclerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

        // When send button is clicked, create message object on Parse
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
                Message message = new Message();
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.setBody(data);
                message.setOtherId(otherId);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i(TAG,"Successfully created message on Parse");
                        } else {
                            Log.e(TAG, "Failed to save message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByDescending("createdAt");

        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    // Only show message history of current 2 users
                    for(Message message: messages){
                        String uId = message.getUserId();
                        String oId = message.getOtherId();
                        if((uId.equals(userId) && oId.equals(otherId)) || (uId.equals(otherId) && oId.equals(userId))){
                            mMessages.add(message);
                        }
                    }

                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // Only start checking for new messages when the app becomes active in foreground
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        getSupportActionBar().hide();
    }

    @Override
    protected void onPause() {
        // Stop background task from refreshing messages, to avoid unnecessary traffic & battery drain
        myHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
        getSupportActionBar().show();
    }
}