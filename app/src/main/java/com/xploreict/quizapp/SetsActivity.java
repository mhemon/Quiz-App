package com.xploreict.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private GridView gridView;
    private InterstitialAd mInterstitialAd;
    private List<String> sets;
    private String title,url;
    private ImageView imageView,backbtn,shareapp;
    private TextView setTxt;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        loadads();

        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("ImageUrl");


        gridView = findViewById(R.id.gridview);
        imageView = findViewById(R.id.image_view);
        setTxt = findViewById(R.id.setTxt);
        backbtn = findViewById(R.id.backbtn);
        shareapp = findViewById(R.id.share_app);
        linearLayout = findViewById(R.id.sets_container);

        //linearLayout.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_scale_animation));

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String play = "http://play.google.com/store/apps/details?id=";
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Quiz App challenge " + play + getPackageName());
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Come play and prove that your genius.");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });


        setTxt.setText(title);
        Glide.with(this).load(url).into(imageView);

        imageView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_transition_animation));
        gridView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade_scale_animation));
        sets = CategoriesActivity.list.get(getIntent().getIntExtra("position",0)).getSets();


        GridAdapter adapter = new GridAdapter(sets,getIntent().getStringExtra("title"),mInterstitialAd);
        gridView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadads() {
        AdView mAdView = findViewById(R.id.adView_sets);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.fullscreen_ID));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }
}