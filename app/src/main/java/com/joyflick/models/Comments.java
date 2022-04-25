package com.joyflick.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comments")
public class Comments extends ParseObject {
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_POSTID = "postId";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_OBJECTID = "objectId";

    public String getComment(){
        return getString(KEY_COMMENT);
    }

    public void setComment(String comment){
        put(KEY_COMMENT, comment);
    }

    public String getPostId(){
        return getString(KEY_POSTID);
    }

    public void setPostId(String postId){
        put(KEY_POSTID, postId);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }
}
