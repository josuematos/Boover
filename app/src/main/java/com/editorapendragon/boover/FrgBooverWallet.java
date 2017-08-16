package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverWallet extends Fragment {

    private static final String TAG = "FrgBooverMarket";
    private static final String APPLICATION_NAME = "Boover";
    private static final String API_KEY="AIzaSyDvi_SAEPvNP2XqLkRNYaU7pwtv4Riiklo";
    private static final String AWS_ACCESS_KEY_ID = "AKIAJOZ36CXGBVERFQOA";
    private static final String AWS_SECRET_KEY = "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";
    private Integer paginas = 6;


    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
    private Handler handler;
    private static Activity context;
    private static AlertDialog alerta;
    private ImageButton btnMinhasVendas, btnBack;
    private Button btnExtrato, btnComprar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vPhone = "", vEmail = "";
    private Double vTotal = 0.00d;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgwallet_fragment, container, false);
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        context = getActivity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        btnExtrato = (Button) view.findViewById(R.id.btnExtrato);
        btnComprar = (Button) view.findViewById(R.id.btnRecarregar);
        final TextView txtsaldob = (TextView) view.findViewById(R.id.txt_saldob);
        final DecimalFormat precision = new DecimalFormat("#,##0.00");
        final TextView txttotalbloq = (TextView) view.findViewById(R.id.txt_bloqueadob);
        final TextView txttotaldisp = (TextView) view.findViewById(R.id.txt_saldob);
        final TextView txttotal = (TextView) view.findViewById(R.id.txt_totalb);

        mDatabase.child("Wallet").child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot walletDataSnapshot : dataSnapshot.getChildren()) {
                            if (walletDataSnapshot.getKey().equals("saldob")){
                                if (walletDataSnapshot.getValue() != null
                                        && walletDataSnapshot.getValue().toString().length() > 0) {
                                    txtsaldob.setText(precision.format(Double.parseDouble(walletDataSnapshot.getValue().toString())));
                                    vTotal = vTotal + Double.parseDouble(walletDataSnapshot.getValue().toString());
                                }else{
                                    txtsaldob.setText("0.00");
                                }
                            }
                            if (walletDataSnapshot.getKey().equals("bloqueadob")){
                                if (walletDataSnapshot.getValue() != null
                                        && walletDataSnapshot.getValue().toString().length() > 0) {
                                    txttotalbloq.setText(precision.format(Double.parseDouble(walletDataSnapshot.getValue().toString())));
                                    vTotal = vTotal + Double.parseDouble(walletDataSnapshot.getValue().toString());
                                }else{
                                    txttotalbloq.setText("0.00");
                                }
                            }
                        }
                        txttotal.setText(precision.format(vTotal));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        btnExtrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverExtrato fFrag = new FrgBooverExtrato();
                FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frgwallet_frame, fFrag, "FrgBooverExtrato");
                ft.addToBackStack("FrgBooverExtrato");
                ft.commit();
            }
        });
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverComprarCreditos fFrag = new FrgBooverComprarCreditos();
                FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frgwallet_frame, fFrag, "FrgBooverComprarCreditos");
                ft.addToBackStack("FrgBooverComprarCreditos");
                ft.commit();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}