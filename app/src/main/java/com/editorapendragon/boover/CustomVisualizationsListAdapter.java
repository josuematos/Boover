package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomVisualizationsListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private ArrayList<String> vNome = new ArrayList<String>();
    private ArrayList<String> vtUser = new ArrayList<String>();
    private ArrayList<String> vtStatus = new ArrayList<String>();
    private ArrayList<String> vData = new ArrayList<String>();
    private DatabaseReference mDatabase;

    public CustomVisualizationsListAdapter(Activity context, ArrayList<String> vNome,
                                           ArrayList<String> vtUser, ArrayList<String> vtStatus, ArrayList<String> vData ) {
        super(context, R.layout.channellistview, vNome);
        this.context=context;
        this.vNome=vNome;
        this.vtUser=vtUser;
        this.vtStatus=vtStatus;
        this.vData=vData;
    }

      public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.visualizationlistview, null,true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vNome.get(position));
        final CircleImageView imgChannel = (CircleImageView) rowView.findViewById(R.id.iconUser);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(vtUser.get(position)).child("Default")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot vnoteSnapshot) {
                        if (vnoteSnapshot.getValue()!=null) {
                            Glide.with(getContext())
                                    .load(vnoteSnapshot.getValue().toString())
                                    .into(imgChannel);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("foto", "getUser:onCancelled", databaseError.toException());
                    }
                });        TextView txtStatus = (TextView) rowView.findViewById(R.id.itemmessage);
        if (vtStatus.size()>=position+1){
            txtStatus.setText(" "+vtStatus.get(position)+" ");
            txtStatus.setAllCaps(true);
            if (vtStatus.get(position).equals("on")) {
                txtStatus.setBackgroundColor(Color.GREEN);
            }else{
                txtStatus.setBackgroundColor(Color.LTGRAY);
            }
        }else{
            txtStatus.setBackgroundColor(Color.LTGRAY);
            txtStatus.setText(" Off ");

        }

        Long vVis;
        vVis = Long.parseLong(vData.get(position));
        DateTime dataInicio = new DateTime(vVis*-1);
        DateTimeFormatter dtfExtenso = DateTimeFormat.forPattern("dd 'de' MMMM 'de' yyyy '\n'HH:mm:ss");
        TextView txtVis = (TextView) rowView.findViewById(R.id.txtTempoVisualizacao);
        txtVis.setText(dataInicio.toString(dtfExtenso));

        return rowView;
    }
}
