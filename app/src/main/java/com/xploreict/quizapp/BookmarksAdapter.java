package com.xploreict.quizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.Viewholder> {

    private List<QuestionModel> list;

    public BookmarksAdapter(List<QuestionModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_item,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.setData(list.get(position).getQuestion(),list.get(position).getAnswer(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder{

        private TextView qustion, answer;
        private ImageButton deletebtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            qustion = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            deletebtn = itemView.findViewById(R.id.delete_btn);

        }

        private void setData(String question, String answer, final int position){
            this.qustion.setText(question);
            this.answer.setText(answer);
            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }
}
