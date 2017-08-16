package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomCommentsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vComments = new ArrayList<String>();
    private ArrayList<String> vPhotoUser = new ArrayList<String>();
    private String morder;

    public CustomCommentsListAdapter(Activity context, ArrayList<String> vComments, ArrayList<String> vPhotoUser) {
        super(context, R.layout.channellistview, vComments);
        this.context=context;
        this.vComments=vComments;
        this.vPhotoUser=vPhotoUser;

    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.commentslistview, null,true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vComments.get(position));
        CircleImageView imgChannel = (CircleImageView) rowView.findViewById(R.id.iconUser);
        if (vPhotoUser.size()>=position+1) {
                Glide.with(getContext())
                        .load(vPhotoUser.get(position))
                        .into(imgChannel);
        }
        return rowView;
    }
}
