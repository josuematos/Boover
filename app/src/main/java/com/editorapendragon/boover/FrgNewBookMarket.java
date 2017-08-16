package com.editorapendragon.boover;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FrgNewBookMarket extends Fragment implements View.OnClickListener {

    private ImageView imgBook;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    private TextView txtDelete;
    private ImageButton btnCancel;
    private ImageButton btnSave;
    private String vbook, mUid, vdata, morder, vurl;
    private TextView vTitle;
    private String key = "";
    private EditText vPrecob, vFreteb, vDescricao;
    private Spinner vStatus;
    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater
                .inflate(R.layout.frgnew_bookmarket, container, false);

        mProgress = new ProgressDialog(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        btnCancel = (ImageButton) view.findViewById(R.id.btnCancel);
        txtDelete = (TextView) view.findViewById(R.id.lblremover);
        btnSave = (ImageButton) view.findViewById(R.id.btnSavePost);
        imgBook = (ImageView) view.findViewById(R.id.imgBook);
        vPrecob = (EditText) view.findViewById(R.id.edtPrecob);
        vFreteb = (EditText) view.findViewById(R.id.edtFreteb);
        vStatus = (Spinner) view.findViewById(R.id.spStatus);
        vDescricao = (EditText) view.findViewById(R.id.edtDescricao);
        vTitle = (TextView) view.findViewById(R.id.lbltitle);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status_book, R.layout.spinner_layout);

        adapter.setDropDownViewResource(R.layout.spinner_layout);
        vStatus.setAdapter(adapter);

        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        Bundle bundleNewMarket = this.getArguments();
        if (bundleNewMarket != null) {
            vbook = bundleNewMarket.getString("vbook", "0");
            vurl = bundleNewMarket.getString("vurl", null);
            String vtitle = bundleNewMarket.getString("vtitle", null);
            mUid = bundleNewMarket.getString("mUid", null);
            if (vtitle != null) {
                vTitle.setText(vtitle);
            }
            if (vurl != null) {
                Log.e("url", vurl);
                Glide.with(getContext())
                        .load(vurl)
                        .into(imgBook);
            }

        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            mDatabase.child("Books").child(mUid).child(vbook).child("Troco").setValue("0");
            getFragmentManager().popBackStack(null,getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
        if (v == btnSave) {
            if (vPrecob.getText().length() == 0) {
                Toast.makeText(getContext(), getResources().getString(R.string.dados_invalidos), Toast.LENGTH_SHORT).show();
            }
            if (vFreteb.getText().length() == 0) {
                Toast.makeText(getContext(), getResources().getString(R.string.frete_zerado), Toast.LENGTH_SHORT).show();
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ThemeDialogCustom);
                builder.setTitle(getResources().getString(R.string.tem_certeza_book_market));
                builder.setMessage(getResources().getString(R.string.mensagem_book_market));
                builder.setIcon(R.drawable.ic_boover_rounded);

                builder.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        writeNewBookMarket(vbook, mUid, vPrecob.getText().toString(), vFreteb.getText().toString(),
                                vStatus.getSelectedItem().toString(), vDescricao.getText().toString(), vurl);
                        getFragmentManager().popBackStack();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("Books").child(mUid).child(vbook).child("Troco").setValue("0");
                        getFragmentManager().popBackStack();
                    }
                });
                builder.show();
            }
        }
    }

    private void writeNewBookMarket(String vbook, String mUid, String vPrecob,
                                    String vFreteb, String vStatus, String vDescricao, String vurl ) {
        if (vbook!=null && mUid!=null) {
            morder = Long.toString(-1 * new Date().getTime());
            vdata = Long.toString(new Date().getTime());
            mDatabase.child("Market").child(vbook).child(mUid).child("data").setValue(vdata);
            mDatabase.child("Market").child(vbook).child(mUid).child("morder").setValue(morder);
            mDatabase.child("Market").child(vbook).child(mUid).child("freteb").setValue(vFreteb);
            mDatabase.child("Market").child(vbook).child(mUid).child("precob").setValue(vPrecob);
            mDatabase.child("Market").child(vbook).child(mUid).child("status").setValue(vStatus);
            mDatabase.child("Market").child(vbook).child(mUid).child("descricao").setValue(vDescricao);
            //mDatabase.child("Books").child(mUid).child(vbook).removeValue();
            mDatabase.child("Users").child(mUid).child("mercado").setValue("1");
            mDatabase.child("Books").child(mUid).child(vbook).child("Troco").setValue("1");
            Toast.makeText(getContext(), getResources().getString(R.string.salvo), Toast.LENGTH_SHORT).show();
            String vbody = getContext().getResources().getString(R.string.marquei_como_troco) +" "+vTitle.getText().toString();
            writeNewPost(mUid, mAuth.getCurrentUser().getDisplayName(), vTitle.getText().toString(), vbody, vurl, "post", vbook);
            mProgressBar.setVisibility(View.INVISIBLE);

        }
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
        Toast.makeText(getContext(),getContext().getResources().getString(R.string.novo_post_criado), Toast.LENGTH_SHORT).show();
    }
}
