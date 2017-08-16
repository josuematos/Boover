package com.editorapendragon.boover;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Created by Josue on 02/02/2017.
 */

public class FrgBooverShelfDetail extends Fragment {

    private Handler handler;
    private static ProgressBar mProgressBar;
    private WebView mWebView;
    private ImageButton mBackButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frgboovershelfdetails_fragment, container, false);
        getFragmentManager().beginTransaction().addToBackStack("FrgBooksDetails").commit();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mWebView = (WebView) view.findViewById(R.id.webview);

        Bundle bundleBookDetail = this.getArguments();
        if (bundleBookDetail != null) {
            mWebView.loadUrl(bundleBookDetail.getString("vLink", "0"));
            // Enable Javascript
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Force links and redirects to open in the WebView instead of in a browser
            mWebView.setWebViewClient(new WebViewClient());
        }

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mBackButton = (ImageButton) view.findViewById(R.id.ic_switch);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

}