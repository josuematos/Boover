package com.editorapendragon.boover;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAnalytics mFirebaseAnalytics;
    private ImageButton btBooverChat;
    private ImageButton btUser;
    private ImageButton btBoovers;
    private ImageButton btBlooger;
    private ImageButton btBooverShelf;
    private ImageButton btMyBoovers;
    private ImageView ic_onoff;
    private AlertDialog alerta;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, visCounter = "0", notCounter = "0", vAtualizar = "0";
    private StorageReference mStorage;
    private ArrayList<String> vUri = new ArrayList<String>();

    private ProgressDialog mProgress;
    private TextView lblBoover, lblChat, lblPosts, lblBooks, lblFriends, lblMe;
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn;
    private TextView txtPedidoAmizade;
    private Integer PedidoCont = 0;

    static final int NUM_ITEMS = 8;
    static final String version_app = "1.0.20";

    ViewPager mPager;
    SlidePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        btBoovers = (ImageButton) findViewById(R.id.ic_boovers);
        btBoovers.setOnClickListener(this);

        btBooverChat = (ImageButton) findViewById(R.id.ic_chat);
        btBooverChat.setOnClickListener(this);

        btBlooger = (ImageButton) findViewById(R.id.ic_bloogers);
        btBlooger.setOnClickListener(this);

        btBooverShelf = (ImageButton) findViewById(R.id.ic_bookshelf);
        btBooverShelf.setOnClickListener(this);

        btMyBoovers = (ImageButton) findViewById(R.id.ic_myboovers);
        btMyBoovers.setOnClickListener(this);

        btUser = (ImageButton) findViewById(R.id.ic_user);
        btUser.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();

        mDatabase.child("Users").child(mUid).child("status").setValue("on");
        stopService(new Intent(MainActivity.this, NotificationService.class));
        if (mService != null) {
            unbindService(mServiceConn);
        }

        txtPedidoAmizade = (TextView) findViewById(R.id.txtpedidoamizade);
        mDatabase.child("Invitations").child(mUid)
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

        mDatabase.child("Atualizacao").child("Atualizar")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         if (!dataSnapshot.getValue().toString().equals(version_app)) {
                             mDatabase.child("Atualizacao").child(mUid)
                                     .addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(DataSnapshot atdataSnapshot) {
                                             if (atdataSnapshot.getValue()!=null) {
                                                 vAtualizar = atdataSnapshot.getValue().toString();
                                             }else{
                                                 vAtualizar = "0";
                                             }
                                             if (!vAtualizar.equals(version_app)) {
                                                 AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialogCustom);
                                                 builder.setIcon(R.drawable.ic_boover_rounded);
                                                 builder.setTitle(getResources().getString(R.string.atualizacao));
                                                 builder.setMessage(getResources().getString(R.string.atualizacao_msg));
                                                 builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                     public void onClick(DialogInterface arg0, int arg1) {
                                                         Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.editorapendragon.boover");
                                                         Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                         Globals.vIntentFoto="1";
                                                         finish();
                                                         startActivity(intent);
                                                     }
                                                 });
                                                 builder.setNegativeButton(getResources().getString(R.string.depois), new DialogInterface.OnClickListener() {
                                                     public void onClick(DialogInterface arg0, int arg1) {
                                                         alerta.dismiss();
                                                     }
                                                 });
                                                 alerta = builder.create();
                                                 try {
                                                     alerta.show();
                                                 } catch (WindowManager.BadTokenException t){
                                                     t.printStackTrace();
                                                 }

                                             }
                                         }
                                         @Override
                                         public void onCancelled(DatabaseError databaseError) {

                                         }
                                     });
                         }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mDatabase.child("Channel")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView txtmsgLida = (TextView) findViewById(R.id.txtmsgLida);
                        int msgLida = 0;
                        for (final DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            for (final DataSnapshot valueDataSnapshot : noteDataSnapshot.getChildren()) {
                                if (valueDataSnapshot.getKey().equals("Count-"+mUid) && valueDataSnapshot.getValue().toString().equals("on")){
                                   msgLida++;
                                }
                            }
                        }
                        if (msgLida>0) {
                            txtmsgLida.setVisibility(View.VISIBLE);
                            txtmsgLida.setText("   " + Integer.toString(msgLida) + "   ");
                        }else{
                            txtmsgLida.setVisibility(View.INVISIBLE);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });


        mDatabase.child("Notifications").child(mUid).child("notCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView txtvisCounter = (TextView) findViewById(R.id.txtvisCounter);
                        notCounter = "0";
                        if (dataSnapshot.getValue() != null) {
                            notCounter = Integer.toString(Integer.parseInt(notCounter)+Integer.parseInt(dataSnapshot.getValue().toString()));
                            visCounter = Integer.toString(Integer.parseInt(visCounter) + Integer.parseInt(notCounter));
                            if (!visCounter.equals("0")) {
                                txtvisCounter.setVisibility(View.VISIBLE);
                                txtvisCounter.setText("   " + visCounter + "   ");
                            }else{
                                txtvisCounter.setVisibility(View.INVISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });

        mDatabase.child("Visualizations").child(mUid).child("visCounter")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        visCounter="0";
                        TextView txtvisCounter = (TextView) findViewById(R.id.txtvisCounter);
                        if (dataSnapshot.getValue()!= null) {
                            visCounter = Integer.toString(Integer.parseInt(visCounter)+Integer.parseInt(dataSnapshot.getValue().toString()));
                            visCounter = Integer.toString(Integer.parseInt(visCounter) + Integer.parseInt(notCounter));
                            if (!visCounter.equals("0")) {
                                txtvisCounter.setVisibility(View.VISIBLE);
                                txtvisCounter.setText("   " + visCounter + "   ");
                            }else{
                                txtvisCounter.setVisibility(View.INVISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                });




        GPSTracker gps = new GPSTracker(this);

        if(gps.canGetLocation()){
            double dlat = gps.getLatitude();
            double dlong = gps.getLongitude();
            mDatabase.child("Users").child(mUid).child("latitude").setValue(Double.toString(dlat));
            mDatabase.child("Users").child(mUid).child("longitude").setValue(Double.toString(dlong));
        }else{
            gps.showSettingsAlert();
        }

        lblBoover = (TextView) findViewById(R.id.lblboover);
        lblChat = (TextView) findViewById(R.id.lblchat);
        lblPosts = (TextView) findViewById(R.id.lblposts);
        lblBooks = (TextView) findViewById(R.id.lblbooks);
        lblFriends = (TextView) findViewById(R.id.lblfriends);
        lblMe = (TextView) findViewById(R.id.lbluser);


        /* Instantiate a ViewPager and a PagerAdapter. */
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mDatabase.child("Users").child(mUid).child("email")
                .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue()==null){
                        Globals.vIntentFoto="1";
                        Intent secondActivity = new Intent(getApplication(), ActivityFotoUser.class);
                        startActivity(secondActivity);
                        finish();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("Log","Log");
                }
            }
        );




        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            lblBoover.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                            lblChat.setBackgroundResource(android.R.color.transparent);
                            lblPosts.setBackgroundResource(android.R.color.transparent);
                            lblBooks.setBackgroundResource(android.R.color.transparent);
                            lblFriends.setBackgroundResource(android.R.color.transparent);
                            lblMe.setBackgroundResource(android.R.color.transparent);
                        } else if (position == 1) {
                            lblBoover.setBackgroundResource(android.R.color.transparent);
                            lblChat.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                            lblPosts.setBackgroundResource(android.R.color.transparent);
                            lblBooks.setBackgroundResource(android.R.color.transparent);
                            lblFriends.setBackgroundResource(android.R.color.transparent);
                            lblMe.setBackgroundResource(android.R.color.transparent);
                        } else if (position == 2) {
                            lblBoover.setBackgroundResource(android.R.color.transparent);
                            lblChat.setBackgroundResource(android.R.color.transparent);
                            lblPosts.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                            lblBooks.setBackgroundResource(android.R.color.transparent);
                            lblFriends.setBackgroundResource(android.R.color.transparent);
                            lblMe.setBackgroundResource(android.R.color.transparent);
                        } else if (position == 3) {
                            lblBoover.setBackgroundResource(android.R.color.transparent);
                            lblChat.setBackgroundResource(android.R.color.transparent);
                            lblPosts.setBackgroundResource(android.R.color.transparent);
                            lblBooks.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                            lblFriends.setBackgroundResource(android.R.color.transparent);
                            lblMe.setBackgroundResource(android.R.color.transparent);
                        } else if (position == 4) {
                            lblBoover.setBackgroundResource(android.R.color.transparent);
                            lblChat.setBackgroundResource(android.R.color.transparent);
                            lblPosts.setBackgroundResource(android.R.color.transparent);
                            lblBooks.setBackgroundResource(android.R.color.transparent);
                            lblFriends.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                            lblMe.setBackgroundResource(android.R.color.transparent);
                        } else if (position == 5) {
                            lblBoover.setBackgroundResource(android.R.color.transparent);
                            lblChat.setBackgroundResource(android.R.color.transparent);
                            lblPosts.setBackgroundResource(android.R.color.transparent);
                            lblBooks.setBackgroundResource(android.R.color.transparent);
                            lblFriends.setBackgroundResource(android.R.color.transparent);
                            lblMe.setBackgroundResource(R.drawable.rounded_edittext_transparent);
                        }

                    }
                });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.child("Users").child(mUid).child("status").setValue("off");
        stopService(new Intent(MainActivity.this, NotificationService.class));
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuth.getCurrentUser()!=null) {
            mDatabase.child("Users").child(mUid).child("status").setValue("off");
            if (Globals.vIntentFoto.equals("0")) {
                startService(new Intent(MainActivity.this, NotificationService.class));
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
                startActivity(startMain);
            }
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Globals.vIntentFoto="0";
        mDatabase.child("Users").child(mUid).child("status").setValue("on");
        stopService(new Intent(MainActivity.this, NotificationService.class));
    }
    @Override
    protected void onResume() {
        super.onResume();
        Globals.vIntentFoto="0";
        mDatabase.child("Users").child(mUid).child("status").setValue("on");
        stopService(new Intent(MainActivity.this, NotificationService.class));
    }
    @Override
    protected void onStart() {
        super.onStart();
        Globals.vIntentFoto="0";
        mDatabase.child("Users").child(mUid).child("status").setValue("on");
        stopService(new Intent(MainActivity.this, NotificationService.class));
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser()!=null) {
            mDatabase.child("Users").child(mUid).child("status").setValue("off");
            if (Globals.vIntentFoto.equals("0")) {
                startService(new Intent(MainActivity.this, NotificationService.class));
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
                startActivity(startMain);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if  ( v == btBoovers) {
            mPager.setCurrentItem(0);
            lblBoover.setBackgroundResource(R.drawable.rounded_edittext_transparent);
            lblChat.setBackgroundResource(android.R.color.transparent);
            lblPosts.setBackgroundResource(android.R.color.transparent);
            lblBooks.setBackgroundResource(android.R.color.transparent);
            lblFriends.setBackgroundResource(android.R.color.transparent);
            lblMe.setBackgroundResource(android.R.color.transparent);
        }
        if  ( v == btBooverChat) {
            mPager.setCurrentItem(1);
            lblBoover.setBackgroundResource(android.R.color.transparent);
            lblChat.setBackgroundResource(R.drawable.rounded_edittext_transparent);
            lblPosts.setBackgroundResource(android.R.color.transparent);
            lblBooks.setBackgroundResource(android.R.color.transparent);
            lblFriends.setBackgroundResource(android.R.color.transparent);
            lblMe.setBackgroundResource(android.R.color.transparent);

        }
        if  ( v == btBlooger) {
            mPager.setCurrentItem(2);
            lblBoover.setBackgroundResource(android.R.color.transparent);
            lblChat.setBackgroundResource(android.R.color.transparent);
            lblPosts.setBackgroundResource(R.drawable.rounded_edittext_transparent);
            lblBooks.setBackgroundResource(android.R.color.transparent);
            lblFriends.setBackgroundResource(android.R.color.transparent);
            lblMe.setBackgroundResource(android.R.color.transparent);

        }
        if  ( v == btBooverShelf) {
            mPager.setCurrentItem(3);
            lblBoover.setBackgroundResource(android.R.color.transparent);
            lblChat.setBackgroundResource(android.R.color.transparent);
            lblPosts.setBackgroundResource(android.R.color.transparent);
            lblBooks.setBackgroundResource(R.drawable.rounded_edittext_transparent);
            lblFriends.setBackgroundResource(android.R.color.transparent);
            lblMe.setBackgroundResource(android.R.color.transparent);
        }
        if  ( v == btMyBoovers) {
            mPager.setCurrentItem(4);
            lblBoover.setBackgroundResource(android.R.color.transparent);
            lblChat.setBackgroundResource(android.R.color.transparent);
            lblPosts.setBackgroundResource(android.R.color.transparent);
            lblBooks.setBackgroundResource(android.R.color.transparent);
            lblFriends.setBackgroundResource(R.drawable.rounded_edittext_transparent);
            lblMe.setBackgroundResource(android.R.color.transparent);

        }
        if  ( v == btUser) {
            mPager.setCurrentItem(5);
            lblBoover.setBackgroundResource(android.R.color.transparent);
            lblChat.setBackgroundResource(android.R.color.transparent);
            lblPosts.setBackgroundResource(android.R.color.transparent);
            lblBooks.setBackgroundResource(android.R.color.transparent);
            lblFriends.setBackgroundResource(android.R.color.transparent);
            lblMe.setBackgroundResource(R.drawable.rounded_edittext_transparent);

        }
    }

    public class SlidePagerAdapter extends FragmentPagerAdapter {
        public SlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new FrgMeetBoover();
            } else if (position == 1) {
                FrgChannel fFrag = new FrgChannel();
                return fFrag;
            } else if (position == 2) {
                return new FrgBlooger();
            } else if (position == 3) {
                return new FrgBooverShelf();
            } else if (position == 4) {
                return new FrgMyBoovers();
            } else if (position == 5) {
                return new FrgUser();
            } else {
                return new StaticFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public int getItemPosition(Object object) {
                return POSITION_NONE;
        }
    }
}

