package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomBooksMarketListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> vidBook = new ArrayList<String>();
    private ArrayList<String> vTitleBook = new ArrayList<String>();
    private ArrayList<String> vAuthorBook = new ArrayList<String>();
    private ArrayList<String> vMessageBook = new ArrayList<String>();
    private ArrayList<String> vPhotoBook = new ArrayList<String>();
    private ArrayList<String> vLinkBook = new ArrayList<String>();
    private ArrayList<String> vReviewBook = new ArrayList<String>();
    private ArrayList<String> gCount = new ArrayList<String>();
    private ArrayList<Integer> bsomaNota = new ArrayList<Integer>();
    private ArrayList<Integer> bCount = new ArrayList<Integer>();

    private String morder, vbody, back;
    private DatabaseReference mDatabase;
    private Integer bStars;
    private FirebaseAuth mAuth;
    private AlertDialog alerta;
    private String mUid, tipo, vtxtMessage, bookUserUid;
    private Double vtxtmaiorPrecob,vtxtmenorPrecob, vtxtmaiorPrecobl,vtxtmenorPrecobl, vtxtmaiorFreteb,
            vtxtmenorFreteb, vtxtmenorFretebl,vtxtmaiorFretebl, vmaiortotalbl, vmaiortotalb, vmenortotalbl, vmenortotalb;
    final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "Boover";
    private static final String API_KEY="AIzaSyDvi_SAEPvNP2XqLkRNYaU7pwtv4Riiklo";


    public CustomBooksMarketListAdapter(Activity context, ArrayList<String> vidBook, ArrayList<String> vTitleBook,
                                        ArrayList<String> vMessageBook, ArrayList<String> vPhotoBook,
                                        ArrayList<String> vLinkBook, ArrayList<String> vReviewBook,
                                        ArrayList<String> gCount, String tipo, String back,
                                        ArrayList<String> vAuthorBook, String mUid) {
        super(context, R.layout.booksmarketlistview, vidBook);
        this.context=context;
        this.vidBook=vidBook;
        this.vMessageBook=vMessageBook;
        this.vTitleBook=vTitleBook;
        this.vPhotoBook=vPhotoBook;
        this.vLinkBook = vLinkBook;
        this.vReviewBook = vReviewBook;
        this.vAuthorBook = vAuthorBook;
        this.gCount = gCount;
        this.morder = Long.toString(-1 * new Date().getTime());
        this.tipo=tipo;
        this.back=back;
        this.mUid=mUid;
    }

    public ArrayList<String> getvLinkBook() {
        return vLinkBook;
    }

    public View getView(final int position, View view, final ViewGroup parent) {
        final LayoutInflater inflater=context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.booksmarketlistview, null,true);
        final View alertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        final TextView txtMaiorPrecob = (TextView) rowView.findViewById(R.id.txtmaiorprecob);
        final TextView lblMaiorPrecob = (TextView) rowView.findViewById(R.id.lblmaiorprecob);
        final TextView txtMenorPrecob = (TextView) rowView.findViewById(R.id.txtmenorprecob);
        final TextView lblMenorPrecob = (TextView) rowView.findViewById(R.id.lblmenorprecob);
        final LinearLayout llPrecob = (LinearLayout) rowView.findViewById(R.id.llprecob);


        final Button btnVerOfertas = (Button) rowView.findViewById(R.id.btnVerOfertas);
        btnVerOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleBookDetail = new Bundle();
                bundleBookDetail.putString("vBook", vidBook.get(position));
                bundleBookDetail.putString("vTitle", vTitleBook.get(position));
                bundleBookDetail.putString("vPhoto", vPhotoBook.get(position));
                bundleBookDetail.putString("vAuthor", vAuthorBook.get(position));
                FrgBooverMarketDetails fFrag = new FrgBooverMarketDetails();
                FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundleBookDetail);
                ft.replace( R.id.frgbooks_frame, fFrag, "FrgBooverMarketDetails");
                ft.addToBackStack("FrgBooverMarketDetails");
                ft.commit();
            }
        });

        final ImageButton btnRemove = (ImageButton) rowView.findViewById(R.id.ic_remove_bookshelf);

        final TextView txtMessage = (TextView) rowView.findViewById(R.id.itemmessage);
        if (vMessageBook.size()>0) {
            try {
                txtMessage.setText(vMessageBook.get(position));
            }catch (IndexOutOfBoundsException e){
                txtMessage.setText("");
            }
        }else{
            txtMessage.setText("");
        }
        txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleBookDetail = new Bundle();
                bundleBookDetail.putString("vLink", vLinkBook.get(position));
                FrgBooverShelfDetail fFrag = new FrgBooverShelfDetail();
                android.app.FragmentTransaction ft =  context.getFragmentManager().beginTransaction();
                fFrag.setArguments(bundleBookDetail);
                ft.add( R.id.frgbooks_frame, fFrag, "FrgBooverShelfDetail");
                ft.addToBackStack(null);
                ft.commit();

            }
        });

        mDatabase.child("Market").child(vidBook.get(position)).orderByChild("morder")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        vtxtmaiorFreteb=0.0; vtxtmaiorPrecob=0.0; vtxtmaiorPrecobl=0.0; vtxtmaiorFretebl=0.0;
                        vtxtmenorFreteb=0.0; vtxtmenorPrecob=0.0; vtxtmenorPrecobl=0.0; vtxtmenorFretebl=0.0;
                        for (DataSnapshot uidDataSnapshot : dataSnapshot.getChildren()) {
                            if (uidDataSnapshot.getKey().equals(mUid)){
                                btnRemove.setVisibility(View.VISIBLE);
                            }else{
                                btnRemove.setVisibility(View.INVISIBLE);
                            }
                            for (DataSnapshot bookDataSnapshot : uidDataSnapshot.getChildren()) {
                                    if (bookDataSnapshot.getKey().equals("freteb")
                                            && bookDataSnapshot.getValue() != null
                                            && bookDataSnapshot.getValue().toString().length() > 0) {
                                        if (Double.parseDouble(bookDataSnapshot.getValue().toString())>vtxtmaiorFreteb){
                                            vtxtmaiorFreteb=Double.parseDouble(bookDataSnapshot.getValue().toString());
                                        }
                                        if (Double.parseDouble(bookDataSnapshot.getValue().toString())<vtxtmenorFreteb
                                                || vtxtmenorFreteb==0.0){
                                            vtxtmenorFreteb=Double.parseDouble(bookDataSnapshot.getValue().toString());
                                        }
                                    }
                                    if (bookDataSnapshot.getKey().equals("precob")
                                            && bookDataSnapshot.getValue() != null
                                            && bookDataSnapshot.getValue().toString().length() > 0) {
                                        if (Double.parseDouble(bookDataSnapshot.getValue().toString())>vtxtmaiorPrecob){
                                            vtxtmaiorPrecob=Double.parseDouble(bookDataSnapshot.getValue().toString());
                                        }
                                        if (Double.parseDouble(bookDataSnapshot.getValue().toString())<vtxtmenorPrecob
                                                || vtxtmenorPrecob==0.0){
                                            vtxtmenorPrecob=Double.parseDouble(bookDataSnapshot.getValue().toString());
                                        }
                                    }
                            }

                        }
                        DecimalFormat precision = new DecimalFormat("#,##0.00");
                        if (vtxtmenorPrecob==0.0){
                            vtxtmenorPrecob=vtxtmaiorPrecob;
                        }

                        txtMaiorPrecob.setText(precision.format(vtxtmaiorPrecob));
                        txtMenorPrecob.setText(precision.format(vtxtmenorPrecob));

                        if (vtxtmaiorPrecob==0.0 && vtxtmenorPrecob==0.0){
                            llPrecob.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llPrecob.getLayoutParams();
                            lp.height = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                builder.setTitle(R.string.excluir_livro);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_retirar_book_market);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabase.child("Market").child(vidBook.get(position)).child(mUid).removeValue();
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Troco").setValue("0");
                        Toast.makeText(context,R.string.livro_removido, Toast.LENGTH_SHORT).show();

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

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        txtTitle.setText(vTitleBook.get(position));

        TextView txtAuthor = (TextView) rowView.findViewById(R.id.itemauthor);
        txtAuthor.setText(vAuthorBook.get(position));


        ImageView imgChannel = (ImageView) rowView.findViewById(R.id.iconBook);
        if (vPhotoBook.size()>=position+1) {
                Glide.with(getContext())
                        .load(vPhotoBook.get(position))
                        .into(imgChannel);
        }

        TextView txtReviewAmazon = (TextView) rowView.findViewById(R.id.ic_reviews_amazon);
        txtReviewAmazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleBookDetail = new Bundle();
                if (vReviewBook.size()>position) {
                    bundleBookDetail.putString("vLink", vReviewBook.get(position));
                    FrgBooverShelfDetail fFrag = new FrgBooverShelfDetail();
                    android.app.FragmentTransaction ft = context.getFragmentManager().beginTransaction();
                    fFrag.setArguments(bundleBookDetail);
                    ft.add(R.id.frgbooks_frame, fFrag, "FrgBooverShelfDetail");
                    ft.addToBackStack(null);
                    ft.commit();
                }

            }
        });

        TextView txtCount = (TextView) rowView.findViewById(R.id.ic_users_google);
        if (gCount.size()>position) {
           txtCount.setText(gCount.get(position));
        }

        final ImageButton bStar1 = (ImageButton) rowView.findViewById(R.id.ic_bstar1);
        final ImageButton bStar2 = (ImageButton) rowView.findViewById(R.id.ic_bstar2);
        final ImageButton bStar3 = (ImageButton) rowView.findViewById(R.id.ic_bstar3);
        final ImageButton bStar4 = (ImageButton) rowView.findViewById(R.id.ic_bstar4);
        final ImageButton bStar5 = (ImageButton) rowView.findViewById(R.id.ic_bstar5);
        bStar1.setImageResource(R.drawable.ic_boover_rounded_vazado);
        bStar2.setImageResource(R.drawable.ic_boover_rounded_vazado);
        bStar3.setImageResource(R.drawable.ic_boover_rounded_vazado);
        bStar4.setImageResource(R.drawable.ic_boover_rounded_vazado);
        bStar5.setImageResource(R.drawable.ic_boover_rounded_vazado);
        mDatabase.child("BooksRating").child(vidBook.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bStars = 0;
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            if (noteDataSnapshot.getKey().equals("somaNota")) {
                                bsomaNota.add(Integer.parseInt(noteDataSnapshot.getValue().toString()));
                            }
                            if (noteDataSnapshot.getKey().equals("contaNota")) {
                                bCount.add(Integer.parseInt(noteDataSnapshot.getValue().toString()));
                            }
                            if (bsomaNota.size()>position && bCount.size()>position) {
                                if (bCount.get(position)>0) {
                                    bStars = bsomaNota.get(position) / bCount.get(position);
                                }else{
                                    bStars = 0;
                                }
                            }else{
                                bStars = 0;
                            }
                        }
                        if (bStars!=null){
                            TextView txtbCount = (TextView) rowView.findViewById(R.id.ic_users_boover);
                            if (bCount.size()>position) {
                                txtbCount.setText(Integer.toString(bCount.get(position)) + " "+getContext().getResources().getString(R.string.votos));
                            } else{
                                txtbCount.setText("0 "+getContext().getResources().getString(R.string.votos));
                            }
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
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return rowView;
    }

}
