package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Josue on 31/01/2017.
 */

public class ImageAdapterVideoUser extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vChaveUri = new ArrayList<String>();
    private Handler handler;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    public void setvUri(ArrayList<String> vUri, ArrayList<String> vChaveUri) {
        this.vUri = vUri;
        this.vChaveUri = vChaveUri;
    }

    public ImageAdapterVideoUser(Context c) {
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

        View v = convertView;
        LayoutInflater in = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = in.inflate(R.layout.grid_view_videos, null);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        WebView imageView = (WebView) v.findViewById(R.id.vvideo);
        final TextView txtVideo = (TextView) v.findViewById(R.id.textVideo);

            if (vUri.size()>position) {
                mDatabase.child("posts").orderByChild("uid")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String vtitle = "nok";
                                    for (DataSnapshot titleSnapshot : postSnapshot.getChildren()) {
                                        if (titleSnapshot.getKey().equals("imagekey")){
                                            if (titleSnapshot.getValue().toString().equals(vUri.get(position))){
                                                vtitle= "ok";
                                            }

                                        }
                                        if (titleSnapshot.getKey().equals("title") && vtitle.equals("ok")){
                                            txtVideo.setText(titleSnapshot.getValue().toString());
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });
                if (vUri.get(position).contains("youtube")) {
                    Globals.vIntentFoto = "1";
                    String link = vUri.get(position);
                    link = link.replace("youtube:","");
                    String iframe = "<iframe width=\"340\" height=\"215\" src=\"https://www.youtube.com/embed/"+link+"\" frameborder=\"0\" allowfullscreen></iframe>";
                    imageView.getSettings().setJavaScriptEnabled(true);
                    imageView.getSettings().setLoadWithOverviewMode(true);
                    imageView.setWebViewClient(new WebViewClient());
                    imageView.setBackgroundColor(0x00000000);
                    imageView.getSettings().setBuiltInZoomControls(true);
                    imageView.loadData(iframe, "text/html", "UTF-8");
                } else {
                    imageView.loadUrl(vUri.get(position));
                }

            }

        return v;
    }

}