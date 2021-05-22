package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCategoryList extends RecyclerView.Adapter<AdapterCategoryList.PostViewHolder> {

    private ArrayList<ClassCategory> items;
    private Context context;
    private String state;
    private String city;

    public AdapterCategoryList(ArrayList<ClassCategory> items, Context context,String state,String city) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.category_cardview, parent, false);
            return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        ClassCategory item =items.get(position);
        final String head = item.getHead();
        holder.textView1.setText(head);
       // holder.textView2.setText(item.getImageUrl());
        final String uid = item.getUid();
        if(!item.getImageUrl().equals(null))
            Picasso.get().load(item.getImageUrl()).into(holder.imageView);


        /*if(!item.isVisible()) {
            holder.itemView.setAlpha(0.6f);
            holder.itemView.setEnabled(false);
            holder.cardView.setElevation(0f);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                // Toast.makeText(context, matchId, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,SubCategoryActivity.class);

                intent.putExtra("state",state);
                intent.putExtra("city",city);
                intent.putExtra("category",uid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
      //  public TextView textView2;
        public ImageView imageView;
        public CardView cardView;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textViewHead);
          //  textView2=itemView.findViewById(R.id.textViewSubHead);
            imageView=itemView.findViewById(R.id.imageView);
            cardView=itemView.findViewById(R.id.cardView);

        }
    }
}
