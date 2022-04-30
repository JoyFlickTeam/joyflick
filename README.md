# JoyFlick

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
JoyFlick is an application that will allow users to view and post video game reviews. Users will be able to search for games and see other user reviews for each game. Users will have profiles where all of their reviews will be visible, allowing other users to see which games a specific user likes or dislikes.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social
- **Mobile:** This application is primarily developed for mobile devices.
- **Story:** JoyFlick is a place to see and post game reviews. Users can determine if a game is worth their time based on these reviews.
- **Market:** The target market for JoyFlick is for casual and hardcore gamers alike. People who want to share their gaming opinions can post reviews, while people who are just curious about if a certain game is worthwhile can look at other reviews.
- **Habit:** JoyFlick would be used every time a new game releases. Usage would vary based on when games release.
- **Scope:** The starting functionality would consist of posting and viewing game reviews, searching for games and users, and viewing user profiles. Expanded functionality could consist of following users and expanding the social aspect, and verifying users with the option to only view verified reviews.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Users can log in, sign up, and log out of the app
- [x] Users can add a profile picture
- [x] Users can add a game review post
- [x] Users can search for games or other users
- [x] Users can view a detailed page about a game and see user reviews
- [x] Users can view a feed of recently released games
- [x] Users can view user profiles with that user's game reviews
- [x] Users can view and post comments on game reviews

**Optional Nice-to-have Stories**

- [ ] Users can update their credentials in profile settings
- [ ] Users can enable a mode to only view reviews from verified users
- [x] Users can follow other users

### 2. Screen Archetypes

* Login
   * Users can log in, sign up, and log out of the app
* Signup
   * Users can log in, sign up, and log out of the app
* Main
   * Users can view a feed of recent reviewed games
* Search
   * Users can search for games or other users
* Profile
   * Users can view user profiles with that user's game reviews
   * Users can add a profile picture
* Game
   * Users can view a detailed page about a game and see user reviews
* Post Review
   * Users can add a game review post
* Review Detail
   * Users can view a detailed page about a game and see user reviews
   * Users can view and post comments on game reviews
* Comment
   * Users can view and post comments on game reviews
* Game Selection
   * Users can search for games or other users

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home to Main
* Add to Post Review
* User to Profile

**Flow Navigation** (Screen to Screen)

* Login
   * Main
   * Signup
* Signup
   * Main
   * Login
* Main
   * Search
   * Game
* Search
   * Game
   * Profile
* Profile
   * Review Detail
* Game
   * Post Review
   * Review Detail
* Post Review
   * Game Selection
   * Game
* Review Detail
   * Game
   * Profile
   * Comment
* Comment
   * Review Detail
* Game Selection
   * Post Review

## Wireframes
<img src="https://i.imgur.com/jdOB1HD.png" width=600>

### [BONUS] Digital Wireframes & Mockups
<img src="https://i.imgur.com/2H36I7g.png" width=600>

### [BONUS] Interactive Prototype
<img src="https://i.imgur.com/0c4RxYt.gif" width=349>

## Schema 
### Models
#### Post
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | postId        | Number   | unique id for the user post (default field) |
   | user          | Pointer to User | post user |
   | gameId        | String     | game that the user is posting about |
   | post          | String   | user's review about the game |
   | rating        | Double   | user's number rating of the game |
   | createdAt     | DateTime | date when post is created (default field) |
#### Comments
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | commentId     | Number   | unique id for the comment (default field) |
   | postId        | String   | id of the post the user is commenting on |
   | user          | Pointer to User   | comment user |
   | comment       | String   | user's comment on a post |
   | createdAt     | DateTime | date when comment is created (default field) |
#### User
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | userId        | Number   | unique id for the user (default field) |
   | username      | String   | name associated with user |
   | password      | String   | user's password |
   | profilePicture | Image   | user's profile picture |
### Networking
- Log In Screen
  - (GET) Log user in if they exist in the user table.
  ```java
  ParseUser.logInInBackground(username, password, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
          if(e != null){
              // Error logging in
              Log.e(TAG, "Issue with loginUser: " + e.toString());
              return;
          }
          // Login success
          // TODO: redirect user to main screen
      }
  });
  ```
- Sign Up Screen
  - (POST) Send the username and password to the user table.
  ```java
  ParseUser user = new ParseUser();
  user.setUsername(username);
  user.setPassword(password);
  user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
          if(e != null){
              // Error signing up user
              Log.e(TAG, "Issue with signupUser: " + e.toString());
              return;
          }
          // Sign up success
          // TODO: notify user of success, redirect user to main screen
      }
  });
  ```
- Home Screen
  - (GET) Query all games from api according to recent releases.
  ```java
    AsyncHttpClient client = new AsyncHttpClient();
    
    RequestParams params = new RequestParams();
    params.put("sort", "original_release_date:desc"); //release date in descending order, from latest.
    params.put("limit", listOneBatchLimit.toString()); // listOneBatchLimit is a int value
    params.put("offset", offsetToGet.toString()); // offsetToGet is a int value

    
    client.get(API_BASE+"/"+apiTypeName+"/?api_key="+API_KEY, params, new TextHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, String response) {
                // Response is a raw string of xml, use convert to convert to json file
                XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                JsonObject responceJsonObject = xmlToJson.toJson();
                
                JsonGameListExtractor jsonextracter = new JsonGameListExtractor(responceJsonObject);
                gamelist = jsonextracter.extractGames; // global variable gamelist
                

                // TODO: displaying games and updating the recyclerview and other fancy client stuff below this, sorted by release date...
        }

        @Override
        public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
            Log.d("APIReciever", errorResponse);
        }
    });
  ```
- Game Screen
  - (GET) Query all the users who have reviewed the game, including each rating.
  ```java
  ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
  query.include(Post.KEY_USER);
  query.whereEqualTo(Post.KEY_GAMEID, getGameId());
  query.setLimit(20);
  query.addDescendingOrder(Post.KEY_CREATED_AT);

  query.findInBackground(new FindCallback<Post>() {
      @Override
      public void done(List<Post> posts, ParseException e) {
          if(e != null){
              // Issue querying reviews for game
              Log.e(TAG, "Issue with getting posts: " + e.toString());
              return;
          }
          // Query successful
          adapter.clear();
          adapter.addAll(posts);
          adapter.notifyDataSetChanged();
          // TODO: display retrieved reviews and ratings for current game...
      }
  });
  ```
- Comment Screen
  - (POST) Adding a comment on a review for a game.
  ```java
  Comment comment = new Comment();
  comment.setComment(userComment);
  comment.setUser(currentUser);
  comment.setPost(currentPost);
  comment.saveInBackground(new SaveCallback() {
    @Override
    public void done(ParseException e) {
      if(e != null){
        // Issue with posting comment
        Log.e(TAG, "Issue while saving: " + e.toString());
        return;
      }
      // Comment posted
      Log.i(TAG, "Review saved successfully!");
    }
  });
  ```
- Search Screen
  - (GET) Query the name of the game/users from the api.
  ```java
    // Query games based on name
    AsyncHttpClient client = new AsyncHttpClient();
    
    RequestParams params = new RequestParams();
    params.put("name", name.toString()); // query games based on name
    params.put("limit", listOneBatchLimit.toString()); // listOneBatchLimit is a int value
    params.put("offset", offsetToGet.toString()); // offsetToGet is a int value

    
    client.get(API_BASE+"/"+apiTypeName+"/?api_key="+API_KEY, params, new TextHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, String response) {
                // Response is a raw string of xml, use convert to convert to json file
                XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                JsonObject responceJsonObject = xmlToJson.toJson();
                
                JsonGameListExtractor jsonextracter = new JsonGameListExtractor(responceJsonObject);
                gamelist = jsonextracter.extractGames; // global variable gamelist
                
                // TODO: displaying games and updating the recyclerview and other fancy client stuff below this, all according to the name...
        }

        @Override
        public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
            Log.d("APIReciever", errorResponse);
        }
    });
  // Query users based on name
  ParseQuery<User> query = ParseQuery.getQuery(User.class);
  query.include(User.KEY_USERNAME);
  query.whereEqualTo(User.KEY_USERNAME, name.toString());
  query.setLimit(20);
  query.addDescendingOrder(User.KEY_CREATED_AT);

  query.findInBackground(new FindCallback<User>() {
      @Override
      public void done(List<User> users, ParseException e) {
          if(e != null){
              // Issue querying users
              Log.e(TAG, "Issue with getting users: " + e.toString());
              return;
          }
          // Query successful
          adapter.clear();
          adapter.addAll(users);
          adapter.notifyDataSetChanged();
          // TODO: display user name and profile image based on name, under list of games...
      }
  });
  ```
- Post Review Screen
  - (POST) Add a post for the current user.
  ```java
  Post review = new Post();
  review.setPost(post);
  review.setRating(rating);
  review.setUser(currentUser);
  review.setGame(currentGame);
  review.saveInBackground(new SaveCallback() {
    @Override
    public void done(ParseException e) {
      if(e != null){
        // Issue with posting review
        Log.e(TAG, "Issue while saving: " + e.toString());
        return;
      }
      // Review posted
      Log.i(TAG, "Review saved successfully!");
    }
  });
  ```
- Review Detail Screen
  - (GET) Get the details of the comments made by a user.
  ```java
  ParseQuery<Post> query = ParseQuery.getQuery(Comment.class);
  query.include(Comment.KEY_USER);
  query.whereEqualTo(Comment.KEY_POSTID, getPostId());
  query.setLimit(20);
  query.addDescendingOrder(Comment.KEY_CREATED_AT);

  query.findInBackground(new FindCallback<Comment>() {
      @Override
      public void done(List<Comment> comments, ParseException e) {
          if(e != null){
              // Issue querying comments for review
              Log.e(TAG, "Issue with getting comments: " + e.toString());
              return;
          }
          // Query successful
          adapter.clear();
          adapter.addAll(comments);
          adapter.notifyDataSetChanged();
          // TODO: display retrieved comments for current review...
      }
  });
  ```
- Game Selection 
  - (GET) Query the name of the game from the api.
  ```java
    AsyncHttpClient client = new AsyncHttpClient();
    
    RequestParams params = new RequestParams();
    params.put("name", name.toString()); // query games based on name
    params.put("limit", listOneBatchLimit.toString()); // listOneBatchLimit is a int value
    params.put("offset", offsetToGet.toString()); // offsetToGet is a int value

    
    client.get(API_BASE+"/"+apiTypeName+"/?api_key="+API_KEY, params, new TextHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, String response) {
                // Response is a raw string of xml, use convert to convert to json file
                XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
                JsonObject responceJsonObject = xmlToJson.toJson();
                
                JsonGameListExtractor jsonextracter = new JsonGameListExtractor(responceJsonObject);
                gamelist = jsonextracter.extractGames; // global variable gamelist
                
                // TODO: displaying games and updating the recyclerview and other fancy client stuff below this, all according to the name...
        }

        @Override
        public void onFailure(int statusCode, @Nullable Headers headers, String errorResponse, @Nullable Throwable throwable) {
            Log.d("APIReciever", errorResponse);
        }
    });
  ```
- Profile Screen
  - (GET) Query all the posts made by the user.
  ```java
  ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
  query.include(Post.KEY_USER);
  query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
  query.setLimit(20);
  query.addDescendingOrder(Post.KEY_CREATED_AT);

  query.findInBackground(new FindCallback<Post>() {
      @Override
      public void done(List<Post> posts, ParseException e) {
          if(e != null){
              // Issue querying reviews for user
              Log.e(TAG, "Issue with getting posts: " + e.toString());
              return;
          }
          // Query successful
          adapter.clear();
          adapter.addAll(posts);
          adapter.notifyDataSetChanged();
          // TODO: display retrieved reviews and ratings for current user...
      }
  });
  ```
## Sprint 3
<img src="https://i.imgur.com/xTDEXt3.gif" width=600>

## Sprint 2  
<img src="https://i.imgur.com/7mb1iwE.gif" width=600>

## Sprint 1
<img src="https://i.imgur.com/mHoTmXI.gif" width=600>
