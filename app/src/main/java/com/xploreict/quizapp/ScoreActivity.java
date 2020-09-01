package com.xploreict.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ScoreActivity extends AppCompatActivity {

    private TextView scored,total,level;
    private Button donebtn;
    private FloatingActionButton addQues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scored = findViewById(R.id.score);
        total = findViewById(R.id.total);
        donebtn = findViewById(R.id.done_btn);
        level = findViewById(R.id.level);
        addQues = findViewById(R.id.add_question_btn);

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


        addQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdialog();
            }
        });


        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void showdialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_question_dialog);

        Button upload,cancel;
        final EditText contactemail,question,categoryName;
        final RadioGroup options;
        final LinearLayout answers;
        final Dialog loading;

        upload = dialog.findViewById(R.id.upload_btn);
        cancel = dialog.findViewById(R.id.cancel_btn);
        options = dialog.findViewById(R.id.options);
        answers = dialog.findViewById(R.id.answer);
        contactemail = dialog.findViewById(R.id.contact_email);
        question = dialog.findViewById(R.id.question);
        categoryName = dialog.findViewById(R.id.category);

        loading = new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.btn_shape));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = contactemail.getText().toString();

                if (email.isEmpty()){
                    contactemail.setError("Required");
                    return;
                }

                if (!isEmailValid(email)){
                    contactemail.setError("Your Email Id is Invalid.");
                    return;
                }

                if (question.getText().toString().isEmpty()){
                    question.setError("Required");
                    return;
                }


                int correct = -1;
                for (int i =0; i< options.getChildCount();i++){

                    EditText answer = (EditText) answers.getChildAt(i);

                    if (answer.getText().toString().isEmpty()){
                        answer.setError("Required");
                        return;
                    }

                    RadioButton radioButton = (RadioButton) options.getChildAt(i);
                    if (radioButton.isChecked()){
                        correct = i;
                        break;
                    }
                }
                if (correct == -1){
                    Toast.makeText(ScoreActivity.this, "Please Select Correct Ans!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (categoryName.getText().toString().isEmpty()){
                    categoryName.setError("Required");
                    return;
                }

                final HashMap<String,Object> map = new HashMap<>();
                map.put("correctANS",((EditText)answers.getChildAt(correct)).getText().toString());
                map.put("optionA",((EditText)answers.getChildAt(0)).getText().toString());
                map.put("optionB",((EditText)answers.getChildAt(1)).getText().toString());
                map.put("optionC",((EditText)answers.getChildAt(2)).getText().toString());
                map.put("optionD",((EditText)answers.getChildAt(3)).getText().toString());
                map.put("question",question.getText().toString());
                map.put("contactEmail",contactemail.getText().toString());
                map.put("categoryName",categoryName.getText().toString());


                 final String  id = UUID.randomUUID().toString();


                loading.show();
                FirebaseDatabase.getInstance().getReference()
                        .child("USER").child(id)
                        .setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            final Dialog dialog1 = new Dialog(ScoreActivity.this);
                            dialog1.setContentView(R.layout.success_dialog);
                            Button donebtn;
                            donebtn = dialog1.findViewById(R.id.success_btn);
                            donebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    loading.cancel();
                                    dialog1.cancel();
                                }
                            });
                            dialog1.show();
                        }else {
                            Toast.makeText(ScoreActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void loadads() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}