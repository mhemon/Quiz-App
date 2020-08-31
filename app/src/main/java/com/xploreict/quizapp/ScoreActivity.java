package com.xploreict.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class ScoreActivity extends AppCompatActivity {

    private TextView scored,total,level,qus,ans;
    private Button donebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scored = findViewById(R.id.score);
        total = findViewById(R.id.total);
        donebtn = findViewById(R.id.done_btn);
        level = findViewById(R.id.level);

        loadads();

        String Score = String.valueOf(getIntent().getIntExtra("score",0));
        String Total = String.valueOf(getIntent().getIntExtra("total",0));

        double amount = Double.parseDouble(Score);
        double amount2 = Double.parseDouble(Total);
        double percentage = (amount * 100.0f) / amount2;


        scored.setText(Score);
        total.setText(Total);

        if (percentage <= 65 || percentage >= 45){
            level.setText("Medium");
        }

        if (percentage <=44){
            level.setText("Work Hard");
        }

        if (percentage >=66){
            level.setText("Good");
        }


        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void loadads() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}