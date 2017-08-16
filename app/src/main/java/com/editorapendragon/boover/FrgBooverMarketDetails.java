package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverMarketDetails extends Fragment {

    private static final String TAG = "FrgBooverMarket";
    private static final String APPLICATION_NAME = "Boover";
    private static final String API_KEY="AIzaSyDvi_SAEPvNP2XqLkRNYaU7pwtv4Riiklo";
    private static final String AWS_ACCESS_KEY_ID = "AKIAJOZ36CXGBVERFQOA";
    private static final String AWS_SECRET_KEY = "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";
    private Integer paginas = 6;


    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
    private Handler handler;
    private static ProgressBar mProgressBar;
    private static ListView listdetails;
    private static Activity context;
    private static AlertDialog alerta;
    private ImageButton btnOfertas, btnMinhasOfertas, btnMinhasVendas, btnMinhaReputacao, btnMinhasCompras, btnBack, btnWallet;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vTitle, vNome, vBook = "", vPhoto, vAuthorBook;
    private ArrayList<String> market = new ArrayList<String>();
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vDescricao = new ArrayList<String>();
    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<Double> vPrecob = new ArrayList<Double>();
    private ArrayList<Double> vFreteb = new ArrayList<Double>();

    private static String ENDPOINT;
    private CustomBooksMarketDetailsListAdapter adapterdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgboovermarketdetails_fragment, container, false);
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        context = getActivity();

        ENDPOINT =  context.getResources().getString(R.string.site_amazon);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listdetails= (ListView)  view.findViewById (R.id.lstViewMarket);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        vNome = mAuth.getCurrentUser().getDisplayName();
        btnWallet = (ImageButton) view.findViewById(R.id.ic_minha_carteira);
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        btnOfertas = (ImageButton) view.findViewById(R.id.ic_ofertas);
        btnMinhasOfertas = (ImageButton) view.findViewById(R.id.ic_minhas_ofertas);
        btnMinhasVendas = (ImageButton) view.findViewById(R.id.ic_meus_negocios);
        btnMinhasCompras = (ImageButton) view.findViewById(R.id.ic_minhas_compras);
        btnMinhaReputacao = (ImageButton) view.findViewById(R.id.ic_minha_reputacao);
        TextView lblmyshelf = (TextView) view.findViewById(R.id.lblnomemarket);
        final TextView txtsaldob = (TextView) view.findViewById(R.id.txt_saldob);
        final DecimalFormat precision = new DecimalFormat("#,##0.00");
        final TextView txtbloqueadob = (TextView) view.findViewById(R.id.txt_bloqueadob);


        btnWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverWallet fFrag = new FrgBooverWallet();
                FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frgboovermyshelf_frame, fFrag, "FrgBooverWallet");
                ft.addToBackStack("FrgBooverWallet");
                ft.commit();
            }
        });

        mDatabase.child("Wallet").child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot walletDataSnapshot : dataSnapshot.getChildren()) {
                            if (walletDataSnapshot.getKey().equals("saldob")){
                                if (walletDataSnapshot.getValue() != null
                                        && walletDataSnapshot.getValue().toString().length() > 0) {
                                    txtsaldob.setText(precision.format(Double.parseDouble(walletDataSnapshot.getValue().toString())));
                                }else{
                                    txtsaldob.setText("0.00");
                                }
                            }
                            if (walletDataSnapshot.getKey().equals("bloqueadob")){
                                if (walletDataSnapshot.getValue() != null
                                        && walletDataSnapshot.getValue().toString().length() > 0) {
                                    txtbloqueadob.setText(precision.format(Double.parseDouble(walletDataSnapshot.getValue().toString())));
                                }else{
                                    txtbloqueadob.setText("0.00");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Bundle bundleboovermyshelf = this.getArguments();
        if (bundleboovermyshelf!=null) {
            vBook = bundleboovermyshelf.getString("vBook", "0");
            vTitle = bundleboovermyshelf.getString("vTitle", "0");
            vPhoto = bundleboovermyshelf.getString("vPhoto", "0");
            vAuthorBook = bundleboovermyshelf.getString("vAuthor", "0");

            lblmyshelf.setText(getResources().getString(R.string.ofertas)+ ": "+vTitle);

                    vStatus.clear();
                    vDescricao.clear();
                    vUid.clear();
                    vPrecob.clear();
                    vFreteb.clear();

                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    mDatabase.child("Market").child(vBook).orderByChild("precob")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot bookDataSnapshot : dataSnapshot.getChildren()) {
                                        if (!mUid.equals(bookDataSnapshot.getKey().toString())) {
                                            vUid.add(bookDataSnapshot.getKey().toString());
                                            for (DataSnapshot uidDataSnapshot : bookDataSnapshot.getChildren()) {
                                                if (uidDataSnapshot.getKey().equals("descricao")) {
                                                    vDescricao.add(uidDataSnapshot.getValue().toString());
                                                }
                                                if (uidDataSnapshot.getKey().equals("status")) {
                                                    vStatus.add(uidDataSnapshot.getValue().toString());
                                                }
                                                if (uidDataSnapshot.getKey().equals("freteb")) {
                                                    if (uidDataSnapshot.getValue() != null
                                                            && uidDataSnapshot.getValue().toString().length() > 0) {
                                                        vFreteb.add(Double.parseDouble(uidDataSnapshot.getValue().toString()));
                                                    } else {
                                                        vFreteb.add(0.0);
                                                    }
                                                }
                                                if (uidDataSnapshot.getKey().equals("precob")) {
                                                    if (uidDataSnapshot.getValue() != null
                                                            && uidDataSnapshot.getValue().toString().length() > 0) {
                                                        vPrecob.add(Double.parseDouble(uidDataSnapshot.getValue().toString()));
                                                    } else {
                                                        vPrecob.add(0.0);
                                                    }
                                                }
                                            }
                                        }

                                    }
                                    adapterdetails = new CustomBooksMarketDetailsListAdapter(context, vUid, vTitle, vPhoto, vStatus,
                                            vDescricao, vPrecob, vFreteb, vAuthorBook, vBook);
                                    adapterdetails.notifyDataSetChanged();
                                    listdetails.setAdapter(adapterdetails);
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    // ...
                                }
                            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().popBackStack();
                }
            });
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        return view;
    }

}