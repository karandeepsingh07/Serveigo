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
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSubCategoryList extends RecyclerView.Adapter<AdapterSubCategoryList.PostViewHolder> {

    private ArrayList<ClassSubCategory> items;
    private Context context;
    private String state;
    private String city;
    private  String category;

    public AdapterSubCategoryList(ArrayList<ClassSubCategory> items, Context context,String state,String city,String category) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
        this.category=category;
    }

    @NonNull
    @Override
    public AdapterSubCategoryList.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*if(viewType==0) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.subcategory_cardviewsecond, parent, false);
            return new AdapterSubCategoryList.PostViewHolder(view);
        }else{}*/
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.subcategory_cardview, parent, false);
        return new AdapterSubCategoryList.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdapterSubCategoryList.PostViewHolder holder, int position) {

        final ClassSubCategory item =items.get(position);
        final String head = item.getHead();
        final String uid = item.getUid();
        final String banner = item.getBanner();
        holder.textView1.setText(item.getHead());
        //holder.textView2.setText(item.getSubHead());
        Picasso.get().load(item.getBanner()).placeholder(R.color.colorPrimaryDark)
                .error(R.color.colorPrimaryDark).into(holder.imageView);

        /*if(!item.isVisible()) {
            holder.itemView.setAlpha(0.6f);
            holder.itemView.setEnabled(false);
        }*/

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
                intent.putExtra("subCategory",uid);
                intent.putExtra("head",head);
                intent.putExtra("imageUrl",item.getImageUrl());
                intent.putExtra("banner",item.getBanner());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        // public TextView textView2;
        public ImageView imageView;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textViewHead);
            //textView2=itemView.findViewById(R.id.textViewSubHead);
            imageView = itemView.findViewById(R.id.imageView);


        }
    }
}

