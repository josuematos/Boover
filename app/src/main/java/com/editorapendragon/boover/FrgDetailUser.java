package com.editorapendragon.boover;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.TimeZone;

public class FrgDetailUser extends Fragment implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private ToggleButton tglMasculino, tglAmbos;
    private ToggleButton tglFeminino;
    private ToggleButton tglHomem;
    private ToggleButton tglMulher;
    private ToggleButton tglAmigos;
    private ToggleButton tglPaquera;
    private ImageView imgUser;
    private ImageButton btnSave;
    private ImageButton btnCancel;
    private EditText datBirthday;
    private EditText edtNome;
    private EditText edtSobrenome;


    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private String mEmail, mCategoria;
    private String mUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgdetail_user, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(getContext());

        tglAmbos = (ToggleButton) view.findViewById(R.id.tglAmbos);
        tglMasculino = (ToggleButton) view.findViewById(R.id.tglMasculino);
        tglFeminino = (ToggleButton) view.findViewById(R.id.tglFeminino);
        tglHomem = (ToggleButton) view.findViewById(R.id.tglHomem);
        tglMulher = (ToggleButton) view.findViewById(R.id.tglMulher);
        tglAmigos = (ToggleButton) view.findViewById(R.id.tglAmigos);
        tglPaquera = (ToggleButton) view.findViewById(R.id.tglPaquera);
        datBirthday = (EditText) view.findViewById(R.id.datBirthday);
        btnSave = (ImageButton) view.findViewById(R.id.btnSave);
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        imgUser = (ImageView) view.findViewById(R.id.imgUser);
        edtNome = (EditText) view.findViewById(R.id.edtName);
        edtSobrenome = (EditText) view.findViewById(R.id.edtLastName);

        tglMasculino.setOnClickListener(this);
        tglFeminino.setOnClickListener(this);
        tglHomem.setOnClickListener(this);
        tglMulher.setOnClickListener(this);
        tglAmigos.setOnClickListener(this);
        tglPaquera.setOnClickListener(this);
        tglAmbos.setOnClickListener(this);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        datBirthday.setOnClickListener(this);

        mEmail = mAuth.getCurrentUser().getEmail().toString();
        mUid = mAuth.getCurrentUser().getUid().toString();

        edtNome.requestFocus();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
                mDatabase.child("Users").child(user.getUid()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User mUser = dataSnapshot.getValue(User.class);
                            String sexo = mUser.sexo.toString();
                            String interesse = mUser.interesse.toString();
                            String procurando = mUser.procurando.toString();
                            if (mUser.categoria!=null) {
                                mCategoria = mUser.categoria.toString();
                            }

                            edtNome.setText(mUser.nome.toString());
                            edtSobrenome.setText(mUser.sobrenome.toString());
                            datBirthday.setText(mUser.data_nascimento.toString());
                            Globals.day = Integer.parseInt(mUser.Day);
                            Globals.month = Integer.parseInt(mUser.Month);
                            Globals.year = Integer.parseInt(mUser.Year);

                            if ( sexo.equals(getResources().getString(R.string.masculino))){
                                tglMasculino.setChecked(false);
                                tglMasculino.setTextColor(Color.parseColor("#ffffff"));
                                tglMasculino.setBackgroundColor(Color.parseColor("#004c98"));
                                tglMasculino.toggle();
                            }else{
                                tglFeminino.setSelected(true);
                                tglFeminino.setChecked(false);
                                tglFeminino.setTextColor(Color.parseColor("#ffffff"));
                                tglFeminino.setBackgroundColor(Color.parseColor("#004c98"));
                                tglFeminino.toggle();
                            }
                            if (interesse.equals(getResources().getString(R.string.homens))){
                                tglHomem.setSelected(true);
                                tglHomem.setChecked(false);
                                tglHomem.setSelected(true);
                                tglHomem.setTextColor(Color.parseColor("#ffffff"));
                                tglHomem.setBackgroundColor(Color.parseColor("#004c98"));
                                tglHomem.toggle();
                            }else if (interesse.equals(getResources().getString(R.string.mulheres))){
                                tglMulher.setSelected(true);
                                tglMulher.setChecked(false);
                                tglMulher.setTextColor(Color.parseColor("#ffffff"));
                                tglMulher.setBackgroundColor(Color.parseColor("#004c98"));
                                tglMulher.toggle();
                            }else{
                                tglAmbos.setSelected(true);
                                tglAmbos.setChecked(false);
                                tglAmbos.setTextColor(Color.parseColor("#ffffff"));
                                tglAmbos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglAmbos.toggle();
                            }
                            if (procurando.equals(getResources().getString(R.string.amigos))){
                                tglAmigos.setChecked(false);
                                tglAmigos.setSelected(true);
                                tglAmigos.setTextColor(Color.parseColor("#ffffff"));
                                tglAmigos.setBackgroundColor(Color.parseColor("#004c98"));
                                tglAmigos.toggle();
                            }else{
                                tglPaquera.setChecked(false);
                                tglPaquera.setSelected(true);
                                tglPaquera.setTextColor(Color.parseColor("#ffffff"));
                                tglPaquera.setBackgroundColor(Color.parseColor("#004c98"));
                                tglPaquera.toggle();
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

    private void writeNewUser(String email, String nome, String sobrenome, String sexo, String data_nascimento, String interesse, String procurando, int mDay, int mMonth, int mYear, String categoria) {

        mDatabase.child("Users").child(mUid).child("email").setValue(email);
        mDatabase.child("Users").child(mUid).child("nome").setValue(nome);
        mDatabase.child("Users").child(mUid).child("sobrenome").setValue(sobrenome);
        mDatabase.child("Users").child(mUid).child("sexo").setValue(sexo);
        mDatabase.child("Users").child(mUid).child("data_nascimento").setValue(data_nascimento);
        mDatabase.child("Users").child(mUid).child("interesse").setValue(interesse);
        mDatabase.child("Users").child(mUid).child("Day").setValue(Integer.toString(mDay));
        mDatabase.child("Users").child(mUid).child("Month").setValue(Integer.toString(mMonth+1));
        mDatabase.child("Users").child(mUid).child("Year").setValue(Integer.toString(mYear));
        mDatabase.child("Users").child(mUid).child("procurando").setValue(procurando);
        mDatabase.child("Users").child(mUid).child("categoria").setValue(categoria);
        mDatabase.child("Users").child(mUid).child("status").setValue("on");

            Toast.makeText(getContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        String mGender = getResources().getString(R.string.feminino);
        String mInteresse = getResources().getString(R.string.ambos);
        String mProcurando = getResources().getString(R.string.amigos);

        if (v == btnCancel) {
            getFragmentManager().popBackStack();
        }

        if(v == btnSave){
            if (tglFeminino.isChecked()){
                mGender = getResources().getString(R.string.feminino);
            }else{
                mGender = getResources().getString(R.string.masculino);
            }

            if (tglMulher.isChecked()){
                mInteresse = getResources().getString(R.string.mulheres);
            }else if (tglHomem.isChecked()){
                mInteresse = getResources().getString(R.string.homens);
            }else{
                mInteresse = getResources().getString(R.string.ambos);
            }

            if (tglAmigos.isChecked()){
                mProcurando = getResources().getString(R.string.amigos);
            }else{
                mProcurando = getResources().getString(R.string.paqueras);
            }

            if (TextUtils.isEmpty(edtNome.getText().toString())) {
                Toast.makeText(getContext(), getResources().getString(R.string.digite_primeiro_nome), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(edtSobrenome.getText().toString())) {
                Toast.makeText(getContext(), getResources().getString(R.string.digite_ultimo_nome), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(datBirthday.getText().toString())) {
                Toast.makeText(getContext(), getResources().getString(R.string.digite_aniversario), Toast.LENGTH_SHORT).show();
            }else {
                writeNewUser(mEmail, edtNome.getText().toString(), edtSobrenome.getText().toString(), mGender,
                        datBirthday.getText().toString(), mInteresse, mProcurando, Globals.day, Globals.month, Globals.year, mCategoria);

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtNome.getText().toString()+" "+edtSobrenome.getText().toString())
                        .build();
                mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        Toast.makeText(getContext(), getResources().getString(R.string.perfil_atualizado), Toast.LENGTH_SHORT).show();
                                    } catch (IllegalStateException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                // colocar finish();
                //Intent secondActivity = new Intent(this, MainActivity.class);
                //startActivity(secondActivity);
            }
        }

        if (v == tglFeminino){
            tglMasculino.setChecked(false);
            tglFeminino.setChecked(true);
            tglFeminino.setBackgroundColor(Color.parseColor("#004c98"));
            tglMasculino.setBackgroundColor(Color.WHITE);
            tglFeminino.setTextColor(Color.WHITE);
            tglMasculino.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglMasculino){
            tglFeminino.setChecked(false);
            tglMasculino.setChecked(true);
            tglMasculino.setBackgroundColor(Color.parseColor("#004c98"));
            tglFeminino.setBackgroundColor(Color.WHITE);
            tglMasculino.setTextColor(Color.WHITE);
            tglFeminino.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglMulher){
            tglHomem.setChecked(false);
            tglAmbos.setChecked(false);
            tglMulher.setChecked(true);
            tglMulher.setBackgroundColor(Color.parseColor("#004c98"));
            tglHomem.setBackgroundColor(Color.WHITE);
            tglAmbos.setBackgroundColor(Color.WHITE);
            tglMulher.setTextColor(Color.WHITE);
            tglHomem.setTextColor(Color.parseColor("#004c98"));
            tglAmbos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglHomem){
            tglMulher.setChecked(false);
            tglAmbos.setChecked(false);
            tglHomem.setChecked(true);
            tglHomem.setBackgroundColor(Color.parseColor("#004c98"));
            tglMulher.setBackgroundColor(Color.WHITE);
            tglAmbos.setBackgroundColor(Color.WHITE);
            tglHomem.setTextColor(Color.WHITE);
            tglMulher.setTextColor(Color.parseColor("#004c98"));
            tglAmbos.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglAmbos){
            tglMulher.setChecked(false);
            tglHomem.setChecked(false);
            tglAmbos.setChecked(true);
            tglAmbos.setBackgroundColor(Color.parseColor("#004c98"));
            tglMulher.setBackgroundColor(Color.WHITE);
            tglHomem.setBackgroundColor(Color.WHITE);
            tglAmbos.setTextColor(Color.WHITE);
            tglMulher.setTextColor(Color.parseColor("#004c98"));
            tglHomem.setTextColor(Color.parseColor("#004c98"));
        }


        if (v == tglAmigos){
            tglPaquera.setChecked(false);
            tglAmigos.setChecked(true);
            tglAmigos.setBackgroundColor(Color.parseColor("#004c98"));
            tglPaquera.setBackgroundColor(Color.WHITE);
            tglAmigos.setTextColor(Color.WHITE);
            tglPaquera.setTextColor(Color.parseColor("#004c98"));
        }
        if (v == tglPaquera){
            tglAmigos.setChecked(false);
            tglPaquera.setChecked(true);
            tglPaquera.setBackgroundColor(Color.parseColor("#004c98"));
            tglAmigos.setBackgroundColor(Color.WHITE);
            tglPaquera.setTextColor(Color.WHITE);
            tglAmigos.setTextColor(Color.parseColor("#004c98"));
        }

        if (v == datBirthday) {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
            DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    String year1 = String.valueOf(selectedYear);
                    String month1 = String.valueOf(selectedMonth);
                    String day1 = String.valueOf(selectedDay);
                    Globals.day = selectedDay;
                    Globals.month = selectedMonth;
                    Globals.year = selectedYear;
                    datBirthday.setText(day1 + "/" + month1 + "/" + year1);

                }
            };

            DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                    AlertDialog.THEME_HOLO_LIGHT, datePickerListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePicker.setCancelable(false);
            datePicker.setTitle(getResources().getString(R.string.selecione_data));
            datePicker.show();
        }

    }

}
