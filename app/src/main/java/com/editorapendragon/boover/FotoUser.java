package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class FotoUser extends Fragment implements View.OnClickListener {

    private ImageView imgUser;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private Button btnUserImage;
    private ImageButton btnMakeDefault;
    private ImageButton btnCancel;
    private ImageButton btnDelete, btnFotoLike;
    private String vTipoUsuario = null;
    private Uri vUriUsuario;
    private String mUid, vchaveurl, dUid;
    private TextView lblremove;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgfoto_user, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUid = mAuth.getCurrentUser().getUid().toString();

        btnUserImage = (Button) view.findViewById(R.id.btnImage);
        btnMakeDefault = (ImageButton) view.findViewById(R.id.btnMakeDefault);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnDelete = (ImageButton) view.findViewById(R.id.btnRemoveImage);
        lblremove = (TextView) view.findViewById(R.id.lblremover);
        btnFotoLike = (ImageButton) view.findViewById(R.id.ic_fotolike);
        imgUser = (ImageView) view.findViewById(R.id.imgUser);

        btnUserImage.setOnClickListener(this);
        btnMakeDefault.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        Bundle bundlefotouser = this.getArguments();
        if (bundlefotouser != null) {
            vTipoUsuario = bundlefotouser.getString("tipo", "0");
            String vurl = bundlefotouser.getString("uri", null);
            vchaveurl = bundlefotouser.getString("chaveuri", null);
            dUid = bundlefotouser.getString("dUid", null);

            if (vurl!=null){
                Globals.ImgUrl = Uri.parse(vurl);
                vUriUsuario = Uri.parse(vurl);
            }
        }else{
            vTipoUsuario="0";
        }

            if (vTipoUsuario.equals("1")) {
                if (vUriUsuario != null) {
                    Glide.with(getContext())
                            .load(vUriUsuario.toString())
                            .into(imgUser);
                }
                dUid = mUid;
                btnFotoLike.setVisibility(View.VISIBLE);
                btnUserImage.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                lblremove.setVisibility(View.VISIBLE);
                btnMakeDefault.setVisibility(View.VISIBLE);
            } else if (vTipoUsuario.equals("2")) {
                if (vUriUsuario != null) {
                    Glide.with(getContext())
                        .load(vUriUsuario.toString())
                        .into(imgUser);
                }
                btnFotoLike.setVisibility(View.INVISIBLE);
                btnUserImage.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                lblremove.setVisibility(View.INVISIBLE);
                btnMakeDefault.setVisibility(View.INVISIBLE);
            } else if (vTipoUsuario.equals("3")) {
                if (vUriUsuario != null) {
                    Glide.with(getContext())
                            .load(vUriUsuario.toString())
                            .into(imgUser);


                    mDatabase.child("Likes").child(dUid).child(mUid).child(vchaveurl)
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
                }
                btnFotoLike.setVisibility(View.VISIBLE);
                btnUserImage.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                lblremove.setVisibility(View.INVISIBLE);
                btnMakeDefault.setVisibility(View.INVISIBLE);
            } else {
                btnFotoLike.setVisibility(View.INVISIBLE);
                btnMakeDefault.setVisibility(View.VISIBLE);
                btnUserImage.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                lblremove.setVisibility(View.INVISIBLE);
                /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                        Glide.with(getContext())
                                .load(user.getPhotoUrl().toString())
                                .into(imgUser);
                }*/
            }

        btnFotoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnFotoLike.getTag()!=null) {
                    if (btnFotoLike.getTag().equals(0)) {
                        btnFotoLike.setTag(1);
                        btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                        mDatabase.child("Likes").child(dUid).child(mUid).child(vchaveurl).setValue("1");
                    } else {
                        btnFotoLike.setTag(0);
                        btnFotoLike.setImageResource(R.drawable.ic_heart_vazado);
                        mDatabase.child("Likes").child(dUid).child(mUid).child(vchaveurl).removeValue();
                    }
                } else {
                    btnFotoLike.setTag(1);
                    mDatabase.child("Likes").child(dUid).child(mUid).child(vchaveurl).setValue("1");
                    btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
        }
        if (v == btnDelete) {
            if (vchaveurl!=null) {
               mDatabase.child("UserPhotos").child(mUid).child(vchaveurl).removeValue();
               Toast.makeText(getContext(), getResources().getString(R.string.foto_apagada), Toast.LENGTH_SHORT).show();
               getFragmentManager().popBackStack();
            }
        }
        if(v == btnMakeDefault){
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(Globals.ImgUrl)
                        .build();
                mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.foto_perfl_atualizada), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                writeNewImage(Globals.ImgUrl.toString(), "Default");

        }
        if (v == btnUserImage) {
            Globals.vIntentFoto="1";
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selecione_imagem)), CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Globals.vIntentFoto="0";
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            mProgress.setMessage(getResources().getString(R.string.atualizando_imagem));
            mProgress.show();
            Uri uri = data.getData();
            if (mAuth.getCurrentUser() != null) {
                StorageReference filepath = mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child(uri.getLastPathSegment());
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        Globals.ImgUrl = taskSnapshot.getDownloadUrl();
                        Glide.with(getContext())
                                .load(downloadUri.toString())
                                .into(imgUser);
                        if (vTipoUsuario.equals("2")) {
                            writeNewImage(Globals.ImgUrl.toString(), "Default");
                        }
                        writeNewImage(Globals.ImgUrl.toString(), "");

                        Toast.makeText(getContext(), getResources().getString(R.string.upload_finalizado), Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getResources().getString(R.string.falha_imagem), Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                });
            }
        }

    }
    private void writeNewImage(String url, String chave) {
        HashMap<String, Object> result = new HashMap<>();
        String key;
        if (chave.length()>0){
            key = chave;
            mDatabase.child("Users").child(mUid).child(key).setValue(url);
        }else{
            key = mDatabase.child("UserPhotos").child(mUid).push().getKey();
        }
        result.put("/UserPhotos/"+mUid+"/"+key,url);
        mDatabase.updateChildren(result);
        mDatabase.child("Wallet").child(mUid).child("saldob")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double saldo = 0.00;
                        if (dataSnapshot.getValue()!=null) {
                            saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.1;
                        }else{
                            saldo = 0.10;
                        }
                        mDatabase.child("Wallet").child(mUid).child("saldob").setValue(saldo);
                        mDatabase.child("Wallet").child(mUid).child("extrato").child("fotos").child("valor")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Double saldo = 0.00;
                                        if (dataSnapshot.getValue()!=null) {
                                            saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                                        }else{
                                            saldo = 0.10;
                                        }
                                        try {
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("fotos").child("valor").setValue(saldo);
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("fotos").child("datasolicitacao")
                                                    .setValue(Long.toString(-1 * new Date().getTime()));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("fotos").child("status")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_fotos));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("fotos").child("tipo")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                        } catch (NullPointerException e){
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Toast.makeText(getContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
    }
}
