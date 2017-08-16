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
public class Channel {

    public String uid;
    public String title;
    public String imagekey;
    public String morder;

    public Channel() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Channel(String uid, String title, String imagekey ) {
        this.uid = uid;
        this.title = title;
        this.imagekey = imagekey;
        this.morder = Long.toString(-1 * new Date().getTime());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(uid, title);
        result.put("photo", imagekey);
        result.put("morder", morder);
        return result;
    }

}
