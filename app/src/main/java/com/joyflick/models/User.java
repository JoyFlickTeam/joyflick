package com.joyflick.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {
    public static final String KEY_EMAIL_VERIFIED = "emailVerified";
    public static final String KEY_UPDATED_AT = "updatedAt";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PROFILE = "profilePicture";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_OBJECTID = "objectId";

    public static String getKeyEmailVerified() {
        return KEY_EMAIL_VERIFIED;
    }

    public static String getKeyUpdatedAt() {
        return KEY_UPDATED_AT;
    }

    public static String getKeyUsername() {
        return KEY_USERNAME;
    }

    public static String getKeyEmail() {
        return KEY_EMAIL;
    }

    public static String getKeyProfile() {
        return KEY_PROFILE;
    }

    public static String getKeyCreatedAt() {
        return KEY_CREATED_AT;
    }

    public static String getKeyObjectid() {
        return KEY_OBJECTID;
    }
}
