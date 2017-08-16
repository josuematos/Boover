package com.editorapendragon.boover;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrgUserPhotos extends Fragment implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid;
    private StorageReference mStorage;
    private ImageButton btnNewImage;
    private ImageButton btnCancel;
    private GridView gridview;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vChaveUri = new ArrayList<String>();
    private ProgressDialog mProgress;
    private Handler handler;
    private ImageAdapterUser imgAduser;
    private Activity _activity;
    private ProgressBar mProgressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frguser_photos, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        btnNewImage = (ImageButton) view.findViewById(R.id.btnNewImage);
        btnNewImage.setOnClickListener(this);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        imgAduser = new ImageAdapterUser(getContext());
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        gridview = (GridView) view.findViewById(R.id.gridview);

        if (vUri.size()>0){
            imgAduser.setvUri(vUri,vChaveUri);
            gridview.setAdapter(imgAduser);
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Globals.bUserPhotos = 0;
                }
            }, 500);
        }else {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    vUri.clear();
                    vChaveUri.clear();
                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        if (noteDataSnapshot.getKey().equals(mUid)) {
                            HashMap<String, String> mUserPhotos = (HashMap<String, String>) noteDataSnapshot.getValue();
                            for (Map.Entry<String, String> entry : mUserPhotos.entrySet()) {
                                if (!entry.getKey().toString().equals("Default") &&
                                        !entry.getValue().toString().contains("video")) {
                                    vChaveUri.add(entry.getKey());
                                    vUri.add(entry.getValue());
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
                            imgAduser.setvUri(vUri, vChaveUri);
                            gridview.setAdapter(imgAduser);
                        } else {
                            handler.postDelayed(this, 500);
                        }
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }
            }, 1000);
        }
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Globals.vIntentFoto="1";
                Intent i = new Intent(getContext(), FullScreenViewActivity.class);
                i.putExtra("position", position);
                i.putStringArrayListExtra("utils", vUri);
                i.putStringArrayListExtra("vchaveurl", vChaveUri);
                i.putExtra("tipo", "1");
                i.putExtra("dUid", mUid);
                startActivity(i);
            }
        });

        return view;
    }
    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            getFragmentManager().popBackStack();
        }
        if (v == btnNewImage) {
            Bundle bundleuserphotos = new Bundle();
            bundleuserphotos.putString("tipo", "0");
            FotoUser fFrag = new FotoUser();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            fFrag.setArguments(bundleuserphotos);
            ft.add( R.id.frguser_frame, fFrag, "FrgFotoUser");
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
