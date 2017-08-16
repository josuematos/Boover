package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverExtrato extends Fragment {

    private Handler handler;
    private static ProgressBar mProgressBar;
    private static ListView listdetails;
    private static Activity context;
    private static AlertDialog alerta;
    private ImageButton btnCredito, btnRecarga, btnDebito, btnBack, btnTodos;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vBook, vNome, vFiltro;
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vTransacao = new ArrayList<String>();
    private ArrayList<String> vtData = new ArrayList<String>();
    private ArrayList<String> vTipo = new ArrayList<String>();
    private ArrayList<Double> vValor = new ArrayList<Double>();
    private ArrayList<Double> vSaldo = new ArrayList<Double>();

    private static String ENDPOINT;
    private CustomExtratoListAdapter adapterdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgbooverextrato_fragment, container, false);
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        context = getActivity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listdetails= (ListView)  view.findViewById (R.id.lstViewMarket);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        btnRecarga = (ImageButton) view.findViewById(R.id.ic_recarga);
        btnDebito = (ImageButton) view.findViewById(R.id.ic_debitos);
        btnCredito = (ImageButton) view.findViewById(R.id.ic_creditos);
        btnTodos = (ImageButton) view.findViewById(R.id.ic_todos);
        final TextView lblsaldo = (TextView) view.findViewById(R.id.lblsaldo);
        final DecimalFormat precision = new DecimalFormat("#,##0.00");

        Bundle bundleboovermyshelf = this.getArguments();
        if (bundleboovermyshelf!=null) {
            vFiltro = bundleboovermyshelf.getString("vFiltro", null);
        }

        vStatus.clear();
        vtData.clear();
        vTransacao.clear();
        vValor.clear();
        vSaldo.clear();
        vTipo.clear();
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mDatabase.child("Wallet").child(mUid).child("saldob")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            if (dataSnapshot.getValue() != null
                                    && dataSnapshot.getValue().toString().length() > 0) {
                                lblsaldo.setText(getContext().getResources().getString(R.string.saldo_disponivel) + " " +
                                        precision.format(Double.parseDouble(dataSnapshot.getValue().toString())));
                            } else {
                                lblsaldo.setText(getContext().getResources().getString(R.string.saldo_disponivel) + " " + "0.00");
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child("Wallet").child(mUid).child("extrato").orderByChild("datasolicitacao")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot transDataSnapshot : dataSnapshot.getChildren()) {
                                    vTransacao.add(transDataSnapshot.getKey().toString());
                                    for (DataSnapshot extratoDataSnapshot : transDataSnapshot.getChildren()) {
                                        if (extratoDataSnapshot.getKey().equals("datasolicitacao")) {
                                            vtData.add(extratoDataSnapshot.getValue().toString());
                                        }
                                        if (extratoDataSnapshot.getKey().equals("status")) {
                                            vStatus.add(extratoDataSnapshot.getValue().toString());
                                        }
                                        if (extratoDataSnapshot.getKey().equals("tipo")) {
                                            vTipo.add(extratoDataSnapshot.getValue().toString());
                                        }
                                        if (extratoDataSnapshot.getKey().equals("valor")) {
                                            if (extratoDataSnapshot.getValue() != null
                                                    && extratoDataSnapshot.getValue().toString().length() > 0) {
                                                vValor.add(Double.parseDouble(extratoDataSnapshot.getValue().toString()));
                                            } else {
                                                vValor.add(0.0);
                                            }
                                        }
                                    }
                                }
                                Double vsaldo = 0.00d, vvalor;
                                for (int i=0; i<vTipo.size(); i++) {
                                    if (vTipo.get(i)!=null) {
                                        if (vTipo.get(i).equals(getContext().getResources().getString(R.string.status_extrato_debito).toString())
                                                || vTipo.get(i).equals(getContext().getResources().getString(R.string.status_extrato_compra).toString())) {
                                            vvalor = vValor.get(i);
                                            vValor.remove(i);
                                            vValor.add(i, -vvalor);
                                            if (i == 0) {
                                                vSaldo.add(-vvalor);
                                            } else {
                                                vsaldo = vSaldo.get(i - 1);
                                                vSaldo.add(vsaldo - vvalor);
                                            }
                                        } else {
                                            if (i == 0) {
                                                vSaldo.add(vValor.get(i));
                                            } else {
                                                vsaldo = vSaldo.get(i - 1);
                                                vSaldo.add(vsaldo + vValor.get(i));
                                            }
                                        }
                                    }
                                }

                                Collections.reverse(vStatus);
                                Collections.reverse(vTransacao);
                                Collections.reverse(vtData);
                                Collections.reverse(vTipo);
                                Collections.reverse(vValor);
                                Collections.reverse(vSaldo);

                                if (vFiltro != null) {
                                    for (int i=0; i<vStatus.size(); i++) {
                                        if (vTipo.get(i)!=null) {
                                            if (!vTipo.get(i).equals(vFiltro)) {
                                                try {
                                                    vStatus.remove(i);
                                                    vTransacao.remove(i);
                                                    vtData.remove(i);
                                                    vTipo.remove(i);
                                                    vValor.remove(i);
                                                    vSaldo.remove(i);
                                                } catch (NullPointerException e) {
                                                    e.printStackTrace();
                                                } catch (IndexOutOfBoundsException n) {
                                                    n.printStackTrace();
                                                }
                                                i--;
                                            }
                                        }
                                    }
                                }
                                adapterdetails = new CustomExtratoListAdapter(context, vStatus,
                                        vTransacao, vtData, vValor, vSaldo, vTipo);
                                listdetails.setAdapter(adapterdetails);
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                // ...
                            }
                        });

        btnCredito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundlevendas = new Bundle();
                    bundlevendas.putString("vFiltro", getContext().getResources().getString(R.string.status_extrato_credito).toString());
                    FrgBooverExtrato fFrag = new FrgBooverExtrato();
                    fFrag.setArguments(bundlevendas);
                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frgextrato_frame, fFrag, "FrgBooverExtrato");
                    ft.addToBackStack("FrgBooverExtrato");
                    ft.commit();
                }
            });

        btnTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverExtrato fFrag = new FrgBooverExtrato();
                FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frgextrato_frame, fFrag, "FrgBooverExtrato");
                ft.addToBackStack("FrgBooverExtrato");
                ft.commit();
            }
        });

        btnDebito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlevendas = new Bundle();
                bundlevendas.putString("vFiltro", getContext().getResources().getString(R.string.status_extrato_debito).toString());
                FrgBooverExtrato fFrag = new FrgBooverExtrato();
                fFrag.setArguments(bundlevendas);
                FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frgextrato_frame, fFrag, "FrgBooverExtrato");
                ft.addToBackStack("FrgBooverExtrato");
                ft.commit();
            }
        });

        btnRecarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        FrgBooverComprarCreditos fFrag = new FrgBooverComprarCreditos();
                        FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frgextrato_frame, fFrag, "FrgBooverComprarCreditos");
                        ft.addToBackStack("FrgBooverComprarCreditos");
                        ft.commit();

            }
        });


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().popBackStack("FrgBooverWallet", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        return view;
    }

}