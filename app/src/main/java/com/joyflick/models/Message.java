package com.joyflick.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String KEY_USER_ID= "userId";
    public static final String KEY_BODY = "body";
    public static final String KEY_OTHER_ID = "otherId";

    public String getUserId() {
        return getString(KEY_USER_ID);
    }

    public String getBody() {
        return getString(KEY_BODY);
    }

    public String getOtherId(){
        return getString(KEY_OTHER_ID);
    }

    public void setUserId(String userId) {
        put(KEY_USER_ID, userId);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public void setOtherId(String otherId){
        put(KEY_OTHER_ID, otherId);
    }
}