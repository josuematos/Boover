package com.editorapendragon.boover;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgChannel extends Fragment {
    private ListView list;
    private CircleImageView imgPhotoUser;
    private TextView txtDisplayNameChannel;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "FrgChannel";
    private Bundle bundle;
    private DatabaseReference mDatabase, mDatabase2;
    private DatabaseReference mDatabaseM, mDatabaseU;
    private FirebaseAuth mAuth;
    private String mUid, pad;
    private Handler handler;
    private ArrayList<String> vChannel = new ArrayList<String>();
    private ArrayList<String> vNomeChannel = new ArrayList<String>();
    private ArrayList<String> vTipoChannel = new ArrayList<String>();
    private ArrayList<String> vLido = new ArrayList<String>();
    private ImageButton btnReload;
    private ProgressBar mProgressBar;
    private android.app.AlertDialog alerta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.frgchannel_fragment, container, false);
        // Inflate the layout for this fragment
        getFragmentManager().beginTransaction().addToBackStack("FrgChannel").commit();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference("Channel");
        mDatabase2 = FirebaseDatabase.getInstance().getReference("Channel");
        mDatabaseM = FirebaseDatabase.getInstance().getReference("messages");
        mDatabaseU = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        list = (ListView)  view.findViewById (R.id.lstViewchannel);

        mDatabase.orderByChild("morder")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vChannel.clear();
                vNomeChannel.clear();
                vTipoChannel.clear();
                vLido.clear();
                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> mUserPhotos = (HashMap<String, String>) noteDataSnapshot.getValue();
                            String vnomeChannel = "";
                            for (final Map.Entry<String, String> entry : mUserPhotos.entrySet()) {
                                if (entry.getKey().toString().equals(mUid)) {
                                    vChannel.add(noteDataSnapshot.getKey().toString());
                                    vnomeChannel = entry.getValue().toString();
                                    vnomeChannel = vnomeChannel.replace(".","");
                                    vnomeChannel = vnomeChannel.replace("#","");
                                    vnomeChannel = vnomeChannel.replace("$","");
                                    vnomeChannel = vnomeChannel.replace("[","");
                                    vnomeChannel = vnomeChannel.replace("]","");

                                    vNomeChannel.add(vnomeChannel);
                                    if (noteDataSnapshot.getKey().length()>30){
                                        vTipoChannel.add("I");
                                    }else{
                                        vTipoChannel.add("G");
                                    }
                                }
                                if (entry.getKey().toString().equals("Count-"+mUid)) {
                                    vLido.add(entry.getValue().toString());
                                }
                            }
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (vChannel.size() > 0) {
                    Collections.reverse(vChannel);
                    Collections.reverse(vNomeChannel);
                    Collections.reverse(vTipoChannel);
                    Collections.reverse(vLido);
                    if (getActivity()!=null) {
                        CustomChannelListAdapter adapter = new CustomChannelListAdapter(getActivity(), vChannel, vNomeChannel, vLido);
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);
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

     /*   handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vChannel.size() > 0) {
                    Collections.reverse(vChannel);
                    Collections.reverse(vNomeChannel);
                    Collections.reverse(vTipoChannel);
                    Collections.reverse(vLido);
                    if (getActivity()!=null) {
                        CustomChannelListAdapter adapter = new CustomChannelListAdapter(getActivity(), vChannel, vNomeChannel, vLido);
                        adapter.notifyDataSetChanged();
                        list.setAdapter(adapter);
                    }

                } else {
                    handler.postDelayed(this, 500);
                }
            }
        }, 1000);*/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Slecteditem = vChannel.get(position);
                Bundle bundleChannel = new Bundle();
                bundleChannel.putString("vChannel", Slecteditem);
                bundleChannel.putString("vNome", vNomeChannel.get(position));
                bundleChannel.putString("vTipo", vTipoChannel.get(position));

                FrgBooverChat fFrag = new FrgBooverChat();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fFrag.setArguments(bundleChannel);
                ft.add( R.id.frgchannel_frame, fFrag, "FrgBooverChat");
                ft.addToBackStack(null);
                ft.commit();

            }
        });


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                final String Slecteditem = vChannel.get(position);
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setIcon(R.drawable.ic_boover_rounded);

                    builder.setTitle(R.string.sair_conversa);
                    builder.setMessage(getString(R.string.sair_conversa_msg)+"?");
                    builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                                mDatabase.child(Slecteditem).child(mUid).removeValue();
                                FrgChannel fFrag = new FrgChannel();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.add( R.id.frgchannel_frame, fFrag, "FrgChannel");
                                ft.addToBackStack("FrgChannel");
                                ft.commit();
                        }
                    });
                    builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alerta.dismiss();
                        }
                    });


                alerta = builder.create();
                alerta.show();

                return true;
            }
        });



        // Fim Codigo tela principal USer

        btnReload = (ImageButton) view.findViewById(R.id.ic_switch);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgChannel fFrag = new FrgChannel();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add( R.id.frgchannel_frame, fFrag, "FrgChannel");
                ft.addToBackStack("FrgChannel");
                ft.commit();
            }
        });


        ImageButton btnGroup = (ImageButton) view.findViewById(R.id.ic_new_group);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.digite_nome_grupo);

                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                input.setWidth(1280);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);

                builder.setPositiveButton(R.string.criar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nomeChannel = input.getText().toString();

                        nomeChannel = nomeChannel.replace(".","");
                        nomeChannel = nomeChannel.replace("#","");
                        nomeChannel = nomeChannel.replace("$","");
                        nomeChannel = nomeChannel.replace("[","");
                        nomeChannel = nomeChannel.replace("]","");

                        writeNewChannel(mUid,nomeChannel,null);
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
        return view;
    }

    private void writeNewChannel(String userId, String title, String imagekey) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        if (imagekey==null){
            imagekey="https://firebasestorage.googleapis.com/v0/b/boover-82fc3.appspot.com/o/Photos%2Fic_group_2.png?alt=media&token=73998661-5d61-4688-a025-bbd84c49e50c";
        }
        String key = mDatabase.push().getKey();
        Channel channel = new Channel(userId, title, imagekey);
        Map<String, Object> postValues = channel.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, postValues);
        mDatabase.updateChildren(childUpdates);

        String key2 = mDatabaseM.push().getKey();
        String mdata = Long.toString(System.currentTimeMillis());
        mDatabaseM.child(key).child(key2).child("channel").setValue(key);
        mDatabaseM.child(key).child(key2).child("text").setValue(getResources().getString(R.string.grupo_criado));
        mDatabaseM.child(key).child(key2).child("mdata").setValue(mdata);
        mDatabaseM.child(key).child(key2).child("name").setValue("System");
        mDatabaseM.child(key).child(key2).child("sender").setValue(mUid);

        Toast.makeText(getContext(),getResources().getString(R.string.grupo_criado), Toast.LENGTH_SHORT).show();

        FrgChannel fFrag = new FrgChannel();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add( R.id.frgchannel_frame, fFrag, "FrgChannel");
        ft.addToBackStack("FrgChannel");
        ft.commit();

    }
}