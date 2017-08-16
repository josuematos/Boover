package com.editorapendragon.boover;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationListener;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Josué on 23/05/2016.
 */
public class NotificationService extends Service  {

    private static final String TAG = "NotificationService";
    private static final int LOCATION_INTERVAL = 1000;
    private Context mContext;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String visCounter, mUid;
    private Query qMessageChannel;
    private ValueEventListener listener;


    private NotificationManager mNM;
    private AudioManager mAudioManager;
    Bundle b;
    Intent notificationIntent;
    private final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate(){

        Log.e(TAG, "onCreate");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mContext = this;
        getNotification();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(NotificationService.this, 0, new Intent(NotificationService.this,   MainActivity.class), 0);
        notificationIntent = new Intent(this, NotificationService.class);
        mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);

    }

    private void showNotification(String text, Integer numero) {
        PendingIntent contentIntent = PendingIntent.getActivity(NotificationService.this, 0, new Intent(NotificationService.this,   MainActivity.class), 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_boover_rounded);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new Notification.Builder(mContext)
                .setContentTitle("Boover")
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_boover_small)
                .setLargeIcon(largeIcon)
                .setLights(Color.BLUE,500,500)
                .setContentIntent(contentIntent)
                .setSound(uri)
                .build();

        if (mAudioManager.getRingerMode()!=AudioManager.RINGER_MODE_SILENT) {
            Vibrar();
        }
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );

        mNM.notify( numero  , notification);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mNM.cancel(R.string.local_service_started);
        stopSelf();
        //qMessageChannel.removeEventListener(listener);

    }

    private void getNotification(){
        //TODO

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null) {
            mUid = mAuth.getCurrentUser().getUid().toString();

            mDatabase.child("Invitations").child(mUid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot convite : dataSnapshot.getChildren()) {
                                if (convite.getValue() != null) {
                                    showNotification(getResources().getString(R.string.msg_pedido_amizade),1);
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
            mDatabase.child("Notifications").child(mUid).child("notCount")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                visCounter = dataSnapshot.getValue().toString();
                                if (!visCounter.equals("0")) {
                                    showNotification(visCounter + " "+getResources().getString(R.string.notificacoes_nao_lidas),2);
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
            mDatabase.child("Channel").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot Ndata : dataSnapshot.getChildren()) {
                                for (final DataSnapshot N2data : Ndata.getChildren()) {
                                        if (N2data.getKey().equals("Count-"+mUid) && N2data.getValue().equals("on")) {
                                            showNotification(getResources().getString(R.string.mensagem_recebida),3);
                                        }
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


        }
    }
    private void Vibrar()
    {

        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 300;//
        rr.vibrate(milliseconds);
    }

 }