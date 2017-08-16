package com.editorapendragon.boover;

/**
 * Created by Josue on 12/03/2017.
 */



import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private ArrayList<String> vChaveurl;
    private LayoutInflater inflater;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, Tipo, dUid;
    Integer ContLike = 0;



    // constructor
    public FullScreenImageAdapter(Activity activity,
                                  ArrayList<String> imagePaths,
                                  ArrayList<String> vChaveurl,
                                  String Tipo, String dUid) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.vChaveurl   =  vChaveurl;
        this.Tipo = Tipo;
        this.dUid = dUid;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgDisplay;
        Button btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();


        TextView mPage = (TextView) viewLayout.findViewById(R.id.txtpage);
        final TextView txtLike = (TextView) viewLayout.findViewById(R.id.txtlike);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        mPage.setText("  "+Integer.toString(position+1)+" / "+Integer.toString(_imagePaths.size())+"  ");

        ImageButton btnMakeDefault = (ImageButton) viewLayout.findViewById(R.id.btnMakeDefault);
        ImageButton btnDelete = (ImageButton) viewLayout.findViewById(R.id.btnRemoveImage);
        final ImageButton btnFotoLike = (ImageButton) viewLayout.findViewById(R.id.ic_fotolike);
        mDatabase.child("Likes").child(dUid).child(mUid).child(vChaveurl.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            btnFotoLike.setTag(1);
                            btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                        } else {
                            btnFotoLike.setTag(0);
                            btnFotoLike.setImageResource(R.drawable.ic_heart_vazado);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });
        mDatabase.child("Likes").child(mUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ContLike = 0;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot fotoDataSnapshot : noteDataSnapshot.getChildren()) {
                                if (fotoDataSnapshot.getKey().equals(vChaveurl.get(position))){
                                    ContLike++;
                                }
                            }
                        }
                        if (Tipo.equals("1")) {
                            if (ContLike > 0) {
                                txtLike.setVisibility(View.VISIBLE);
                                btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                                txtLike.setText(Integer.toString(ContLike));
                            } else {
                                txtLike.setVisibility(View.INVISIBLE);
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


        if (Tipo.equals("1")) {
            if (ContLike>0){
                txtLike.setVisibility(View.VISIBLE);
                btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                txtLike.setText(Integer.toString(ContLike));
            }else{
                txtLike.setVisibility(View.INVISIBLE);
            }
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vChaveurl.get(position) != null) {
                        mDatabase.child("UserPhotos").child(mUid).child(vChaveurl.get(position)).removeValue();
                        Toast.makeText(_activity, _activity.getResources().getString(R.string.foto_apagada), Toast.LENGTH_SHORT).show();
                        vChaveurl.remove(position);
                        _imagePaths.remove(position);
                        _activity.recreate();
                    }

                }
            });
            btnMakeDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(Uri.parse(_imagePaths.get(position)))
                            .build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(_activity, _activity.getResources().getString(R.string.foto_perfl_atualizada), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    writeNewImage(_imagePaths.get(position), "Default");
                }
            });
        }else{
            btnDelete.setVisibility(View.INVISIBLE);
            btnMakeDefault.setVisibility(View.INVISIBLE);
            btnFotoLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnFotoLike.getTag()!=null) {
                        if (btnFotoLike.getTag().equals(0)) {
                            btnFotoLike.setTag(1);
                            btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                            mDatabase.child("Likes").child(dUid).child(mUid).child(vChaveurl.get(position)).setValue("1");
                        } else {
                            btnFotoLike.setTag(0);
                            btnFotoLike.setImageResource(R.drawable.ic_heart_vazado);
                            mDatabase.child("Likes").child(dUid).child(mUid).child(vChaveurl.get(position)).removeValue();
                        }
                    } else {
                        btnFotoLike.setTag(1);
                        mDatabase.child("Likes").child(dUid).child(mUid).child(vChaveurl.get(position)).setValue("1");
                        btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                    }
                }
            });

        }

        Glide.with(_activity)
                .load(_imagePaths.get(position))
                .into(imgDisplay);
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
                Globals.vIntentFoto="0";
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    private void writeNewImage(String url, String chave) {
        HashMap<String, Object> result = new HashMap<>();
        String key;
        if (chave.length()>0){
            key = chave;
        }else{
            key = mDatabase.child("UserPhotos").child(mUid).push().getKey();
        }
        result.put("/UserPhotos/"+mUid+"/"+key,url);
        mDatabase.child("Users").child(mUid).child("Default").setValue(url);
        mDatabase.updateChildren(result);
        Toast.makeText(_activity, _activity.getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
    }
}