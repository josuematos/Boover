package com.editorapendragon.boover;

/**
 * Created by Josue on 30/01/2017.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.NullValue;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class CustomBooksListAdapter extends ArrayAdapter<String> {

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
    private String mUid, tipo, vNomeUsuario;
    final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Boover";
    private static final String API_KEY="AIzaSyDvi_SAEPvNP2XqLkRNYaU7pwtv4Riiklo";
    /*final Books  books = new Books.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory, null)
            .setApplicationName(APPLICATION_NAME)
            .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
            .build();*/




    public CustomBooksListAdapter(Activity context, ArrayList<String> vidBook, ArrayList<String> vTitleBook,
                                  ArrayList<String> vMessageBook, ArrayList<String> vPhotoBook,
                                  ArrayList<String> vLinkBook, ArrayList<String> vReviewBook,
                                  ArrayList<String> gCount, String tipo, String back,
                                  ArrayList<String> vAuthorBook, String mUid) {
        super(context, R.layout.bookslistview, vidBook);
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

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.bookslistview, null,true);
        final View alertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        if (mAuth.getCurrentUser().getDisplayName()!=null){
            vNomeUsuario = mAuth.getCurrentUser().getDisplayName();
        }else{
            mDatabase.child("Users").child(mUid).child("nome")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null){
                                vNomeUsuario = dataSnapshot.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }



        ImageButton btnRating = (ImageButton) rowView.findViewById(R.id.ic_rating);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustomRating);
                builder.setTitle(R.string.quantos_boovers);
                final RatingBar rate = new RatingBar(context);
                final EditText input = new EditText(context);
                rate.setNumStars(5);
                rate.setStepSize(1);
                rate.setRating(1);
                rate.setMax(5);
                rate.setPadding(15,15,15,15);
                rate.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(rate);
                builder.setIcon(R.drawable.ic_boover_rounded);

                builder.setPositiveButton(R.string.votar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nota = Float.toString(rate.getRating());
                        int vsomaNota = 0, vCount = 0;
                        if (bsomaNota.size()>position){if (bsomaNota.get(position)!=null){ vsomaNota = bsomaNota.get(position); }}
                        if (bCount.size()>position){if (bCount.get(position)!=null){ vCount = bCount.get(position); }}
                        mDatabase.child("BooksRating").child(vidBook.get(position)).child("somaNota").setValue(Integer.toString(vsomaNota+Math.round(Float.parseFloat(nota))));
                        mDatabase.child("BooksRating").child(vidBook.get(position)).child("contaNota").setValue(Integer.toString(vCount+1));
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Nota").setValue(nota);
                    }
                });
                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        ImageButton btnRemove = (ImageButton) rowView.findViewById(R.id.ic_remove_bookshelf);
        if (tipo.equals("BookShelf") && !back.equals("1")){
            btnRemove.setVisibility(View.VISIBLE);
        }else{
            btnRemove.setVisibility(View.INVISIBLE);
        }
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.ThemeDialogCustom);
                builder.setTitle(R.string.excluir_livro);
                builder.setIcon(R.drawable.ic_boover_rounded);
                builder.setMessage(R.string.tem_certeza_excluir_livro_estante);
                builder.setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).removeValue();
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

        ImageView imgChannel = (ImageView) rowView.findViewById(R.id.iconBook);
        if (vPhotoBook.size()>=position+1) {
                Glide.with(getContext())
                        .load(vPhotoBook.get(position))
                        .into(imgChannel);
        }


        TextView txtOfertas = (TextView) rowView.findViewById(R.id.ic_ofertas);
        txtOfertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleMyShelf = new Bundle();
                bundleMyShelf.putString("vBook", vidBook.get(position));
                FrgBooverMarket fFrag = new FrgBooverMarket();
                android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMyShelf);
                ft.replace(R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                ft.addToBackStack("FrgBooverMarket");
                ft.commit();

            }
        });

        TextView txtComprarAmazon = (TextView) rowView.findViewById(R.id.ic_comprar_amazon);
        txtComprarAmazon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleBookDetail = new Bundle();
                String vAsin = vidBook.get(position);
                bundleBookDetail.putString("vLink", "http://www.amazon.com.br/gp/aws/cart/add.html?AWSAccessKeyId=AKIAJOZ36CXGBVERFQOA&AssociateTag=AmazonAPI-User&ASIN.1="+vAsin+"&Quantity.1=1");

                FrgBooverShelfDetail fFrag = new FrgBooverShelfDetail();
                android.app.FragmentTransaction ft =  context.getFragmentManager().beginTransaction();
                fFrag.setArguments(bundleBookDetail);
                ft.add( R.id.frgbooks_frame, fFrag, "FrgBooverShelfDetail");
                ft.addToBackStack(null);
                ft.commit();

            }
        });


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


        final ImageButton btnTroca = (ImageButton) rowView.findViewById(R.id.ic_troca_book);
        btnTroca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnTroca.getTag()!=null) {
                    if (btnTroca.getTag().equals(0)) {
                        btnTroca.setTag(1);
                        btnTroca.setImageResource(R.drawable.ic_market);
                        //mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Troco").setValue("1");
                        //mDatabase.child("Users").child(mUid).child("mercado").setValue("1");
                        vbody = context.getResources().getString(R.string.marquei_como_troco) +" "+vTitleBook.get(position);
                        Bundle bundleMyShelf = new Bundle();
                        bundleMyShelf.putString("mUid", mUid);
                        bundleMyShelf.putString("vbook", vidBook.get(position));
                        bundleMyShelf.putString("vurl", vPhotoBook.get(position));
                        bundleMyShelf.putString("vtitle", vTitleBook.get(position));
                        FrgNewBookMarket fFrag = new FrgNewBookMarket();
                        android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        fFrag.setArguments(bundleMyShelf);
                        ft.replace(R.id.frgbooks_frame, fFrag, "FrgNewBookMarket");
                        ft.addToBackStack("FrgNewBookMarket");
                        ft.commit();
                        //Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_troco), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_nao_troco), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnTroca.setTag(1);
                    btnTroca.setImageResource(R.drawable.ic_market);
                    //mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Troco").setValue("1");
                    //mDatabase.child("Users").child(mUid).child("mercado").setValue("1");
                    vbody = context.getResources().getString(R.string.marquei_como_troco) +" "+vTitleBook.get(position);
                    Bundle bundleMyShelf = new Bundle();
                    bundleMyShelf.putString("mUid", mUid);
                    bundleMyShelf.putString("vbook", vidBook.get(position));
                    bundleMyShelf.putString("vurl", vPhotoBook.get(position));
                    bundleMyShelf.putString("vtitle", vTitleBook.get(position));
                    FrgNewBookMarket fFrag = new FrgNewBookMarket();
                    android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                    fFrag.setArguments(bundleMyShelf);
                    ft.replace(R.id.frgbooks_frame, fFrag, "FrgNewBookMarket");
                    ft.addToBackStack(null);
                    ft.commit();
                    //Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_troco), Toast.LENGTH_SHORT).show();
                }


            }
        });




        final ImageButton btnReading = (ImageButton) rowView.findViewById(R.id.ic_reading_book);
        btnReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnReading.getTag()!=null) {
                    if (btnReading.getTag().equals(0)) {
                        btnReading.setTag(1);
                        btnReading.setImageResource(R.drawable.ic_reading_book);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lendo").setValue("1");
                        vbody = context.getResources().getString(R.string.marquei_como_lendo) +" "+vTitleBook.get(position);
                        writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );

                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_lendo), Toast.LENGTH_SHORT).show();
                    } else {
                        btnReading.setTag(0);
                        btnReading.setImageResource(R.drawable.ic_reading_book_vazado);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lendo").setValue("0");
                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_nao_lendo), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnReading.setTag(1);
                    btnReading.setImageResource(R.drawable.ic_reading_book);
                    mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lendo").setValue("1");
                    vbody = context.getResources().getString(R.string.marquei_como_lendo) +" "+ vTitleBook.get(position);
                    writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );

                    Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_lendo), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageButton btnReaded = (ImageButton) rowView.findViewById(R.id.ic_book_readed);
        btnReaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnReaded.getTag()!=null) {
                    if (btnReaded.getTag().equals(0)) {
                        btnReaded.setTag(1);
                        btnReaded.setImageResource(R.drawable.ic_book_readed);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lido").setValue("1");
                        vbody = context.getResources().getString(R.string.marquei_como_lido)+ " " + vTitleBook.get(position);
                        writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );

                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_lido), Toast.LENGTH_SHORT).show();
                    } else {
                        btnReaded.setTag(0);
                        btnReaded.setImageResource(R.drawable.ic_book_readed_vazado);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lido").setValue("0");
                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_nao_lido), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnReaded.setTag(1);
                    btnReaded.setImageResource(R.drawable.ic_book_readed);
                    mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Lido").setValue("1");
                    vbody = context.getResources().getString(R.string.marquei_como_lido)+ " " + vTitleBook.get(position);
                    writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );
                    Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_lido), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageButton btnWantRead = (ImageButton) rowView.findViewById(R.id.ic_quero_ler);
        btnWantRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnWantRead.getTag()!=null) {
                    if (btnWantRead.getTag().equals(0)) {
                        btnWantRead.setTag(1);
                        btnWantRead.setImageResource(R.drawable.ic_add_bookshelf);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("QueroLer").setValue("1");
                        vbody = context.getResources().getString(R.string.livro_marcado_quero_ler)+ " " + vTitleBook.get(position);
                        writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );

                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_quero_ler), Toast.LENGTH_SHORT).show();
                    } else {
                        btnWantRead.setTag(0);
                        btnWantRead.setImageResource(R.drawable.ic_add_bookshelf_vazado);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("QueroLer").setValue("0");
                        Toast.makeText(context,context.getResources().getString(R.string.unset_quero_ler), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnWantRead.setTag(1);
                    btnWantRead.setImageResource(R.drawable.ic_add_bookshelf);
                    mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("QueroLer").setValue("1");
                    vbody = context.getResources().getString(R.string.livro_marcado_quero_ler)+ " " + vTitleBook.get(position);
                    writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );

                    Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_quero_ler), Toast.LENGTH_SHORT).show();
                }
            }
        });

        final ImageButton btnFavorite = (ImageButton) rowView.findViewById(R.id.ic_book_favorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnFavorite.getTag()!=null) {
                    if (btnFavorite.getTag().equals(0)) {
                        btnFavorite.setTag(1);
                        btnFavorite.setImageResource(R.drawable.ic_book_favorite);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Favorito").setValue("1");
                        vbody = context.getResources().getString(R.string.marquei_como_favorito)+" " + vTitleBook.get(position);
                        writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );
                        Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_favorito), Toast.LENGTH_SHORT).show();
                    } else {
                        btnFavorite.setTag(0);
                        btnFavorite.setImageResource(R.drawable.ic_book_favorite_vazado);
                        mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Favorito").setValue("0");
                        Toast.makeText(context,context.getResources().getString(R.string.unset_favorito), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    btnFavorite.setTag(1);
                    btnFavorite.setImageResource(R.drawable.ic_book_favorite);
                    mDatabase.child("Books").child(mUid).child(vidBook.get(position)).child("Favorito").setValue("1");

                    vbody = context.getResources().getString(R.string.marquei_como_favorito)+" " + vTitleBook.get(position);
                    writeNewPost(mUid, vNomeUsuario, vTitleBook.get(position), vbody, vPhotoBook.get(position), "post", vidBook.get(position) );
                    Toast.makeText(context,context.getResources().getString(R.string.livro_marcado_favorito), Toast.LENGTH_SHORT).show();
                }
            }
        });


        mDatabase.child("Books").child(mUid).child(vidBook.get(position))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            if (noteDataSnapshot.getValue()!=null) {
                                if (noteDataSnapshot.getKey().toString().equals("Lido")) {
                                    if (noteDataSnapshot.getValue().toString().equals("1")) {
                                        btnReaded.setTag(1);
                                        btnReaded.setImageResource(R.drawable.ic_book_readed);
                                    } else {
                                        btnReaded.setTag(0);
                                        btnReaded.setImageResource(R.drawable.ic_book_readed_vazado);
                                    }
                                }
                                if (noteDataSnapshot.getKey().toString().equals("Lendo")) {
                                    if (noteDataSnapshot.getValue().toString().equals("1")) {
                                        btnReading.setTag(1);
                                        btnReading.setImageResource(R.drawable.ic_reading_book);
                                    } else {
                                        btnReading.setTag(0);
                                        btnReading.setImageResource(R.drawable.ic_reading_book_vazado);
                                    }
                                }
                                if (noteDataSnapshot.getKey().toString().equals("Favorito")) {
                                    if (noteDataSnapshot.getValue().toString().equals("1")) {
                                        btnFavorite.setTag(1);
                                        btnFavorite.setImageResource(R.drawable.ic_book_favorite);
                                    } else {
                                        btnFavorite.setTag(0);
                                        btnFavorite.setImageResource(R.drawable.ic_book_favorite_vazado);
                                    }
                                }
                                if (noteDataSnapshot.getKey().toString().equals("QueroLer")) {
                                    if (noteDataSnapshot.getValue().toString().equals("1")) {
                                        btnWantRead.setTag(1);
                                        btnWantRead.setImageResource(R.drawable.ic_add_bookshelf);
                                    } else {
                                        btnWantRead.setTag(0);
                                        btnWantRead.setImageResource(R.drawable.ic_add_bookshelf_vazado);
                                    }
                                }
                                if (noteDataSnapshot.getKey().toString().equals("Troco")) {
                                    if (noteDataSnapshot.getValue().toString().equals("1")) {
                                        btnTroca.setTag(1);
                                        btnTroca.setImageResource(R.drawable.ic_market);
                                    } else {
                                        btnTroca.setTag(0);
                                        btnTroca.setImageResource(R.drawable.ic_market_desmarcado);
                                    }
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

        ImageButton btnNewPost = (ImageButton) rowView.findViewById(R.id.ic_book_resenha);
        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "4");
                bundlePost.putString("uri", vPhotoBook.get(position));
                bundlePost.putString("vtitle", context.getResources().getString(R.string.resenha_livro)+" "+vTitleBook.get(position));
                bundlePost.putString("vbook", vidBook.get(position));
                FrgNewPost fFrag = new FrgNewPost();
                android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.add( R.id.frgbooks_frame, fFrag, "FrgNewPost");
                ft.addToBackStack("FrgNewPost");
                ft.commit();
            }
        });
        ImageButton btnResenhas = (ImageButton) rowView.findViewById(R.id.ic_ver_resenhas);
        btnResenhas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundlePost = new Bundle();
                bundlePost.putString("tipo", "5");
                bundlePost.putString("vbook", vidBook.get(position));
                FrgBlooger fFrag = new FrgBlooger();
                android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
                fFrag.setArguments(bundlePost);
                ft.add( R.id.frgbooks_frame, fFrag, "FrgBloogerResenhas");
                ft.addToBackStack("FrgBloogerResenhas");
                ft.commit();
            }
        });

        ImageButton btnGroup = (ImageButton) rowView.findViewById(R.id.ic_add_bookgroup);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeChannel = context.getResources().getString(R.string.grupo)+" "+vTitleBook.get(position);
                nomeChannel = nomeChannel.replace(".","");
                nomeChannel = nomeChannel.replace("#","");
                nomeChannel = nomeChannel.replace("$","");
                nomeChannel = nomeChannel.replace("[","");
                nomeChannel = nomeChannel.replace("]","");

                writeNewChannel(mUid,nomeChannel,vPhotoBook.get(position));

            }
        });

        return rowView;
    }

    private void writeNewPost(String userId, String username, String title, String body, String imagekey, String tipo, String book ) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body, imagekey, tipo, book);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
        mDatabase.child("Wallet").child(mUid).child("saldob")
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
                        mDatabase.child("Wallet").child(mUid).child("extrato").child("posts").child("valor")
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
                                                    .child("posts").child("valor").setValue(saldo);
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("posts").child("datasolicitacao")
                                                    .setValue(Long.toString(-1 * new Date().getTime()));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("posts").child("status")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_posts));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("posts").child("tipo")
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
        Toast.makeText(context,context.getResources().getString(R.string.novo_post_criado), Toast.LENGTH_SHORT).show();
    }

    private void writeNewChannel(String userId, String title, String imagekey) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        if (imagekey==null){
            imagekey="https://firebasestorage.googleapis.com/v0/b/boover-82fc3.appspot.com/o/Photos%2Fic_group_2.png?alt=media&token=73998661-5d61-4688-a025-bbd84c49e50c";
        }
        String key = mDatabase.child("Channel").push().getKey();
        Channel channel = new Channel(userId, title, imagekey);
        Map<String, Object> postValues = channel.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Channel/" + key, postValues);
        mDatabase.updateChildren(childUpdates);

        Bundle bundleChannel = new Bundle();
        bundleChannel.putString("vChannel",key);
        bundleChannel.putString("vNome", title);
        bundleChannel.putString("vPhotoChannel", imagekey);
        bundleChannel.putString("dUid", userId);

        String key2 = mDatabase.child("messages").push().getKey();
        String mdata = Long.toString(System.currentTimeMillis());
        mDatabase.child("messages").child(key).child(key2).child("channel").setValue(key);
        mDatabase.child("messages").child(key).child(key2).child("text").setValue(context.getResources().getString(R.string.grupo_criado));
        mDatabase.child("messages").child(key).child(key2).child("mdata").setValue(mdata);
        mDatabase.child("messages").child(key).child(key2).child("name").setValue("System");
        mDatabase.child("messages").child(key).child(key2).child("sender").setValue(mUid);
        mDatabase.child("Wallet").child(mUid).child("saldob")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double saldo = 0.00;
                        if (dataSnapshot.getValue()!=null) {
                            saldo = Double.parseDouble(dataSnapshot.getValue().toString())+1;
                        }else{
                            saldo = 1.00;
                        }
                        mDatabase.child("Wallet").child(mUid).child("saldob").setValue(saldo);
                        mDatabase.child("Wallet").child(mUid).child("extrato").child("grupos").child("valor")
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
                                                    .child("grupos").child("valor").setValue(saldo);
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("grupos").child("datasolicitacao")
                                                    .setValue(Long.toString(-1 * new Date().getTime()));
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("grupos").child("status")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_grupos).toString());
                                            mDatabase.child("Wallet").child(mUid).child("extrato")
                                                    .child("grupos").child("tipo")
                                                    .setValue(getContext().getResources().getString(R.string.status_extrato_credito).toString());
                                        } catch (NullPointerException e ){
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


        FrgBooverChat fFrag = new FrgBooverChat();
        android.support.v4.app.FragmentTransaction ft =  ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction();
        fFrag.setArguments(bundleChannel);
        ft.add( R.id.frgbooks_frame, fFrag, "FrgBooverChat");
        ft.addToBackStack(null);
        ft.commit();
        Toast.makeText(context,context.getResources().getString(R.string.grupo_criado), Toast.LENGTH_SHORT).show();

    }


}
