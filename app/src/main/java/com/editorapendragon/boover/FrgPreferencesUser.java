package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FrgPreferencesUser extends Fragment implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private ToggleButton tglChatTodos, tglChatAmigos;
    private ToggleButton tglFotoTodos, tglFotoAmigos;
    private ToggleButton tglPerfilTodos, tglPerfilAmigos;
    private ToggleButton tglPostTodos, tglPostAmigos;
    private ToggleButton tglVideoTodos, tglVideoAmigos;
    private ImageButton btnCancel, btnSave;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private String mUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgpreferences_user, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getContext());

        tglChatTodos = (ToggleButton) view.findViewById(R.id.tglChatTodos);
        tglChatAmigos = (ToggleButton) view.findViewById(R.id.tglChatAmigos);
        tglFotoTodos = (ToggleButton) view.findViewById(R.id.tglFotoTodos);
        tglFotoAmigos = (ToggleButton) view.findViewById(R.id.tglFotoAmigos);
        tglPerfilTodos = (ToggleButton) view.findViewById(R.id.tglPerfilTodos);
        tglPerfilAmigos = (ToggleButton) view.findViewById(R.id.tglPerfilAmigos);
        tglPostTodos = (ToggleButton) view.findViewById(R.id.tglPostTodos);
        tglPostAmigos = (ToggleButton) view.findViewById(R.id.tglPostAmigos);
        tglVideoTodos = (ToggleButton) view.findViewById(R.id.tglVideoTodos);
        tglVideoAmigos = (ToggleButton) view.findViewById(R.id.tglVideoAmigos);

        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        btnSave = (ImageButton) view.findViewById(R.id.btnSave);

        tglChatTodos.setOnClickListener(this);
        tglChatAmigos.setOnClickListener(this);
        tglFotoTodos.setOnClickListener(this);
        tglFotoAmigos.setOnClickListener(this);
        tglPerfilTodos.setOnClickListener(this);
        tglPerfilAmigos.setOnClickListener(this);
        tglPostTodos.setOnClickListener(this);
        tglPostAmigos.setOnClickListener(this);
        tglVideoTodos.setOnClickListener(this);
        tglVideoAmigos.setOnClickListener(this);

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        mUid = mAuth.getCurrentUser().getUid().toString();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
                mDatabase.child("Preferences").child(user.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Preferences mUser = dataSnapshot.getValue(Preferences.class);
                            String verChat = getResources().getString(R.string.todos),
                                   verFoto = getResources().getString(R.string.todos),
                                   verPerfil = getResources().getString(R.string.todos),
                                   verVideo = getResources().getString(R.string.todos),
                                   verPost = getResources().getString(R.string.todos);
                            if (mUser!=null) {
                                if (mUser.chat != null) {
                                    verChat = mUser.chat.toString();
                                }
                                if (mUser.foto != null) {
                                    verFoto = mUser.foto.toString();
                                }
                                if (mUser.perfil != null) {
                                    verPerfil = mUser.perfil.toString();
                                }
                                if (mUser.post != null) {
                                    verPost = mUser.post.toString();
                                }
                                if (mUser.video != null) {
                                    verPost = mUser.video.toString();
                                }
                            }

                            if ( verChat.equals(getResources().getString(R.string.todos))){
                                tglChatTodos.setChecked(false);
                                tglChatTodos.setTextColor(Color.parseColor("#ffffff"));
                                tglChatTodos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglChatTodos.toggle();
                            }else{
                                tglChatAmigos.setSelected(true);
                                tglChatAmigos.setChecked(false);
                                tglChatAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglChatAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglChatAmigos.toggle();
                            }
                            if (verPerfil.equals(getResources().getString(R.string.todos))){
                                tglPerfilTodos.setSelected(true);
                                tglPerfilTodos.setChecked(false);
                                tglPerfilTodos.setSelected(true);
                                tglPerfilTodos.setTextColor(Color.parseColor("#ffffff"));
                                tglPerfilTodos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglPerfilTodos.toggle();

                            }else{
                                tglPerfilAmigos.setSelected(true);
                                tglPerfilAmigos.setChecked(false);
                                tglPerfilAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglPerfilAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglPerfilAmigos.toggle();
                            }
                            if (verFoto.equals(getResources().getString(R.string.todos))){
                                tglFotoTodos.setChecked(false);
                                tglFotoTodos.setSelected(true);
                                tglFotoTodos.setTextColor(Color.parseColor("#ffffff"));
                                tglFotoTodos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglFotoTodos.toggle();
                            }else{
                                tglFotoAmigos.setChecked(false);
                                tglFotoAmigos.setSelected(true);
                                tglFotoAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglFotoAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglFotoAmigos.toggle();
                            }
                            if (verPost.equals(getResources().getString(R.string.todos))){
                                tglPostTodos.setChecked(false);
                                tglPostTodos.setSelected(true);
                                tglPostTodos.setTextColor(Color.parseColor("#ffffff"));
                                tglPostTodos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglPostTodos.toggle();
                            }else{
                                tglPostAmigos.setChecked(false);
                                tglPostAmigos.setSelected(true);
                                tglPostAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglPostAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglPostAmigos.toggle();
                            }
                            if (verVideo.equals(getResources().getString(R.string.todos))){
                                tglVideoTodos.setChecked(false);
                                tglVideoTodos.setSelected(true);
                                tglVideoTodos.setTextColor(Color.parseColor("#ffffff"));
                                tglVideoTodos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglVideoTodos.toggle();
                            }else{
                                tglVideoAmigos.setChecked(false);
                                tglVideoAmigos.setSelected(true);
                                tglVideoAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglVideoAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglVideoAmigos.toggle();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
                );
        }

        return view;
    }

    private void writeNewUser(String chat, String foto, String perfil, String post, String video) {

            Preferences uPreferences = new Preferences(chat, foto, perfil, post, video);
            mDatabase.child("Preferences").child(mUid).setValue(uPreferences);
            Toast.makeText(getContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        String mChat = getResources().getString(R.string.todos);
        String mFoto = getResources().getString(R.string.todos);
        String mPerfil = getResources().getString(R.string.todos);
        String mPosts = getResources().getString(R.string.todos);
        String mVideo = getResources().getString(R.string.todos);

        if (v == btnCancel) {
            getFragmentManager().popBackStack();
        }

        if(v == btnSave){
            if (tglChatTodos.isChecked()){
                mChat = getResources().getString(R.string.todos);
            }else{
                mChat = getResources().getString(R.string.amigos);
            }

            if (tglFotoTodos.isChecked()){
                mFoto = getResources().getString(R.string.todos);
            }else{
                mFoto = getResources().getString(R.string.amigos);
            }

            if (tglPerfilTodos.isChecked()){
                mPerfil = getResources().getString(R.string.todos);
            }else{
                mPerfil = getResources().getString(R.string.amigos);
            }

            if (tglPostTodos.isChecked()){
                mPosts = getResources().getString(R.string.todos);
            }else{
                mPosts = getResources().getString(R.string.amigos);
            }
            if (tglVideoTodos.isChecked()){
                mVideo = getResources().getString(R.string.todos);
            }else{
                mVideo = getResources().getString(R.string.amigos);
            }

            writeNewUser(mChat, mFoto, mPerfil, mPosts, mVideo);

        }

        if (v == tglChatTodos){
            tglChatAmigos.setChecked(false);
            tglChatTodos.setChecked(true);
            tglChatTodos.setBackgroundColor(Color.parseColor("#004c98"));
            tglChatAmigos.setBackgroundColor(Color.WHITE);
            tglChatTodos.setTextColor(Color.WHITE);
            tglChatAmigos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglChatAmigos){
            tglChatTodos.setChecked(false);
            tglChatAmigos.setChecked(true);
            tglChatAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglChatTodos.setBackgroundColor(Color.WHITE);
            tglChatAmigos.setTextColor(Color.WHITE);
            tglChatTodos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglFotoTodos){
            tglFotoAmigos.setChecked(false);
            tglFotoTodos.setChecked(true);
            tglFotoTodos.setBackgroundColor(Color.parseColor("#004c98"));
            tglFotoAmigos.setBackgroundColor(Color.WHITE);
            tglFotoTodos.setTextColor(Color.WHITE);
            tglFotoAmigos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglFotoAmigos){
            tglFotoTodos.setChecked(false);
            tglFotoAmigos.setChecked(true);
            tglFotoAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglFotoTodos.setBackgroundColor(Color.WHITE);
            tglFotoAmigos.setTextColor(Color.WHITE);
            tglFotoTodos.setTextColor(Color.parseColor("#004c98"));
        }

        if (v == tglPerfilTodos){
            tglPerfilAmigos.setChecked(false);
            tglPerfilTodos.setChecked(true);
            tglPerfilTodos.setBackgroundColor(Color.parseColor("#004c98"));
            tglPerfilAmigos.setBackgroundColor(Color.WHITE);
            tglPerfilTodos.setTextColor(Color.WHITE);
            tglPerfilAmigos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglPerfilAmigos){
            tglPerfilTodos.setChecked(false);
            tglPerfilAmigos.setChecked(true);
            tglPerfilAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglPerfilTodos.setBackgroundColor(Color.WHITE);
            tglPerfilAmigos.setTextColor(Color.WHITE);
            tglPerfilTodos.setTextColor(Color.parseColor("#004c98"));
        }

        if (v == tglPostTodos){
            tglPostAmigos.setChecked(false);
            tglPostTodos.setChecked(true);
            tglPostTodos.setBackgroundColor(Color.parseColor("#004c98"));
            tglPostAmigos.setBackgroundColor(Color.WHITE);
            tglPostTodos.setTextColor(Color.WHITE);
            tglPostAmigos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglPostAmigos){
            tglPostTodos.setChecked(false);
            tglPostAmigos.setChecked(true);
            tglPostAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglPostTodos.setBackgroundColor(Color.WHITE);
            tglPostAmigos.setTextColor(Color.WHITE);
            tglPostTodos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglVideoTodos){
            tglVideoAmigos.setChecked(false);
            tglVideoTodos.setChecked(true);
            tglVideoTodos.setBackgroundColor(Color.parseColor("#004c98"));
            tglVideoAmigos.setBackgroundColor(Color.WHITE);
            tglVideoTodos.setTextColor(Color.WHITE);
            tglVideoAmigos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglVideoAmigos){
            tglVideoTodos.setChecked(false);
            tglVideoAmigos.setChecked(true);
            tglVideoAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglVideoTodos.setBackgroundColor(Color.WHITE);
            tglVideoAmigos.setTextColor(Color.WHITE);
            tglVideoTodos.setTextColor(Color.parseColor("#004c98"));
        }

    }

}
