package com.xploreict.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView companyLogo;
    TextView mainTitle,subTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        companyLogo=findViewById(R.id.companyLogo);
        mainTitle=findViewById(R.id.mainTitle);
        subTitle=findViewById(R.id.subTitle);

        final String[] leveltext ={"Ultimate Brain Tester","Ultimate IQ Tester","Ultimate Exam Tester","Ultimate GK Tester","Ultimate Brain Booster","Ultimate Exam Prep"};
        final int random = (int) (Math.random()*6);
        final String level = leveltext[random];
        subTitle.setText(level);

        Animation company_logo = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_animation);
        Animation fadeIn  = AnimationUtils.loadAnimation(this, R.anim.fadein);

        companyLogo.startAnimation(company_logo);
        mainTitle.startAnimation(fadeIn);
        subTitle.startAnimation(fadeIn);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        Intent splashintent = new Intent(SplashActivity.this,MainActivity.class);
                        splashintent.putExtra("level",level);
                        startActivity(splashintent);
                        finish();
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}