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


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverMarketVendas extends Fragment {

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
    private ImageButton btnNaoEntregues, btnEntregues, btnEnviados, btnBack, btnWallet;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vBook, vNome, vFiltro;
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vStatusLivro = new ArrayList<String>();
    private ArrayList<String> vDescricao = new ArrayList<String>();
    private ArrayList<String> vTransacao = new ArrayList<String>();
    private ArrayList<String> vtBook = new ArrayList<String>();
    private ArrayList<String> vtAuthor = new ArrayList<String>();
    private ArrayList<String> vtPhoto = new ArrayList<String>();
    private ArrayList<String> vtTitle = new ArrayList<String>();
    private ArrayList<String> vtDataCompra = new ArrayList<String>();
    private ArrayList<String> vRastreio = new ArrayList<String>();
    private ArrayList<String> vEmpresa = new ArrayList<String>();

    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<Double> vPrecob = new ArrayList<Double>();
    private ArrayList<Double> vFreteb = new ArrayList<Double>();

    private static String ENDPOINT;
    private CustomBooksMarketVendasListAdapter adapterdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgboovermarketfiltros_fragment, container, false);
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
        btnEntregues = (ImageButton) view.findViewById(R.id.ic_entregue);
        btnEnviados = (ImageButton) view.findViewById(R.id.ic_enviado);
        btnNaoEntregues = (ImageButton) view.findViewById(R.id.ic_nao_enviado);
        TextView lblmyshelf = (TextView) view.findViewById(R.id.lblnomemarket);


        Bundle bundleboovermyshelf = this.getArguments();
        if (bundleboovermyshelf!=null) {
            vBook = bundleboovermyshelf.getString("vBook", null);
            vFiltro = bundleboovermyshelf.getString("vFiltro", null);
        }
        lblmyshelf.setText(getResources().getString(R.string.minhas_vendas));

        vStatus.clear();
        vDescricao.clear();
        vUid.clear();
        vPrecob.clear();
        vFreteb.clear();
        vtBook.clear();
        vtTitle.clear();
        vtPhoto.clear();
        vtAuthor.clear();
        vtDataCompra.clear();
        vRastreio.clear();
        vEmpresa.clear();

        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        if (vBook!=null) {
            Log.e("oi", "fui");
        }else {
                mDatabase.child("Vendas").child(mUid).orderByChild("mordercompra")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot bookDataSnapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot transactionDataSnapshot : bookDataSnapshot.getChildren()) {
                                        vtBook.add(bookDataSnapshot.getKey().toString());
                                        vTransacao.add(transactionDataSnapshot.getKey().toString());
                                        String teRastreio = "", teEmpresa="";
                                        for (DataSnapshot dadosDataSnapshot : transactionDataSnapshot.getChildren()) {
                                            if (dadosDataSnapshot.getKey().equals("title")) {
                                                vtTitle.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("author")) {
                                                vtAuthor.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("photo")) {
                                                vtPhoto.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("datacompra")) {
                                                vtDataCompra.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("cUid")) {
                                                vUid.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("descricao")) {
                                                vDescricao.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("status")) {
                                                vStatus.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("rastreio")) {
                                                teRastreio=dadosDataSnapshot.getValue().toString();
                                            }
                                            if (dadosDataSnapshot.getKey().equals("empresa")) {
                                                teEmpresa=dadosDataSnapshot.getValue().toString();
                                            }
                                            if (dadosDataSnapshot.getKey().equals("statuslivro")) {
                                                vStatusLivro.add(dadosDataSnapshot.getValue().toString());
                                            }
                                            if (dadosDataSnapshot.getKey().equals("freteb")) {
                                                if (dadosDataSnapshot.getValue() != null
                                                        && dadosDataSnapshot.getValue().toString().length() > 0) {
                                                    vFreteb.add(Double.parseDouble(dadosDataSnapshot.getValue().toString()));
                                                } else {
                                                    vFreteb.add(0.0);
                                                }
                                            }
                                            if (dadosDataSnapshot.getKey().equals("precob")) {
                                                if (dadosDataSnapshot.getValue() != null
                                                        && dadosDataSnapshot.getValue().toString().length() > 0) {
                                                    vPrecob.add(Double.parseDouble(dadosDataSnapshot.getValue().toString()));
                                                } else {
                                                    vPrecob.add(0.0);
                                                }
                                            }
                                        }
                                        vEmpresa.add(teEmpresa);
                                        vRastreio.add(teRastreio);

                                    }
                                }
                                if (vFiltro != null) {
                                    for (int i=0; i<vStatus.size(); i++) {
                                        if (!vStatus.get(i).equals(vFiltro)) {
                                                vStatus.remove(i);
                                                vUid.remove(i);
                                                vDescricao.remove(i);
                                                vPrecob.remove(i);
                                                vFreteb.remove(i);
                                                vtBook.remove(i);
                                                vStatusLivro.remove(i);
                                                vTransacao.remove(i);
                                                vtAuthor.remove(i);
                                                vtTitle.remove(i);
                                                vtPhoto.remove(i);
                                                vtDataCompra.remove(i);
                                            try {
                                                vRastreio.remove(i);
                                                vEmpresa.remove(i);
                                            }catch (NullPointerException e) {
                                                e.printStackTrace();
                                            }catch (IndexOutOfBoundsException n){
                                                n.printStackTrace();
                                            }
                                            i--;
                                        }
                                    }
                                }
                                adapterdetails = new CustomBooksMarketVendasListAdapter(context, vUid, vStatus,
                                        vDescricao, vPrecob, vFreteb, vtBook, vStatusLivro, vTransacao,
                                        vtAuthor, vtTitle, vtPhoto, vtDataCompra, vRastreio, vEmpresa);
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


            btnWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FrgBooverWallet fFrag = new FrgBooverWallet();
                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frgboovermarketfiltros_frame, fFrag, "FrgBooverWallet");
                    ft.addToBackStack("FrgBooverWallet");
                    ft.commit();
                }
            });

            btnEntregues.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundlevendas = new Bundle();
                    bundlevendas.putString("vFiltro", getContext().getResources().getString(R.string.status_recebido).toString());
                    FrgBooverMarketVendas fFrag = new FrgBooverMarketVendas();
                    fFrag.setArguments(bundlevendas);
                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frgboovermarketfiltros_frame, fFrag, "FrgBooverMarketVendas");
                    ft.addToBackStack("FrgBooverMarketVendas");
                    ft.commit();
                }
            });

            btnEnviados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundlevendas = new Bundle();
                    bundlevendas.putString("vFiltro", getContext().getResources().getString(R.string.status_enviado).toString());
                    FrgBooverMarketVendas fFrag = new FrgBooverMarketVendas();
                    fFrag.setArguments(bundlevendas);
                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frgboovermarketfiltros_frame, fFrag, "FrgBooverMarketVendas");
                    ft.addToBackStack("FrgBooverMarketVendas");
                    ft.commit();
                }
            });
            btnNaoEntregues.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundlevendas = new Bundle();
                    bundlevendas.putString("vFiltro", getContext().getResources().getString(R.string.status_nao_enviado).toString());
                    FrgBooverMarketVendas fFrag = new FrgBooverMarketVendas();
                    fFrag.setArguments(bundlevendas);
                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frgboovermarketfiltros_frame, fFrag, "FrgBooverMarketVendas");
                    ft.addToBackStack("FrgBooverMarketVendas");
                    ft.commit();
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        }
        return view;
    }

}