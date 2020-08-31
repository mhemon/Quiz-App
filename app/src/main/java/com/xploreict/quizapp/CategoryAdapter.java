package com.xploreict.quizapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.viewholder> {

    private List<CategoriesModel> categoriesModelList;
    Context context;

    public CategoryAdapter(Context context,List<CategoriesModel> categoriesModelList) {
        this.categoriesModelList = categoriesModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_item,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.setData(categoriesModelList.get(position).getUrl(),categoriesModelList.get(position).getName(),position);
        holder.imageView.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_transition_animation));
        holder.container.setAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return categoriesModelList.size();
    }

    class viewholder extends RecyclerView.ViewHolder{

         CircleImageView imageView;
         TextView title;
         LinearLayout container;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            title = itemView.findViewById(R.id.title_text);
            container = itemView.findViewById(R.id.category);
        }
        private void setData(final String url, final String title, final int position){
            Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent setIntent = new Intent(itemView.getContext(),SetsActivity.class);
                    setIntent.putExtra("title",title);
                    setIntent.putExtra("position",position);
                    setIntent.putExtra("ImageUrl",url);
                    itemView.getContext().startActivity(setIntent);
                }
            });
        }
    }
}
