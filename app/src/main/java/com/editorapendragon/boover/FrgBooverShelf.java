package com.editorapendragon.boover;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.LocaleList;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.model.Bookshelf;
import com.google.api.services.books.model.Bookshelves;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;
import com.google.api.services.books.Books;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverShelf extends Fragment {

    private static final String TAG = "FrgMeetBoover";
    private static final String APPLICATION_NAME = "Boover";
    private static final String API_KEY="AIzaSyDvi_SAEPvNP2XqLkRNYaU7pwtv4Riiklo";
    private static final String AWS_ACCESS_KEY_ID = "AKIAJOZ36CXGBVERFQOA";
    private static final String AWS_SECRET_KEY = "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";
    private Integer paginas = 6;


    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
    private Handler handler;
    private static ProgressBar mProgressBar;
    private static ListView list;
    private static Activity context;
    private static AlertDialog alerta;
    private String tipo;
    private String inicio = "0";
    private ImageButton btnSAuthor, btnSTitle, btnSISBN, btnSPublisher, btnBShelf, btnReload, btnBack, btnSCategory, btnMarket;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String mUid, vNome, vBack = "0", AmazonURL;
    private ArrayList<String> bookshelf = new ArrayList<String>();
    private ArrayList<String> vidBook = new ArrayList<String>();
    private ArrayList<String> vTitleBook = new ArrayList<String>();
    private ArrayList<String> vPhotoBook = new ArrayList<String>();
    private ArrayList<String> vMessageBook = new ArrayList<String>();
    private ArrayList<String> vLinkBook = new ArrayList<String>();
    private ArrayList<String> vAuthorBook = new ArrayList<String>();
    private ArrayList<String> vReviewsBook = new ArrayList<String>();
    private ArrayList<String> gCount = new ArrayList<String>();

    private static String ENDPOINT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgboovershelf_fragment, container, false);
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        final View convertView = (View) inflater.inflate(R.layout.customlistalertdialog, null);
        //getFragmentManager().beginTransaction().addToBackStack("FrgBooverShelf").commit();
        context = getActivity();

         ENDPOINT =  context.getResources().getString(R.string.site_amazon);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        list = (ListView)  view.findViewById (R.id.lstViewBooks);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid().toString();
        btnReload = (ImageButton) view.findViewById(R.id.ic_switch);
        btnBack = (ImageButton) view.findViewById(R.id.btnBack);

        if (mAuth.getCurrentUser().getDisplayName()!=null){
            vNome = mAuth.getCurrentUser().getDisplayName();
        }else{
            mDatabase.child("Users").child(mUid).child("nome")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue()!=null){
                                vNome = dataSnapshot.getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }



        Bundle bundleboovershelf = this.getArguments();
        if (bundleboovershelf!=null) {
            mUid = bundleboovershelf.getString("dUid", "0");
            vNome = bundleboovershelf.getString("vNome", "0");
            vBack = bundleboovershelf.getString("vBack", "0");
             if (vBack.equals("1")){
                 btnBack.setVisibility(View.VISIBLE);
                 btnReload.setVisibility(View.INVISIBLE);
             }else{
                 btnBack.setVisibility(View.INVISIBLE);
                 btnReload.setVisibility(View.VISIBLE);
                 vBack="0";
             }
        }

            mDatabase.child("Books").child(mUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        bookshelf.clear();
                        for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                            if (noteDataSnapshot.getKey()!=null) {
                                bookshelf.add(noteDataSnapshot.getKey());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        // ...
                    }
                });
        btnBShelf = (ImageButton) view.findViewById(R.id.ic_boovershef);
        btnBShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundleMyShelf = new Bundle();
                bundleMyShelf.putString("dUid", mUid);
                bundleMyShelf.putString("vNome", vNome);
                FrgBooverMyShelf fFrag = new FrgBooverMyShelf();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                fFrag.setArguments(bundleMyShelf);
                ft.replace( R.id.frgbooks_frame, fFrag, "FrgBooverMyShelf");
                ft.addToBackStack("FrgBooverMyShelf");
                ft.commit();
            }
        });

        btnMarket = (ImageButton) view.findViewById(R.id.ic_trocas);
        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrgBooverMarket fFrag = new FrgBooverMarket();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace( R.id.frgbooks_frame, fFrag, "FrgBooverMarket");
                ft.addToBackStack("FrgBooverMarket");
                ft.commit();
            }
        });

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Shelf de "+ vNome, Toast.LENGTH_LONG).show();
                FrgBooverShelf fFrag = new FrgBooverShelf();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.add( R.id.frgbooks_frame, fFrag, "FrgBooverShelf");
                ft.addToBackStack("FrgBooverShelf");
                ft.commit();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        btnSAuthor = (ImageButton) view.findViewById(R.id.ic_search_author);
        btnSAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.busca_autor);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setWidth(1440);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);
                inicio = "0";
                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthor = input.getText().toString();
                        String query = "author:"+newAuthor;
                     try{
                         mProgressBar.setVisibility(ProgressBar.VISIBLE);
                         zeraamazon();
                         for (int i=1;i<=paginas;i++) {
                             AmazonURL = queryAmazonBooks(newAuthor, "", "", "", null, vBack, i);
                             Log.e("URL", AmazonURL);
                             if (AmazonURL != null) {
                                 fetchTitle(AmazonURL, "Normal", vBack);
                             }
                         }
                         mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            inicio = "0";
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
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

        btnSTitle = (ImageButton) view.findViewById(R.id.ic_search_title);
        btnSTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.busca_titulo);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setWidth(1440);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);
                inicio = "0";
                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthor = input.getText().toString();
                        String query = "title:"+newAuthor;
                     try{
                         mProgressBar.setVisibility(ProgressBar.VISIBLE);
                         zeraamazon();
                         for (int i=1;i<=paginas;i++) {
                             AmazonURL = queryAmazonBooks("", "", newAuthor, "",null, vBack, i);
                             Log.e("URL",AmazonURL);
                             if (AmazonURL!=null) {
                                 fetchTitle(AmazonURL, "Normal", vBack);
                             }
                         }
                         mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            inicio = "0";
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
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

        btnSISBN = (ImageButton) view.findViewById(R.id.ic_search_isbn);
        btnSISBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.busca_isbn);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setWidth(1440);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);
                inicio = "0";
                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthor = input.getText().toString();
                     try{
                         mProgressBar.setVisibility(ProgressBar.VISIBLE);
                         zeraamazon();
                         for (int i=1;i<=paginas;i++) {
                             AmazonURL = queryAmazonBooks("", "", "", newAuthor,null,vBack, i);
                             Log.e("URL",AmazonURL);
                             if (AmazonURL!=null) {
                                 fetchTitle(AmazonURL, "Normal", vBack);
                             }
                         }
                         mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                            inicio = "0";
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
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

        btnSCategory = (ImageButton) view.findViewById(R.id.ic_search_category);
        btnSCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.busca_category);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setWidth(1440);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);
                inicio = "0";
                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthor = input.getText().toString();
                        try{
                            mProgressBar.setVisibility(ProgressBar.VISIBLE);
                            zeraamazon();
                            for (int i=1;i<=paginas;i++) {
                                AmazonURL = queryAmazonBooks("", "", "", newAuthor,null,vBack, i);
                                Log.e("URL",AmazonURL);
                                if (AmazonURL!=null) {
                                    fetchTitle(AmazonURL, "Normal", vBack);
                                }
                            }
                            mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                            inicio = "0";
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
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


        btnSPublisher = (ImageButton) view.findViewById(R.id.ic_search_publisher);
        btnSPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.ThemeDialogCustom);
                builder.setTitle(R.string.busca_editora);
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                input.setWidth(1440);
                input.setHeight(150);
                input.setPadding(15,0,15,0);
                input.setTextColor(getContext().getResources().getColor(R.color.blue_main));
                input.setBackground(getContext().getResources().getDrawable(R.drawable.rounded_edittext));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_boover_rounded);
                inicio = "0";
                builder.setPositiveButton(R.string.buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAuthor = input.getText().toString();
                        String query = "publisher:"+newAuthor;
                   try{
                       mProgressBar.setVisibility(ProgressBar.VISIBLE);
                       zeraamazon();
                            for (int i=1;i<=paginas;i++) {
                                AmazonURL = queryAmazonBooks("", newAuthor, "", "", null, vBack, i);
                                Log.e("URL",AmazonURL);
                                if (AmazonURL!=null) {
                                    fetchTitle(AmazonURL, "Normal", vBack);
                                }
                            }
                       mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                       inicio = "0";
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
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

        return view;
    }




    private static void queryGoogleBooks(JsonFactory jsonFactory, String query, String tipo) throws Exception {
        // Set up classBooks client.

        ArrayList<String> vidBook = new ArrayList<String>();
        ArrayList<String> vTitleBook = new ArrayList<String>();
        ArrayList<String> vPhotoBook = new ArrayList<String>();
        ArrayList<String> vMessageBook = new ArrayList<String>();
        ArrayList<String> vLinkBook = new ArrayList<String>();
        ArrayList<String> gStars = new ArrayList<String>();
        ArrayList<String> gCount = new ArrayList<String>();
        String detalhes;

        final Books books = new Books.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory, null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();

        // Set query string and filter only Google eBooks.

        List volumesList = books.volumes().list(query);
        if (tipo.equals("0")) {
            volumesList.setFilter("ebooks");
        }else{
            volumesList.setFilter("free-ebooks");
        }
        volumesList.setPrintType("books");
        volumesList.setMaxResults(Long.parseLong("40"));
        String lang = Locale.getDefault().getLanguage().substring(0,2);
        volumesList.setLangRestrict(lang);

        // Execute the query.
        Volumes volumes = volumesList.execute();
        if (volumes.getTotalItems() == 0 || volumes.getItems() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialogCustom);
            builder.setTitle(R.string.buscar);
            builder.setIcon(R.drawable.ic_boover_rounded);
            builder.setMessage(R.string.nao_encontrado);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Log.e("log","No matches found.");
                }
            });
            alerta = builder.create();
            alerta.show();

            return;
        }
        for (Volume volume : volumes.getItems()) {
            Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
            Volume.SaleInfo saleInfo = volume.getSaleInfo();
            // Title.
            vidBook.add(volume.getId());
            vTitleBook.add(volumeInfo.getTitle());
            // Author(s).
            //java.util.List<String> authors = volumeInfo.getAuthors();
            /*if (authors != null && !authors.isEmpty()) {
                Log.e("Log","Author(s): ");
                for (int i = 0; i < authors.size(); ++i) {
                    Log.e("Log",authors.get(i));
                    if (i < authors.size() - 1) {
                        Log.e("Log",", ");
                    }
                }
            }*/
            detalhes ="";
            if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                if (volumeInfo.getDescription().length()>270) {
                    detalhes = detalhes + context.getResources().getString(R.string.descricao)+" " + volumeInfo.getDescription().substring(0, 270) + " ... (" + context.getString(R.string.veja_mais) + ")";
                }else{
                    detalhes = detalhes + context.getResources().getString(R.string.descricao) +" " + volumeInfo.getDescription() + " ... (" + context.getString(R.string.veja_mais) + ")";
                }
            }
            // Ratings (if any).
            if (volumeInfo.getRatingsCount() != null && volumeInfo.getRatingsCount() > 0) {
                int fullRating = (int) Math.round(volumeInfo.getAverageRating().doubleValue());
                gStars.add(Integer.toString(fullRating));
                if (volumeInfo.getRatingsCount()>0) {
                    gCount.add(volumeInfo.getRatingsCount() + " "+context.getResources().getString(R.string.votos));
                }else{
                    gCount.add("0 "+context.getResources().getString(R.string.votos));
                }
            }
            // Price (if any).
            if (saleInfo != null && "FOR_SALE".equals(saleInfo.getSaleability())) {
                double save = saleInfo.getListPrice().getAmount() - saleInfo.getRetailPrice().getAmount();
                if (save > 0.0) {
                    detalhes = detalhes + "\n\n" +context.getResources().getString(R.string.preco) +" " + CURRENCY_FORMATTER.format(saleInfo.getListPrice().getAmount());
                    detalhes = detalhes + "\n" + context.getResources().getString(R.string.preco_ebook) + " "
                            + CURRENCY_FORMATTER.format(saleInfo.getRetailPrice().getAmount());
                }

                if (save > 0.0) {
                    detalhes = detalhes + "\n" + context.getResources().getString(R.string.voce_economiza)+" " + CURRENCY_FORMATTER.format(save) + " ("
                            + PERCENT_FORMATTER.format(save / saleInfo.getListPrice().getAmount()) + ")";
                }
            }
            // Access status.
            String accessViewStatus = volume.getAccessInfo().getAccessViewStatus();
            String message = "Additional information about this book is available from Google eBooks at:";
            if ("FULL_PUBLIC_DOMAIN".equals(accessViewStatus)) {
                message = "This public domain book is available for free from Google eBooks at:";
            } else if ("SAMPLE".equals(accessViewStatus)) {
                message = "A preview of this book is available from Google eBooks at:";
            }
            vLinkBook.add(volumeInfo.getInfoLink());
            vMessageBook.add(detalhes);
            vPhotoBook.add(volumeInfo.getImageLinks().getThumbnail());
        }
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        //CustomBooksListAdapter adapter = new CustomBooksListAdapter(context, vidBook, vTitleBook, vMessageBook, vPhotoBook, vLinkBook, gStars, gCount,"Normal", "0");
        //list.setAdapter(adapter);

    }

    private static void queryGoogleBookShelf(JsonFactory jsonFactory, ArrayList<String>  query, String vBack) throws Exception {
        // Set up classBooks client.

        ArrayList<String> vidBook = new ArrayList<String>();
        ArrayList<String> vTitleBook = new ArrayList<String>();
        ArrayList<String> vPhotoBook = new ArrayList<String>();
        ArrayList<String> vMessageBook = new ArrayList<String>();
        ArrayList<String> vLinkBook = new ArrayList<String>();
        ArrayList<String> gStars = new ArrayList<String>();
        ArrayList<String> gCount = new ArrayList<String>();
        String detalhes;

        final Books books = new Books.Builder(new com.google.api.client.http.javanet.NetHttpTransport(), jsonFactory, null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new BooksRequestInitializer(API_KEY))
                .build();

        // Set query string and filter only Google eBooks.
        for (int i=0;i<query.size();i++) {
            List volumesList = books.volumes().list(query.get(i).toString());
            // Execute the query.
            volumesList.setMaxResults(Long.parseLong("1"));
            String lang = Locale.getDefault().getLanguage().substring(0,2);
            volumesList.setLangRestrict(lang);
            Volumes volumes = volumesList.execute();
            if (volumes.getTotalItems() > 0 && volumes.getItems() != null) {
                for (Volume volume : volumes.getItems()) {
                    Volume.VolumeInfo volumeInfo = volume.getVolumeInfo();
                    Volume.SaleInfo saleInfo = volume.getSaleInfo();
                    // Title.
                    vidBook.add(volume.getId());
                    vTitleBook.add(volumeInfo.getTitle());
                    detalhes = "";
                    if (volumeInfo.getDescription() != null && volumeInfo.getDescription().length() > 0) {
                        if (volumeInfo.getDescription().length() > 270) {
                            detalhes = detalhes + context.getResources().getString(R.string.descricao)+" " + volumeInfo.getDescription().substring(0, 270) + " ... (" + context.getString(R.string.veja_mais) + ")";
                        } else {
                            detalhes = detalhes + context.getResources().getString(R.string.descricao)+" " + volumeInfo.getDescription() + " ... (" + context.getString(R.string.veja_mais) + ")";
                        }
                    }
                    // Ratings (if any).
                    if (volumeInfo.getRatingsCount() != null && volumeInfo.getRatingsCount() > 0) {
                        int fullRating = (int) Math.round(volumeInfo.getAverageRating().doubleValue());
                        gStars.add(Integer.toString(fullRating));
                        if (volumeInfo.getRatingsCount()>0) {
                            gCount.add(volumeInfo.getRatingsCount() + " "+context.getResources().getString(R.string.classificacoes));
                        }else{
                            gCount.add("0 "+context.getResources().getString(R.string.classificacoes));
                        }
                    }
                    // Price (if any).
                    if (saleInfo != null && "FOR_SALE".equals(saleInfo.getSaleability())) {
                        double save = saleInfo.getListPrice().getAmount() - saleInfo.getRetailPrice().getAmount();
                        if (save > 0.0) {
                            detalhes = detalhes + "\n\n" +context.getResources().getString(R.string.preco) +" " + CURRENCY_FORMATTER.format(saleInfo.getListPrice().getAmount());
                            detalhes = detalhes + "\n" + context.getResources().getString(R.string.preco_ebook) + " "
                                    + CURRENCY_FORMATTER.format(saleInfo.getRetailPrice().getAmount());
                        }

                        if (save > 0.0) {
                            detalhes = detalhes + "\n" + context.getResources().getString(R.string.voce_economiza)+" " + CURRENCY_FORMATTER.format(save) + " ("
                                    + PERCENT_FORMATTER.format(save / saleInfo.getListPrice().getAmount()) + ")";
                        }
                    }
                    // Access status.
                    String accessViewStatus = volume.getAccessInfo().getAccessViewStatus();
                    String message = "Additional information about this book is available from Google eBooks at:";
                    if ("FULL_PUBLIC_DOMAIN".equals(accessViewStatus)) {
                        message = "This public domain book is available for free from Google eBooks at:";
                    } else if ("SAMPLE".equals(accessViewStatus)) {
                        message = "A preview of this book is available from Google eBooks at:";
                    }
                    vLinkBook.add(volumeInfo.getInfoLink());
                    vMessageBook.add(detalhes);
                    vPhotoBook.add(volumeInfo.getImageLinks().getThumbnail());
                }
            }
        }
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        //CustomBooksListAdapter adapter = new CustomBooksListAdapter(context, vidBook, vTitleBook, vMessageBook, vPhotoBook, vLinkBook, gStars, gCount, "BookShelf", vBack);
        //list.setAdapter(adapter);

    }

    public static String queryAmazonBooks(String Author, String Publisher, String Title, String ISBN,
                                        ArrayList<String> BookShelf, String vBack, Integer pagina) {
        SignedRequestsHelper helper;

        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String requestUrl = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AWSAccessKeyId", "AKIAJOZ36CXGBVERFQOA");
        params.put("AssociateTag", "763967706501");
        params.put("SearchIndex", "Books");
        params.put("Sort", "salesrank");
        params.put("ItemPage", Integer.toString(pagina));
        params.put("Availability", "Available");
        params.put("ResponseGroup", "EditorialReview,Images,ItemAttributes,Offers,SalesRank");
        String vTipo = "Normal";
        if (BookShelf!=null) {
            String constShelf = "";
            for (int i=0;i<BookShelf.size();i++) {
                 constShelf = constShelf + BookShelf.get(i)+"||";
            }
            params.put("Keywords", constShelf);
            vTipo="BookShelf";
        }else {
            if (!Author.isEmpty()) {
                params.put("Author", Author);
            }
            if (!Publisher.isEmpty()) {
                params.put("Publisher", Publisher);
            }
            if (!Title.isEmpty()) {
                params.put("Title", Title);
            }
            if (!ISBN.isEmpty()) {
                params.put("Keywords", ISBN);
            }
        }

        requestUrl = helper.sign(params);
        return requestUrl;
    }

    private void fetchTitle(String requestUrl, String Tipo, String vBack) {


        NodeList  items, item, dados, reviews, foto, resenha;

        try {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(requestUrl);
            items = doc.getElementsByTagName("Item");
            for (int i = 0; i < items.getLength(); i++) {
                Node node = items.item(i);
                    item = node.getChildNodes();
                String tMessage="";
                    for ( int z = 0; z < item.getLength(); z++ ) {
                        Node nodez = item.item(z);
                        if (nodez.getNodeName().toString().equals("ASIN")){
                            vidBook.add(nodez.getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("LargeImage")){
                            foto = nodez.getChildNodes();
                            vPhotoBook.add(foto.item(0).getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("SalesRank")){
                            gCount.add(NumberFormat.getInstance().format(Long.parseLong(nodez.getTextContent())));
                        }
                        if (nodez.getNodeName().toString().equals("DetailPageURL")){
                            vLinkBook.add(nodez.getTextContent());
                        }
                        if (nodez.getNodeName().toString().equals("ItemAttributes")){
                            dados = nodez.getChildNodes();
                            for ( int x = 0; x < dados.getLength(); x++ ) {
                                Node nodex = dados.item(x);
                                if (nodex.getNodeName().toString().equals("Author")){
                                    vAuthorBook.add(nodex.getTextContent());

                                }
                                if (nodex.getNodeName().toString().equals("Title")){
                                    vTitleBook.add(nodex.getTextContent());
                                }
                            }
                        }
                        if (nodez.getNodeName().toString().equals("EditorialReviews")){
                            tMessage="1";
                            reviews = nodez.getChildNodes();
                            Node content = reviews.item(0).getChildNodes().item(1);
                            if (content.getTextContent().length()>0) {
                                String texto = content.getTextContent();
                                if (texto.length()>270){
                                    vMessageBook.add(texto.substring(0,270)+"("+context.getResources().getString(R.string.ver_mais)+")");
                                }else{
                                    vMessageBook.add(texto+"("+context.getResources().getString(R.string.ver_mais)+")");
                                }

                            }else{
                                vMessageBook.add("...");
                            }
                        }
                        if (nodez.getNodeName().toString().equals("ItemLinks")){
                            resenha = nodez.getChildNodes();
                            Node vresenha = resenha.item(2).getChildNodes().item(1);
                            if (vresenha.getTextContent().length()>0) {
                                vReviewsBook.add(vresenha.getTextContent());
                            }
                        }

                    }
                    if (!tMessage.equals("1")){
                        vMessageBook.add("..."+"("+context.getResources().getString(R.string.ver_mais)+")");
                    }

            }
        } catch (ParserConfigurationException a) {

            a.printStackTrace();
        } catch (IOException x){
            x.printStackTrace();

        } catch (SAXException e ){
            e.printStackTrace();
        }

        CustomBooksListAdapter adapter = new CustomBooksListAdapter(context, vidBook, vTitleBook, vMessageBook, vPhotoBook, vLinkBook, vReviewsBook, gCount,Tipo, vBack, vAuthorBook,mUid);
        list.setAdapter(adapter);

//return titles;

    }


    public void zeraamazon (){
        vidBook.clear();
        vTitleBook.clear();
        vPhotoBook.clear();
        vMessageBook.clear();
        vLinkBook.clear();
        vReviewsBook.clear();
        vAuthorBook.clear();
        gCount.clear();
    }

    public void onStart() {
        super.onStart();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                zeraamazon();
                for (int i=1;i<=paginas;i++) {
                    AmazonURL = queryAmazonBooks("","","","",bookshelf,vBack, i);
                    if (AmazonURL!=null) {
                        fetchTitle(AmazonURL, "Normal", vBack);
                    }
                }
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);


            }
        }, 1000);
    }

}