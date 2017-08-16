package com.editorapendragon.boover;

import java.util.Date;

/**
 * Created by Josue on 06/02/2017.
 */

public class FriendlyMessage {
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private String receiver;
    private String sender;
    private String mdata;
    private String channel;
    private String simage;


    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String receiver, String sender, String channel, String simage) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.receiver = receiver;
        this.sender = sender;
        this.mdata = Long.toString(System.currentTimeMillis());
        this.channel = channel;
        this.simage = simage;
    }

    public String getChannel() {
        return channel;
    }

    public String getSimage() {
        return simage;
    }

    public void setSimage(String simage) {
        this.simage = simage;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMdata() {
        return mdata;
    }

    public void setMdata(String mdata) {
        this.mdata = mdata;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
