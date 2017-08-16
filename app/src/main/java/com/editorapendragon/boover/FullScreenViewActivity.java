package com.editorapendragon.boover;

/**
 * Created by Josue on 12/03/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.google.api.client.googleapis.util.Utils;

import java.util.ArrayList;

public class FullScreenViewActivity extends Activity{

    private ArrayList<String> utils, vchaveurl;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private String Tipo, dUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        utils = i.getStringArrayListExtra("utils");
        vchaveurl = i.getStringArrayListExtra("vchaveurl");
        dUid = i.getStringExtra("dUid");
        Tipo = i.getStringExtra("tipo");

        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, utils, vchaveurl, Tipo, dUid);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}
