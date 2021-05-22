package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoryClassAdapterSearch extends RecyclerView.Adapter<SubCategoryClassAdapterSearch.PostViewHolder> {

    private ArrayList<ClassSubCategory> items;
    private Context context;
    private String state;
    private String city;

    public SubCategoryClassAdapterSearch(ArrayList<ClassSubCategory> items, Context context,String state,String city) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
    }

    @NonNull
    @Override
    public SubCategoryClassAdapterSearch.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.serach_cardview, parent, false);
            return new SubCategoryClassAdapterSearch.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(SubCategoryClassAdapterSearch.PostViewHolder holder, int position) {

        final ClassSubCategory item =items.get(position);
        final String head = item.getHead();
        holder.textView1.setText(item.getHead());
        final String category=item.getCategory();


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                // Toast.makeText(context, matchId, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,ServiceActivity.class);

                intent.putExtra("state",state);
                intent.putExtra("city",city);
                intent.putExtra("category",category);
                intent.putExtra("subCategory",head);
                intent.putExtra("imageUrl",item.getImageUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView);


        }
    }
}

