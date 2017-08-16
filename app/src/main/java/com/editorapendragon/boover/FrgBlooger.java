package com.editorapendragon.boover;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBlooger extends Fragment {

    private static final String TAG = "FrgBlooger";
    private Bundle bundle;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vTipoUsuario,dUid, vbook, mData = "";
    private Handler handler;
    private ArrayList<String> vAuthorPost = new ArrayList<String>();
    private ArrayList<String> vPhotoPost = new ArrayList<String>();
    private ArrayList<String> vTitlePost = new ArrayList<String>();
    private ArrayList<String> vMessagePost = new ArrayList<String>();
    private ArrayList<String> vDataPost = new ArrayList<String>();
    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<String> vKey = new ArrayList<String>();
    private ArrayList<String> vstarCount = new ArrayList<String>();
    private ArrayList<String> vTotalComment = new ArrayList<String>();
    private ArrayList<String> vTipo = new ArrayList<String>();
    private ProgressBar mProgressBar;
    private ImageButton btnNewPost, btnMyPosts, btnResenhas, btnBoover, btnBack;
    private ListView list;
    private CustomBlogListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgbloogers_fragment, container, false);
        getFragmentManager().beginTransaction().addToBackStack("FrgBlooger").commit();

        Bundle bundleBlooger = this.getArguments();
        vbook="";
        if (bundleBlooger != null) {
            vTipoUsuario = bundleBlooger.getString("tipo", "0");
            dUid = bundleBlooger.getString("dUid", "0");
            vbook = bundleBlooger.getString("vbook", "0");
        }else{
            vTipoUsuario="0";
        }

        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference("posts");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vAuthorPost.clear();
                vTitlePost.clear();
                vPhotoPost.clear();
                vMessagePost.clear();
                vUid.clear();
                vKey.clear();
                vstarCount.clear();
                vTotalComment.clear();
                vTipo.clear();
                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (vTipoUsuario.equals("1")) {
                        btnBack.setVisibility(View.INVISIBLE);
                        if (noteDataSnapshot.child("uid").getValue().toString().equals(mUid)) {
                            try {
                                vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                                if (noteDataSnapshot.child("title").getValue()!=null) {
                                    vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                                }
                                if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                    vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                                }
                                if (noteDataSnapshot.child("body").getValue()!=null) {
                                    vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                                }
                                if (noteDataSnapshot.child("uid").getValue()!=null) {
                                    vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                                }
                                if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                    vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                    vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                    vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                                }
                                vKey.add(noteDataSnapshot.getKey().toString());
                                try {
                                    Locale current = getResources().getConfiguration().locale;
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                    mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                    vDataPost.add(mData);
                                } catch (IllegalStateException e) {
                                    Log.e("Exception", "Failure");
                                }
                            } catch (NullPointerException n) {
                                n.printStackTrace();
                            }
                        }
                    } else if (vTipoUsuario.equals("2")) {
                        btnBack.setVisibility(View.INVISIBLE);
                        if (noteDataSnapshot.child("tipo").getValue().toString().equals("resenha")) {
                            vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                            if (noteDataSnapshot.child("title").getValue()!=null) {
                                vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                            }
                            if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                            }
                            if (noteDataSnapshot.child("body").getValue()!=null) {
                                vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                            }
                            if (noteDataSnapshot.child("uid").getValue()!=null) {
                                vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                            }
                            if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                            }
                            if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                            }
                            if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                            }
                            vKey.add(noteDataSnapshot.getKey().toString());
                            try {
                                Locale current = getResources().getConfiguration().locale;
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                vDataPost.add(mData);
                            } catch (IllegalStateException e) {
                                Log.e("Exception", "Failure");
                            }
                        }
                    } else if (vTipoUsuario.equals("3")) {
                        btnBack.setVisibility(View.VISIBLE);
                        if (noteDataSnapshot.child("uid").getValue().toString().equals(dUid)) {
                            try {
                                vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                                if (noteDataSnapshot.child("title").getValue()!=null) {
                                    vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                                }
                                if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                    vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                                }
                                if (noteDataSnapshot.child("body").getValue()!=null) {
                                    vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                                }
                                if (noteDataSnapshot.child("uid").getValue()!=null) {
                                    vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                                }
                                if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                    vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                    vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                    vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                                }
                                vKey.add(noteDataSnapshot.getKey().toString());
                                try {
                                    Locale current = getResources().getConfiguration().locale;
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                    mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                    vDataPost.add(mData);
                                } catch (IllegalStateException e) {
                                    Log.e("Exception", "Failure");
                                }
                            }catch (NullPointerException n){
                                try {
                                    Toast.makeText(getContext(), getResources().getString(R.string.erro_recuperar_dados), Toast.LENGTH_SHORT).show();
                                } catch (IllegalStateException t){
                                    t.printStackTrace();
                                }
                            }
                        }
                    } else if (vTipoUsuario.equals("4")) {
                        btnBack.setVisibility(View.VISIBLE);
                        if ((noteDataSnapshot.child("uid").getValue().toString().equals(dUid)) && (noteDataSnapshot.child("tipo").getValue().toString().equals("resenha"))) {


                                vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                                if (noteDataSnapshot.child("title").getValue()!=null) {
                                    vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                                }
                                if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                    vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                                }
                                if (noteDataSnapshot.child("body").getValue()!=null) {
                                    vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                                }
                                if (noteDataSnapshot.child("uid").getValue()!=null) {
                                    vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                                }
                                if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                    vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                    vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                                }
                                if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                    vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                                }
                                vKey.add(noteDataSnapshot.getKey().toString());
                                mData = "";
                                try {
                                    Locale current = getResources().getConfiguration().locale;
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                    mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                } catch (IllegalStateException e) {
                                    Log.e("Exception", "Failure");
                                }
                                vDataPost.add(mData);
                        }
                    } else if (vTipoUsuario.equals("5")) {
                        btnBack.setVisibility(View.VISIBLE);
                        if (noteDataSnapshot.child("bookId").getValue()!=null) {
                            if ((noteDataSnapshot.child("bookId").getValue().toString().equals(vbook))) {
                                try {
                                    vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                                    if (noteDataSnapshot.child("title").getValue()!=null) {
                                        vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                        vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("body").getValue()!=null) {
                                        vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("uid").getValue()!=null) {
                                        vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                        vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                        vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                                    }
                                    if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                        vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                                    }
                                    vKey.add(noteDataSnapshot.getKey().toString());
                                    mData = "";
                                    try {
                                        Locale current = getResources().getConfiguration().locale;
                                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                        mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                    } catch (IllegalStateException e) {
                                        Log.e("Exception", "Failure");
                                    }
                                    vDataPost.add(mData);
                                }catch (NullPointerException m){
                                    m.printStackTrace();
                                }
                            }
                        }
                    } else {
                        btnBack.setVisibility(View.INVISIBLE);
                        if (noteDataSnapshot.child("author").getValue()!=null &&
                            noteDataSnapshot.child("title").getValue()!=null) {
                            vAuthorPost.add(noteDataSnapshot.child("author").getValue().toString());
                            if (noteDataSnapshot.child("title").getValue()!=null) {
                                vTitlePost.add(noteDataSnapshot.child("title").getValue().toString());
                            }
                            if (noteDataSnapshot.child("imagekey").getValue()!=null) {
                                vPhotoPost.add(noteDataSnapshot.child("imagekey").getValue().toString());
                            }
                            if (noteDataSnapshot.child("body").getValue()!=null) {
                                vMessagePost.add(noteDataSnapshot.child("body").getValue().toString());
                            }
                            if (noteDataSnapshot.child("uid").getValue()!=null) {
                                vUid.add(noteDataSnapshot.child("uid").getValue().toString());
                            }
                            if (noteDataSnapshot.child("starCount").getValue()!=null) {
                                vstarCount.add(noteDataSnapshot.child("starCount").getValue().toString());
                            }
                            if (noteDataSnapshot.child("commentsCount").getValue()!=null) {
                                vTotalComment.add(noteDataSnapshot.child("commentsCount").getValue().toString());
                            }
                            if (noteDataSnapshot.child("tipo").getValue()!=null) {
                                vTipo.add(noteDataSnapshot.child("tipo").getValue().toString());
                            }
                            vKey.add(noteDataSnapshot.getKey().toString());
                            mData = "";
                            try {
                                Locale current = getResources().getConfiguration().locale;
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
                                String mData = sdf.format(new Date(Long.parseLong(noteDataSnapshot.child("data").getValue().toString())));
                                vDataPost.add(mData);
                            } catch (IllegalStateException e) {
                                Log.e("Exception", e.toString());
                            }
                        }
                    }

                }
                Collections.reverse(vDataPost);
                Collections.reverse(vTitlePost);
                Collections.reverse(vMessagePost);
                Collections.reverse(vAuthorPost);
                Collections.reverse(vPhotoPost);
                Collections.reverse(vUid);
                Collections.reverse(vKey);
                Collections.reverse(vstarCount);
                Collections.reverse(vTotalComment);
                Collections.reverse(vTipo);
                //adapter = new CustomBlogListAdapter(getActivity(), vAuthorPost, vTitlePost, vPhotoPost, vMessagePost, vDataPost, vUid, vKey, vstarCount, vTotalComment, vTipo);
                //list.setAdapter(adapter);
                try {
                    adapter.notifyDataSetChanged();
                } catch (NullPointerException  e){
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        list = (ListView)  view.findViewById (R.id.lstViewBlog);
        //list.setFastScrollAlwaysVisible(true);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vAuthorPost.size() > 0) {
                    if (getActivity()!=null) {
                        adapter = new CustomBlogListAdapter(getActivity(), vAuthorPost, vTitlePost, vPhotoPost, vMessagePost, vDataPost, vUid, vKey, vstarCount, vTotalComment, vTipo);
                        list.setAdapter(adapter);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 1000);

        btnNewPost = (ImageButton) view.findViewById(R.id.btnNewPost);
        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "0");
                FrgNewPost fFrag = new FrgNewPost();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.replace( R.id.frgbloogers_frame, fFrag, "FrgNewPost");
                ft.addToBackStack("FrgNewPost");
                ft.commit();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        btnBoover = (ImageButton) view.findViewById(R.id.btnBoover);
        btnBoover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "0");
                FrgBlooger fFrag = new FrgBlooger();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.replace( R.id.frgbloogers_frame, fFrag, "FrgBlooger");
                ft.addToBackStack("FrgBlooger");
                ft.commit();
            }
        });



        btnMyPosts = (ImageButton) view.findViewById(R.id.btnMyPosts);
        btnMyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "1");
                FrgBlooger fFrag = new FrgBlooger();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.add( R.id.frgbloogers_frame, fFrag, "FrgBloogerMyPosts");
                ft.addToBackStack("FrgBloogerMyPosts");
                ft.commit();
            }
        });

        btnResenhas = (ImageButton) view.findViewById(R.id.btnResenhas);
        btnResenhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "2");
                FrgBlooger fFrag = new FrgBlooger();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.add( R.id.frgbloogers_frame, fFrag, "FrgBloogerResenhas");
                ft.addToBackStack("FrgBloogerResenhas");
                ft.commit();
            }
        });
        return view;
    }
}