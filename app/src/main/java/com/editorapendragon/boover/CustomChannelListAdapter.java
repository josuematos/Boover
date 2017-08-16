package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomChannelListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vChannel = new ArrayList<String>();
    private ArrayList<String> vNomeChannel = new ArrayList<String>();
    private ArrayList<String> vLido = new ArrayList<String>();
    private String morder;
    private DatabaseReference mDatabase, mDatabase2;
    private String mUid, mUsernameAnonymous = "";
    private FirebaseAuth mAuth;

    public CustomChannelListAdapter(Activity context, ArrayList<String> vChannel, ArrayList<String> vNomeChannel,
                                    ArrayList<String> vLido ) {
            super(context, R.layout.channellistview, vChannel);

            this.context = context;
            this.vChannel = vChannel;
            this.vNomeChannel = vNomeChannel;
            this.vLido = vLido;
            this.morder = Long.toString(-1 * new Date().getTime());

    }

    public View getView(final int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        final View rowView=inflater.inflate(R.layout.channellistview, null,true);
        final TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase2 = FirebaseDatabase.getInstance().getReference();
        final TextView txtMessage = (TextView) rowView.findViewById(R.id.itemmessage);

        if (vNomeChannel.size()>position) {
            txtTitle.setText(vNomeChannel.get(position));
            if (vNomeChannel.get(position).equals("anonymous")) {
                mUsernameAnonymous = vNomeChannel.get(position);
            }
        }else{
            txtTitle.setText("...");
        }


        if (vLido.size()>position) {
            if (vLido.get(position).equals("on")) {
                txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
                txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
            } else {
                txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
                txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
            }
        }
        mDatabase.child("Channel").child(vChannel.get(position)).child("Count-" + mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            if (dataSnapshot.getValue().equals("on")) {
                                txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
                                txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
                            } else {
                                txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
                                txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Query qMessageChannel = mDatabase.child("messages").child(vChannel.get(position))
                .orderByKey().limitToLast(1);
        qMessageChannel.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot mdataSnapshot) {
                        String vpad = "0";
                        txtMessage.setText("");
                        for (DataSnapshot mnoteDataSnapshot : mdataSnapshot.getChildren()) {

                            for (DataSnapshot MessageDataSnapshot : mnoteDataSnapshot.getChildren()) {
                                if (MessageDataSnapshot.getKey().equals("text")) {
                                    if (MessageDataSnapshot.getValue().toString().length()>40) {
                                        txtMessage.setText(MessageDataSnapshot.getValue().toString().substring(0, 40) + "...");
                                    }else{
                                        txtMessage.setText(MessageDataSnapshot.getValue().toString());
                                    }
                                }else{
                                    txtMessage.setText("");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Channel", "getUser:onCancelled", databaseError.toException());
                    }
                });

        final CircleImageView imgChannel = (CircleImageView) rowView.findViewById(R.id.iconUser);

        if (vNomeChannel.size()>position) {
            String limpaNomeChannel = vNomeChannel.get(position);
            limpaNomeChannel = limpaNomeChannel.replace(".", "");
            limpaNomeChannel = limpaNomeChannel.replace("#", "");
            limpaNomeChannel = limpaNomeChannel.replace("$", "");
            limpaNomeChannel = limpaNomeChannel.replace("[", "");
            limpaNomeChannel = limpaNomeChannel.replace("]", "");

            mDatabase2.child("Channel").child(limpaNomeChannel).child("Count-" + mUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                if (dataSnapshot.getValue().equals("on")) {
                                    txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
                                    txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.BOLD);
                                } else {
                                    txtTitle.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
                                    txtMessage.setTypeface(txtTitle.getTypeface(), Typeface.NORMAL);
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

        }
        mDatabase.child("Channel").child(vChannel.get(position))
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot pdataSnapshot) {
                                try {
                                    String tipo = "I";
                                    for (final DataSnapshot pnoteDataSnapshot : pdataSnapshot.getChildren()) {
                                        if (pnoteDataSnapshot.getKey().equals("photo")) {
                                            tipo = "G";
                                            Glide.with(getContext())
                                                    .load(pnoteDataSnapshot.getValue().toString())
                                                    .into(imgChannel);
                                        }
                                    }
                                    if (tipo.equals("I")) {
                                        for (final DataSnapshot pnoteDataSnapshot : pdataSnapshot.getChildren()) {
                                            if (!pnoteDataSnapshot.getKey().equals(mUid) && pnoteDataSnapshot.getKey().length() < 30) {
                                                if (!pnoteDataSnapshot.getKey().equals("morder")) {
                                                    mDatabase.child("UserPhotos").child(pnoteDataSnapshot.getKey())
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot zdataSnapshot) {
                                                                    for (DataSnapshot userSnapshot : zdataSnapshot.getChildren()) {
                                                                        try {
                                                                            if (userSnapshot.getKey().equals("Default")) {
                                                                                Glide.with(getContext())
                                                                                        .load(userSnapshot.getValue().toString())
                                                                                        .into(imgChannel);
                                                                            } else {
                                                                                Glide.with(getContext())
                                                                                        .load("https://firebasestorage.googleapis.com/v0/b/boover-82fc3.appspot.com/o/Photos%2Fic_Boover_rounded_vazado.png?alt=media&token=0638a639-8cea-42aa-a1e7-a081f9778bb3")
                                                                                        .into(imgChannel);
                                                                            }
                                                                        } catch (IllegalArgumentException e){
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                    if (mUsernameAnonymous.equals("anonymous")) {
                                                        mDatabase.child("Users").child(pnoteDataSnapshot.getKey()).child("nome")
                                                                .addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.getValue() != null) {
                                                                            txtTitle.setText(dataSnapshot.getValue().toString());
                                                                            mDatabase2.child("Channel").child(vChannel.get(position))
                                                                                    .child(mUid)
                                                                                    .setValue(dataSnapshot.getValue().toString());

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {
                                                                        // Getting Post failed, log a message
                                                                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                                                        // ...
                                                                    }
                                                                });
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }catch (IllegalArgumentException e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("tag", "getUser:onCancelled", databaseError.toException());
                            }
                        });


        return rowView;
    }
}
