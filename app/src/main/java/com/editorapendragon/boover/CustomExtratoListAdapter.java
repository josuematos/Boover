package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

public class CustomExtratoListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vStatus = new ArrayList<String>();
    private ArrayList<String> vTransacao = new ArrayList<String>();
    private ArrayList<String> vtData = new ArrayList<String>();
    private ArrayList<Double> vValor = new ArrayList<Double>();
    private ArrayList<Double> vSaldo = new ArrayList<Double>();
    private ArrayList<String> vTipo = new ArrayList<String>();


    private String morder, vbody, back, vData, mNotific;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String vTitleBook, mUid, vNomeVendedor;
    private Double vtotal=0.00d;

    public CustomExtratoListAdapter(Activity context, ArrayList<String> vStatus,
                                    ArrayList<String> vTransacao,
                                    ArrayList<String> vtData, ArrayList<Double> vValor,
                                    ArrayList<Double> vSaldo, ArrayList<String> vTipo) {
        super(context, R.layout.booksmarketdetailslistview, vStatus);
        this.context=context;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.vtData = vtData;
        this.vStatus=vStatus;
        this.vTransacao=vTransacao;
        this.vValor=vValor;
        this.vTipo=vTipo;
        this.vSaldo=vSaldo;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.extratolistview, null,true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        final DecimalFormat precision = new DecimalFormat("#,##0.00");

        TextView txtData = (TextView) rowView.findViewById(R.id.txtdata);
        TextView txtTransacao = (TextView) rowView.findViewById(R.id.txtTrasancao);
        TextView txtStatus = (TextView) rowView.findViewById(R.id.txtStatus);
        TextView txtValor = (TextView) rowView.findViewById(R.id.txtvalor);
        TextView txtSaldo = (TextView) rowView.findViewById(R.id.txtsaldo);
        Locale current = getContext().getResources().getConfiguration().locale;

        if (vtData.size()>position) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d yyyy", current);
            String mData = sdf.format(new Date(Long.parseLong(vtData.get(position))*-1));
            txtData.setText(mData);
        }
        if (vTransacao.size()>position) {
            txtTransacao.setText(vTransacao.get(position));
        }
        if (vStatus.size()>position) {
            txtStatus.setText(vTipo.get(position)+"\n"
                    +getContext().getResources().getString(R.string.status)+": "
                    +vStatus.get(position));
        }
        if (vValor.size()>position) {
            if (vTipo.get(position)!=null) {
                if (vTipo.get(position).equals(getContext().getResources().getString(R.string.status_extrato_debito).toString())
                        || vTipo.get(position).equals(getContext().getResources().getString(R.string.status_extrato_compra).toString())) {

                    txtValor.setTextColor(Color.RED);
                }
            }
            txtValor.setText(precision.format(vValor.get(position)));
        }
        if (vSaldo.size()>position) {
            txtSaldo.setText(precision.format(vSaldo.get(position)));
        }

        return rowView;
    }

}
