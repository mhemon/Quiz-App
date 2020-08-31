package com.xploreict.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.xploreict.quizapp.R.drawable.option_contanier;
import static com.xploreict.quizapp.R.drawable.share;

public class QuestionActivity extends AppCompatActivity {

    public static final String FILE_NAME = "QuizApp";
    public static final String KEY_NAME = "Questions";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;


    private TextView question, noIndicator,path;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionContainer;
    private Button sharebtn, nextbtn;
    private ImageView closebtn,shareapp;
    private int count = 0;
    private List<QuestionModel> list;
    private int position = 0;
    private int score = 0;
    private String setId,categoryname;

    private List<QuestionModel> bookmarksList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchedquestionposition;
    private AppCompatSeekBar seekBar;


    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.q_no);
        bookmarkBtn = findViewById(R.id.bookmark_btn);
        optionContainer = findViewById(R.id.option_container);
        sharebtn = findViewById(R.id.share_btn);
        nextbtn = findViewById(R.id.next_btn);
        path = findViewById(R.id.path);
        closebtn = findViewById(R.id.close_btn);
        seekBar = findViewById(R.id.seekBar);
        shareapp = findViewById(R.id.share_app);

        loadads();

        categoryname = getIntent().getStringExtra("category");
        setId = getIntent().getStringExtra("setId");

        path.setText(categoryname+" > ");
        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        getBookmarks();

        closebtn.setOnClickListener(new View.OnClickListener() {
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

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelMatch()) {
                    bookmarksList.remove(matchedquestionposition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                } else {
                    bookmarksList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.btn_shape));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);

        list = new ArrayList<>();

        loadingDialog.show();
        myRef
                .child("SETS").child(setId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot dataSnapshot1 : datasnapshot.getChildren()) {
                    String id = dataSnapshot1.getKey();
                    String question = dataSnapshot1.child("question").getValue().toString();
                    String a = dataSnapshot1.child("optionA").getValue().toString();
                    String b = dataSnapshot1.child("optionB").getValue().toString();
                    String c = dataSnapshot1.child("optionC").getValue().toString();
                    String d = dataSnapshot1.child("optionD").getValue().toString();
                    String correctAns = dataSnapshot1.child("correctANS").getValue().toString();

                    list.add(new QuestionModel(id, question, a, b, c, d, correctAns, setId));
                }
                if (list.size() > 0) {
                    for (int i = 0; i < 4; i++) {
                        optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                checkAnswer((Button) view);
                            }
                        });
                    }
                    playAnim(question, 0, list.get(position).getQuestion());


                    nextbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextbtn.setEnabled(false);
                            nextbtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;
                            if (position == list.size()) {
                                //send user to score activity
                                if (interstitialAd.isLoaded()) {
                                    interstitialAd.show();
                                    return;
                                }
                                Intent scoreIntent = new Intent(QuestionActivity.this, ScoreActivity.class);
                                scoreIntent.putExtra("score", score);
                                scoreIntent.putExtra("total", list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(question, 0, list.get(position).getQuestion());
                        }
                    });
                    sharebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String body = list.get(position).getQuestion() + "\n" +
                                    list.get(position).getA() + "\n" +
                                    list.get(position).getB() + "\n" +
                                    list.get(position).getC() + "\n" +
                                    list.get(position).getD();
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quiz App Challenge");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                            startActivity(Intent.createChooser(shareIntent, "share via"));
                        }
                    });
                } else {
                    finish();
                    Toast.makeText(QuestionActivity.this, "No Questions", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data) {

        for (int i = 0; i < 4; i++) {
            optionContainer.getChildAt(i).setBackgroundResource(option_contanier);
        }

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (value == 0 && count < 4) {
                    String option = "";
                    if (count == 0) {
                        option = list.get(position).getA();
                    } else if (count == 1) {
                        option = list.get(position).getB();
                    } else if (count == 2) {
                        option = list.get(position).getC();
                    } else if (count == 3) {
                        option = list.get(position).getD();
                    }
                    playAnim(optionContainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        seekBar.setMax(list.size());
                        seekBar.setProgress(position + 1);
                        noIndicator.setText(position + 1 + "/" + list.size());
                        if (modelMatch()) {
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                        } else {
                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                        }
                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playAnim(view, 1, data);
                } else {
                    enableOption(true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void checkAnswer(Button selectedOption) {
        enableOption(false);
        nextbtn.setEnabled(true);
        nextbtn.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(position).getAnswer())) {
            //correct
            score++;
            selectedOption.setBackgroundResource(R.drawable.correct_shape);
            selectedOption.setTextColor(Color.WHITE);
        } else {
            //incorrect
            selectedOption.setBackgroundResource(R.drawable.incorrect_shape);
            selectedOption.setTextColor(Color.WHITE);
            Button correctOptions = (Button) optionContainer.findViewWithTag(list.get(position).getAnswer());
            correctOptions.setBackgroundResource(R.drawable.correct_shape);
            correctOptions.setTextColor(Color.WHITE);
        }
    }

    private void enableOption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            optionContainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                optionContainer.getChildAt(i).setBackgroundResource(option_contanier);
                Button correctOptions = (Button) optionContainer.findViewWithTag(list.get(position).getAnswer());
                Button selectedOption = (Button) optionContainer.getChildAt(i);
                correctOptions.setTextColor(getResources().getColor(R.color.grey));
                selectedOption.setTextColor(getResources().getColor(R.color.grey));
            }
        }
    }

    private void getBookmarks() {
        String json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        bookmarksList = gson.fromJson(json, type);

        if (bookmarksList == null) {
            bookmarksList = new ArrayList<>();
        }
    }

    private boolean modelMatch() {
        boolean matched = false;
        int i = 0;
        for (QuestionModel model : bookmarksList) {
            if (model.getQuestion().equals(list.get(position).getQuestion())
                    && model.getAnswer().equals(list.get(position).getAnswer())
                    && model.getSet().equals(list.get(position).getSet())) {
                matched = true;
                matchedquestionposition = i;
            }
            i++;
        }
        return matched;
    }

    private void storeBookmarks() {
        String json = gson.toJson(bookmarksList);
        editor.putString(KEY_NAME, json);
        editor.commit();
    }

    private void loadads() {
        AdView mAdView = findViewById(R.id.adViewqus);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.fullscreen_ID));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
                Intent scoreIntent = new Intent(QuestionActivity.this, ScoreActivity.class);
                scoreIntent.putExtra("score", score);
                scoreIntent.putExtra("total", list.size());
                startActivity(scoreIntent);
                finish();
            }
        });
    }
}