package com.editorapendragon.boover;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgMeetDetailUser extends Fragment {

    private static final String TAG = "FrgMeetDetailuser";
    private ImageButton btnChat, btnBack, btndUserPhotos, btnUserShelf, btnUserVideos;
    private ImageButton imgFotoUser;
    private ImageButton btnFotoLike, btnyoutube;
    private ImageButton btnAdduser, btnPosts, btnResenhas, btnBlockuser;
    private ImageView imageStatus;
    private TextView nomeUser,vInteresse,vIdade,vProcurando, vBookGenre, vBookReaded;
    private TextView vLivrosVendidos, lblmensagem, lblestante, lblposts, lblresenhas, lblvideos, lblfotos;
    private String dUid, vNome, vChannel, vUri, mUid, vStatus, vSexo, vLocalizacao, vtIdade, vAno, vMes, vDia,  visCounter;
    private String prefChat, prefFoto, prefPost, prefPerfil, mNotific, vNomeUser, prefVideo;
    Boolean vAmigo=false;
    private double dlat, dlong = 0.00000;
    private Float bStars = 0.0f, countStars = 0.0f;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private AlertDialog alerta;
    private Integer notCounter = 0, commentCount = 0;
    private ArrayList<String> vtcommentsUid = new ArrayList<String>();
    private ArrayList<String> vtcommentsText = new ArrayList<String>();
    private ArrayList<String> vtcommentsStar = new ArrayList<String>();
    private ArrayList<String> vtcommentsData = new ArrayList<String>();
    private ListView list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgmeetdetailsuser_fragment, container, false);

        imgFotoUser = (ImageButton) view.findViewById(R.id.ic_meetfotouser);
        imageStatus = (ImageView) view.findViewById(R.id.ic_onoff);
        nomeUser = (TextView) view.findViewById(R.id.txtNomeUser);
        vIdade = (TextView) view.findViewById(R.id.txtIdade);
        vInteresse = (TextView) view.findViewById(R.id.txtInteresse);
        vBookGenre = (TextView) view.findViewById(R.id.txtBookGenero);
        vBookReaded = (TextView) view.findViewById(R.id.txtBooks);
        vProcurando = (TextView) view.findViewById(R.id.txtProcurando);
        lblmensagem = (TextView) view.findViewById(R.id.lblmensagem);
        lblestante = (TextView) view.findViewById(R.id.lblestante);
        lblposts = (TextView) view.findViewById(R.id.lblposts);
        lblresenhas = (TextView) view.findViewById(R.id.lblresenhas);
        lblvideos = (TextView) view.findViewById(R.id.lblvideos);
        lblfotos = (TextView) view.findViewById(R.id.lblfotos);

        vLivrosVendidos = (TextView) view.findViewById(R.id.txtLivrosVendidos);
        TextView txtreputation = (TextView) view.findViewById(R.id.txtreputation);
        txtreputation.setText(getContext().getResources().getString(R.string.reputation)+": ");

        btnBack = (ImageButton) view.findViewById(R.id.ic_switch);
        btnPosts = (ImageButton) view.findViewById(R.id.ic_posts_user);
        btnResenhas = (ImageButton) view.findViewById(R.id.ic_details_user);
        btnFotoLike = (ImageButton) view.findViewById(R.id.ic_fotolike);
        btnAdduser = (ImageButton) view.findViewById(R.id.ic_adduser);
        btnBlockuser = (ImageButton) view.findViewById(R.id.ic_blockuser);
        btnChat = (ImageButton) view.findViewById(R.id.ic_messageuser);
        btnUserShelf = (ImageButton) view.findViewById(R.id.ic_boovershelf_user);
        btndUserPhotos = (ImageButton) view.findViewById(R.id.ic_gallery);
        btnUserVideos = (ImageButton) view.findViewById(R.id.ic_video);
        btnyoutube = (ImageButton) view.findViewById(R.id.ic_youtube);

        list = (ListView)  view.findViewById (R.id.lstViewComentarios);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid().toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        btnChat.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           Bundle bundleMeet = new Bundle();
                                           bundleMeet.putString("dUid", dUid);
                                           bundleMeet.putString("vNome", vNome);
                                           bundleMeet.putString("vChannel", vChannel);
                                           bundleMeet.putString("vPhoto",vUri);

                                           FrgBooverChat fFrag = new FrgBooverChat();
                                           FragmentTransaction ft = getFragmentManager().beginTransaction();
                                           fFrag.setArguments(bundleMeet);
                                           ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgBooverChat");
                                           ft.addToBackStack(null);
                                           ft.commit();

                                           //((MainActivity) getActivity()).mPager.setCurrentItem(1);

                                       }
                                   });
        btnBack.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                              getFragmentManager().popBackStack();
                                       }
                                   });

        btnPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleMeet = new Bundle();
                bundleMeet.putString("dUid", dUid);
                bundleMeet.putString("tipo", "3");
                FrgBlooger fFrag = new FrgBlooger();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMeet);
                ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgBloogerUserPosts");
                ft.addToBackStack("FrgBloogerUserPosts");
                ft.commit();
            }
        });

        btnyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.vIntentFoto="1";
                Intent secondActivity = new Intent(getActivity(), ActivityVideoList.class);
                secondActivity.putExtra("dUid", dUid);
                startActivity(secondActivity);

            }
        });


        btnUserShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),getResources().getString(R.string.estante_de)+" "+ vNome, Toast.LENGTH_SHORT).show();
                Bundle bundleMeet = new Bundle();
                bundleMeet.putString("dUid", dUid);
                bundleMeet.putString("vNome", vNome);
                bundleMeet.putString("vBack", "1");
                FrgBooverMyShelf fFrag = new FrgBooverMyShelf();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMeet);
                ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgBooverMyShelf");
                ft.addToBackStack("FrgBooverMyShelf");
                ft.commit();
            }
        });

        btnResenhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleMeet = new Bundle();
                bundleMeet.putString("dUid", dUid);
                bundleMeet.putString("tipo", "4");
                FrgBlooger fFrag = new FrgBlooger();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMeet);
                ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgBloogerUserResenhas");
                ft.addToBackStack("FrgBloogerUserResenhas");
                ft.commit();
            }
        });


        btnFotoLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnFotoLike.getTag()!=null) {
                    if (btnFotoLike.getTag().equals(0)) {
                        btnFotoLike.setTag(1);
                        btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                        mDatabase.child("Likes").child(dUid).child(mUid).child("Default").setValue("1");
                        notCounter++;
                        mDatabase.child("Notifications").child(dUid).child("notCount").setValue(Integer.toString(notCounter));
                        mDatabase.child("Notifications").child(dUid).child(mUid).setValue(getResources().getString(R.string.gostou_foto)+"\n"+mNotific);
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
                                        mDatabase.child("Wallet").child(mUid).child("extrato").child("likes").child("valor")
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
                                                                    .child("likes").child("valor").setValue(saldo);
                                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                    .child("likes").child("datasolicitacao")
                                                                    .setValue(Long.toString(-1 * new Date().getTime()));
                                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                    .child("likes").child("status")
                                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_likes));
                                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                    .child("likes").child("tipo")
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
                    } else {
                        btnFotoLike.setTag(0);
                        btnFotoLike.setImageResource(R.drawable.ic_heart_vazado);
                        mDatabase.child("Likes").child(dUid).child(mUid).child("Default").removeValue();
                    }
                } else {
                    btnFotoLike.setTag(1);
                    mDatabase.child("Likes").child(dUid).child(mUid).child("Default").setValue("1");
                    btnFotoLike.setImageResource(R.drawable.ic_heart_vazado_selecionado);
                }
            }
        });

        btnAdduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle("Amigos");
                builder.setIcon(R.drawable.ic_boover_rounded);
                if (btnAdduser.getTag()!=null) {
                    if (btnAdduser.getTag().equals(0)) {
                        builder.setMessage(getResources().getString(R.string.tem_certeza_adc_usuario));
                    } else {
                        builder.setMessage(getResources().getString(R.string.tem_certeza_desfazer_amizade));
                    }
                } else {
                    builder.setMessage(getResources().getString(R.string.tem_certeza_adc_usuario));
                }
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (btnAdduser.getTag()!=null) {
                            if (btnAdduser.getTag().equals(0)) {
                                btnAdduser.setTag(1);
                                btnAdduser.setImageResource(R.drawable.ic_delete_user);
                                mDatabase.child("Invitations").child(dUid).child(mUid).setValue(vNome);
                                notCounter++;
                                mDatabase.child("Notifications").child(dUid).child("notCount").setValue(Integer.toString(notCounter));
                                mDatabase.child("Notifications").child(dUid).child(mUid).setValue(getResources().getString(R.string.pedido_amizade)+"\n"+mNotific);
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
                                                mDatabase.child("Wallet").child(mUid).child("extrato").child("convites").child("valor")
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
                                                                            .child("convites").child("valor").setValue(saldo);
                                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                            .child("convites").child("datasolicitacao")
                                                                            .setValue(Long.toString(-1 * new Date().getTime()));
                                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                            .child("convites").child("status")
                                                                            .setValue(getContext().getResources().getString(R.string.status_extrato_convites));
                                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                            .child("convites").child("tipo")
                                                                            .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                                                } catch (NullPointerException e) {
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

                                Toast.makeText(getActivity(),getResources().getString(R.string.pedido_amizade), Toast.LENGTH_SHORT).show();
                            } else {
                                btnAdduser.setTag(0);
                                btnAdduser.setImageResource(R.drawable.ic_adduser);
                                mDatabase.child("Contacts").child(mUid).child(dUid).removeValue();
                                mDatabase.child("Contacts").child(dUid).child(mUid).removeValue();
                                mDatabase.child("Invitations").child(dUid).child(mUid).removeValue();
                                notCounter++;
                                mDatabase.child("Notifications").child(dUid).child("notCount").setValue(Integer.toString(notCounter));
                                mDatabase.child("Notifications").child(dUid).child(mUid).setValue(getResources().getString(R.string.amizade_desfeita)+"\n"+mNotific);

                                Toast.makeText(getActivity(),getResources().getString(R.string.amizade_desfeita), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            btnAdduser.setTag(1);
                            mDatabase.child("Invitations").child(dUid).child(mUid).setValue(vNome);
                            btnAdduser.setImageResource(R.drawable.ic_delete_user);
                            Toast.makeText(getActivity(),getResources().getString(R.string.pedido_amizade), Toast.LENGTH_SHORT).show();
                            notCounter++;
                            mDatabase.child("Notifications").child(dUid).child("notCount").setValue(Integer.toString(notCounter));
                            mDatabase.child("Notifications").child(dUid).child(mUid).setValue(getResources().getString(R.string.pedido_amizade)+"\n"+mNotific);
                            mDatabase.child("Wallet").child(mUid).child("saldob")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Double saldo = 0.00;
                                            if (dataSnapshot.getValue()!=null) {
                                                saldo = Double.parseDouble(dataSnapshot.getValue().toString())+1;
                                            }else{
                                                saldo = 1.00;
                                            }
                                            mDatabase.child("Wallet").child(mUid).child("saldob").setValue(saldo);
                                            mDatabase.child("Wallet").child(mUid).child("extrato").child("convites").child("valor")
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
                                                                        .child("convites").child("valor").setValue(saldo);
                                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                        .child("convites").child("datasolicitacao")
                                                                        .setValue(Long.toString(-1 * new Date().getTime()));
                                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                        .child("convites").child("status")
                                                                        .setValue(getContext().getResources().getString(R.string.status_extrato_convites));
                                                                mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                        .child("convites").child("tipo")
                                                                        .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                                            } catch (NullPointerException e ) {
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
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alerta.dismiss();
                    }
                });
                alerta = builder.create();
                alerta.show();

            }
        });
        btnBlockuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.bloquear_usuario));
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(getResources().getString(R.string.tem_certeza_bloquear_usuario));
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (btnBlockuser.getTag()!=null) {
                            if (btnBlockuser.getTag().equals(0)) {
                                btnBlockuser.setTag(1);
                                btnBlockuser.setVisibility(View.INVISIBLE);
                                mDatabase.child("Block").child(mUid).child(dUid).setValue(vNome);
                                Toast.makeText(getActivity(),getResources().getString(R.string.usuario_bloqueado), Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            } else {
                                btnBlockuser.setTag(0);
                                btnBlockuser.setImageResource(R.drawable.ic_block_user);
                                mDatabase.child("Block").child(mUid).child(dUid).removeValue();
                                //setValue("0");
                            }
                        } else {
                            btnBlockuser.setTag(1);
                            mDatabase.child("Block").child(mUid).child(dUid).setValue(vNome);
                            btnBlockuser.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(),getResources().getString(R.string.usuario_bloqueado), Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                        }

                    }
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alerta.dismiss();
                    }
                });
                alerta = builder.create();
                alerta.show();
            }
        });

        btndUserPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundledMeet = new Bundle();
                bundledMeet.putString("dUid", dUid);
                bundledMeet.putString("vNome", vNome);
                bundledMeet.putString("vChannel", vChannel);
                bundledMeet.putString("videoimage","image");
                FrgDestUserPhotos fFrag = new FrgDestUserPhotos();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fFrag.setArguments(bundledMeet);
                ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgDestUserPhotos");
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        btnUserVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundledMeet = new Bundle();
                bundledMeet.putString("dUid", dUid);
                bundledMeet.putString("vNome", vNome);
                bundledMeet.putString("vChannel", vChannel);
                bundledMeet.putString("videoimage","video");
                FrgDestUserPhotos fFrag = new FrgDestUserPhotos();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fFrag.setArguments(bundledMeet);
                ft.add( R.id.frgmeetdetailuser_frame, fFrag, "FrgDestUserPhotos");
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        Bundle bundlemeetdetailuser = this.getArguments();
        if (bundlemeetdetailuser != null) {
            dUid = bundlemeetdetailuser.getString("dUid", "0");
            vNome = bundlemeetdetailuser.getString("vNome", "0");
            vChannel = bundlemeetdetailuser.getString("vChannel", "0");
            vUri = bundlemeetdetailuser.getString("vUri", "");
            vStatus = bundlemeetdetailuser.getString("vStatus", "0");
            if (dUid.equals(mUid)){
                btnBlockuser.setVisibility(View.INVISIBLE);
                btnAdduser.setVisibility(View.INVISIBLE);
                imageStatus.setVisibility(View.INVISIBLE);
                nomeUser.setVisibility(View.INVISIBLE);
                btnyoutube.setVisibility(View.INVISIBLE);
                TextView lblblock = (TextView) view.findViewById(R.id.lblblock);
                TextView lbladc = (TextView) view.findViewById(R.id.lbladc);
                lblblock.setVisibility(View.INVISIBLE);
                lbladc.setVisibility(View.INVISIBLE);
            }
            if (vUri.length()>0) {
                Glide.with(getContext())
                        .load(vUri)
                        .into(imgFotoUser);
            }else{
                mDatabase.child("Users").child(dUid).child("Default")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot vnoteSnapshot) {
                                if (vnoteSnapshot.getValue()!=null) {
                                    Glide.with(getContext())
                                            .load(vnoteSnapshot.getValue().toString())
                                            .into(imgFotoUser);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("foto", "getUser:onCancelled", databaseError.toException());
                            }
                        });
            }
            nomeUser.setText(vNome);

            visCounter = "0";
            mDatabase.child("Visualizations").child(dUid).child("visCounter")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                visCounter = dataSnapshot.getValue().toString();
                            } else {
                                visCounter = "0";
                            }
                            mDatabase.child("Visualizations").child(dUid).child("visCounter").setValue(Integer.parseInt(visCounter)+1);
                            mDatabase.child("Visualizations").child(dUid).child(mUid).setValue(Long.toString(-1 * new Date().getTime()).toString());
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            notCounter = 0;
            mDatabase.child("Notifications").child(dUid).child("notCounter")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
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
            mDatabase.child("Notifications").child(dUid).child(mUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                mNotific = dataSnapshot.getValue().toString();
                            } else {
                                mNotific = "";
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            mDatabase.child("Likes").child(dUid).child(mUid).child("Default")
                    .addValueEventListener(new ValueEventListener() {
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

            mDatabase.child("Contacts").child(mUid).child(dUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                btnAdduser.setTag(1);
                                btnAdduser.setImageResource(R.drawable.ic_delete_user);
                                vAmigo = true;
                            } else {
                                btnAdduser.setTag(0);
                                btnAdduser.setImageResource(R.drawable.ic_adduser);
                                vAmigo = false;
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            mDatabase.child("Block").child(mUid).child(dUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                btnBlockuser.setTag(1);
                                btnBlockuser.setVisibility(View.INVISIBLE);
                                imgFotoUser.setImageResource(R.drawable.ic_block_user);
                                nomeUser.setText(getResources().getString(R.string.usuario_bloqueado));
                                btnPosts.setVisibility(View.INVISIBLE);
                                btnResenhas.setVisibility(View.INVISIBLE);
                                btnFotoLike.setVisibility(View.INVISIBLE);
                                btnAdduser.setVisibility(View.INVISIBLE);
                                btnUserShelf.setVisibility(View.INVISIBLE);
                                btnChat.setVisibility(View.INVISIBLE);
                                btndUserPhotos.setVisibility(View.INVISIBLE);
                                btnUserVideos.setVisibility(View.INVISIBLE);

                            } else {
                                btnBlockuser.setTag(0);
                                btnBlockuser.setImageResource(R.drawable.ic_block_user);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            mDatabase.child("Books").child(dUid)
                    .addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot xdataSnapshot) {
                                    int i = 0;
                                    for (final DataSnapshot noteDataSnapshot : xdataSnapshot.getChildren()) {
                                        i++;
                                    }
                                    try {
                                        vBookReaded.setText(getResources().getString(R.string.book_readed) + " " + Integer.toString(i) + ".");
                                    } catch (IllegalStateException e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
            mDatabase.child("Users").child(dUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                    if (noteDataSnapshot.getKey().toString().equals("interesse")) {
                                        vInteresse.setText(getResources().getString(R.string.interesse) + " " + noteDataSnapshot.getValue().toString());
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("procurando")) {
                                        vProcurando.setText(getResources().getString(R.string.procurando) + " " + noteDataSnapshot.getValue().toString());
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("sexo")) {
                                        vSexo = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("Day")) {
                                        vDia = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("Month")) {
                                        vMes = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("Year")) {
                                        vAno = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("latitude")) {
                                        dlat = Double.parseDouble(noteDataSnapshot.getValue().toString());
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("longitude")) {
                                        dlong = Double.parseDouble(noteDataSnapshot.getValue().toString());
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("nome")) {
                                        vNome = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("sobrenome")) {
                                        nomeUser.setText(vNome + " " + noteDataSnapshot.getValue().toString());
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("status")) {
                                        vStatus = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("categoria")) {
                                        vBookGenre.setText(getResources().getString(R.string.book_genre) + " " + noteDataSnapshot.getValue().toString());
                                    }


                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            LocalDate vNow = new LocalDate();
                            LocalDate vNasc;
                            try {
                                vNasc = new LocalDate(Integer.parseInt(vAno), Integer.parseInt(vMes), Integer.parseInt(vDia));
                            } catch (NullPointerException e){
                                vNasc = vNow;
                            } catch (NumberFormatException n){
                                vNasc = vNow;
                            } catch (IllegalFieldValueException v){
                                vNasc = vNow;
                            }
                            Years age = Years.yearsBetween(vNasc, vNow);
                            try {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(dlat, dlong, 1);
                                    if (addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        vLocalizacao = address.getSubLocality() + ", " + address.getLocality();
                                    } else {
                                        vLocalizacao = "";
                                    }
                                } catch (IOException e) {
                                    Log.e("Erro", "erro");
                                    vLocalizacao = "";
                                }
                            }catch (NullPointerException g) {
                                g.printStackTrace();
                                vLocalizacao = "";
                            }
                            if (vLocalizacao.length()>0) {
                                vIdade.setText(Integer.toString(age.getYears()) + ", " + vSexo + ", " + vLocalizacao);
                            }else{
                                vIdade.setText(Integer.toString(age.getYears()) + ", " + vSexo + ".");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
            }
        });

            btnUserVideos = (ImageButton) view.findViewById(R.id.ic_video);


            final ImageButton bStar1 = (ImageButton) view.findViewById(R.id.ic_bstar1);
            final ImageButton bStar2 = (ImageButton) view.findViewById(R.id.ic_bstar2);
            final ImageButton bStar3 = (ImageButton) view.findViewById(R.id.ic_bstar3);
            final ImageButton bStar4 = (ImageButton) view.findViewById(R.id.ic_bstar4);
            final ImageButton bStar5 = (ImageButton) view.findViewById(R.id.ic_bstar5);
            bStar1.setImageResource(R.drawable.ic_boover_rounded_vazado);
            bStar2.setImageResource(R.drawable.ic_boover_rounded_vazado);
            bStar3.setImageResource(R.drawable.ic_boover_rounded_vazado);
            bStar4.setImageResource(R.drawable.ic_boover_rounded_vazado);
            bStar5.setImageResource(R.drawable.ic_boover_rounded_vazado);

            vLivrosVendidos.setText(getResources().getString(R.string.livros_vendidos) + ": 0.");

            mDatabase.child("Reputation").child(dUid).child("comments").orderByChild("morder")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                Integer cLinhas = 0;
                                for (final DataSnapshot commentsSnapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot transSnapshot : commentsSnapshot.getChildren()) {
                                        for (DataSnapshot uidSnapshot : transSnapshot.getChildren()) {
                                            if (uidSnapshot.getKey().equals("text")) {
                                                vtcommentsText.add(uidSnapshot.getValue().toString());
                                                cLinhas = cLinhas + (uidSnapshot.getValue().toString().length() / 20) + 1;
                                                vtcommentsUid.add(commentsSnapshot.getKey().toString());
                                                commentCount++;
                                            }
                                            if (uidSnapshot.getKey().equals("gStar")) {
                                                vtcommentsStar.add(uidSnapshot.getValue().toString());
                                                bStars = bStars + Float.parseFloat(uidSnapshot.getValue().toString());
                                                countStars++;
                                            }
                                            if (uidSnapshot.getKey().equals("morder")) {
                                                vtcommentsData.add(uidSnapshot.getValue().toString());
                                            }
                                        }
                                    }
                                }
                                vLivrosVendidos.setText(getResources().getString(R.string.livros_vendidos) + ": " +Integer.toString(commentCount)+".");
                                Float vReputation = bStars/countStars;
                                if (vReputation > 0) {
                                    bStar1.setImageResource(R.drawable.ic_boover_rounded);
                                }
                                if (vReputation > 1.5) {
                                    bStar2.setImageResource(R.drawable.ic_boover_rounded);
                                }
                                if (vReputation > 2.5) {
                                    bStar3.setImageResource(R.drawable.ic_boover_rounded);
                                }
                                if (vReputation > 3.5) {
                                    bStar4.setImageResource(R.drawable.ic_boover_rounded);
                                }
                                if (vReputation > 4.5) {
                                    bStar5.setImageResource(R.drawable.ic_boover_rounded);
                                }

                                ViewGroup.LayoutParams params = list.getLayoutParams();
                                params.height = (30*cLinhas)+(vtcommentsUid.size()*180);
                                list.setLayoutParams(params);
                                list.requestLayout();
                                CustomAvaliacoesListAdapter adapteravaliacoes = new CustomAvaliacoesListAdapter(getActivity(), vtcommentsUid, vtcommentsText, vtcommentsStar, vtcommentsData);
                                list.setAdapter(adapteravaliacoes);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            if (vStatus!=null){
                if (vStatus.equals("on")){
                    imageStatus.setImageResource(R.drawable.ic_online);
                }else{
                    imageStatus.setImageResource(R.drawable.ic_offline);
                }
            }

            mDatabase.child("Preferences").child(dUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try {
                                prefChat = "";
                                prefPost = "";
                                prefFoto = "";
                                prefPerfil = "";
                                prefVideo = "";
                                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                    if (noteDataSnapshot.getKey().toString().equals("chat")) {
                                        prefChat = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("perfil")) {
                                        prefPerfil = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("foto")) {
                                        prefFoto = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("post")) {
                                        prefPost = noteDataSnapshot.getValue().toString();
                                    }
                                    if (noteDataSnapshot.getKey().toString().equals("video")) {
                                        prefVideo = noteDataSnapshot.getValue().toString();
                                    }
                                }

                                if (prefChat.equals(getResources().getString(R.string.amigos)) && !vAmigo) {
                                    btnChat.setVisibility(View.INVISIBLE);
                                    lblmensagem.setVisibility(View.INVISIBLE);
                                }
                                if (prefVideo.equals(getResources().getString(R.string.amigos)) && !vAmigo) {
                                    btnyoutube.setVisibility(View.INVISIBLE);
                                }
                                if (prefFoto.equals(getResources().getString(R.string.amigos)) && !vAmigo) {
                                    btnFotoLike.setVisibility(View.INVISIBLE);
                                }
                                if (prefPost.equals(getResources().getString(R.string.amigos)) && !vAmigo) {
                                    btnPosts.setVisibility(View.INVISIBLE);
                                    lblposts.setVisibility(View.INVISIBLE);
                                    btnResenhas.setVisibility(View.INVISIBLE);
                                    lblresenhas.setVisibility(View.INVISIBLE);
                                }
                                if (prefPerfil.equals(getResources().getString(R.string.amigos)) && !vAmigo) {
                                    nomeUser.setText(getResources().getString(R.string.somente_amigos));
                                    btnPosts.setVisibility(View.INVISIBLE);
                                    lblposts.setVisibility(View.INVISIBLE);
                                    btnResenhas.setVisibility(View.INVISIBLE);
                                    lblresenhas.setVisibility(View.INVISIBLE);
                                    btnFotoLike.setVisibility(View.INVISIBLE);
                                    btnUserShelf.setVisibility(View.INVISIBLE);
                                    lblestante.setVisibility(View.INVISIBLE);
                                    btnChat.setVisibility(View.INVISIBLE);
                                    lblmensagem.setVisibility(View.INVISIBLE);
                                    btndUserPhotos.setVisibility(View.INVISIBLE);
                                    lblfotos.setVisibility(View.INVISIBLE);
                                    btnUserVideos.setVisibility(View.INVISIBLE);
                                    lblvideos.setVisibility(View.INVISIBLE);
                                }
                            }catch (IllegalStateException e){
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

            mDatabase.child("Block").child(dUid).child(mUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null) {
                                btnBlockuser.setTag(1);
                                btnBlockuser.setVisibility(View.INVISIBLE);
                                imgFotoUser.setImageResource(R.drawable.ic_block_user);
                                nomeUser.setText(getResources().getString(R.string.nao_autorizado));
                                btnPosts.setVisibility(View.INVISIBLE);
                                btnResenhas.setVisibility(View.INVISIBLE);
                                btnFotoLike.setVisibility(View.INVISIBLE);
                                btnAdduser.setVisibility(View.INVISIBLE);
                                btnUserShelf.setVisibility(View.INVISIBLE);
                                btnChat.setVisibility(View.INVISIBLE);
                                btndUserPhotos.setVisibility(View.INVISIBLE);
                                btnUserVideos.setVisibility(View.INVISIBLE);

                            } else {
                                btnBlockuser.setTag(0);
                                btnBlockuser.setImageResource(R.drawable.ic_block_user);
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

        return view;
    }

}