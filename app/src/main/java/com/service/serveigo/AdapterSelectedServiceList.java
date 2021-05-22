package com.service.serveigo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterSelectedServiceList extends RecyclerView.Adapter<AdapterSelectedServiceList.PostViewHolder> {

    private ArrayList<String> items;
    private Context context;
    private String state;
    private String city;
    private  String category;
    private String subCategory;

    public AdapterSelectedServiceList(ArrayList<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.services_cardview,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        final String item =items.get(position);
        holder.textView1.setText(items.get(position));
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textViewHead);

        }
    }
}

