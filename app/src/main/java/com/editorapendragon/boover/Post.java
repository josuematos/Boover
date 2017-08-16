package com.editorapendragon.boover;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Josue on 14/02/2017.
 */

@IgnoreExtraProperties
public class Post {

    public String uid;
    public String author;
    public String title;
    public String body;
    public String imagekey;
    public String vbook;
    public int starCount = 0;
    public int commentsCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public String morder, vdata, tipo;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String imagekey, String tipo, String vbook) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.imagekey = imagekey;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.vdata = Long.toString(new Date().getTime());
        this.tipo=tipo;
        this.vbook=vbook;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("imagekey", imagekey);
        result.put("data", vdata);
        result.put("morder", morder);
        result.put("tipo", tipo);
        result.put("commentsCount", commentsCount);
        result.put("bookId", vbook);

        return result;
    }

}
