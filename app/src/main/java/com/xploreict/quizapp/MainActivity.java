package com.xploreict.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    private Button startbtn, bookmarksbtn;
    TextView anim1,anim2;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppRater.app_launched(this);

        MobileAds.initialize(this);

        loadads();

        linearLayout = findViewById(R.id.linear_layout);
        startbtn = findViewById(R.id.start_btn);
        bookmarksbtn = findViewById(R.id.bookmarks_btn);
        anim1 = findViewById(R.id.text_anim_1);
        anim2 = findViewById(R.id.text_anim_2);

        level = getIntent().getStringExtra("level");


        anim2.setText(level);


        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(MainActivity.this,CategoriesActivity.class);
                startActivity(categoryIntent);
            }
        });

        bookmarksbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookmarkIntenet = new Intent(MainActivity.this,BookmarksActivity.class);
                startActivity(bookmarkIntenet);
            }
        });

    }

    private void loadads() {
       AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}