package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

/**
 * Created by Josue on 31/01/2017.
 */

public class ImageAdapterUser extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vChaveUri = new ArrayList<String>();
    private Handler handler;
    private ProgressDialog mProgress;

    public void setvUri(ArrayList<String> vUri, ArrayList<String> vChaveUri) {
        this.vUri = vUri;
        this.vChaveUri = vChaveUri;
    }

    public ImageAdapterUser(Context c) {
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
        final CircleImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new CircleImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250,250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(3, 3, 3, 3);
        } else {
            imageView = (CircleImageView) convertView;
        }
        Glide.with(mContext)
                .load(vUri.get(position))
                .into(imageView);

        return imageView;
    }

}