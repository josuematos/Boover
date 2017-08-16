package com.editorapendragon.boover;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.appindexing.Action;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverChat extends Fragment {

    private static final String TAG = "FrgBooverChat";

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public ImageView sendImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            sendImageView = (ImageView) itemView.findViewById(R.id.sendImage);
        }
    }


        //private static final String TAG = "MainActivity";

        public static final String MESSAGES_CHILD = "messages";
        public String MESSAGES_CHANNEL = "channel";
        private static final int REQUEST_INVITE = 1;
        public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
        public static final String ANONYMOUS = "anonymous";
        private static final String MESSAGE_SENT_EVENT = "message_sent";
        private String mUsername;
        private String mPhotoUrl, vPhoto, simage;
        private SharedPreferences mSharedPreferences;
        private GoogleApiClient mGoogleApiClient;
        private static final String MESSAGE_URL = "https://boover-82fc3.firebaseio.com/message/";

    private ImageButton mSendButton, mSendImageButton;
    private ImageButton mBackButton;
    private ImageButton btnChangeImage, btnAddUser, btnExitGroup, btnBlockUser, btnUsersGrupo;
    private TextView txtChangeImage, txtAddUser, txtExitGroup, txtBlockUser, txtUsersGrupo;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorage;
    private FirebaseAnalytics mFirebaseAnalytics;
    private EditText mMessageEditText;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String dUid = null;
    private String vChannel = "", vTipoChannel = "";
    private String vNome = null;
    private String mUid;
    private DatabaseReference mDatabase;
    private ArrayList<String> chave = new ArrayList<String>();
    private ArrayList<String> vtFotoUser = new ArrayList<String>();
    private ArrayList<String> vtNomeUser = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> ntFotoUser = new ArrayList<String>();
    private ArrayList<String> ntNomeUser = new ArrayList<String>();
    private ArrayList<String> ntUser = new ArrayList<String>();

    private AlertDialog alerta;
    private ProgressDialog mProgress;
    private static final int CAMERA_REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgbooverchat_fragment, container, false);
        final LayoutInflater inflaterC = getActivity().getLayoutInflater();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUid = mFirebaseUser.getUid().toString();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(getContext());
        mStorage = FirebaseStorage.getInstance().getReference();

        if (mFirebaseUser == null) {
            return null;
        } else {
                mDatabase.child("Users").child(mUid).child("nome")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    mUsername = dataSnapshot.getValue().toString();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                // ...
                            }
                        });

                mDatabase.child("UserPhotos").child(mUid).child("Default")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    mPhotoUrl = dataSnapshot.getValue().toString();
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

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        btnExitGroup = (ImageButton) view.findViewById(R.id.ic_sair_grupo);
        btnAddUser = (ImageButton) view.findViewById(R.id.ic_adduser_grupo);
        btnChangeImage = (ImageButton) view.findViewById(R.id.ic_upload_image_grupo);
        btnBlockUser = (ImageButton) view.findViewById(R.id.ic_blockuser);
        btnUsersGrupo = (ImageButton) view.findViewById(R.id.ic_users_grupo);
        txtExitGroup = (TextView) view.findViewById(R.id.lblsairgrupo);
        txtAddUser = (TextView) view.findViewById(R.id.lbladicionar);
        txtChangeImage = (TextView) view.findViewById(R.id.lblalterar);
        txtBlockUser = (TextView) view.findViewById(R.id.lblblock);
        txtUsersGrupo = (TextView) view.findViewById(R.id.lblagrupo);



        mLinearLayoutManager.setStackFromEnd(true);


        Bundle bundlechat = this.getArguments();
        if (bundlechat != null) {
            dUid = bundlechat.getString("dUid", "0");
            vNome = bundlechat.getString("vNome", "0");
            vChannel = bundlechat.getString("vChannel", "");
            vTipoChannel = bundlechat.getString("vTipo", "");

            if (vTipoChannel.equals("G")) {
                btnAddUser.setVisibility(View.VISIBLE);
                btnChangeImage.setVisibility(View.VISIBLE);
                btnExitGroup.setVisibility(View.VISIBLE);
                btnBlockUser.setVisibility(View.INVISIBLE);
                btnUsersGrupo.setVisibility(View.VISIBLE);
                txtAddUser.setVisibility(View.VISIBLE);
                txtChangeImage.setVisibility(View.VISIBLE);
                txtExitGroup.setVisibility(View.VISIBLE);
                txtBlockUser.setVisibility(View.INVISIBLE);
                txtUsersGrupo.setVisibility(View.VISIBLE);


            } else {
                btnAddUser.setVisibility(View.INVISIBLE);
                btnChangeImage.setVisibility(View.INVISIBLE);
                btnExitGroup.setVisibility(View.INVISIBLE);
                btnBlockUser.setVisibility(View.VISIBLE);
                btnUsersGrupo.setVisibility(View.INVISIBLE);
                txtAddUser.setVisibility(View.INVISIBLE);
                txtChangeImage.setVisibility(View.INVISIBLE);
                txtExitGroup.setVisibility(View.INVISIBLE);
                txtBlockUser.setVisibility(View.VISIBLE);
                txtUsersGrupo.setVisibility(View.INVISIBLE);

                if (dUid.equals("0")) {
                    mDatabase.child("Channel").child(vChannel)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                         if (!noteDataSnapshot.getKey().equals("morder") &&
                                              !noteDataSnapshot.getKey().equals(mUid) &&
                                                 !noteDataSnapshot.getKey().contains("Count-")){
                                             dUid = noteDataSnapshot.getKey();
                                         }
                                    }
                                    mDatabase.child("Block").child(mUid).child(dUid)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
                                                        btnBlockUser.setTag(1);
                                                        btnBlockUser.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(getContext(), getResources().getString(R.string.nao_autorizado), Toast.LENGTH_SHORT).show();
                                                        getFragmentManager().popBackStack();
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
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }else{
                    mDatabase.child("Block").child(mUid).child(dUid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        btnBlockUser.setTag(1);
                                        btnBlockUser.setVisibility(View.INVISIBLE);
                                        Toast.makeText(getContext(), getResources().getString(R.string.nao_autorizado), Toast.LENGTH_SHORT).show();
                                        getFragmentManager().popBackStack();
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
            if (vChannel.isEmpty() || vChannel.equals("0")) {
                chave.add(mUid);
                chave.add(dUid);
                Collections.sort(chave, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });
                vChannel = chave.get(0).toString() + " - " + chave.get(1).toString();
                mDatabase.child("Channel").child(vChannel).child(mUid).setValue(vNome);
                mDatabase.child("Channel").child(vChannel).child(dUid).setValue(mUsername);
                Long mOrder = (-1 * new Date().getTime());
                mDatabase.child("Channel").child(vChannel).child("morder").setValue(mOrder.toString());
                String key2 = mDatabase.child("messages").push().getKey();
                String mdata = Long.toString(System.currentTimeMillis());

            }
            MESSAGES_CHANNEL = vChannel;

        }
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(MESSAGES_CHANNEL)) {

            @Override
            protected FriendlyMessage parseSnapshot(DataSnapshot snapshot) {
                FriendlyMessage friendlyMessage = super.parseSnapshot(snapshot);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(snapshot.getKey());
                }
                return friendlyMessage;
            }

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {
                if (vChannel != null) {
                    String cChannel = friendlyMessage.getChannel();
                    if (cChannel.equals(vChannel)) {
                        mDatabase.child("Channel").child(vChannel).child("Count-"+mUid).setValue("off");
                        viewHolder.messageTextView.setText(friendlyMessage.getText());
                        Locale current = getResources().getConfiguration().locale;
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d HH:mm:ss", current);
                        String mData = sdf.format(new Date(Long.parseLong(friendlyMessage.getMdata())));
                        viewHolder.messengerTextView.setText(friendlyMessage.getName() + " - " + mData);
                        if (friendlyMessage.getPhotoUrl() != null) {
                            Glide.with(getActivity())
                                    .load(friendlyMessage.getPhotoUrl())
                                    .into(viewHolder.messengerImageView);
                        }
                        if (friendlyMessage.getSimage() != null) {
                            Glide.with(getActivity())
                                    .load(friendlyMessage.getSimage())
                                    .into(viewHolder.sendImageView);
                        }

                        // write this message to the on-device index
                        FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));

                        // log a view action on it
                        FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
                    }
                }

            }
        };

        /* Contacts */

        mDatabase.child("Contacts").child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        vtUser.clear();
                        vtFotoUser.clear();
                        vtNomeUser.clear();
                        for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            mDatabase.child("Channel").child(vChannel)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot UserdataSnapshot) {
                                            Boolean vNotExist = true;
                                            for (final DataSnapshot UserDataSnapshot : UserdataSnapshot.getChildren()) {
                                                if (UserDataSnapshot.getKey().equals(noteDataSnapshot.getKey())) {
                                                    vNotExist = false;
                                                }
                                            }
                                            if (vNotExist){
                                                mDatabase.child("UserPhotos").child(noteDataSnapshot.getKey().toString()).child("Default")
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot UdataSnapshot) {
                                                                if (UdataSnapshot.getKey().equals("Default")) {
                                                                    if (UdataSnapshot.getValue() != null) {
                                                                        vtUser.add(noteDataSnapshot.getKey().toString());
                                                                        vtFotoUser.add(UdataSnapshot.getValue().toString());
                                                                        vtNomeUser.add(noteDataSnapshot.getValue().toString());
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

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        mDatabase.child("Channel").child(vChannel)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        ntUser.clear();
                        ntFotoUser.clear();
                        ntNomeUser.clear();
                        try {
                            for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                if (!noteDataSnapshot.getKey().equals("morder") &&
                                        !noteDataSnapshot.getKey().equals("photo") &&
                                        noteDataSnapshot.getKey().length() < 30) {
                                    mDatabase.child("UserPhotos").child(noteDataSnapshot.getKey().toString()).child("Default")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(final DataSnapshot UdataSnapshot) {
                                                    if (UdataSnapshot.getKey().equals("Default")) {
                                                        if (UdataSnapshot.getValue() != null) {
                                                            mDatabase.child("Users").child(noteDataSnapshot.getKey().toString()).child("nome")
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(DataSnapshot NdataSnapshot) {
                                                                            ntUser.add(noteDataSnapshot.getKey().toString());
                                                                            ntFotoUser.add(UdataSnapshot.getValue().toString());
                                                                            ntNomeUser.add(NdataSnapshot.getValue().toString());
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(DatabaseError databaseError) {

                                                                        }
                                                                    });

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
                        }catch (NullPointerException e){
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


        /* End Contacts */

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        // Initialize and request AdMob ad.
        /*mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        // Initialize Firebase Measurement.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        // Initialize Firebase Remote Config.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 10L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        // Fetch remote config.
        fetchConfig();

        mMessageEditText = (EditText) view.findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(CodelabPreferences.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (ImageButton) view.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername,
                        mPhotoUrl, dUid, mUid, vChannel, null);
                mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(MESSAGES_CHANNEL).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
                mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
                mDatabase.child("Channel").child(vChannel).child("Count-"+dUid).setValue("on");
                Long mOrder = (-1 * new Date().getTime());
                mDatabase.child("Channel").child(vChannel).child("morder").setValue(mOrder.toString());
            }
        });

        mSendImageButton = (ImageButton) view.findViewById(R.id.sendImageButton);
        mSendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Globals.ChatTipoFoto = "SendFoto";
                Globals.vIntentFoto="1";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selecione_imagem)), CAMERA_REQUEST_CODE);
            }
        });

        mBackButton = (ImageButton) view.findViewById(R.id.ic_switch);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
                /*FrgChannel fFrag = new FrgChannel();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add( R.id.frgbooverchat_frame, fFrag, "FrgChannel");
                ft.addToBackStack("FrgChannel");
                ft.commit();*/
            }
        });


        btnBlockUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.bloquear_usuario));
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(getResources().getString(R.string.tem_certeza_bloquear_usuario));
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (btnBlockUser.getTag()!=null) {
                            if (btnBlockUser.getTag().equals(0)) {
                                btnBlockUser.setTag(1);
                                btnBlockUser.setVisibility(View.INVISIBLE);
                                mDatabase.child("Block").child(mUid).child(dUid).setValue(vNome);
                                Toast.makeText(getActivity(),getResources().getString(R.string.usuario_bloqueado), Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            } else {
                                btnBlockUser.setTag(0);
                                btnBlockUser.setImageResource(R.drawable.ic_block_user);
                                mDatabase.child("Block").child(mUid).child(dUid).removeValue();
                                //setValue("0");
                            }
                        } else {
                            btnBlockUser.setTag(1);
                            mDatabase.child("Block").child(mUid).child(dUid).setValue(vNome);
                            btnBlockUser.setVisibility(View.INVISIBLE);
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


        btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle(R.string.sair_grupo);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_sair_grupo);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getContext(), R.string.usuario_removido, Toast.LENGTH_SHORT).show();
                        mDatabase.child("Channel").child(vChannel).child(mUid).removeValue();
                        getFragmentManager().popBackStack();
                        FrgChannel fFrag = new FrgChannel();
                        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                        ft.add( R.id.frgbooverchat_frame, fFrag, "FrgChannel");
                        ft.addToBackStack("FrgChannel");
                        ft.commit();
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

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle(R.string.alterar_imagem_grupo);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_alterar_img_grupo);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Globals.ChatTipoFoto = "ChangeImageGroup";
                        Globals.vIntentFoto="1";
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selecione_imagem)), CAMERA_REQUEST_CODE);
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

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View convertView = (View) inflaterC.inflate(R.layout.customlistalertdialog, null);
                final ListView list = (ListView) convertView.findViewById(R.id.listView1);
                final AlertDialog.Builder builderC = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                final CustomContactsListAdapter adapterL = new CustomContactsListAdapter(getActivity(), vtNomeUser, vtFotoUser, vtUser, null, "on");
                list.setAdapter(adapterL);
                builderC.setIcon(R.drawable.ic_boover_rounded);
                builderC.setTitle(R.string.adicionar_grupo);
                builderC.setAdapter(adapterL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("dis",""+which);
                    }
                });
                builderC.setPositiveButton(R.string.adicionar, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                ArrayList<String> Check = adapterL.getVtCheck();
                                if (Check!=null){
                                    for (int i = 0; i < Check.size(); i++) {
                                         mDatabase.child("Channel").child(vChannel).child(Check.get(i)).setValue(vNome);
                                         mDatabase.child("Notifications").child(Check.get(i))
                                                  .child(mUid).setValue(getResources().getString(R.string.adicionado_grupo)+" "+vNome+"\n");
                                    }
                                }
                            }
                        });
                builderC.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alerta.dismiss();
                    }
                });

                alerta = builderC.create();
                alerta.show();


            }
        });


        btnUsersGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View convertView = (View) inflaterC.inflate(R.layout.customlistalertdialog, null);
                final ListView list = (ListView) convertView.findViewById(R.id.listView1);
                final AlertDialog.Builder builderC = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                final CustomContactsListAdapter adapterL = new CustomContactsListAdapter(getActivity(), ntNomeUser, ntFotoUser, ntUser, null, "off");
                list.setAdapter(adapterL);
                builderC.setIcon(R.drawable.ic_boover_rounded);
                builderC.setTitle(R.string.boovers_grupo);
                builderC.setAdapter(adapterL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("dis",""+which);
                    }
                });
                builderC.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                         alerta.dismiss();
                    }
                });

                alerta = builderC.create();
                alerta.show();


            }
        });



        mProgressBar.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Globals.vIntentFoto="0";
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            mProgress.setMessage(getResources().getString(R.string.atualizando_imagem));
            mProgress.show();
            Uri uri = data.getData();
            if (mFirebaseAuth.getCurrentUser() != null) {
                StorageReference filepath = mStorage.child("Photos").child("Channel").child(vChannel);
                filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mProgress.dismiss();
                        String DownloadURL = taskSnapshot.getDownloadUrl().toString();
                        if (Globals.ChatTipoFoto.equals("ChangeImageGroup")) {
                            mDatabase.child("Channel").child(vChannel).child("photo").setValue(DownloadURL);
                        }else{
                            FriendlyMessage friendlyMessage = new FriendlyMessage("Photo", mUsername,
                                    mPhotoUrl, dUid, mUid, vChannel, DownloadURL);
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(MESSAGES_CHANNEL).push().setValue(friendlyMessage);
                            mMessageEditText.setText("");
                            mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
                            DownloadURL = null;
                        }
                        Toast.makeText(getContext(), R.string.upload_finalizado, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.falha_imagem, Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                });
            }
        }
    }

    private Action getMessageViewAction(FriendlyMessage friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getName(), MESSAGE_URL.concat(friendlyMessage.getId()))
                .setMetadata(new Action.Metadata.Builder().setUpload(false))
                .build();
    }

    private Indexable getMessageIndexable(FriendlyMessage friendlyMessage) {
        PersonBuilder sender = Indexables.personBuilder()
                .setIsSelf(mUsername == friendlyMessage.getName())
                .setName(friendlyMessage.getName())
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

        PersonBuilder recipient = Indexables.personBuilder()
                .setName(mUsername)
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

        String vMsg = "";
        if (friendlyMessage.getText()!=null) { vMsg = friendlyMessage.getText();}
        Indexable messageToIndex = Indexables.messageBuilder()
                .setName(vMsg)
                .setUrl(MESSAGE_URL.concat(friendlyMessage.getId()))
                .setSender(sender)
                .setRecipient(recipient)
                .build();

        return messageToIndex;
    }

    @Override
    public void onPause() {
        /*if (mAdView != null) {
            mAdView.pause();
        }*/
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mAdView != null) {
            mAdView.resume();
        }*/
    }

    @Override
    public void onDestroy() {
        /*if (mAdView != null) {
            mAdView.destroy();
        }*/
        super.onDestroy();
    }

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }

    // Fetch the config to determine the allowed length of messages.
    public void fetchConfig() {
        long cacheExpiration = 3600; // 1 hour in seconds
        // If developer mode is enabled reduce cacheExpiration to 0 so that each fetch goes to the
        // server. This should not be used in release builds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Make the fetched config available via FirebaseRemoteConfig get<type> calls.
                        mFirebaseRemoteConfig.activateFetched();
                        applyRetrievedLengthLimit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // There has been an error fetching the config
                        Log.w(TAG, "Error fetching config: " + e.getMessage());
                        applyRetrievedLengthLimit();
                    }
                });
    }



    /**
     * Apply retrieved length limit to edit text field. This result may be fresh from the server or it may be from
     * cached values.
     */
    private void applyRetrievedLengthLimit() {
        //Long friendly_msg_length = mFirebaseRemoteConfig.getLong("friendly_msg_length");
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
        //Log.d(TAG, "FML is: " + friendly_msg_length);
    }


    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

}