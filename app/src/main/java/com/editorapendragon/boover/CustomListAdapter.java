package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, visCounter;

    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.listview, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        try {
            View rowView = inflater.inflate(R.layout.listview, null, true);

            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            final TextView txtvisCounter = (TextView) rowView.findViewById(R.id.txtvisCounter);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

            try {
                txtTitle.setText(itemname[position]);
                imageView.setImageResource(imgid[position]);
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mAuth = FirebaseAuth.getInstance();
            mUid = mAuth.getCurrentUser().getUid().toString();

            if (position == 4) {
                mDatabase.child("Visualizations").child(mUid).child("visCounter")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    visCounter = dataSnapshot.getValue().toString();
                                    if (!visCounter.equals("0")) {
                                        txtvisCounter.setVisibility(View.VISIBLE);
                                        txtvisCounter.setText("   " + visCounter + "   ");
                                    } else {
                                        txtvisCounter.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    visCounter = "0";
                                    txtvisCounter.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });

                if (visCounter == null) {
                    txtvisCounter.setVisibility(View.INVISIBLE);
                } else {
                    if (!visCounter.equals("0")) {
                        txtvisCounter.setVisibility(View.VISIBLE);
                        txtvisCounter.setText("   " + visCounter + "   ");
                    }
                }
            } else if (position == 3) {

                mDatabase.child("Notifications").child(mUid).child("notCount")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    visCounter = dataSnapshot.getValue().toString();
                                    if (!visCounter.equals("0")) {
                                        txtvisCounter.setVisibility(View.VISIBLE);
                                        txtvisCounter.setText("   " + visCounter + "   ");
                                    } else {
                                        txtvisCounter.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    visCounter = "0";
                                    txtvisCounter.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });
                if (visCounter == null) {
                    txtvisCounter.setVisibility(View.INVISIBLE);
                } else {
                    if (!visCounter.equals("0")) {
                        txtvisCounter.setVisibility(View.VISIBLE);
                        txtvisCounter.setText("   " + visCounter + "   ");
                    }
                }
            } else {
                txtvisCounter.setVisibility(View.INVISIBLE);
            }
            return rowView;
        } catch (OutOfMemoryError e){
            e.printStackTrace();
            return null;
        }


    }
}
