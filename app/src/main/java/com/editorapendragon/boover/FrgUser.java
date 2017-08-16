package com.editorapendragon.boover;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgUser extends Fragment {
    private ListView list;
    private RelativeLayout rlUser;
    private CircleImageView imgPhotoUser;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabase;
    private TextView txtDisplayNameUser, txtGenreUser;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private static final String TAG = "FrgUser";
    private String mUid;
    private ArrayList<String> vUri = new ArrayList<String>();
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_INVITE = 0x0;




    Integer[] imgid={
            R.drawable.ic_camera,
            R.drawable.ic_video,
            R.drawable.ic_youtube,
            R.drawable.ic_notification,
            R.drawable.ic_visualization,
            R.drawable.ic_genre_book,
            R.drawable.ic_carteira,
            R.drawable.ic_privacidade,
            R.drawable.ic_send_invite_white,
            R.drawable.ic_password,
            R.drawable.ic_exit,
            R.drawable.ic_delete
    };

    String[] genre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frguser_fragment, container, false);
        // Inflate the layout for this fragment

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final String[] itemname ={
                getResources().getString(R.string.minhas_fotos),
                getResources().getString(R.string.meus_videos),
                getResources().getString(R.string.meus_youtube),
                getResources().getString(R.string.notificacoes),
                getResources().getString(R.string.visualizacoes),
                getResources().getString(R.string.genero_literario),
                getResources().getString(R.string.minha_carteira),
                getResources().getString(R.string.privacidade),
                getResources().getString(R.string.convidar_amigos),
                getResources().getString(R.string.alterar_senha),
                getResources().getString(R.string.sair),
                getResources().getString(R.string.excluir_conta)
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestScopes(new Scope(Scopes.PLUS_LOGIN)).
                requestIdToken(getString(R.string.server_client_id)).
                requestEmail().
                build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).
                addApi(Auth.GOOGLE_SIGN_IN_API, gso).
                build();
        mGoogleApiClient.connect();
        genre = getResources().getStringArray(R.array.genre);
        Arrays.sort(genre);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mUid = mAuth.getCurrentUser().getUid().toString();

        list = (ListView)  view.findViewById (R.id.lstView);
        rlUser = (RelativeLayout) view.findViewById(R.id.rlUser);

        imgPhotoUser = (CircleImageView) view.findViewById(R.id.imgPhotoPerfil);
        imgPhotoUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FotoUser fFrag = new FotoUser();
            Bundle bundlefrguser = new Bundle();
            bundlefrguser.putString("tipo", "2");
            bundlefrguser.putString("uri", Globals.ImgUrl.toString());
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            fFrag.setArguments(bundlefrguser);
            ft.add( R.id.frguser_frame, fFrag, "FrgFotoUser");
            ft.addToBackStack(null);
            ft.commit();
        }
    });
        txtGenreUser = (TextView) view.findViewById(R.id.txtGenre);
        txtDisplayNameUser = (TextView) view.findViewById(R.id.txtPerfil);
        txtDisplayNameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgDetailUser fFrag = new FrgDetailUser();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add( R.id.frguser_frame, fFrag, "FrgDetailUser");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mDatabaseUsers.child(mUid).child("Default")
                            .addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot xdataSnapshot) {
                                            if (xdataSnapshot.getValue()!=null) {
                                                    Uri photoUrl = Uri.parse(xdataSnapshot.getValue().toString());
                                                    Glide.with(getContext())
                                                            .load(photoUrl)
                                                            .into(imgPhotoUser);
                                                    Globals.ImgUrl = photoUrl;
                                            }else{
                                                Glide.with(getContext())
                                                        .load("https://firebasestorage.googleapis.com/v0/b/boover-82fc3.appspot.com/o/Photos%2Fic_Boover_rounded_vazado.png?alt=media&token=0638a639-8cea-42aa-a1e7-a081f9778bb3")
                                                        .into(imgPhotoUser);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    }
                            );
                    try {
                        mDatabase.child("Users").child(mUid).child("categoria")
                                .addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot xdataSnapshot) {
                                                try {
                                                    if (xdataSnapshot.getValue() != null) {
                                                        txtGenreUser.setText(getResources().getString(R.string.genero) + " " + xdataSnapshot.getValue().toString());
                                                    }
                                                } catch (IllegalStateException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        }
                                );
                    } catch (IllegalStateException e ){
                        e.printStackTrace();
                    }

                    try {
                    mDatabase.child("Users").child(mUid).child("nome")
                            .addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot xdataSnapshot) {
                                            if (xdataSnapshot.getValue()!=null) {
                                                txtDisplayNameUser.setText(xdataSnapshot.getValue().toString());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    }
                            );
                    } catch (IllegalStateException e ){
                        e.printStackTrace();
                    }
                }
            }
        };

         // Codigo tela principal User

        CustomListAdapter adapter = new CustomListAdapter(getActivity(), itemname, imgid);
        list.setAdapter(adapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String Slecteditem = itemname[+position];

                    if (position == 0){

                        FrgUserPhotos fFrag = new FrgUserPhotos();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgUserPhotos");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    if (position == 1) {
                        Bundle bundledMeet = new Bundle();
                        bundledMeet.putString("dUid", mUid);
                        bundledMeet.putString("videoimage", "video");
                        FrgDestUserPhotos fFrag = new FrgDestUserPhotos();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        fFrag.setArguments(bundledMeet);
                        ft.add(R.id.frguser_frame, fFrag, "FrgDestUserPhotos");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    if (position == 2){
                        Globals.vIntentFoto="1";
                        Intent secondActivity = new Intent(getActivity(), ActivityVideoList.class);
                        secondActivity.putExtra("mUid", mUid);
                        startActivity(secondActivity);
                    }
                    if (position == 3){

                        FrgNotifications fFrag = new FrgNotifications();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgNotifications");
                        ft.addToBackStack(null);
                        ft.commit();
                    }

                    if (position == 4){

                        FrgVisualizations fFrag = new FrgVisualizations();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgVisualizations");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    if (position == 5){
                        final AlertDialog.Builder builderC = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom2);
                        builderC.setIcon(R.drawable.ic_boover_rounded);
                        builderC.setTitle(getResources().getString(R.string.escolha_genero));
                        builderC.setItems(genre, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                mDatabase.child("Users").child(mUid).child("categoria").setValue(genre[position]);
                                txtGenreUser.setText(getResources().getString(R.string.genero_literario)+" "+genre[position]);
                                Toast.makeText(getActivity(), getResources().getString(R.string.genero_alterado)+" "+genre[position]+".", Toast.LENGTH_SHORT).show();
                            }

                        });
                        builderC.setNegativeButton(getResources().getString(R.string.voltar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                alerta.dismiss();
                            }
                        });

                        alerta = builderC.create();
                        alerta.show();

                    }
                    if (position == 6){

                        FrgBooverWallet fFrag = new FrgBooverWallet();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgBooverWallet");
                        ft.addToBackStack("FrgBooverWallet");
                        ft.commit();

                    }
                    if (position == 7){

                        FrgPreferencesUser fFrag = new FrgPreferencesUser();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgPreferencesUser");
                        ft.addToBackStack(null);
                        ft.commit();

                    }
                    if (position == 8) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                        builder.setTitle(getResources().getString(R.string.convidar_amigos));
                        builder.setIcon(R.drawable.ic_boover_rounded);
                        builder.setMessage(getResources().getString(R.string.obrigado_por_fazer_o_boover));
                        builder.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                onInviteClicked();
                            }
                        });
                        builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alerta = builder.create();
                        alerta.show();

                    }
                    if (position == 9){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                        builder.setTitle(getResources().getString(R.string.digite_nova_senha));

                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        input.setWidth(1280);
                        input.setHeight(150);
                        input.setPadding(15,0,15,0);
                        input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                        input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                        builder.setView(input);
                        builder.setIcon(R.drawable.ic_boover_rounded);

                        builder.setPositiveButton(getResources().getString(R.string.alterar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String newPassword = input.getText().toString();
                                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), getResources().getString(R.string.senha_alterada), Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        LoginManager.getInstance().logOut();
                                        if (mGoogleApiClient.isConnected()) {
                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            Log.e("SAIR", "signOut:onResult:" + status);
                                                            Intent secondActivity = new Intent(getActivity(), LoginActivity.class);
                                                            startActivity(secondActivity);
                                                        }
                                                    });
                                        }else{
                                            Intent secondActivity = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(secondActivity);
                                        }                                  }
                                });
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
                    if (position == 10) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                        builder.setTitle(getResources().getString(R.string.sair));
                        builder.setIcon(R.drawable.ic_boover_rounded);
                        builder.setMessage(getResources().getString(R.string.tem_certeza_sair_programa));
                        builder.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mAuth.signOut();
                                mDatabase.child("Users").child(mUid).child("status").setValue("off");
                                LoginManager.getInstance().logOut();
                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    Log.e("SAIR", "signOut:onResult:" + status);
                                                    getActivity().finish();
                                                    Intent secondActivity = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(secondActivity);

                                                }
                                            });
                                }else{
                                    getActivity().finish();
                                    Intent secondActivity = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(secondActivity);

                                }


                            }
                        });
                        builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alerta = builder.create();
                        alerta.show();

                    }
                    if (position == 11) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                        builder.setTitle(getResources().getString(R.string.excluir_conta));
                        builder.setIcon(R.drawable.ic_boover_rounded);
                        builder.setMessage(getResources().getString(R.string.tem_certeza_excluir_conta));
                        builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mDatabase.child("Users").child(mUid).removeValue();
                                mDatabase.child("Books").child(mUid).removeValue();
                                mDatabase.child("Contacts").child(mUid).removeValue();
                                mDatabase.child("Invitations").child(mUid).removeValue();
                                mDatabase.child("Likes").child(mUid).removeValue();
                                mDatabase.child("Notifications").child(mUid).removeValue();
                                mDatabase.child("Preferences").child(mUid).removeValue();
                                mDatabase.child("UserPhotos").child(mUid).removeValue();
                                mDatabase.child("Visualizations").child(mUid).removeValue();
                                /*StorageReference storageRef = mStorage.child("Photos").child(mUid);
                                storageRef.delete().addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });*/
                                if (mGoogleApiClient.isConnected()) {
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                            new ResultCallback<Status>() {
                                                @Override
                                                public void onResult(Status status) {
                                                    Log.e("SAIR", "signOut:onResult:" + status);
                                                }
                                            });
                                }
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.e("Delete", "Sucessfuly");
                                                }
                                            }
                                        });
                                mAuth.signOut();
                                LoginManager.getInstance().logOut();
                                getActivity().finish();
                            }
                        });
                        builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        alerta = builder.create();
                        alerta.show();
                    }

                }
            });
        // Fim Codigo tela principal USer

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void onInviteClicked() {
        Globals.vIntentFoto="1";
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setEmailHtmlContent(getString(R.string.invitation_emailhtmlcontent))
                .setEmailSubject(getString(R.string.invitation_title))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}