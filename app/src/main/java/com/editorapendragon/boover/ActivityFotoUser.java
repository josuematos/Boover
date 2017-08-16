package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class ActivityFotoUser extends AppCompatActivity implements View.OnClickListener {

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
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        setContentView(R.layout.frgfoto_user);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        try {
            if (mAuth.getCurrentUser().getUid() != null) {
                mUid = mAuth.getCurrentUser().getUid().toString();
            }

            btnUserImage = (Button) findViewById(R.id.btnImage);
            btnMakeDefault = (ImageButton) findViewById(R.id.btnMakeDefault);
            btnCancel = (ImageButton) findViewById(R.id.btnCancel);
            btnDelete = (ImageButton) findViewById(R.id.btnRemoveImage);
            btnFotoLike = (ImageButton) findViewById(R.id.ic_fotolike);
            TextView txtDefault = (TextView) findViewById(R.id.lblperfil);
            TextView txtRemover = (TextView) findViewById(R.id.lblremover);
            imgUser = (ImageView) findViewById(R.id.imgUser);

            btnMakeDefault.setVisibility(View.INVISIBLE);
            txtDefault.setVisibility(View.INVISIBLE);
            txtRemover.setVisibility(View.INVISIBLE);
            btnCancel.setVisibility(View.INVISIBLE);
            btnUserImage.setOnClickListener(this);
            vTipoUsuario = "0";
            btnFotoLike.setVisibility(View.INVISIBLE);
            btnUserImage.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeDialogCustom);
            builder.setTitle(getResources().getString(R.string.boas_vindas));
            builder.setIcon(R.drawable.ic_boover_rounded);
            builder.setMessage(getResources().getString(R.string.boas_vindas_msg));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            alerta = builder.create();
            alerta.show();
        } catch (NullPointerException e) {
            finish();
            Intent secondActivity = new Intent(this, LoginActivity.class);
            startActivity(secondActivity);
        }


    }

    @Override
    public void onClick(View v) {
        if (v == btnUserImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selecione_imagem)), CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            mProgress.setMessage(getResources().getString(R.string.atualizando_imagem));
            mProgress.show();
            Uri uri = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(
                        uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                finish();
            }

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] byteArray = stream.toByteArray();
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {

                e.printStackTrace();
                finish();
            }

            if (mAuth.getCurrentUser() != null) {
                StorageReference filepath = mStorage.child("Photos").child(mAuth.getCurrentUser().getUid().toString()).child(uri.getLastPathSegment());
                filepath.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        Globals.ImgUrl = taskSnapshot.getDownloadUrl();
                        Glide.with(getApplicationContext())
                                .load(downloadUri.toString())
                                .into(imgUser);
                        writeNewImage(Globals.ImgUrl.toString(), "Default");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.upload_finalizado), Toast.LENGTH_SHORT).show();
                        Intent secondActivity = new Intent(getApplication(), ActivityDetailUser.class);
                        startActivity(secondActivity);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.falha_imagem), Toast.LENGTH_SHORT).show();
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
        }else{
            key = mDatabase.child("UserPhotos").child(mUid).push().getKey();
        }
        result.put("/UserPhotos/"+mUid+"/"+key,url);
        mDatabase.updateChildren(result);
        mDatabase.child("Users").child(mUid).child("Default").setValue(url);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build();
        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.foto_perfl_atualizada), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
