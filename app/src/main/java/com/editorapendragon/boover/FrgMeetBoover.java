package com.editorapendragon.boover;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgMeetBoover extends Fragment {

    private static final String TAG = "FrgMeetBoover";
    private DatabaseReference mDatabaseUsers, mDatabaseMUsers;
    private DatabaseReference mDatabasePhotos, mDatabasePosts, mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private GridView gridview;
    private ArrayList<String> vUri = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> vUser = new ArrayList<String>();
    private ArrayList<String> vtNome = new ArrayList<String>();
    private ArrayList<String> vtStatus = new ArrayList<String>();
    private Handler handler;
    private ImageAdapterAllUsers imgAduser;
    private ImageButton btnReload;
    //private ArrayList<String> vUsers;
    private String mUid, vFiltro, vPosition, mCity, mSubCity, mCategoria, vEmail, mEmail;
    private String vLocalizacao, vProcurando, vNome = "", vStatus, vInteresse, oInteresse, vSexo, oSexo;
    private String dUid = "";
    private String vResenha = "off";
    private ProgressBar mProgressBar;
    Double mvlat = 0.0000, mvlng = 0.0000;
    private HashMap<String, List<FiltroMeetBoover>> lstItensGrupo = new HashMap<>();
    private List<String> lstGrupos = new ArrayList<>();
    private List<FiltroMeetBoover> lstItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgmeetboover_fragment, container, false);
        //getFragmentManager().beginTransaction().addToBackStack("FrgMeetBoover").commit();

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabasePhotos = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseMUsers = FirebaseDatabase.getInstance().getReference("Users");
        mDatabasePosts = FirebaseDatabase.getInstance().getReference("posts");
        mDatabasePhotos.orderByKey();
        mDatabasePhotos.limitToLast(9);
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        vUri.clear();
        vtNome.clear();
        vtUser.clear();
        vtStatus.clear();
        imgAduser = new ImageAdapterAllUsers(getContext());
        gridview = (GridView) view.findViewById(R.id.gridviewmeet);

        mDatabaseUsers.child(mUid).child("categoria")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue()!=null) {
                        mCategoria = dataSnapshot.getValue().toString();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /* todos os boovers */
        /*mDatabaseUsers.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot UserdataSnapshot) {
                        vUri.clear();
                        vtNome.clear();
                        vtUser.clear();
                        vtStatus.clear();
                        for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                            if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {

                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                    if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                        vProcurando = ValueUserdataSnapshot.getValue().toString();
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                        vtNome.add(ValueUserdataSnapshot.getValue().toString());
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                        vtStatus.add(ValueUserdataSnapshot.getValue().toString());
                                    }
                                }
                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }else{
                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                    if (ValueUserdataSnapshot.getKey().toString().equals("interesse")) {
                                        oInteresse = ValueUserdataSnapshot.getValue().toString();
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("Sexo")) {
                                        oSexo = ValueUserdataSnapshot.getValue().toString();
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("categoria")) {
                                        mCategoria = ValueUserdataSnapshot.getValue().toString();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        vFiltro = getResources().getString(R.string.todos_boovers);
        */
        /* todos os boovers */



        /* Boovers OnLine */
        mDatabaseUsers.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot UserdataSnapshot) {
                        vUri.clear();
                        vtNome.clear();
                        vtUser.clear();
                        vtStatus.clear();
                        vProcurando = ""; vNome = ""; vStatus = "";
                        for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                            if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {

                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                    if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                        vProcurando = ValueUserdataSnapshot.getValue().toString();
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                        vNome = ValueUserdataSnapshot.getValue().toString();
                                    }
                                    if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                        vStatus = ValueUserdataSnapshot.getValue().toString();
                                    }
                                }
                                if (vStatus != null && vStatus.equals("on") && !vNome.equals("")) {
                                    vtNome.add(vNome);
                                    vtStatus.add(vStatus);
                                    mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                            for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                    vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                    vtUser.add(PhotodataSnapshot.getKey().toString());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        vFiltro = getResources().getString(R.string.boovers_on_line);
         /* Fim online */


        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vUri.size() > 0) {
                    gridview.setVisibility(View.VISIBLE);
                    imgAduser.setvUri(vUri,vtUser,vtNome, vtStatus);
                    gridview.setAdapter(imgAduser);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                }else{
                    handler.postDelayed(this,500);
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    gridview.setVisibility(View.INVISIBLE);
                }
            }
        }, 500);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Bundle bundleMeet = new Bundle();

                if (vUri.size()>position) {
                    bundleMeet.putString("vUri", vUri.get(position));
                }

                if (vtNome.size()>position) {
                    bundleMeet.putString("vNome", vtNome.get(position));
                }
                if (vtStatus.size()>position) {
                    bundleMeet.putString("vStatus", vtStatus.get(position));
                }
                bundleMeet.putString("vChannel", "");
                if (vtUser.size()>position) {
                    bundleMeet.putString("dUid", vtUser.get(position));
                    FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    fFrag.setArguments(bundleMeet);
                    ft.add(R.id.frgmeetboover_frame, fFrag, "FrgMeetDetailUserChat");
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });

        btnReload = (ImageButton) view.findViewById(R.id.ic_switch);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgMeetBoover fFrag = new FrgMeetBoover();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.add( R.id.frgmeetboover_frame, fFrag, "FrgMeetBoover");
                ft.addToBackStack("FrgMeetBoover");
                ft.commit();
            }
        });


        /* Código ExpandableListView */

        final ExpandableListView elvCompra = (ExpandableListView) view.findViewById(R.id.elistFilterMeet);
        lstItensGrupo.clear();
        lstItems.clear();
        lstGrupos.clear();
        // cria os grupos
        lstGrupos.add(vFiltro);

        // cria os itens de cada grupo
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.todos_boovers), 0.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_on_line), 1.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_proximos), 2.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_mercado), 3.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boover_mesmo_gosto), 4.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_videos), 5.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_posts), 6.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_resenha), 7.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_amizade), 8.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_paquera), 9.0));
        lstItems.add(new FiltroMeetBoover(getResources().getString(R.string.boovers_email), 10.0));

        // cria o "relacionamento" dos grupos com seus itens
        lstItensGrupo.put(lstGrupos.get(0), lstItems);

        // cria um adaptador (BaseExpandableListAdapter) com os dados acima
        AdapterFilterMeet adaptador = new AdapterFilterMeet(getActivity(), lstGrupos, lstItensGrupo);
        // define o apadtador do ExpandableListView
        elvCompra.setAdapter(adaptador);

        elvCompra.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //Nothing here ever fires
                Bundle bundleMeet = new Bundle();
                bundleMeet.putString("vposition", Integer.toString(childPosition));
                GPSTracker gps = new GPSTracker(getContext());

                if (childPosition==0){

                    if(gps.canGetLocation()) {

                        mvlat = gps.getLatitude();
                        mvlng = gps.getLongitude();
                        mDatabaseUsers.child(mUid).child("latitude").setValue(Double.toString(mvlat));
                        mDatabaseUsers.child(mUid).child("longitude").setValue(Double.toString(mvlng));

                        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(mvlat, mvlng, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                mSubCity = address.getCountryName();
                            } else {
                                mSubCity = "";
                            }
                        } catch (IOException e) {
                            Log.e("Erro", "erro");
                        }

                        mDatabaseUsers.addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot UserdataSnapshot) {
                                        vUri.clear();
                                        vtNome.clear();
                                        vtUser.clear();
                                        vtStatus.clear();
                                        vProcurando = "";
                                        vNome = "";
                                        vStatus = "";
                                        for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                            if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                                Double vlat = 0.0000, vlng = 0.0000;
                                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                        vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                        vtNome.add(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                        vtStatus.add(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("latitude")) {
                                                        vlat = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("longitude")) {
                                                        vlng = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                }
                                                //Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                                try {
                                                    List<Address> vAdresses = geocoder.getFromLocation(vlat, vlng, 1);
                                                    if (vAdresses.size() > 0) {
                                                        Address address = vAdresses.get(0);
                                                        vLocalizacao = address.getCountryName();
                                                    } else {
                                                        vLocalizacao = null;
                                                    }
                                                } catch (IOException e) {
                                                    Log.e("Erro GPS", "erro");
                                                }
                                                if (vLocalizacao != null && vLocalizacao.equals(mSubCity)) {
                                                    mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                            for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                    vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                    vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                }
                        );
                    }else {
                        gps.showSettingsAlert();
                    }
                    vFiltro = getResources().getString(R.string.todos_boovers);

                }

                if (childPosition==1){
                    if(gps.canGetLocation()) {

                        mvlat = gps.getLatitude();
                        mvlng = gps.getLongitude();
                        mDatabaseUsers.child(mUid).child("latitude").setValue(Double.toString(mvlat));
                        mDatabaseUsers.child(mUid).child("longitude").setValue(Double.toString(mvlng));

                        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(mvlat, mvlng, 1);
                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                mSubCity = address.getCountryName();
                            } else {
                                mSubCity = "";
                            }
                        } catch (IOException e) {
                            Log.e("Erro", "erro");
                        }
                        mDatabaseUsers.addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot UserdataSnapshot) {
                                        vUri.clear();
                                        vtNome.clear();
                                        vtUser.clear();
                                        vtStatus.clear();
                                        vProcurando = "";
                                        vNome = "";
                                        vStatus = "";
                                        for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                            if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                                Double vlat = 0.0000, vlng = 0.0000;
                                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                        vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                        vNome = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                        vStatus = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("latitude")) {
                                                        vlat = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("longitude")) {
                                                        vlng = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                }
                                                //Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                                try {
                                                    List<Address> vAdresses = geocoder.getFromLocation(vlat, vlng, 1);
                                                    if (vAdresses.size() > 0) {
                                                        Address address = vAdresses.get(0);
                                                        vLocalizacao = address.getCountryName();
                                                    } else {
                                                        vLocalizacao = null;
                                                    }
                                                } catch (IOException e) {
                                                    Log.e("Erro GPS", "erro");
                                                }
                                                if (vStatus != null && vStatus.equals("on") && !vNome.equals("") && vLocalizacao != null && vLocalizacao.equals(mSubCity)) {
                                                    vtNome.add(vNome);
                                                    vtStatus.add(vStatus);
                                                    mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                            for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                    vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                    vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                }
                        );
                    }else {
                        gps.showSettingsAlert();
                    }
                    vFiltro = getResources().getString(R.string.boovers_on_line);

                }
                if (childPosition==2){


                    if(gps.canGetLocation()){

                        mvlat = gps.getLatitude();
                        mvlng = gps.getLongitude();
                        mDatabaseUsers.child(mUid).child("latitude").setValue(Double.toString(mvlat));
                        mDatabaseUsers.child(mUid).child("longitude").setValue(Double.toString(mvlng));

                        final Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(mvlat, mvlng, 1);
                            if (addresses.size()>0) {
                                Address address = addresses.get(0);
                                mSubCity = address.getSubLocality();
                            }else{
                                mSubCity="";
                            }
                        }catch (IOException e){
                            Log.e("Erro","erro");
                        }

                        mDatabaseUsers.addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot UserdataSnapshot) {
                                        vUri.clear();
                                        vtNome.clear();
                                        vtUser.clear();
                                        vtStatus.clear();
                                        vProcurando = ""; vNome = ""; vStatus = "";
                                        Double vlat = 0.0000, vlng = 0.0000;
                                        for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                            if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                                for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                        vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                        vNome = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                        vStatus = ValueUserdataSnapshot.getValue().toString();
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("latitude")) {
                                                        vlat = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                    if (ValueUserdataSnapshot.getKey().toString().equals("longitude")) {
                                                        vlng = Double.parseDouble(ValueUserdataSnapshot.getValue().toString());
                                                    }
                                                }
                                                //Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                                try {
                                                    List<Address> vAdresses = geocoder.getFromLocation(vlat, vlng, 1);
                                                    if (vAdresses.size() > 0) {
                                                        Address address = vAdresses.get(0);
                                                        vLocalizacao = address.getSubLocality();
                                                    } else {
                                                        vLocalizacao = null;
                                                    }
                                                } catch (IOException e) {
                                                    Log.e("Erro GPS", "erro");
                                                }
                                                if (vLocalizacao != null && vLocalizacao.equals(mSubCity) && !vNome.equals("")) {
                                                    vtNome.add(vNome);
                                                    vtStatus.add(vStatus);
                                                    mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                            for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                    vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                    vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                }
                        );

                    }else {
                        gps.showSettingsAlert();
                    }
                    vFiltro = getResources().getString(R.string.boovers_proximos);
                }
                if (childPosition==3){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                            vProcurando="";
                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            mDatabase.child("Market").orderByKey()
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Integer contadorUser = 0;
                                                            for (DataSnapshot bookdataSnapshot : dataSnapshot.getChildren()) {
                                                                for (DataSnapshot UserdataSnapshot : bookdataSnapshot.getChildren()) {
                                                                    if (UserdataSnapshot.getKey().equals(ChaveUserdataSnapshot.getKey())) {
                                                                        vProcurando = UserdataSnapshot.getKey().toString();
                                                                        contadorUser++;
                                                                        if (contadorUser==1) {
                                                                            vtNome.add(vNome);
                                                                            vtStatus.add(vStatus);
                                                                            mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                                                    for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                                        if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                                            vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                                            vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(DatabaseError databaseError) {
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_mercado);
                }

                if (childPosition==4){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    String vCategoria = "";
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("categoria")) {
                                                    vCategoria = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            if (vCategoria != null && vCategoria.equals(mCategoria) && !vNome.equals("")) {
                                                vtNome.add(vNome);
                                                vtStatus.add(vStatus);
                                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );

                    vFiltro = getResources().getString(R.string.boover_mesmo_gosto);
                }


                if (childPosition==5){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                            vProcurando="0";
                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("videos")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            if (vProcurando != null && Integer.parseInt(vProcurando)>0 && !vNome.equals("")) {
                                                vtNome.add(vNome);
                                                vtStatus.add(vStatus);
                                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_videos);
                }

                if (childPosition==6){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                            vProcurando="0";
                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("posts")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            if (vProcurando != null && Integer.parseInt(vProcurando)>0 && !vNome.equals("")) {
                                                vtNome.add(vNome);
                                                vtStatus.add(vStatus);
                                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_posts);
                }

                if (childPosition==7){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {
                                            vProcurando="0";
                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("resenhas")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            if (vProcurando != null && Integer.parseInt(vProcurando)>0 && !vNome.equals("")) {
                                                vtNome.add(vNome);
                                                vtStatus.add(vStatus);
                                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_resenha);
                }

                if (childPosition==8){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {

                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }
                                            if (vProcurando != null && vProcurando.equals("Amigos") && !vNome.equals("")) {
                                                vtNome.add(vNome);
                                                vtStatus.add(vStatus);
                                                mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                        for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                            if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                vtUser.add(PhotodataSnapshot.getKey().toString());
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_amizade);
                }

                if (childPosition==9){
                    mDatabaseUsers.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UserdataSnapshot) {
                                    vUri.clear();
                                    vtNome.clear();
                                    vtUser.clear();
                                    vtStatus.clear();
                                    vProcurando = ""; vNome = ""; vStatus = "";
                                    for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                        if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {

                                            for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                    vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                    vNome = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                    vStatus = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("sexo")) {
                                                    vSexo = ValueUserdataSnapshot.getValue().toString();
                                                }
                                                if (ValueUserdataSnapshot.getKey().toString().equals("interesse")) {
                                                    vInteresse = ValueUserdataSnapshot.getValue().toString();
                                                }
                                            }

                                            if (vProcurando != null && vProcurando.equals("Paqueras")) {
                                                if (oInteresse!=null && vInteresse!=null) {
                                                    if ((oInteresse.equals("Ambos") || (!vInteresse.equals(oInteresse)))&& !vNome.equals("")) {
                                                        if (oInteresse.equals("Ambos")) {
                                                            vtNome.add(vNome);
                                                            vtStatus.add(vStatus);
                                                            mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                                    for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                        if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                            vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                            vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });
                                                        }
                                                        if (((oInteresse.equals("Mulheres") && vSexo.equals("Feminino"))
                                                                || (oInteresse.equals("Homens") && vSexo.equals("Masculino"))) && !vNome.equals("")) {
                                                            vtNome.add(vNome);
                                                            vtStatus.add(vStatus);
                                                            mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                                    for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                        if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                            vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                            vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            }
                    );
                    vFiltro = getResources().getString(R.string.boovers_paquera);
                }

                if (childPosition==10){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                    builder.setTitle(R.string.digite_busca_email);
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    input.setWidth(1440);
                    input.setHeight(150);
                    input.setPadding(15,0,15,0);
                    input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                    input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                    builder.setView(input);
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mEmail = input.getText().toString();
                            mDatabaseUsers.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot UserdataSnapshot) {
                                            vUri.clear();
                                            vtNome.clear();
                                            vtUser.clear();
                                            vtStatus.clear();

                                            for (final DataSnapshot ChaveUserdataSnapshot : UserdataSnapshot.getChildren()) {
                                                if (!ChaveUserdataSnapshot.getKey().toString().equals(mUid)) {

                                                    for (final DataSnapshot ValueUserdataSnapshot : ChaveUserdataSnapshot.getChildren()) {
                                                        if (ValueUserdataSnapshot.getKey().toString().equals("procurando")) {
                                                            vProcurando = ValueUserdataSnapshot.getValue().toString();
                                                        }
                                                        if (ValueUserdataSnapshot.getKey().toString().equals("nome")) {
                                                            vNome = ValueUserdataSnapshot.getValue().toString();
                                                        }
                                                        if (ValueUserdataSnapshot.getKey().toString().equals("status")) {
                                                            vStatus = ValueUserdataSnapshot.getValue().toString();
                                                        }
                                                        if (ValueUserdataSnapshot.getKey().toString().equals("email")) {
                                                            vEmail = ValueUserdataSnapshot.getValue().toString();
                                                        }
                                                    }
                                                    if (vEmail.equals(mEmail) && !vNome.equals("")) {
                                                        vtNome.add(vNome);
                                                        vtStatus.add(vStatus);
                                                        mDatabasePhotos.child(ChaveUserdataSnapshot.getKey().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot PhotodataSnapshot) {
                                                                for (DataSnapshot ValuePhotoDataSnapshot : PhotodataSnapshot.getChildren()) {
                                                                    if (ValuePhotoDataSnapshot.getKey().toString().equals("Default")) {
                                                                        vUri.add(ValuePhotoDataSnapshot.getValue().toString());
                                                                        vtUser.add(PhotodataSnapshot.getKey().toString());
                                                                    }
                                                                }
                                                                gridview.setVisibility(View.VISIBLE);
                                                                imgAduser.setvUri(vUri,vtUser,vtNome, vtStatus);
                                                                gridview.setAdapter(imgAduser);
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    }
                            );
                        }
                    });
                    builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                    vFiltro = getResources().getString(R.string.boovers_email);

                }


                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vUri.size() > 0) {
                            gridview.setVisibility(View.VISIBLE);
                            imgAduser.setvUri(vUri,vtUser,vtNome, vtStatus);
                            imgAduser.notifyDataSetChanged();
                            gridview.setAdapter(imgAduser);

                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                        }else{
                            gridview.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 2000);

                elvCompra.collapseGroup(0);
                lstGrupos.clear();
                lstItensGrupo.clear();
                lstGrupos.add(vFiltro);
                lstItensGrupo.put(lstGrupos.get(0), lstItems);
                AdapterFilterMeet adaptador = new AdapterFilterMeet(getActivity(), lstGrupos, lstItensGrupo);
                elvCompra.setAdapter(adaptador);

                return true;
            }
        });

         /* Fim Código ExpandableListView */

        return view;
    }
}