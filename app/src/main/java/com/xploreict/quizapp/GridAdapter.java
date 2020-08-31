package com.xploreict.quizapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class GridAdapter extends BaseAdapter {

    private List<String> sets;
    private String category;
    private InterstitialAd interstitialAd;

    public GridAdapter(List<String> sets, String category, InterstitialAd interstitialAd) {
        this.sets = sets;
        this.category = category;
        this.interstitialAd = interstitialAd;
    }


    @Override
    public int getCount() {
        return sets.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertview, final ViewGroup parent) {
        View view;
        if (convertview == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item, parent, false);
        } else {
            view = convertview;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                        Intent qusIntent = new Intent(parent.getContext(),QuestionActivity.class);
                        qusIntent.putExtra("category",category);
                        qusIntent.putExtra("setId",sets.get(position));
                        parent.getContext().startActivity(qusIntent);
                    }
                });
                if (interstitialAd.isLoaded()){
                    interstitialAd.show();
                    return;
                }
                Intent qusIntent = new Intent(parent.getContext(),QuestionActivity.class);
                qusIntent.putExtra("category",category);
                qusIntent.putExtra("setId",sets.get(position));
                parent.getContext().startActivity(qusIntent);
            }
        });

        ((TextView) view.findViewById(R.id.text_view)).setText(String.valueOf(position + 1));
        return view;
    }
}
