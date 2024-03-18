package edu.uncc.assignment08.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    String user_id, user_fullname, token;

    public AuthResponse() {
    }

    public AuthResponse(JSONObject responseObject) throws JSONException {
        this.user_id = responseObject.getString("user_id");
        this.user_fullname = responseObject.getString("user_fullname");
        this.token = responseObject.getString("token");
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_fullname() {
        return user_fullname;
    }

    public void setUser_fullname(String user_fullname) {
        this.user_fullname = user_fullname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "user_id='" + user_id + '\'' +
                ", user_fullname='" + user_fullname + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
