package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

/**
 * Created by Josue on 31/01/2017.
 */

public class ImageAdapterAllUsers extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vUser = new ArrayList<String>();
    private ArrayList<String> vNome = new ArrayList<String>();
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ProgressDialog mProgress;
    private DatabaseReference mDatabaseUsers;

    public void setvUri(ArrayList<String> vUri, ArrayList<String> vUser, ArrayList<String> vNome, ArrayList<String> vStatus) {
        this.vUri = vUri;
        this.vUser = vUser;
        this.vNome = vNome;
        this.vStatus = vStatus;
    }

    public ImageAdapterAllUsers(Context c) {
        mContext = c;
    }

    public int getCount() {
        return vUri.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        //final ImageView imageView;
        final CircleImageView imageView;
        final TextView textView;
        final ImageView imageStatus;

        mProgress = new ProgressDialog(mContext);
        View v = convertView;
        LayoutInflater in = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = in.inflate(R.layout.grid_view_items, null);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        textView = (TextView) v.findViewById(R.id.textView);
        imageStatus = (ImageView) v.findViewById(R.id.ic_onoff);
        imageView = (CircleImageView) v.findViewById(R.id.imageViewMeet);
        if (vNome.size()>position) {
            textView.setText(vNome.get(position));
        }

        if (vUser.size()>position) {
            mDatabaseUsers.child(vUser.get(position)).child("nome").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot xdataSnapshot) {
                            if (xdataSnapshot.getValue() != null) {
                                textView.setText(xdataSnapshot.getValue().toString());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }


        if (imageStatus!=null) {
            if (vStatus.size() > position) {
                if (vStatus.get(position).equals("on")) {
                    imageStatus.setImageResource(R.drawable.ic_online);
                } else {
                    imageStatus.setImageResource(R.drawable.ic_offline);
                }
            }
        }

        if (vUser.size()>position) {
            mDatabaseUsers.child(vUser.get(position)).child("status").addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot xdataSnapshot) {
                            if (xdataSnapshot.getValue() != null) {
                                if (xdataSnapshot.getValue().toString().equals("on")) {
                                    imageStatus.setImageResource(R.drawable.ic_online);
                                } else {
                                    imageStatus.setImageResource(R.drawable.ic_offline);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }
        if (vUri.size()>position) {
            Glide.with(mContext)
                    .load(vUri.get(position))
                    .into(imageView);
        }
        return v;
    }

}