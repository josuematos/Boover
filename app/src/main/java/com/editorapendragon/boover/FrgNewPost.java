package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class FrgNewPost extends Fragment implements View.OnClickListener {

    private ImageView imgUser;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,postDatabase;
    private DatabaseReference mDatabaseStar;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    private Button btnUserImage, btnYouTube;
    private TextView txtOu, txtDelete;
    private ImageButton btnMakeDefault;
    private ImageButton btnCancel;
    private ImageButton btnDelete, btnFotoLike, btnSave;
    private String vTipoUsuario = null;
    private Uri vUriUsuario;
    private String mUid, vNome, vchaveurl, dUid, vbook = "", mNotific, mMsg;
    private String key = "";
    private EditText vTitle, vMessage;
    private int vResenhas = 0;
    private int vPosts = 0;
    private int vVideos = 0;
    private Integer notCounter = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgnew_post, container, false);
        Globals.ImgUrl=null;
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        postDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUid = mAuth.getCurrentUser().getUid().toString();
        mDatabase.child("Users").child(mUid).child("nome")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            vNome = dataSnapshot.getValue().toString();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        txtOu = (TextView) view.findViewById(R.id.lblou);
        btnUserImage = (Button) view.findViewById(R.id.btnImage);
        btnYouTube = (Button) view.findViewById(R.id.btnImageYouTube);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnDelete = (ImageButton) view.findViewById(R.id.btnRemovePost);
        txtDelete = (TextView) view.findViewById(R.id.lblremover);
        btnSave = (ImageButton) view.findViewById(R.id.btnSavePost);
        btnFotoLike = (ImageButton) view.findViewById(R.id.ic_fotolike);
        imgUser = (ImageView) view.findViewById(R.id.imgPost);
        vTitle = (EditText) view.findViewById(R.id.txtPostTitle);
        vMessage = (EditText) view.findViewById(R.id.txtPostMessage);

        btnUserImage.setOnClickListener(this);
        btnYouTube.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        Bundle bundleNewPost = this.getArguments();
        if (bundleNewPost != null) {
            vTipoUsuario = bundleNewPost.getString("tipo", "0");
            String vurl = bundleNewPost.getString("uri", null);
            vchaveurl = bundleNewPost.getString("chaveuri", null);
            String vtitle = bundleNewPost.getString("vtitle", null);
            vbook = bundleNewPost.getString("vbook", null);
            dUid = bundleNewPost.getString("dUid", null);
            if (vtitle!=null){
                vTitle.setText(vtitle);
            }
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
                txtOu.setVisibility(View.INVISIBLE);
                btnYouTube.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
                txtDelete.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            } else if (vTipoUsuario.equals("2")) {
                btnFotoLike.setVisibility(View.INVISIBLE);
                btnUserImage.setVisibility(View.VISIBLE);
                txtOu.setVisibility(View.VISIBLE);
                btnYouTube.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                txtDelete.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
            } else if (vTipoUsuario.equals("3")) {
                btnFotoLike.setVisibility(View.VISIBLE);
                btnUserImage.setVisibility(View.INVISIBLE);
                txtOu.setVisibility(View.INVISIBLE);
                btnYouTube.setVisibility(View.INVISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                txtDelete.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.INVISIBLE);
            } else if (vTipoUsuario.equals("4")) {
                if (vUriUsuario != null) {
                    Glide.with(getContext())
                            .load(vUriUsuario.toString())
                            .into(imgUser);
                }
                dUid = mUid;
                btnFotoLike.setVisibility(View.INVISIBLE);
                btnUserImage.setVisibility(View.VISIBLE);
                txtOu.setVisibility(View.VISIBLE);
                btnYouTube.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                txtDelete.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            } else {
                dUid = mUid;
                btnFotoLike.setVisibility(View.INVISIBLE);
                btnSave.setVisibility(View.VISIBLE);
                btnUserImage.setVisibility(View.VISIBLE);
                txtOu.setVisibility(View.VISIBLE);
                btnYouTube.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.INVISIBLE);
                txtDelete.setVisibility(View.INVISIBLE);
            }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            getFragmentManager().popBackStack(null,getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
        if (v == btnSave) {
            if (Globals.ImgUrl==null && vTitle.getText().length()==0 && vMessage.getText().length()==0) {
                Toast.makeText(getContext(), getResources().getString(R.string.dados_invalidos), Toast.LENGTH_SHORT).show();
            } else {
                String tipo = "post";
                if (vTipoUsuario.equals("4")) {
                    tipo = "resenha";
                }
                if (vTitle.getText().length() == 0) {
                    vTitle.setText("");
                }
                if (vMessage.getText().length() == 0) {
                    vMessage.setText("");
                }
                String link = "";
                if (Globals.ImgUrl!=null) {
                    writeNewImage(Globals.ImgUrl.toString(), "");
                    link = Globals.ImgUrl.toString();
                }
                mMsg = vNome + " " + getResources().getString(R.string.post_criado_boover);
                writeNewPost(mUid, vNome, vTitle.getText().toString(), vMessage.getText().toString(), link, tipo, vbook);
                getFragmentManager().popBackStack();
            }
        }
        if (v == btnUserImage) {
            Globals.vIntentFoto="1";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (Build.VERSION.SDK_INT < 19) {
                intent.setType("image/* video/*");
            } else {
                intent.setType("*/*");
            }
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selecione_imagem)), CAMERA_REQUEST_CODE);

        }
        if (v == btnYouTube) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
            builder.setTitle(getResources().getString(R.string.digite_id_youtube));

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
            input.setWidth(1280);
            input.setHeight(150);
            input.setPadding(15,0,15,0);
            input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
            input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
            builder.setView(input);
            builder.setIcon(R.drawable.ic_boover_rounded);

            builder.setPositiveButton(getResources().getString(R.string.adicionar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String link = input.getText().toString();
                    if (link.contains("youtu.be") || link.contains("youtube") ||  link.contains("embed")) {
                        Toast.makeText(getContext(), getResources().getString(R.string.link_errado_youtube), Toast.LENGTH_SHORT).show();
                    }else {
                        AlertDialog.Builder builderYou = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                        builderYou.setTitle(getResources().getString(R.string.verifica_youtube));

                        final WebView vdChannel = new WebView(getActivity());
                        String iframe = "<iframe width=\"340\" height=\"215\" src=\"https://www.youtube.com/embed/" + link + "\" frameborder=\"0\" allowfullscreen></iframe>";
                        vdChannel.getSettings().setJavaScriptEnabled(true);
                        vdChannel.getSettings().setLoadWithOverviewMode(true);
                        vdChannel.setWebViewClient(new WebViewClient());
                        vdChannel.setBackgroundColor(0x00000000);
                        vdChannel.getSettings().setBuiltInZoomControls(true);
                        vdChannel.loadData(iframe, "text/html", "UTF-8");
                        builderYou.setView(vdChannel);
                        builderYou.setIcon(R.drawable.ic_boover_rounded);
                        builderYou.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Globals.ImgUrl = Uri.parse("youtube:" + input.getText().toString());
                                Toast.makeText(getContext(), getResources().getString(R.string.link_youtube_salvo), Toast.LENGTH_SHORT).show();
                                Glide.with(getContext())
                                        .load(R.drawable.ic_youtube)
                                        .into(imgUser);
                            }
                        });
                        builderYou.setNegativeButton(getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builderYou.show();
                    }

                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
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
                        Toast.makeText(getContext(), getResources().getString(R.string.upload_finalizado), Toast.LENGTH_SHORT).show();
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

    private void writeNewPost(final String userId, String username, String title, String body, final String imagekey, final String tipo, String book ) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        if (userId!=null && username!=null && title!=null) {
            String key = mDatabase.child("posts").push().getKey();
            Post post = new Post(userId, username, title, body, imagekey, tipo, book);
            Map<String, Object> postValues = post.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postValues);
            mDatabase.updateChildren(childUpdates);

            postDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getKey().toString().equals("resenhas")) {
                            vResenhas = Integer.parseInt(postSnapshot.getValue().toString());
                        }
                        if (postSnapshot.getKey().toString().equals("posts")) {
                            vPosts = Integer.parseInt(postSnapshot.getValue().toString());
                        }
                        if (postSnapshot.getKey().toString().equals("videos")) {
                            vVideos = Integer.parseInt(postSnapshot.getValue().toString());
                        }
                    }
                    if (tipo.equals("resenha")) {
                        vResenhas++;
                        mDatabase.child("Users").child(userId).child("resenhas").setValue(Integer.toString(vResenhas));
                    }
                    if (tipo.equals("post")) {
                        vPosts++;
                        mDatabase.child("Users").child(userId).child("posts").setValue(Integer.toString(vPosts));
                        if (imagekey.contains("youtube") || imagekey.contains("video")) {
                            vVideos++;
                            mDatabase.child("Users").child(userId).child("videos").setValue(Integer.toString(vVideos));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDatabase.child("Contacts").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                notCounter = 0;
                                mDatabase.child("Notifications").child(noteDataSnapshot.getKey().toString()).child("notCounter")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    notCounter = Integer.parseInt(dataSnapshot.getValue().toString());
                                                } else {
                                                    notCounter = 0;
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Getting Post failed, log a message
                                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                                // ...
                                            }
                                        });
                                mDatabase.child("Notifications").child(noteDataSnapshot.getKey().toString()).child(userId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    mNotific = dataSnapshot.getValue().toString();
                                                } else {
                                                    mNotific = "";
                                                }
                                                notCounter++;
                                                mDatabase.child("Notifications").child(noteDataSnapshot.getKey().toString()).child("notCount").setValue(Integer.toString(notCounter));
                                                mDatabase.child("Notifications").child(noteDataSnapshot.getKey().toString()).child(userId).setValue(mMsg + "\n" + mNotific);
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
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            mDatabase.child("Wallet").child(mUid).child("saldob")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Double saldo = 0.00;
                            if (dataSnapshot.getValue()!=null) {
                                saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                            }else{
                                saldo = 0.10;
                            }
                            mDatabase.child("Wallet").child(mUid).child("saldob").setValue(saldo);
                            mDatabase.child("Wallet").child(mUid).child("extrato").child("posts").child("valor")
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
                                                        .child("posts").child("valor").setValue(saldo);
                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                        .child("posts").child("datasolicitacao")
                                                        .setValue(Long.toString(-1 * new Date().getTime()));
                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                        .child("posts").child("status")
                                                        .setValue(getContext().getResources().getString(R.string.status_extrato_posts).toString());
                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                        .child("posts").child("tipo")
                                                        .setValue(getContext().getResources().getString(R.string.status_extrato_credito).toString());

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

        }
    }

    private void writeNewImage(String url, String chave) {
        HashMap<String, Object> result = new HashMap<>();

        if (chave.length()>0){
            key = chave;
        }else{
            key = mDatabase.child("UserPhotos").child(mUid).push().getKey();
        }
        result.put("/UserPhotos/"+mUid+"/"+key,url);
        mDatabase.updateChildren(result);
        Toast.makeText(getContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
    }

}
