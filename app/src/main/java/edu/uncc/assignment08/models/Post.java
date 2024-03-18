package edu.uncc.assignment08.models;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Post implements Serializable {
    public String created_by_name, post_id, created_by_uid, post_text, created_at;

    public Post(JSONObject jsonObject) throws JSONException {
        this.created_by_name = jsonObject.getString("created_by_name");
        this.post_id = jsonObject.getString("post_id");
        this.created_by_uid = jsonObject.getString("created_by_uid");
        this.post_text = jsonObject.getString("post_text");
        this.created_at = jsonObject.getString("created_at");
    }

    public String getCreated_by_name() {
        return created_by_name;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getCreated_by_uid() {
        return created_by_uid;
    }

    public String getPost_text() {
        return post_text;
    }

    public String getCreated_at() {
        return created_at;
    }

    @Override
    public String toString() {
        return "Post{" +
                "created_by_name='" + created_by_name + '\'' +
                ", post_id='" + post_id + '\'' +
                ", created_by_uid='" + created_by_uid + '\'' +
                ", post_text='" + post_text + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
