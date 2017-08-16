package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FrgDestUserPhotos extends Fragment implements View.OnClickListener {

    private DatabaseReference mDatabase, mDatabaseC;
    private FirebaseAuth mAuth;
    private String mUid, dUid, vImageVideo;
    private StorageReference mStorage;

    private ImageButton btnCancel;
    private ImageButton btnGallery, btnVideo;
    private GridView gridview;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vChaveUri = new ArrayList<String>();
    private ProgressDialog mProgress;
    private Handler handler;
    private ImageAdapterUser imgAduser;
    private ImageAdapterVideoUser vidAduser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String vNome, vChannel;
    private ListView list;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgdest_user_photos, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mAuth = FirebaseAuth.getInstance();


        mUid = mAuth.getCurrentUser().getUid().toString();
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnGallery = (ImageButton) view.findViewById(R.id.ic_gallery);
        btnVideo = (ImageButton) view.findViewById(R.id.ic_videos);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid().toString();
        mDatabaseC = FirebaseDatabase.getInstance().getReference();



        btnCancel.setOnClickListener(this);


        Bundle bundledestuserphoto = this.getArguments();
        if (bundledestuserphoto!=null) {
            dUid = bundledestuserphoto.getString("dUid", "0");
            vNome = bundledestuserphoto.getString("vNome", "0");
            vChannel = bundledestuserphoto.getString("vChannel", "0");
            vImageVideo = bundledestuserphoto.getString("videoimage", "image");
            gridview = (GridView) view.findViewById(R.id.gridview);
            list = (ListView) view.findViewById(R.id.lstViewVideo);
            if (vImageVideo.equals("video")) {
                btnGallery.setVisibility(View.INVISIBLE);
                btnVideo.setVisibility(View.VISIBLE);
                vidAduser = new ImageAdapterVideoUser(getContext());
                gridview.setVisibility(View.INVISIBLE);
                list.setVisibility(View.VISIBLE);
            }else{
                btnGallery.setVisibility(View.VISIBLE);
                btnVideo.setVisibility(View.INVISIBLE);
                gridview.setVisibility(View.VISIBLE);
                list.setVisibility(View.INVISIBLE);
                imgAduser = new ImageAdapterUser(getContext());
            }

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String MyUrl = "";
                    vUri.clear();
                    vChaveUri.clear();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.getKey().equals(dUid)) {
                            HashMap<String, String> mUserPhotos = (HashMap<String, String>) noteDataSnapshot.getValue();
                            for (Map.Entry<String, String> entry : mUserPhotos.entrySet()) {
                                MyUrl = entry.getValue();
                                if (vImageVideo.equals("video")){
                                    if (!entry.getKey().toString().equals("Default")
                                            && (entry.getValue().toString().contains(vImageVideo)
                                                || entry.getValue().toString().contains("youtube:"))) {
                                        vChaveUri.add(entry.getKey().toString());
                                        vUri.add(entry.getValue().toString());
                                    }
                                }else {
                                    if (!entry.getKey().toString().equals("Default")
                                            && entry.getValue().toString().contains(vImageVideo)) {
                                        vChaveUri.add(entry.getKey().toString());
                                        vUri.add(entry.getValue().toString());
                                    }
                                }
                            }
                        }
                    }
                    Globals.bUserPhotos = 1;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            });
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (vUri.size() > 0) {
                        if (vImageVideo.equals("video")) {
                            vidAduser.setvUri(vUri, vChaveUri);
                            list.setAdapter(vidAduser);
                        }else{
                            imgAduser.setvUri(vUri, vChaveUri);
                            gridview.setAdapter(imgAduser);
                        }

                    } else {
                        handler.postDelayed(this, 500);
                    }
                }
            }, 1000);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Globals.vIntentFoto = "1";
                        Intent i = new Intent(getContext(), FullScreenViewActivity.class);
                        i.putExtra("position", position);
                        i.putStringArrayListExtra("utils", vUri);
                        i.putStringArrayListExtra("vchaveurl", vChaveUri);
                        i.putExtra("tipo", "0");
                        i.putExtra("dUid", dUid);
                        startActivity(i);
                    }
                });

        }

        return view;
    }
    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            getFragmentManager().popBackStack();
        }
    }
}
