package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CustomBlogListAdapter extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> vAuthorPost = new ArrayList<String>();
    private ArrayList<String> vTitlePost = new ArrayList<String>();
    private ArrayList<String> vPhotoPost = new ArrayList<String>();
    private ArrayList<String> vMessagePost = new ArrayList<String>();
    private ArrayList<String> vData = new ArrayList<String>();
    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<String> commentUid = new ArrayList<String>();
    private ArrayList<String> vKey = new ArrayList<String>();
    private ArrayList<String> vComment = new ArrayList<String>();
    private ArrayList<String> vstarCount = new ArrayList<String>();
    private ArrayList<String> vtotalComment = new ArrayList<String>();
    private ArrayList<String> vFotoComment = new ArrayList<String>();
    private ArrayList<String> vTipo = new ArrayList<String>();
    private ImageButton btnLike, btnTrash, btnComment, btnShareFace;
    private DatabaseReference mDatabase, postDatabase;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String mUid, mNome;
    private CircleImageView btnIconUser;
    private int vResenhas = 0;
    private int vPosts = 0;


    //private String morder;

    public CustomBlogListAdapter(Activity context, ArrayList<String> vAuthorPost, ArrayList<String> vTitlePost, ArrayList<String> vPhotoPost,
                                 ArrayList<String> vMessagePost, ArrayList<String> vData, ArrayList<String> vUid, ArrayList<String> vKey,
                                 ArrayList<String> vstarCount, ArrayList<String> vtotalComment, ArrayList<String> vTipo ) {
        super(context, R.layout.bloglistview, vAuthorPost);

        this.context=context;
        this.vAuthorPost=vAuthorPost;
        this.vTitlePost=vTitlePost;
        this.vMessagePost=vMessagePost;
        this.vPhotoPost=vPhotoPost;
        this.vData=vData;
        this.vUid=vUid;
        this.vKey=vKey;
        this.vstarCount=vstarCount;
        this.vtotalComment=vtotalComment;
        this.vTipo = vTipo;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        final View commentView = (View) inflater.inflate(R.layout.commentslistview, null);
        final ListView list = (ListView) convertView.findViewById(R.id.listView1);

        View rowView=inflater.inflate(R.layout.bloglistview, null,true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        postDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        mNome = mAuth.getCurrentUser().getDisplayName();

        vComment.clear();
        vFotoComment.clear();
        commentUid.clear();

        final TextView txtStars = (TextView) rowView.findViewById(R.id.txtStarCount);
        txtStars.setText(vstarCount.get(position));
        final TextView txtTotalComments = (TextView) rowView.findViewById(R.id.txtTotalComments);
        txtTotalComments.setText(vtotalComment.get(position));
        TextView txtAuthor = (TextView) rowView.findViewById(R.id.txtPostAutor);
        txtAuthor.setText(vData.get(position)+ " - "+vAuthorPost.get(position));
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtPostTitle);
        TextView txtMessage = (TextView) rowView.findViewById(R.id.txtPostMessage);
        if (vTitlePost.size()>position) {
            try {
                txtTitle.setText(vTitlePost.get(position));
            }catch (IndexOutOfBoundsException e){
                txtTitle.setText("");
            }
        }else{
            txtTitle.setText("");
        }

        if (vMessagePost.size()>position) {
            try {
                txtMessage.setText(vMessagePost.get(position));
            }catch (IndexOutOfBoundsException e){
                txtMessage.setText("");
            }
        }else{
            txtMessage.setText("");
        }

        btnLike = (ImageButton) rowView.findViewById(R.id.ic_fotolike);
        mDatabase.child("Likes").child(vUid.get(position)).child(mUid).child(vKey.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            if (dataSnapshot.getValue().toString().equals("1")) {
                                btnLike.setTag(1);
                                btnLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                            } else {
                                btnLike.setTag(0);
                                btnLike.setImageResource(R.drawable.ic_heart_vazado);
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

        ImageView imgChannel = (ImageView) rowView.findViewById(R.id.imgPost);
        WebView vdChannel = (WebView) rowView.findViewById(R.id.videoPost);
        if (vPhotoPost.size()>position) {
            if (vPhotoPost.get(position).contains("youtube")){
                Globals.vIntentFoto="1";
                imgChannel.setVisibility(View.INVISIBLE);
                String  link = (vPhotoPost.get(position));
                link = link.replace("youtube:","");
                String iframe = "<iframe width=\"340\" height=\"215\" src=\"https://www.youtube.com/embed/"+link+"\" frameborder=\"0\" allowfullscreen></iframe>";
                vdChannel.getSettings().setJavaScriptEnabled(true);
                vdChannel.getSettings().setLoadWithOverviewMode(true);
                vdChannel.setWebViewClient(new WebViewClient());
                vdChannel.setBackgroundColor(0x00000000);
                vdChannel.getSettings().setBuiltInZoomControls(true);
                vdChannel.loadData(iframe, "text/html", "UTF-8");

            } else if (vPhotoPost.get(position).contains("video")){
                imgChannel.setVisibility(View.INVISIBLE);
                vdChannel.setVisibility(View.VISIBLE);
                vdChannel.setLayoutParams(new RelativeLayout.LayoutParams(1140, 800));
                vdChannel.loadUrl(vPhotoPost.get(position));
            }else {
                    vdChannel.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                    vdChannel.loadUrl(vPhotoPost.get(position));
                    if (vPhotoPost.get(position).trim().length()>0) {
                        imgChannel.setLayoutParams(new RelativeLayout.LayoutParams(1140, 800));
                        vdChannel.setVisibility(View.INVISIBLE);
                        imgChannel.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .load(vPhotoPost.get(position))
                                .into(imgChannel);
                    }else{
                        vdChannel.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
                        imgChannel.setVisibility(View.INVISIBLE);
                        vdChannel.setVisibility(View.INVISIBLE);
                    }
                }
        }else{
            vdChannel.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            imgChannel.setVisibility(View.INVISIBLE);
            vdChannel.setVisibility(View.INVISIBLE);
        }


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vStars;
                vStars = Integer.parseInt(vstarCount.get(position));
                if (btnLike.getTag()!=null) {
                    if (btnLike.getTag().equals(0)) {
                        btnLike.setTag(1);
                        mDatabase.child("Likes").child(vUid.get(position)).child(mUid).child(vKey.get(position)).setValue("1");
                        vStars ++;
                        mDatabase.child("posts").child(vKey.get(position)).child("starCount").setValue(Integer.toString(vStars));
                        btnLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                    } else {
                        btnLike.setTag(0);
                        mDatabase.child("Likes").child(vUid.get(position)).child(mUid).child(vKey.get(position)).removeValue();
                        if (vStars>0) {
                            vStars--;
                        }
                        mDatabase.child("posts").child(vKey.get(position)).child("starCount").setValue(Integer.toString(vStars));
                        btnLike.setImageResource(R.drawable.ic_heart_vazado);

                    }
                } else {
                    btnLike.setTag(1);
                    mDatabase.child("Likes").child(vUid.get(position)).child(mUid).child(vKey.get(position)).setValue("1");
                    vStars ++;
                    mDatabase.child("posts").child(vKey.get(position)).child("starCount").setValue(Integer.toString(vStars));
                    btnLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                }
                txtStars.setText(Integer.toString(vStars));


               /* mDatabase.child("Likes").child(vUid.get(position)).child(mUid).child(vKey.get(position))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue()!=null) {
                                    if (dataSnapshot.getValue().toString().equals("1")) {
                                        btnLike.setTag(1);
                                        btnLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                                    } else {
                                        btnLike.setTag(0);
                                        btnLike.setImageResource(R.drawable.ic_heart_vazado);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });*/


            }
        });



        btnTrash = (ImageButton) rowView.findViewById(R.id.btnTrash);
        if (vUid.get(position).equals(mUid)){
            btnTrash.setVisibility(View.VISIBLE);
        } else {
            btnTrash.setVisibility(View.INVISIBLE);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager = CallbackManager.Factory.create();
        final ShareDialog shareDialog = new ShareDialog(context);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

                Log.e("1","1");

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        btnShareFace = (ImageButton) rowView.findViewById(R.id.ic_shareface);
        btnShareFace.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Globals.vIntentFoto="1";
                 if (ShareDialog.canShow(ShareLinkContent.class)) {

                     ShareLinkContent linkContent = new ShareLinkContent.Builder()
                             .setContentTitle(vTitlePost.get(position))
                             .setContentDescription(vMessagePost.get(position))
                             .setContentUrl(Uri.parse(vPhotoPost.get(position)))
                             .setShareHashtag(new ShareHashtag.Builder()
                                     .setHashtag("#boover")
                                     .build())
                             .build();
                     shareDialog.show(linkContent);
                 }

             }
        });

        btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                builder.setTitle(R.string.excluir_post);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_excluir_post);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        postDatabase.child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.getKey().toString().equals("resenhas")){
                                        vResenhas = Integer.parseInt(postSnapshot.getValue().toString());
                                    }
                                    if (postSnapshot.getKey().toString().equals("posts")){
                                        vPosts = Integer.parseInt(postSnapshot.getValue().toString());
                                    }
                                }

                                if (vTipo.get(position).equals("resenha")){
                                    vResenhas--;
                                    mDatabase.child("Users").child(mUid).child("resenhas").setValue(Integer.toString(vResenhas));
                                }
                                if (vTipo.get(position).equals("post")){
                                    vPosts--;
                                    mDatabase.child("Users").child(mUid).child("posts").setValue(Integer.toString(vPosts));
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        mDatabase.child("posts").child(vKey.get(position)).removeValue();
                    }
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alerta = builder.create();
                alerta.show();
            }
        });

        btnComment = (ImageButton) rowView.findViewById(R.id.btnComment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builderC = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                mDatabase.child("posts").child(vKey.get(position)).child("comments")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                vComment.clear();
                                vFotoComment.clear();
                                commentUid.clear();
                                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                    for (final DataSnapshot innerDataSnapshot : noteDataSnapshot.getChildren()) {
                                        if (innerDataSnapshot.getValue() != null) {
                                            mDatabase.child("UserPhotos").child(noteDataSnapshot.getKey().toString()).child("Default")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot UdataSnapshot) {
                                                        if (UdataSnapshot.getKey().equals("Default")){
                                                            if (UdataSnapshot.getValue()!=null) {
                                                                vComment.add(innerDataSnapshot.getValue().toString());
                                                                vFotoComment.add(UdataSnapshot.getValue().toString());
                                                                commentUid.add(noteDataSnapshot.getKey().toString());
                                                                Log.e("User",noteDataSnapshot.getKey().toString());
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
                                    }
                                }

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                            //CharSequence[] items = vComment.toArray(new CharSequence[vComment.size()]);
                                            CustomCommentsListAdapter adapterL = new CustomCommentsListAdapter(context, vComment, vFotoComment);
                                            list.setAdapter(adapterL);
                                            builderC.setIcon(R.drawable.ic_boover_rounded);
                                            builderC.setTitle("Comentários:");
                                            builderC.setSingleChoiceItems(adapterL, 0, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    // Do something with the selection
                                                    Bundle bundleMeetComment = new Bundle();
                                                    bundleMeetComment.putString("vUri", vFotoComment.get(arg1));
                                                    bundleMeetComment.putString("dUid", commentUid.get(arg1));

                                                    FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                                                    android.support.v4.app.FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                                    fFrag.setArguments(bundleMeetComment);
                                                    ft.add(R.id.frgbloogers_frame, fFrag, "FrgMeetDetailUser");
                                                    ft.addToBackStack(null);
                                                    ft.commit();
                                                    alerta.dismiss();
                                                }
                                            });

                                            alerta = builderC.create();
                                            alerta.show();
                                    }
                                }, 500);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });

                builderC.setPositiveButton("Novo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                        builder.setTitle(R.string.comentarios);

                        final EditText input = new EditText(getContext());
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        input.setWidth(1280);
                        input.setHeight(250);
                        input.setPadding(15,0,15,0);
                        input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                        input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                        builder.setIcon(R.drawable.ic_boover_rounded);
                        builder.setView(input);

                        builder.setPositiveButton(R.string.postar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newComment = input.getText().toString();
                                String vData, vTotal;

                                vTotal = Integer.toString(Integer.parseInt(vtotalComment.get(position))+1);
                                vData = Long.toString(-1 * new Date().getTime());
                                mDatabase.child("posts").child(vKey.get(position)).child("commentsCount").setValue(vTotal);
                                mDatabase.child("posts").child(vKey.get(position)).child("comments").child(mUid).child(vData).setValue(mNome+"\n"+newComment);
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
                        });
                        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                    }
                });
                builderC.setNegativeButton(R.string.voltar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alerta.cancel();
                    }
                });
            }
        });

        return rowView;
    }


}
