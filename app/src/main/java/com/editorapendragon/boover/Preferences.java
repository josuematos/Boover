package com.editorapendragon.boover;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Josue on 28/01/2017.
 */

@IgnoreExtraProperties
public class Preferences {
    public String chat;
    public String foto;
    public String perfil;
    public String post;
    public String video;


    public Preferences() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Preferences(String chat, String foto, String perfil, String post, String video) {
        this.chat = chat;
        this.foto = foto;
        this.perfil = perfil;
        this.post= post;
        this.video=video;
    }
}
