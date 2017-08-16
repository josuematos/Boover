package com.editorapendragon.boover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgNotifications extends Fragment {

    private static final String TAG = "FrgMeetBoover";
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseM, mDatabaseU;
    private FirebaseAuth mAuth;
    private ImageButton btnAdduser, btnReload, btnDeleteAll;
    private ProgressBar mProgressBar;
    private String mUid;
    private ArrayList<String> vNome = new ArrayList<String>();
    private ArrayList<String> vData = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> vtStatus = new ArrayList<String>();
    private Handler handler;
    private ListView list;
    private AlertDialog alerta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgvisualizations_fragment, container, false);
        getFragmentManager().beginTransaction().addToBackStack("FrgNotifications").commit();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference("Notifications");
        mDatabaseM = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        list = (ListView)  view.findViewById (R.id.lstViewContacts);

        mDatabase.child(mUid).child("notCount").setValue(0);

        mDatabase.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vNome.clear();
                vtUser.clear();
                vtStatus.clear();
                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                    if (!noteDataSnapshot.getKey().toString().equals("notCount")) {

                        mDatabaseM.child(noteDataSnapshot.getKey().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot UdataSnapshot) {
                                        String dName = "", dStatus = "";
                                        for (DataSnapshot unoteSnapshot : UdataSnapshot.getChildren()) {
                                            if (unoteSnapshot.getKey().toString().equals("status")) {
                                                if (unoteSnapshot.getValue() != null) {
                                                    dStatus = unoteSnapshot.getValue().toString();
                                                }
                                            }
                                            if (unoteSnapshot.getKey().toString().equals("nome")) {
                                                if (unoteSnapshot.getValue() != null) {
                                                    dName = unoteSnapshot.getValue().toString();
                                                }
                                            }
                                            if (unoteSnapshot.getKey().toString().equals("sobrenome")) {
                                                if (unoteSnapshot.getValue() != null) {
                                                    dName = dName + " " + unoteSnapshot.getValue().toString();
                                                }
                                            }
                                        }
                                        vtStatus.add(dStatus);
                                        vNome.add(dName);
                                        vtUser.add(noteDataSnapshot.getKey().toString());
                                        vData.add(noteDataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                    }
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        list = (ListView)  view.findViewById (R.id.lstViewVisualizations);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vNome.size() > 0) {
                    try {
                        CustomNotificationsListAdapter adapter = new CustomNotificationsListAdapter(getActivity(), vNome, vtUser, vtStatus, vData);
                        list.setAdapter(adapter);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                } else {
                    handler.postDelayed(this, 500);
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }, 2000);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Slecteditem = vtUser.get(position);
                Bundle bundleContacts = new Bundle();
                bundleContacts.putString("dUid", vtUser.get(position));
                bundleContacts.putString("vChannel", "");
                bundleContacts.putString("vNome", vNome.get(position));

                FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                fFrag.setArguments(bundleContacts);
                ft.add( R.id.frgvisualizations_frame, fFrag, "FrgMeetDetailUser");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String Slecteditem = vtUser.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.apagar_notificacoes));
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(getResources().getString(R.string.tem_certeza_excluir_notificacoes));
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabase.child(mUid).child(Slecteditem).removeValue();
                        Toast.makeText(getContext(), getResources().getString(R.string.notificacoes_apagadas), Toast.LENGTH_SHORT).show();
                        FrgNotifications fFrag = new FrgNotifications();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add( R.id.frguser_frame, fFrag, "FrgNotifications");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
                builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alerta.dismiss();
                    }
                });
                alerta = builder.create();
                alerta.show();

                return true;
            }
        });

        btnReload = (ImageButton) view.findViewById(R.id.ic_switch);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        btnDeleteAll = (ImageButton) view.findViewById(R.id.ic_delete_all);
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.apagar_notificacoes));
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(getResources().getString(R.string.tem_certeza_excluir_todas_notificacoes));
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabase.child(mUid).removeValue();
                        Toast.makeText(getContext(),getResources().getString(R.string.notificacoes_apagadas), Toast.LENGTH_SHORT).show();
                        FrgNotifications fFrag = new FrgNotifications();
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace( R.id.frguser_frame, fFrag, "FrgNotifications");
                        ft.addToBackStack(null);
                        ft.commit();

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


        return view;
    }

}