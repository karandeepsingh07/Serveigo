package com.service.serveigo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterServiceFinal extends RecyclerView.Adapter<AdapterServiceFinal.PostViewHolder>{

    private ArrayList<ClassService> items;
    private Context context;

    public AdapterServiceFinal(ArrayList<ClassService> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterServiceFinal.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.service_detail_list, parent, false);
            return new AdapterServiceFinal.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdapterServiceFinal.PostViewHolder holder, int position) {

        final ClassService item =items.get(position);
        final String service=item.getHead();
        final long price=item.getPrice();

        holder.textView1.setText(service);
        holder.textView2.setText(""+price);
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
        public TextView textView2;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_service);
            textView2=itemView.findViewById(R.id.textView_price);


        }
    }
}
