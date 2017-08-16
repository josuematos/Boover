package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomContactsListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private ArrayList<String> vNome = new ArrayList<String>();
    private ArrayList<String> vPhotoUser = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> vtStatus = new ArrayList<String>();
    private ArrayList<String> vtCheck = new ArrayList<String>();
    private String vTipo;

    public CustomContactsListAdapter(Activity context, ArrayList<String> vNome, ArrayList<String> vPhotoUser,
                                     ArrayList<String> vtUser, ArrayList<String> vtStatus, String vTipo ) {
        super(context, R.layout.channellistview, vNome);
        this.context=context;
        this.vNome=vNome;
        this.vPhotoUser=vPhotoUser;
        this.vtUser=vtUser;
        this.vtStatus=vtStatus;
        this.vTipo=vTipo;
    }

    public ArrayList<String> getVtCheck() {
        return vtCheck;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.contactslistview, null,true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vNome.get(position));
        CircleImageView imgChannel = (CircleImageView) rowView.findViewById(R.id.iconUser);
        if (vPhotoUser.size()>=position+1) {
                Glide.with(getContext())
                        .load(vPhotoUser.get(position))
                        .into(imgChannel);
        }
        CheckBox chkFriend = (CheckBox) rowView.findViewById(R.id.checkBoxFriend);
        chkFriend.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
                                              vtCheck.add(vtUser.get(position));
                                          }
                                      });


        TextView txtStatus = (TextView) rowView.findViewById(R.id.itemmessage);
        if (vtStatus!=null) {
            chkFriend.setVisibility(View.INVISIBLE);
            if (vtStatus.size() >= position + 1) {
                txtStatus.setText(" " + vtStatus.get(position) + " ");

                txtStatus.setAllCaps(true);
                if (vtStatus.get(position).equals("on")) {
                    txtStatus.setBackgroundColor(Color.GREEN);
                } else {
                    txtStatus.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                txtStatus.setBackgroundColor(Color.LTGRAY);
                txtStatus.setText(" Off ");
            }
        }else{
            chkFriend.setVisibility(View.VISIBLE);
        }

        if (vTipo.equals("off")){
            chkFriend.setVisibility(View.INVISIBLE);
        }else{
            chkFriend.setVisibility(View.VISIBLE);
        }


        return rowView;
    }
}
