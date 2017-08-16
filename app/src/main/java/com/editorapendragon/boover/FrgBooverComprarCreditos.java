package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverComprarCreditos extends Fragment {

    private Handler handler;
    private static ProgressBar mProgressBar;
    private static ListView listdetails;
    private static Activity context;
    private ImageButton  btnBack;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vBook, vNome;
    private ArrayList<String> vProductId = new ArrayList<String>();
    private ArrayList<String> vProductName = new ArrayList<String>();
    private ArrayList<String> vPrice = new ArrayList<String>();

    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;


    private static String ENDPOINT;
    private CustomComprarCreditosListAdapter adapterdetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgcomprarcreditos_fragment, container, false);
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        context = getActivity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listdetails= (ListView) view.findViewById (R.id.lstViewComprar);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);
        final DecimalFormat precision = new DecimalFormat("#,##0.00");

       btnBack.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               getFragmentManager().popBackStack();
           }
       });

       return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        Globals.vIntentFoto="0";
        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
                Toast.makeText(context, "Billing Not Activaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
                if (mService!=null) {
                    ArrayList<String> skuList = new ArrayList<String>();
                    skuList.clear();
                    skuList.add("boover_100");
                    skuList.add("boover_500");
                    skuList.add("boover_1000");
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                    try {
                        Bundle skuDetails = mService.getSkuDetails(3,
                                context.getPackageName(), "inapp", querySkus);
                        int response = skuDetails.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> responseList
                                    = skuDetails.getStringArrayList("DETAILS_LIST");

                            for (String thisResponse : responseList) {
                                JSONObject object = new JSONObject(thisResponse);
                                String sku = object.getString("productId");
                                String price = object.getString("price");
                                String desc = object.getString("description");
                                if (sku.equals("boover_100")) {
                                    vProductId.add("boover_100");
                                    vProductName.add(desc);
                                    vPrice.add(price);

                                } else if (sku.equals("boover_500")) {
                                    vProductId.add("boover_500");
                                    vProductName.add(desc);
                                    vPrice.add(price);

                                } else if (sku.equals("boover_1000")) {
                                    vProductId.add("boover_1000");
                                    vProductName.add(desc);
                                    vPrice.add(price);

                                }
                            }
                            adapterdetails = new CustomComprarCreditosListAdapter(context, vProductId, vProductName, vPrice, mService);
                            listdetails.setAdapter(adapterdetails);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        context.bindService(serviceIntent, mServiceConn, getApplicationContext().BIND_AUTO_CREATE);
    }
}