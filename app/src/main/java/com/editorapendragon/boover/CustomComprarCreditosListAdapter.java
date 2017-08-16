package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CustomComprarCreditosListAdapter extends ArrayAdapter<String> {

    private static final int RESULT_OK = 1;
    private final Activity context;
    private ArrayList<String> vProductName = new ArrayList<String>();
    private ArrayList<String> vProdutctId = new ArrayList<String>();
    private ArrayList<String> vPrice = new ArrayList<String>();


    private String morder, vbody, back, vData, mNotific;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String vTitleBook, mUid, vNomeVendedor;
    private Double vtotal=0.00d;
    private IInAppBillingService mService;

    public CustomComprarCreditosListAdapter(Activity context, ArrayList<String> vProdutctId,
                                            ArrayList<String> vProductName,
                                            ArrayList<String> vPrice,IInAppBillingService mService) {
        super(context, R.layout.booksmarketdetailslistview, vProdutctId);
        this.context=context;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.vProdutctId=vProdutctId;
        this.vProductName=vProductName;
        this.vPrice=vPrice;
        this.mService=mService;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.comprarcreditoslistview, null,true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        final DecimalFormat precision = new DecimalFormat("#,##0.00");

        TextView txtProductId = (TextView) rowView.findViewById(R.id.txtProductId);
        TextView txtProductName = (TextView) rowView.findViewById(R.id.txtProductName);
        Button btnComprar = (Button) rowView.findViewById(R.id.btnComprar);
        Locale current = getContext().getResources().getConfiguration().locale;

        if (vProdutctId.size()>position) {
            txtProductId.setText(vProdutctId.get(position));
        }
        if (vProductName.size()>position) {
            txtProductName.setText(vProductName.get(position));
        }
        if (vPrice.size()>position) {
            btnComprar.setText(vPrice.get(position));
        }

        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.e("buy",vProdutctId.get(position));
                    Bundle buyIntentBundle = mService.getBuyIntent(3, context.getPackageName(),
                            vProdutctId.get(position), "inapp", "b@@ver33418095");
                    if (buyIntentBundle!=null) {
                        Globals.vIntentFoto="1";
                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                        context.startIntentSenderForResult(pendingIntent.getIntentSender(),
                                1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                Integer.valueOf(0));
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        return rowView;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Toast.makeText(context, "\"You have bought the \" + sku + \". Excellent choice,\n" +
                            "                            adventurer!\"", Toast.LENGTH_SHORT).show();
                    int response = mService.consumePurchase(3, context.getPackageName(), jo.getString("token"));
                } catch (JSONException e) {
                    Toast.makeText(context, "Failed to parse purchase data.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
