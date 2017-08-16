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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverMarket extends Fragment {

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
    private static ListView list;
    private static Activity context;
    private static AlertDialog alerta;
    private ImageButton btnOfertas, btnMinhasOfertas, btnMinhasVendas, btnMinhaReputacao, btnMinhasCompras, btnBack;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vNome,AmazonURL, vBook = "";
    private ArrayList<String> market = new ArrayList<String>();
    private ArrayList<String> vidBook = new ArrayList<String>();
    private ArrayList<String> vTitleBook = new ArrayList<String>();
    private ArrayList<String> vPhotoBook = new ArrayList<String>();
    private ArrayList<String> vMessageBook = new ArrayList<String>();
    private ArrayList<String> vLinkBook = new ArrayList<String>();
    private ArrayList<String> vAuthorBook = new ArrayList<String>();
    private ArrayList<String> vReviewsBook = new ArrayList<String>();
    private ArrayList<String> gCount = new ArrayList<String>();

    private static String ENDPOINT;
    private CustomBooksMarketListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgboovermarket_fragment, container, false);
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        context = getActivity();

        ENDPOINT =  context.getResources().getString(R.string.site_amazon);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        list = (ListView)  view.findViewById (R.id.lstViewMarket);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        vNome = mAuth.getCurrentUser().getDisplayName();
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        btnOfertas = (ImageButton) view.findViewById(R.id.ic_ofertas);
        btnMinhasOfertas = (ImageButton) view.findViewById(R.id.ic_minhas_ofertas);
        btnMinhasVendas = (ImageButton) view.findViewById(R.id.ic_meus_negocios);
        btnMinhasCompras = (ImageButton) view.findViewById(R.id.ic_minhas_compras);
        btnMinhaReputacao = (ImageButton) view.findViewById(R.id.ic_minha_reputacao);
        final TextView lblmyshelf = (TextView) view.findViewById(R.id.lblnomemarket);

        Bundle bundleboovermyshelf = this.getArguments();
        if (bundleboovermyshelf!=null) {
            mUid = bundleboovermyshelf.getString("dUid", "0");
            vNome = bundleboovermyshelf.getString("vNome", "0");
            vBook = bundleboovermyshelf.getString("vBook", "0");
        }else{
            vBook="";
        }
        lblmyshelf.setText(getResources().getString(R.string.ofertas));

        if (vBook.length()>0){
            //JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            market.add(vBook);
            zeraamazon();
            AmazonURL = queryAmazonBooks(market, 1);
            if (AmazonURL!=null) {
               fetchTitle(AmazonURL);
            }
        }
        btnOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vBook="";
                zeraamazon();
                market.clear();
                try {
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException er){
                    er.printStackTrace();
                }
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                lblmyshelf.setText(getResources().getString(R.string.ofertas));
                mDatabase.child("Market")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot uidDataSnapshot : noteDataSnapshot.getChildren()) {
                                        if (!uidDataSnapshot.getKey().equals(mUid)) {
                                            market.add(noteDataSnapshot.getKey());
                                        }
                                    }
                                }
                                for (int i=1;i<=paginas;i++) {
                                    AmazonURL = queryAmazonBooks(market, i);
                                    if (AmazonURL != null) {
                                        fetchTitle(AmazonURL);
                                    }
                                }
                                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                // ...
                            }
                        });
            }
        });

        btnMinhasOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zeraamazon();
                market.clear();
                adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                lblmyshelf.setText(getResources().getString(R.string.minhas_ofertas));
                try{
                    mDatabase.child("Market")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    market.clear();
                                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                        for (DataSnapshot uidDataSnapshot : noteDataSnapshot.getChildren()) {
                                            if (uidDataSnapshot.getKey().equals(mUid)) {
                                                market.add(noteDataSnapshot.getKey());
                                            }
                                        }
                                    }
                                    for (int i = 1; i <= paginas; i++) {
                                        AmazonURL = queryAmazonBooks(market, i);
                                        if (AmazonURL != null) {
                                            fetchTitle(AmazonURL);
                                        }
                                    }
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    // ...
                                }
                            });
                } catch (Throwable t) {
                    t.printStackTrace();
                }

            }
        });
        btnMinhasVendas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverMarketVendas fFrag = new FrgBooverMarketVendas();
                FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frgbooks_frame, fFrag, "FrgBooverMarketVendas");
                ft.addToBackStack("FrgBooverMarketVendas");
                ft.commit();
            }
        });
        btnMinhasCompras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverMarketCompras fFrag = new FrgBooverMarketCompras();
                FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                ft.replace( R.id.frgbooks_frame, fFrag, "FrgBooverMarketCompras");
                ft.addToBackStack("FrgBooverMarketCompras");
                ft.commit();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        btnMinhaReputacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlereputation = new Bundle();
                bundlereputation.putString("dUid", mUid);
                FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundlereputation);
                ft.add(R.id.frgbooks_frame, fFrag, "FrgMeetDetailUserChat");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

    public static String queryAmazonBooks(ArrayList<String> market, Integer pagina) {
        SignedRequestsHelper helper;

        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String requestUrl = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AWSAccessKeyId", "AKIAJOZ36CXGBVERFQOA");
        params.put("AssociateTag", "763967706501");
        params.put("SearchIndex", "Books");
        params.put("Sort", "salesrank");
        params.put("ItemPage", Integer.toString(pagina));
        params.put("Availability", "Available");
        params.put("ResponseGroup", "EditorialReview,Images,ItemAttributes,Offers,SalesRank");
        if (market!=null) {
            String constShelf = "";
            for (int i=0;i<market.size();i++) {
                if (i+1==market.size()) {
                    constShelf = constShelf + market.get(i);
                }else{
                    constShelf = constShelf + market.get(i) + "||";
                }
            }
            params.put("Keywords", constShelf);
        }

        requestUrl = helper.sign(params);
        return requestUrl;
    }

    private void fetchTitle(String requestUrl) {


        NodeList  items, item, dados, reviews, foto, resenha;

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            items = doc.getElementsByTagName("Item");
            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                    item = node.getChildNodes();
                String tMessage="";
                    for ( int z = 0; z < item.getLength(); z++ ) {
                        Node nodez = item.item(z);
                        if (nodez.getNodeName().toString().equals("ASIN")){
                            vidBook.add(nodez.getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("LargeImage")){
                            foto = nodez.getChildNodes();
                            vPhotoBook.add(foto.item(0).getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("SalesRank")){
                            gCount.add(NumberFormat.getInstance().format(Long.parseLong(nodez.getTextContent())));
                        }
                        if (nodez.getNodeName().toString().equals("DetailPageURL")){
                            vLinkBook.add(nodez.getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("ItemAttributes")){
                            dados = nodez.getChildNodes();
                            for ( int x = 0; x < dados.getLength(); x++ ) {
                                Node nodex = dados.item(x);
                                if (nodex.getNodeName().toString().equals("Author")){
                                    vAuthorBook.add(nodex.getTextContent());

                                }
                                if (nodex.getNodeName().toString().equals("Title")){
                                    vTitleBook.add(nodex.getTextContent());
                                }
                            }
                        }
                        if (nodez.getNodeName().toString().equals("EditorialReviews")){
                            tMessage="1";
                            reviews = nodez.getChildNodes();
                            Node content = reviews.item(0).getChildNodes().item(1);
                            if (content.getTextContent().length()>0) {
                                String texto = Html.fromHtml(content.getTextContent()).toString();
                                if (texto.length()>270){
                                    vMessageBook.add(texto.substring(0,270)+"("+context.getResources().getString(R.string.ver_mais)+")");
                                }else{
                                    vMessageBook.add(texto+"("+context.getResources().getString(R.string.ver_mais)+")");
                                }

                            }
                        }
                        if (nodez.getNodeName().toString().equals("ItemLinks")){
                            resenha = nodez.getChildNodes();
                            Node vresenha = resenha.item(2).getChildNodes().item(1);
                            if (vresenha.getTextContent().length()>0) {
                                vReviewsBook.add(vresenha.getTextContent());
                            }
                        }

                    }
                if (!tMessage.equals("1")){
                        vMessageBook.add("..."+"("+context.getResources().getString(R.string.ver_mais)+")");
                }

            }
        } catch (ParserConfigurationException a) {
            a.printStackTrace();
        } catch (IOException x){
            x.printStackTrace();
        } catch (SAXException e ){
            e.printStackTrace();
        }
        adapter = new CustomBooksMarketListAdapter(context, vidBook, vTitleBook, vMessageBook, vPhotoBook, vLinkBook, vReviewsBook, gCount, "market","0",vAuthorBook, mUid);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);

//return titles;

    }

    public void zeraamazon (){
        vidBook.clear();
        vTitleBook.clear();
        vPhotoBook.clear();
        vMessageBook.clear();
        vLinkBook.clear();
        vReviewsBook.clear();
        vAuthorBook.clear();
        gCount.clear();
    }

    public void onStart() {
        super.onStart();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        if (vBook.length()>0){
            market.add(vBook);
            zeraamazon();
            AmazonURL = queryAmazonBooks(market, 1);
            if (AmazonURL!=null) {
                fetchTitle(AmazonURL);
            }
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }else {
            mDatabase.child("Market")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            market.clear();
                            for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot uidDataSnapshot : noteDataSnapshot.getChildren()) {
                                    if (!uidDataSnapshot.getKey().equals(mUid)) {
                                        market.add(noteDataSnapshot.getKey());
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            // ...
                        }
                    });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(ProgressBar.VISIBLE);
                    zeraamazon();
                    for (int i = 1; i <= paginas; i++) {
                        AmazonURL = queryAmazonBooks(market, i);
                        if (AmazonURL != null) {
                            fetchTitle(AmazonURL);
                        }
                    }
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }
            }, 1000);
        }
    }

}