package com.editorapendragon.boover;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgMyBoovers extends Fragment {

    private static final String TAG = "FrgMeetBoover";
    private DatabaseReference mDatabase, mDatabaseW;
    private DatabaseReference mDatabaseM, mDatabaseU, mDatabaseF, mDatabaseN, mDatabaseI;
    private FirebaseAuth mAuth;
    private ImageButton btnFriends, btnReload, btnBlock, btnInvitation, btnSendInvitesFriends;
    private ProgressBar mProgressBar;
    private String mUid, vTipoContato, mNotific = "";
    private ArrayList<String> vNome = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> vtStatus = new ArrayList<String>();
    private ArrayList<String> vPhotoUser = new ArrayList<String>();
    private Handler handler;
    private ListView list;
    private TextView txtTipoContato, txtPedidoAmizade;
    private AlertDialog alerta;
    private Integer notCounter = 0, PedidoCont = 0;
    private static final int REQUEST_INVITE = 0x0;
    final DecimalFormat precision = new DecimalFormat("#,##0.00");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgmyboovers_fragment, container, false);
        getFragmentManager().beginTransaction().addToBackStack("FrgContacts").commit();

        list = (ListView)  view.findViewById (R.id.lstViewContacts);
        txtTipoContato = (TextView) view.findViewById(R.id.txtTipoContato);
        Bundle bundlecontacts = this.getArguments();
        if (bundlecontacts != null) {
            vTipoContato = bundlecontacts.getString("vTipoContato", "0");
            if (vTipoContato.equals("Contacts")){
                txtTipoContato.setText(getResources().getString(R.string.amigos));
                list.setLongClickable(false);
            } else {
                if (vTipoContato.equals("Block")) {
                    txtTipoContato.setText(getContext().getResources().getString(R.string.boovers_bloqueados));
                }else{
                    txtTipoContato.setText(getContext().getResources().getString(R.string.convites_recebidos));
                }
                list.setLongClickable(true);
            }
        }else{
            vTipoContato = "Contacts";
            txtTipoContato.setText(getResources().getString(R.string.amigos));
            list.setLongClickable(false);
        }

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mDatabase = FirebaseDatabase.getInstance().getReference(vTipoContato);
        mDatabaseW = FirebaseDatabase.getInstance().getReference();
        mDatabaseU = FirebaseDatabase.getInstance().getReference("UserPhotos");
        mDatabaseM = FirebaseDatabase.getInstance().getReference("Users");
        mDatabaseF = FirebaseDatabase.getInstance().getReference("Contacts");
        mDatabaseN = FirebaseDatabase.getInstance().getReference("Notifications");
        mDatabaseI = FirebaseDatabase.getInstance().getReference("Invitations");
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        txtPedidoAmizade = (TextView) view.findViewById(R.id.txtpedidoamizade);
        mDatabaseI.child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        PedidoCont = 0;
                        for (DataSnapshot convite : dataSnapshot.getChildren()) {
                            if (convite.getValue() != null) {
                                PedidoCont++;
                            }
                        }
                        if (PedidoCont>0) {
                            txtPedidoAmizade.setVisibility(View.VISIBLE);
                            txtPedidoAmizade.setText("   "+Integer.toString(PedidoCont)+"   ");
                        }else{
                            txtPedidoAmizade.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });


        mDatabase.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vNome.clear();
                vPhotoUser.clear();
                vtUser.clear();
                vtStatus.clear();
                for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {

                    mDatabaseU.child(noteDataSnapshot.getKey().toString()).child("Default")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot UdataSnapshot) {
                                    if (UdataSnapshot.getValue() != null) {
                                        vPhotoUser.add(UdataSnapshot.getValue().toString());
                                    }
                                }
                                @Override
                                public void onCancelled (DatabaseError databaseError){
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                    mDatabaseM.child(noteDataSnapshot.getKey().toString()).child("status")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot SdataSnapshot) {
                                    if (SdataSnapshot.getValue() != null) {
                                        vtStatus.add(SdataSnapshot.getValue().toString());

                                    }else{
                                        vtStatus.add("off");
                                    }

                                }
                                @Override
                                public void onCancelled (DatabaseError databaseError){
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });
                    mDatabaseM.child(noteDataSnapshot.getKey().toString()).child("nome")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot NdataSnapshot) {
                                    if (NdataSnapshot.getValue() != null) {
                                        vNome.add(NdataSnapshot.getValue().toString());
                                    }
                                    vtUser.add(noteDataSnapshot.getKey().toString());
                                }
                                @Override
                                public void onCancelled (DatabaseError databaseError){
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (vNome.size() > 0) {
                    CustomContactsListAdapter adapter = new CustomContactsListAdapter(getActivity(), vNome, vPhotoUser, vtUser, vtStatus, "off");
                    adapter.notifyDataSetChanged();
                    list.setAdapter(adapter);
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        }, 2000);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String Slecteditem = vtUser.get(position);

                if (vTipoContato.equals("Contacts")) {
                    Bundle bundleContacts = new Bundle();
                    bundleContacts.putString("dUid", vtUser.get(position));
                    bundleContacts.putString("vChannel", "");
                    bundleContacts.putString("vNome", vNome.get(position));
                    if (vPhotoUser.size() >= position + 1) {
                        bundleContacts.putString("vUri", vPhotoUser.get(position));
                    } else {
                        bundleContacts.putString("vUri", "");
                    }

                    FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    fFrag.setArguments(bundleContacts);
                    ft.add(R.id.frgmyboovers_frame, fFrag, "FrgMeetDetailUserChat");
                    ft.addToBackStack(null);
                    ft.commit();
                }else if (vTipoContato.equals("Block")) {
                    Toast.makeText(getContext(),getResources().getString(R.string.utilize_longo_desbloquear)+" "+ vNome.get(position), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),getResources().getString(R.string.utilize_longo_convite)+" "+ vNome.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!vTipoContato.equals("Contacts")) {
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,
                                               final int position, long id) {
                    String Slecteditem = vtUser.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                    builder.setIcon(R.drawable.ic_boover_rounded);
                    if (vTipoContato.equals("Block")) {
                        builder.setTitle(getResources().getString(R.string.desbloquear_usuario));
                        builder.setMessage(getResources().getString(R.string.tem_certeza_desbloqueaar_usuario) + " " + vNome.get(position) + "?");
                        builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mDatabase.child(mUid).child(vtUser.get(position)).removeValue();
                                Toast.makeText(getContext(), getResources().getString(R.string.usuario_desbloqueado) + " " + vNome.get(position), Toast.LENGTH_SHORT).show();
                                Bundle bundleFriends = new Bundle();
                                bundleFriends.putString("vTipoContato", "Block");
                                FrgMyBoovers fFrag = new FrgMyBoovers();
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                fFrag.setArguments(bundleFriends);
                                ft.add(R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                                ft.addToBackStack("FrgMyBoovers");
                                ft.commit();
                            }
                        });
                        builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                alerta.dismiss();
                            }
                        });

                    } else {
                        builder.setTitle(getResources().getString(R.string.convites));
                        builder.setMessage(getResources().getString(R.string.tem_certeza_aceitar_convite) + " " + vNome.get(position) + "?");
                        builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mDatabase.child(mUid).child(vtUser.get(position)).removeValue();
                                mDatabaseF.child(mUid).child(vtUser.get(position)).setValue(vNome.get(position));
                                mDatabaseF.child(vtUser.get(position)).child(mUid).setValue(mAuth.getCurrentUser().getDisplayName());
                                Toast.makeText(getContext(), getResources().getString(R.string.convite_aceito) + " " + vNome.get(position), Toast.LENGTH_SHORT).show();

                                notCounter = 0;
                                if (vtUser.size() > 0) {
                                    mDatabaseN.child(vtUser.get(position)).child("notCounter")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
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
                                    mDatabaseN.child(vtUser.get(position)).child(mUid)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.getValue() != null) {
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
                                    notCounter++;
                                    mDatabaseN.child(vtUser.get(position)).child("notCount").setValue(Integer.toString(notCounter));
                                    mDatabaseN.child(vtUser.get(position)).child(mUid).setValue(getResources().getString(R.string.aceitou_convite) + "\n" + mNotific);
                                    mDatabaseW.child("Wallet").child(mUid).child("saldob")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Double saldo = 0.00;
                                                    if (dataSnapshot.getValue()!=null) {
                                                        saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                                                    }else{
                                                        saldo = 0.10;
                                                    }
                                                    mDatabase.child("Wallet").child(mUid).child("saldob").setValue(saldo);
                                                    mDatabase.child("Wallet").child(mUid).child("extrato").child("convites").child("valor")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    Double saldo = 0.00;
                                                                    if (dataSnapshot.getValue()!=null) {
                                                                        saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                                                                    }else{
                                                                        saldo = 0.10;
                                                                    }
                                                                    try {
                                                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                                .child("convites").child("valor").setValue(saldo);
                                                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                                .child("convites").child("datasolicitacao")
                                                                                .setValue(Long.toString(-1 * new Date().getTime()));
                                                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                                .child("convites").child("status")
                                                                                .setValue(getContext().getResources().getString(R.string.status_extrato_convites));
                                                                        mDatabase.child("Wallet").child(mUid).child("extrato")
                                                                                .child("convites").child("tipo")
                                                                                .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                                                    } catch (NullPointerException e){
                                                                        e.printStackTrace();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                }
                                Bundle bundleFriends = new Bundle();
                                bundleFriends.putString("vTipoContato", "Invitations");
                                FrgMyBoovers fFrag = new FrgMyBoovers();
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                fFrag.setArguments(bundleFriends);
                                ft.add(R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                                ft.addToBackStack("FrgMyBoovers");
                                ft.commit();
                            }
                        });
                        builder.setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mDatabase.child(mUid).child(vtUser.get(position)).removeValue();
                                Toast.makeText(getContext(), getResources().getString(R.string.convite_recusado) + " " + vNome.get(position), Toast.LENGTH_SHORT).show();
                                alerta.dismiss();

                                Bundle bundleFriends = new Bundle();
                                bundleFriends.putString("vTipoContato", "Invitations");
                                FrgMyBoovers fFrag = new FrgMyBoovers();
                                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                                fFrag.setArguments(bundleFriends);
                                ft.add(R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                                ft.addToBackStack("FrgMyBoovers");
                                ft.commit();
                            }
                        });


                    }
                    alerta = builder.create();
                    alerta.show();

                    return true;
                }
            });
        }

        btnSendInvitesFriends = (ImageButton) view.findViewById(R.id.ic_send_invites);
        btnSendInvitesFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.convidar_amigos));
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(getResources().getString(R.string.obrigado_por_fazer_o_boover));
                builder.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        onInviteClicked();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                alerta = builder.create();
                alerta.show();
            }
        });

        btnReload = (ImageButton) view.findViewById(R.id.ic_switch);
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgMyBoovers fFrag = new FrgMyBoovers();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.add( R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                ft.addToBackStack("FrgMyBoovers");
                ft.commit();
            }
        });

        btnFriends = (ImageButton) view.findViewById(R.id.ic_friends);
        btnFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleFriends = new Bundle();
                bundleFriends.putString("vTipoContato", "Contacts");
                FrgMyBoovers fFrag = new FrgMyBoovers();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleFriends);
                ft.add( R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                ft.addToBackStack("FrgMyBoovers");
                ft.commit();
            }
        });

        btnBlock = (ImageButton) view.findViewById(R.id.ic_blockuser);
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleFriends = new Bundle();
                bundleFriends.putString("vTipoContato", "Block");
                FrgMyBoovers fFrag = new FrgMyBoovers();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleFriends);
                ft.add( R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                ft.addToBackStack("FrgMyBoovers");
                ft.commit();
            }
        });

        btnInvitation = (ImageButton) view.findViewById(R.id.ic_adduser);
        btnInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleFriends = new Bundle();
                bundleFriends.putString("vTipoContato", "Invitations");
                FrgMyBoovers fFrag = new FrgMyBoovers();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleFriends);
                ft.add( R.id.frgmyboovers_frame, fFrag, "FrgMyBoovers");
                ft.addToBackStack("FrgMyBoovers");
                ft.commit();
            }
        });

        return view;
    }
    private void onInviteClicked() {
        Globals.vIntentFoto="1";
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                //.setCallToActionText(getString(R.string.invitation_cta))
                .setEmailHtmlContent(getString(R.string.invitation_emailhtmlcontent))
                .setEmailSubject(getString(R.string.invitation_title))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                final String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                mDatabaseW.child("Wallet").child(mUid).child("saldob")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Double saldo = 0.00;

                                BigDecimal valor;
                                if (dataSnapshot.getValue()!=null) {
                                    saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                                }else{
                                    saldo = 0.10;
                                }
                                for (String id : ids) {
                                    mDatabaseW.child("Wallet").child(mUid).child("saldob").setValue(Double.parseDouble(precision.format(saldo)));
                                    saldo=saldo+0.10;
                                }
                                mDatabase.child("Wallet").child(mUid).child("extrato").child("convites").child("valor")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Double saldo = 0.00;
                                                if (dataSnapshot.getValue()!=null) {
                                                    saldo = Double.parseDouble(dataSnapshot.getValue().toString())+0.10;
                                                }else{
                                                    saldo = 0.10;
                                                }
                                                try {
                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                            .child("convites").child("valor").setValue(saldo);
                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                            .child("convites").child("datasolicitacao")
                                                            .setValue(Long.toString(-1 * new Date().getTime()));
                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                            .child("convites").child("status")
                                                            .setValue(getContext().getResources().getString(R.string.status_extrato_convites));
                                                    mDatabase.child("Wallet").child(mUid).child("extrato")
                                                            .child("convites").child("tipo")
                                                            .setValue(getContext().getResources().getString(R.string.status_extrato_credito));
                                                } catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        }
    }

}