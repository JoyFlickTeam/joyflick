package com.joyflick.models;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String KEY_POST = "post";
    public static final String KEY_RATING = "rating";
    public static final String KEY_GAMEID = "gameId";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getPost(){
        return getString(KEY_POST);
    }

    public void setPost(String post){
        put(KEY_POST, post);
    }

    public Double getRating(){
        return getDouble(KEY_RATING);
    }

    public void setRating(Double rating){
        put(KEY_RATING, rating);
    }

    public String getGameId(){
        return getString(KEY_GAMEID);
    }

    public void setGameId(String gameId){
        put(KEY_GAMEID, gameId);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }
}
