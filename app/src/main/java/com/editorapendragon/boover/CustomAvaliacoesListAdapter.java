package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAvaliacoesListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private ArrayList<String> vMessage = new ArrayList<String>();
    private ArrayList<String> vUser = new ArrayList<String>();
    private ArrayList<String> vStar = new ArrayList<String>();
    private ArrayList<String> vData = new ArrayList<String>();
    private DatabaseReference mDatabase;

    public CustomAvaliacoesListAdapter(Activity context, ArrayList<String> vUser,
                                       ArrayList<String> vMessage, ArrayList<String> vStar, ArrayList<String> vData ) {
        super(context, R.layout.frgmeetdetailsuser_fragment, vUser);
        this.context=context;
        this.vUser=vUser;
        this.vData=vData;
        this.vMessage=vMessage;
        this.vStar=vStar;
    }

      public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.customavaliacoeslistview, null,true);

        final TextView txtAvaliador = (TextView) rowView.findViewById(R.id.txtavaliador);
        TextView txtMessage = (TextView) rowView.findViewById(R.id.txttextoavaliacao);
        txtMessage.setText(vMessage.get(position));
        TextView txtData = (TextView) rowView.findViewById(R.id.txtdataavaliacao);
        final CircleImageView imgUser = (CircleImageView) rowView.findViewById(R.id.iconUser);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(vUser.get(position)).child("Default")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot vnoteSnapshot) {
                        if (vnoteSnapshot.getValue()!=null) {
                            Glide.with(getContext())
                                    .load(vnoteSnapshot.getValue().toString())
                                    .into(imgUser);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("foto", "getUser:onCancelled", databaseError.toException());
                    }
                });
        mDatabase.child("Users").child(vUser.get(position)).child("nome")
                  .addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(DataSnapshot nomenoteSnapshot) {
                          if (nomenoteSnapshot.getValue()!=null) {
                              txtAvaliador.setText(nomenoteSnapshot.getValue().toString());
                          }
                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {
                          Log.w("foto", "getUser:onCancelled", databaseError.toException());
                      }
                  });


        Long vVis;
        vVis = Long.parseLong(vData.get(position));
        DateTime dataInicio = new DateTime(vVis*-1);
        DateTimeFormatter dtfExtenso = DateTimeFormat.forPattern("dd MMMM");
        txtData.setText(dataInicio.toString(dtfExtenso));

          ImageButton bStar1 = (ImageButton) rowView.findViewById(R.id.ic_bstar1);
          ImageButton bStar2 = (ImageButton) rowView.findViewById(R.id.ic_bstar2);
          ImageButton bStar3 = (ImageButton) rowView.findViewById(R.id.ic_bstar3);
          ImageButton bStar4 = (ImageButton) rowView.findViewById(R.id.ic_bstar4);
          ImageButton bStar5 = (ImageButton) rowView.findViewById(R.id.ic_bstar5);
          bStar1.setImageResource(R.drawable.ic_boover_rounded_vazado);
          bStar2.setImageResource(R.drawable.ic_boover_rounded_vazado);
          bStar3.setImageResource(R.drawable.ic_boover_rounded_vazado);
          bStar4.setImageResource(R.drawable.ic_boover_rounded_vazado);
          bStar5.setImageResource(R.drawable.ic_boover_rounded_vazado);
          Integer bStars = Integer.parseInt(vStar.get(position));
          if (bStars > 0) {
              bStar1.setImageResource(R.drawable.ic_boover_rounded);
          }
          if (bStars > 1) {
              bStar2.setImageResource(R.drawable.ic_boover_rounded);
          }
          if (bStars > 2) {
              bStar3.setImageResource(R.drawable.ic_boover_rounded);
          }
          if (bStars > 3) {
              bStar4.setImageResource(R.drawable.ic_boover_rounded);
          }
          if (bStars > 4) {
              bStar5.setImageResource(R.drawable.ic_boover_rounded);
          }

          imgUser.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  if (vUser.size()> position) {
                      try {
                          Bundle bundlereputation = new Bundle();
                          bundlereputation.putString("dUid", vUser.get(position));
                          FrgMeetDetailUser fFrag = new FrgMeetDetailUser();
                          android.support.v4.app.FragmentTransaction ft = ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                          fFrag.setArguments(bundlereputation);
                          ft.add(R.id.frgmeetdetailuser_frame, fFrag, "FrgMeetDetailUser");
                          ft.addToBackStack(null);
                          ft.commit();
                      } catch (IllegalArgumentException e){
                          e.printStackTrace();
                      }
                  }
              }
          });

        return rowView;
    }
}
