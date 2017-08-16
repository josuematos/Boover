package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomBooksMarketComprasListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vStatusLivro = new ArrayList<String>();
    private ArrayList<String> vDescricao = new ArrayList<String>();
    private ArrayList<String> vAuthor = new ArrayList<String>();
    private ArrayList<String> vTitle = new ArrayList<String>();
    private ArrayList<String> vPhoto = new ArrayList<String>();
    private ArrayList<String> vTransacao = new ArrayList<String>();
    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<String> vidBook = new ArrayList<String>();
    private ArrayList<String> vDataCompra = new ArrayList<String>();

    private ArrayList<Double> vPrecob = new ArrayList<Double>();
    private ArrayList<Double> vFreteb = new ArrayList<Double>();
    private ArrayList<Double> vTotalb = new ArrayList<Double>();
    private ArrayList<String> vRastreio = new ArrayList<String>();
    private ArrayList<String> vEmpresa = new ArrayList<String>();
    private ArrayList<String> vTextoQualificacao = new ArrayList<String>();
    private ArrayList<Float> bStars = new ArrayList<Float>();
    private Integer notCounter;


    private String morder, vbody, back, vData, mNotific;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String vTitleBook, mUid, vNomeVendedor;
    private Double vtotalb, vsaldob=0.00, vbloqueadob=0.00;
    private static final String APPLICATION_NAME = "Boover";

    public CustomBooksMarketComprasListAdapter(Activity context, ArrayList<String> vUid,
                                               ArrayList<String> vStatus, ArrayList<String> vDescricao,
                                               ArrayList<Double> vPrecob, ArrayList<Double> vFreteb,
                                               ArrayList<String> vidBook,
                                               ArrayList<String> vStatusLivro, ArrayList<String> vTransacao,
                                               ArrayList<String> vAuthor, ArrayList<String> vTitle,
                                               ArrayList<String> vPhoto, ArrayList<String> vDataCompra,
                                               ArrayList<String> vRastreio, ArrayList<String> vEmpresa) {
        super(context, R.layout.booksmarketdetailslistview, vUid);
        this.context=context;
        this.vidBook=vidBook;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.vData = Long.toString(new Date().getTime());
        this.vUid=vUid;
        this.vStatus=vStatus;
        this.vDescricao=vDescricao;
        this.vPrecob=vPrecob;
        this.vFreteb=vFreteb;
        this.vStatusLivro=vStatusLivro;
        this.vTransacao=vTransacao;
        this.vTitle=vTitle;
        this.vAuthor=vAuthor;
        this.vPhoto=vPhoto;
        this.vDataCompra=vDataCompra;
        this.vRastreio=vRastreio;
        this.vEmpresa=vEmpresa;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.booksmarketcompraslistview, null,true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        final DecimalFormat precision = new DecimalFormat("0.00");


        TextView txtData = (TextView) rowView.findViewById(R.id.txtdatavenda);
        TextView txtPrazo = (TextView) rowView.findViewById(R.id.txtprazoentrega);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.txtstatus);
        Locale current = getContext().getResources().getConfiguration().locale;

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d (HH:mm)", current);
        String mData = sdf.format(new Date(Long.parseLong(vDataCompra.get(position))));
        txtData.setText(mData);

        // Através do Calendar, trabalhamos a data informada e adicionamos 1 dia nela
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(Long.parseLong(vDataCompra.get(position))));
        c.add(Calendar.DATE, +14);
        mData = sdf.format(c.getTime());
        txtPrazo.setText(mData);

        txtStatus.setText(vStatus.get(position));
        TextView txtPreco = (TextView) rowView.findViewById(R.id.txtPreco);
        TextView txtFrete = (TextView) rowView.findViewById(R.id.txtFrete);
        TextView lblPreco = (TextView) rowView.findViewById(R.id.lblpreco);
        TextView lblFrete = (TextView) rowView.findViewById(R.id.lblfrete);
        TextView lblTotal = (TextView) rowView.findViewById(R.id.lbltotal);
        TextView txtStatusLivro = (TextView) rowView.findViewById(R.id.statusbook);
        txtStatusLivro.setText(context.getResources().getString(R.string.livro)+": "+vStatusLivro.get(position));
        TextView txtTotal = (TextView) rowView.findViewById(R.id.txtTotal);
        final TextView txtNomeComprador = (TextView) rowView.findViewById(R.id.txt_nome_comprador);
        Button btninformarEnvio = (Button) rowView.findViewById(R.id.btnInformarEnvio);
        Button btnQualificar = (Button) rowView.findViewById(R.id.btnQualificarComprador);

        String actStatus = getContext().getResources().getString(R.string.status_recebido).toString();
        if (!vStatus.get(position).equals(actStatus)) {
            btnQualificar.setText(getContext().getResources().getString(R.string.informar_recebimento));
        }else{
            btnQualificar.setText(getContext().getResources().getString(R.string.qualificar_vendedor));
        }



        if (vPrecob.size()>position && vFreteb.size()>position) {
            vtotalb = vFreteb.get(position) + vPrecob.get(position);
        }else{
            vtotalb = 0.0;
        }
        vTotalb.add(vtotalb);
        if (vtotalb>0.0){
            txtPreco.setText(precision.format(vPrecob.get(position)));
            txtFrete.setText(precision.format(vFreteb.get(position)));
            txtTotal.setText(precision.format(vtotalb));
            lblFrete.setText(getContext().getResources().getString(R.string.freteb));
            lblPreco.setText(getContext().getResources().getString(R.string.precob));
            lblTotal.setText(getContext().getResources().getString(R.string.totalb));
        }

        mDatabase.child("Users").child(vUid.get(position)).child("nome")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            txtNomeComprador.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        TextView txtDescricao = (TextView) rowView.findViewById(R.id.itemmessage);
        txtDescricao.setText(getContext().getResources().getString(R.string.autor)+" "+vAuthor.get(position)+"\n\n"+vDescricao.get(position));

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vTitle.get(position));

        TextView txtAuthor = (TextView) rowView.findViewById(R.id.itemauthor);
        txtAuthor.setText(getContext().getResources().getString(R.string.transacao) +vTransacao.get(position));

        ImageView imgChannel = (ImageView) rowView.findViewById(R.id.iconBook);
        if (vPhoto.size()>position) {
                Glide.with(getContext())
                        .load(vPhoto.get(position))
                        .into(imgChannel);
        }


        TextView txtComprador = (TextView) rowView.findViewById(R.id.ic_reputacao_comprador);
        txtComprador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vUid.size()> position) {
                    Bundle bundlereputation = new Bundle();
                    bundlereputation.putString("dUid", vUid.get(position));
                    FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                    android.support.v4.app.FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    fFrag.setArguments(bundlereputation);
                    ft.add(R.id.frgbooks_frame, fFrag, "FrgMeetDetailUserChat");
                    ft.addToBackStack(null);
                    ft.commit();
                }
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
                                    vsaldob= Double.parseDouble(walletDataSnapshot.getValue().toString());
                                }
                            }
                            if (walletDataSnapshot.getKey().equals("bloqueadob")){
                                if (walletDataSnapshot.getValue() != null
                                        && walletDataSnapshot.getValue().toString().length() > 0) {
                                    vbloqueadob= Double.parseDouble(walletDataSnapshot.getValue().toString());
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                .child(mUid).child(vTransacao.get(position)).orderByChild("morder")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot reputationDataSnapshot : dataSnapshot.getChildren()) {
                            if (reputationDataSnapshot.getKey().equals("gStar")){
                                if (reputationDataSnapshot.getValue() != null
                                        && reputationDataSnapshot.getValue().toString().length() > 0) {
                                    bStars.add(Float.parseFloat(reputationDataSnapshot.getValue().toString()));
                                }
                            }
                            if (reputationDataSnapshot.getKey().equals("text")){
                                if (reputationDataSnapshot.getValue() != null) {
                                    vTextoQualificacao.add(reputationDataSnapshot.getValue().toString());
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        notCounter = 0;
        mDatabase.child("Notifications").child(vUid.get(position)).child("notCounter")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            notCounter = Integer.parseInt(dataSnapshot.getValue().toString());
                        } else {
                            notCounter = 0;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        mDatabase.child("Notifications").child(vUid.get(position)).child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null) {
                            mNotific = dataSnapshot.getValue().toString();
                        } else {
                            mNotific = "";
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });


        btninformarEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater factory = LayoutInflater.from(context);
                final View textEntryView = factory.inflate(R.layout.layout_informacoes_envio, null);
                final EditText rastreio = (EditText) textEntryView.findViewById(R.id.txtRastreio);
                final EditText empresa = (EditText) textEntryView.findViewById(R.id.txtEmpresa);
                rastreio.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                empresa.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                rastreio.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                rastreio.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                empresa.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                empresa.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                if (vRastreio.size()>position){
                    rastreio.setText(vRastreio.get(position));
                }
                if (vEmpresa.size()>position){
                    empresa.setText(vEmpresa.get(position));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                    builder.setTitle(getContext().getResources().getString(R.string.dados_envio));
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    builder.setView(textEntryView);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {


                        }
                    });
                    builder.show();
            }
        });

        btnQualificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialogCustom);
                builder.setIcon(R.drawable.ic_boover_rounded);
                String actStatus = getContext().getResources().getString(R.string.status_recebido).toString();
                if (!vStatus.get(position).equals(actStatus)) {
                    builder.setTitle(getContext().getResources().getString(R.string.informar_recebimento));
                    builder.setMessage(getContext().getResources().getString(R.string.texto_informar_recebimento));
                    builder.setPositiveButton(getContext().getResources().getString(R.string.qualificar_vendedor), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            LayoutInflater factory = LayoutInflater.from(context);
                            final View textEntryView = factory.inflate(R.layout.layout_qualificacao, null);
                            final RatingBar rating = (RatingBar) textEntryView.findViewById(R.id.txtRating);
                            final EditText textorating = (EditText) textEntryView.findViewById(R.id.txtTextoQualificacao);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialogCustom);
                            builder.setTitle(getContext().getResources().getString(R.string.qualificar_vendedor));
                            builder.setIcon(R.drawable.ic_boover_rounded);
                            builder.setView(textEntryView);
                            builder.setPositiveButton(getContext().getResources().getString(R.string.enviar_qualificacao), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {

                                    mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                            .child(mUid).child(vTransacao.get(position)).child("gStar").setValue(rating.getRating());
                                    mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                            .child(mUid).child(vTransacao.get(position)).child("text").setValue(textorating.getText().toString());
                                    mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                            .child(mUid).child(vTransacao.get(position)).child("morder").setValue(morder);

                                    mDatabase.child("Compras").child(mUid).child(vidBook.get(position))
                                            .child(vTransacao.get(position)).child("status")
                                            .setValue(getContext().getResources().getString(R.string.status_recebido));
                                    mDatabase.child("Compras").child(mUid).child(vidBook.get(position))
                                            .child(vTransacao.get(position)).child("datarecebimento")
                                            .setValue(morder);
                                    mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook.get(position))
                                            .child(vTransacao.get(position)).child("status")
                                            .setValue(getContext().getResources().getString(R.string.status_recebido));
                                    mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook.get(position))
                                            .child(vTransacao.get(position)).child("datarecebimento")
                                            .setValue(morder);


                                    vsaldob = vsaldob + vtotalb;
                                    vbloqueadob = vbloqueadob - vtotalb;
                                    mDatabase.child("Wallet").child(vUid.get(position)).child("bloqueadob").setValue(Double.parseDouble(precision.format(vbloqueadob)));
                                    mDatabase.child("Wallet").child(vUid.get(position)).child("saldob").setValue(Double.parseDouble(precision.format(vsaldob)));
                                    mDatabase.child("Wallet").child(mUid).child("bloqueadob").setValue(Double.parseDouble(precision.format(vbloqueadob)));
                                    String vCredito = "";
                                    if (vtotalb>0){


                                        mDatabase.child("Wallet").child(vUid.get(position)).child("extrato")
                                                 .child(vTransacao.get(position)).child("valor").setValue(Double.parseDouble(precision.format(vtotalb)));
                                        mDatabase.child("Wallet").child(vUid.get(position)).child("extrato")
                                                .child(vTransacao.get(position)).child("datasolicitacao").setValue(morder);
                                        try {
                                            mDatabase.child("Wallet").child(vUid.get(position)).child("extrato")
                                                    .child(vTransacao.get(position)).child("status")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_venda));
                                            mDatabase.child("Wallet").child(vUid.get(position)).child("extrato")
                                                    .child(vTransacao.get(position)).child("tipo")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                        } catch (NullPointerException e){
                                            e.printStackTrace();
                                        }

                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                .child(vTransacao.get(position)).child("valor").setValue(Double.parseDouble(precision.format(vtotalb)));
                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                .child(vTransacao.get(position)).child("datasolicitacao").setValue(morder);
                                        try {
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child(vTransacao.get(position)).child("status")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_compra));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child(vTransacao.get(position)).child("tipo")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_debito));
                                        } catch (NullPointerException e){
                                            e.printStackTrace();
                                        }

                                        vCredito = "BR$ "+precision.format(vtotalb);
                                    }
                                    notCounter++;
                                    mDatabase.child("Notifications").child(vUid.get(position)).child("notCount").setValue(Integer.toString(notCounter));
                                    mDatabase.child("Notifications").child(vUid.get(position)).child(mUid).setValue(getContext().getResources().getString(R.string.voce_foi_qualificado)
                                            +" "+vTitle.get(position)
                                            +"\n "+getContext().getResources().getString(R.string.foi_creditado_em_sua_conta)
                                            +" "+ vCredito
                                            +"\n"+mNotific);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialogCustom);
                                    builder.setTitle(R.string.enviada_qualificacao);
                                    builder.setIcon(R.drawable.ic_boover_rounded);
                                    builder.setMessage(R.string.mensagem_qualificacao_envio);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            context.getFragmentManager().popBackStack();
                                            context.getFragmentManager().popBackStack();
                                            FrgBooverMarket fFrag = new FrgBooverMarket();
                                            FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                            ft.replace(R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                                            ft.addToBackStack("FrgBooverMarket");
                                            ft.commit();
                                            alerta.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            alerta.dismiss();
                                        }
                                    });
                                    alerta = builder.create();
                                    alerta.show();
                                }
                            });
                            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    alerta.dismiss();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }

                    });
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alerta.dismiss();
                        }
                    });

                    alerta = builder.create();
                    alerta.show();
                } else {
                    LayoutInflater factory = LayoutInflater.from(context);
                    final View textEntryView = factory.inflate(R.layout.layout_qualificacao, null);
                    final RatingBar rating = (RatingBar) textEntryView.findViewById(R.id.txtRating);
                    final EditText textorating = (EditText) textEntryView.findViewById(R.id.txtTextoQualificacao);
                    if (bStars.size()>position){
                        rating.setRating(bStars.get(position));
                    }
                    if (vTextoQualificacao.size()>position){
                        textorating.setText(vTextoQualificacao.get(position));
                    }
                    builder.setTitle(getContext().getResources().getString(R.string.qualificar_vendedor));
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    builder.setView(textEntryView);
                    builder.setPositiveButton(getContext().getResources().getString(R.string.enviar_qualificacao), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                    .child(mUid).child(vTransacao.get(position)).child("gStar").setValue(rating.getRating());
                            mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                    .child(mUid).child(vTransacao.get(position)).child("text").setValue(textorating.getText().toString());
                            mDatabase.child("Reputation").child(vUid.get(position)).child("comments")
                                    .child(mUid).child(vTransacao.get(position)).child("morder").setValue(morder);

                            notCounter++;
                            mDatabase.child("Notifications").child(vUid.get(position)).child("notCount").setValue(Integer.toString(notCounter));
                            mDatabase.child("Notifications").child(vUid.get(position)).child(mUid).setValue(getContext().getResources().getString(R.string.voce_foi_qualificado)
                                    +" "+vTitle.get(position)+"\n"+mNotific);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialogCustom);
                            builder.setTitle(R.string.enviada_qualificacao);
                            builder.setIcon(R.drawable.ic_boover_rounded);
                            builder.setMessage(R.string.mensagem_qualificacao_envio);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    context.getFragmentManager().popBackStack();
                                    context.getFragmentManager().popBackStack();
                                    FrgBooverMarket fFrag = new FrgBooverMarket();
                                    FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                                    ft.addToBackStack("FrgBooverMarket");
                                    ft.commit();
                                    alerta.dismiss();
                                }
                            });

                        }
                    });
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alerta.dismiss();
                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                }

            }
        });

        return rowView;
    }

}
