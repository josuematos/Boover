package com.editorapendragon.boover;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

public class ActivityDetailUser extends AppCompatActivity implements View.OnClickListener {



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
    private String mGender, mInteresse, mProcurando;


    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private String mEmail, mCategoria;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        setContentView(R.layout.frgdetail_user);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        tglAmbos = (ToggleButton) findViewById(R.id.tglAmbos);
        tglMasculino = (ToggleButton) findViewById(R.id.tglMasculino);
        tglFeminino = (ToggleButton) findViewById(R.id.tglFeminino);
        tglHomem = (ToggleButton) findViewById(R.id.tglHomem);
        tglMulher = (ToggleButton) findViewById(R.id.tglMulher);
        tglAmigos = (ToggleButton) findViewById(R.id.tglAmigos);
        tglPaquera = (ToggleButton) findViewById(R.id.tglPaquera);
        datBirthday = (EditText) findViewById(R.id.datBirthday);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        imgUser = (ImageView) findViewById(R.id.imgUser);
        edtNome = (EditText) findViewById(R.id.edtName);
        edtSobrenome = (EditText) findViewById(R.id.edtLastName);

        tglMasculino.setOnClickListener(this);
        tglFeminino.setOnClickListener(this);
        tglHomem.setOnClickListener(this);
        tglMulher.setOnClickListener(this);
        tglAmigos.setOnClickListener(this);
        tglPaquera.setOnClickListener(this);
        tglAmbos.setOnClickListener(this);

        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCancel.setVisibility(View.INVISIBLE);

        datBirthday.setOnClickListener(this);

        mEmail = mAuth.getCurrentUser().getEmail().toString();
        mUid = mAuth.getCurrentUser().getUid().toString();

        edtNome.requestFocus();
    }

    private void writeNewUser(String email, String nome, String sobrenome, String sexo, String data_nascimento, String interesse, String procurando, int mDay, int mMonth, int mYear, String categoria) {
        //User user = new User(email, nome, sobrenome, sexo, data_nascimento, interesse, procurando, mDay, mMonth, mYear, categoria);
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
        mDatabase.child("Users").child(mUid).child("categoria").setValue("Romance");
        mDatabase.child("Preferences").child(mUid).child("chat").setValue(getApplicationContext().getResources().getString(R.string.todos));
        mDatabase.child("Preferences").child(mUid).child("foto").setValue(getApplicationContext().getResources().getString(R.string.todos));
        mDatabase.child("Preferences").child(mUid).child("perfil").setValue(getApplicationContext().getResources().getString(R.string.todos));
        mDatabase.child("Preferences").child(mUid).child("post").setValue(getApplicationContext().getResources().getString(R.string.todos));
        if (Globals.ImgUrl!=null) {
            mDatabase.child("Users").child(mUid).child("Default").setValue(Globals.ImgUrl);
        }
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
        Globals.vIntentFoto="1";
        Intent secondActivity = new Intent(getApplication(), MainActivity.class);
        startActivity(secondActivity);
        finish();
    }

    @Override
    public void onClick(View v) {
        mGender = getResources().getString(R.string.feminino);
        mInteresse = getResources().getString(R.string.ambos);
        mProcurando = getResources().getString(R.string.amigos);


        if(v == btnSave) {
            if (tglFeminino.isChecked()) {
                mGender = getResources().getString(R.string.feminino);
            } else {
                mGender = getResources().getString(R.string.masculino);
            }

            if (tglMulher.isChecked()) {
                mInteresse = getResources().getString(R.string.mulheres);
            } else if (tglHomem.isChecked()) {
                mInteresse = getResources().getString(R.string.homens);
            } else {
                mInteresse = getResources().getString(R.string.ambos);
            }

            if (tglAmigos.isChecked()) {
                mProcurando = getResources().getString(R.string.amigos);
            } else {
                mProcurando = getResources().getString(R.string.paqueras);
            }

            if (TextUtils.isEmpty(edtNome.getText().toString())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.digite_primeiro_nome), Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(edtSobrenome.getText().toString())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.digite_ultimo_nome), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(datBirthday.getText().toString())) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.digite_aniversario), Toast.LENGTH_SHORT).show();
            }else {

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edtNome.getText().toString()+" "+edtSobrenome.getText().toString())
                        .build();
                mAuth.getCurrentUser().updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.perfil_atualizado), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                writeNewUser(mEmail, edtNome.getText().toString(), edtSobrenome.getText().toString(), mGender,
                        datBirthday.getText().toString(), mInteresse, mProcurando, Globals.day, Globals.month, Globals.year, mCategoria);
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

            DatePickerDialog datePicker = new DatePickerDialog(this,
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
