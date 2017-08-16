package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomBooksMarketDetailsListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vDescricao = new ArrayList<String>();
    private ArrayList<String> vUid = new ArrayList<String>();
    private ArrayList<Double> vPrecob = new ArrayList<Double>();
    private ArrayList<Double> vFreteb = new ArrayList<Double>();
    private ArrayList<Double> vTotalb = new ArrayList<Double>();

    private String morder, vbody, back, vData, mNotific;
    private Integer notCounter;
    private DatabaseReference mDatabase;
    private Integer bStars;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String vidBook, vTitleBook, vAuthorBook, vPhotoBook, mUid, vNomeVendedor;
    private Double vtotalb, vsaldob=0.00, vbloqueadob=0.00;
    private static final String APPLICATION_NAME = "Boover";

    public CustomBooksMarketDetailsListAdapter(Activity context, ArrayList<String> vUid, String vTitleBook,
                                               String vPhotoBook, ArrayList<String> vStatus, ArrayList<String> vDescricao,
                                               ArrayList<Double> vPrecob, ArrayList<Double> vFreteb,
                                               String vAuthorBook, String vidBook) {
        super(context, R.layout.booksmarketdetailslistview, vUid);
        this.context=context;
        this.vidBook=vidBook;
        this.vTitleBook=vTitleBook;
        this.vPhotoBook=vPhotoBook;
        this.vAuthorBook = vAuthorBook;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.vData = Long.toString(new Date().getTime());
        this.vUid=vUid;
        this.vStatus=vStatus;
        this.vDescricao=vDescricao;
        this.vPrecob=vPrecob;
        this.vFreteb=vFreteb;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.booksmarketdetailslistview, null,true);
        View alertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        final View compraView = (View) inflater.inflate(R.layout.commentslistview, null);
        final ListView list = (ListView) rowView.findViewById(R.id.listView1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        vNomeVendedor = mAuth.getCurrentUser().getDisplayName();
        final DecimalFormat precision = new DecimalFormat("#,##0.00");
        TextView txtDescricao = (TextView) rowView.findViewById(R.id.itemmessage);
        txtDescricao.setText(vDescricao.get(position));
        TextView txtPrecob = (TextView) rowView.findViewById(R.id.txtPrecob);
        txtPrecob.setText(precision.format(vPrecob.get(position)));
        TextView txtFreteb = (TextView) rowView.findViewById(R.id.txtFreteb);
        txtFreteb.setText(precision.format(vFreteb.get(position)));
        TextView txtStatus = (TextView) rowView.findViewById(R.id.statusbook);
        txtStatus.setText(context.getResources().getString(R.string.livro)+": "+vStatus.get(position));
        final TextView txtNomeVendedor = (TextView) rowView.findViewById(R.id.txt_nome_vendedor);
        TextView txtTotalb = (TextView) rowView.findViewById(R.id.txtTotalb);
        TextView txtReputacao = (TextView) rowView.findViewById(R.id.ic_reputacao_vendedor);
        LinearLayout llPrecob = (LinearLayout) rowView.findViewById(R.id.llprecob);
        Button btnPagarB = (Button) rowView.findViewById(R.id.btnPagarB);

        vtotalb = vFreteb.get(position)+vPrecob.get(position);
        txtTotalb.setText(precision.format(vtotalb));
        vTotalb.add(vtotalb);

        mDatabase.child("Users").child(vUid.get(position)).child("nome")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue()!=null){
                            txtNomeVendedor.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        final ImageButton btnRemove = (ImageButton) rowView.findViewById(R.id.ic_remove_bookshelf);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                builder.setTitle(R.string.excluir_livro);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_retirar_book_market);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabase.child("Market").child(vidBook).child(mUid).removeValue();
                        mDatabase.child("Books").child(mUid).child(vidBook).child("Troco").setValue("0");
                        Toast.makeText(context,R.string.livro_removido, Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alerta.dismiss();
                    }
                });
                alerta = builder.create();
                alerta.show();
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


        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vTitleBook);

        TextView txtAuthor = (TextView) rowView.findViewById(R.id.itemauthor);
        txtAuthor.setText(vAuthorBook);

        ImageView imgChannel = (ImageView) rowView.findViewById(R.id.iconBook);
        if (vPhotoBook.length()>0) {
                Glide.with(getContext())
                        .load(vPhotoBook)
                        .into(imgChannel);
        }


        TextView txtVendedor = (TextView) rowView.findViewById(R.id.ic_reputacao_vendedor);
        txtVendedor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleMyShelf = new Bundle();
                bundleMyShelf.putString("vBook", vidBook);
                FrgBooverMarket fFrag = new FrgBooverMarket();
                FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMyShelf);
                ft.replace(R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                ft.addToBackStack("FrgBooverMarket");
                ft.commit();

            }
        });

        mDatabase.child("Wallet").child(mUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
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


        btnPagarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (vsaldob<vTotalb.get(position)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                    builder.setTitle(R.string.saldob_insuficiente);
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    builder.setMessage(R.string.mensagem_saldob_insuficiente);
                    builder.setPositiveButton(R.string.minha_carteira, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            FrgBooverWallet fFrag = new FrgBooverWallet();
                            android.support.v4.app.FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                            ft.add( R.id.frgbooks_frame, fFrag, "FrgBooverWallet");
                            ft.addToBackStack("FrgBooverWallet");
                            ft.commit();

                        }
                    });
                    builder.setNegativeButton(R.string.sair, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alerta.dismiss();
                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                    builder.setTitle(getContext().getResources().getString(R.string.confirmacao_compra));
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    builder.setMessage(getContext().getResources().getString(R.string.valor_compra)+" BR$: " +precision.format(vTotalb.get(position))+".\n\n"+getContext().getResources().getString(R.string.mensagem_confirmacao_compra));
                    builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            mDatabase.child("Wallet").child(mUid).child("saldob").setValue(Double.parseDouble(precision.format(vsaldob-vTotalb.get(position))));
                            mDatabase.child("Wallet").child(mUid).child("bloqueadob").setValue(Double.parseDouble(precision.format(vbloqueadob+vTotalb.get(position))));
                            mDatabase.child("Wallet").child(vUid.get(position)).child("bloqueadob").setValue(Double.parseDouble(precision.format(vbloqueadob+vTotalb.get(position))));
                            String key = mDatabase.child("Compras").child(mUid).child(vidBook).push().getKey();


                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("precob").setValue(Double.parseDouble(precision.format(vPrecob.get(position))));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("freteb").setValue(Double.parseDouble(precision.format(vFreteb.get(position))));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("datacompra").setValue(vData);
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("mordercompra").setValue(morder);
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("status").setValue(getContext().getResources().getString(R.string.status_nao_enviado));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("vUid").setValue(vUid.get(position));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("descricao").setValue(vDescricao.get(position));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("statuslivro").setValue(vStatus.get(position));
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("title").setValue(vTitleBook);
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("author").setValue(vAuthorBook);
                            mDatabase.child("Compras").child(mUid).child(vidBook).child(key).child("photo").setValue(vPhotoBook);

                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("precob").setValue(Double.parseDouble(precision.format(vPrecob.get(position))));
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("freteb").setValue(Double.parseDouble(precision.format(vFreteb.get(position))));
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("datacompra").setValue(vData);
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("mordercompra").setValue(morder);
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("status").setValue(getContext().getResources().getString(R.string.status_nao_enviado));
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("cUid").setValue(mUid);
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("descricao").setValue(vDescricao.get(position));
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("statuslivro").setValue(vStatus.get(position));
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("title").setValue(vTitleBook);
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("author").setValue(vAuthorBook);
                            mDatabase.child("Vendas").child(vUid.get(position)).child(vidBook).child(key).child("photo").setValue(vPhotoBook);

                            mDatabase.child("Books").child(vUid.get(position)).child(vidBook).child("Troco").setValue("0");
                            mDatabase.child("Market").child(vidBook).child(vUid.get(position)).removeValue();

                            notCounter++;
                            mDatabase.child("Notifications").child(vUid.get(position)).child("notCount").setValue(Integer.toString(notCounter));
                            mDatabase.child("Notifications").child(vUid.get(position)).child(mUid)
                                    .setValue(getContext().getResources().getString(R.string.seu_livro_foi_vendido)
                                    +": "+vTitleBook
                                    +"\n"+mNotific);

                            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                            builder.setTitle(R.string.compra_efetuada);
                            builder.setIcon(R.drawable.ic_boover_rounded);
                            builder.setMessage(R.string.mensagem_compra_efetuada);
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    Toast.makeText(context,R.string.compra_efetuada, Toast.LENGTH_SHORT).show();
                                    context.getFragmentManager().popBackStack();
                                    context.getFragmentManager().popBackStack();
                                    FrgBooverMarket fFrag = new FrgBooverMarket();
                                    android.support.v4.app.FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                                    ft.addToBackStack("FrgBooverMarket");
                                    ft.commit();
                                    alerta.dismiss();
                                }
                            });
                            alerta = builder.create();
                            alerta.show();
                        }
                    });
                    builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alerta.dismiss();
                        }
                    });
                    alerta = builder.create();
                    alerta.show();
                }
            }
        });

        txtReputacao.setOnClickListener(new View.OnClickListener() {
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

        return rowView;
    }

}
