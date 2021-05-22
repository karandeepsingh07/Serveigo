package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterBookingList extends RecyclerView.Adapter<AdapterBookingList.PostViewHolder> {

    private ArrayList<ClassBooking> items;
    private Context context;

    public AdapterBookingList(ArrayList<ClassBooking> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.booking_cardview,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        final ClassBooking item =items.get(position);
        String[] nameList=item.getVendorName().split(" ");
        String name=nameList[0];
        Log.d("xyzVTA",item.vendorType);
        holder.textView1.setText(name);
        holder.textView2.setText(item.getVendorType());
        holder.textView3.setText(item.getDate());
        holder.textView4.setText(item.getStatus());
        Picasso.get().load(item.getVendorImage()).placeholder(R.drawable.plumbing)
                .error(R.drawable.plumbing) .into(holder.imageView);
        if(item.getStatus().equals("DuePayment")){
            holder.textView4.setText("Accepted");
            holder.textView4.setTextColor(Color.parseColor("#009846"));
        }else if(item.getStatus().equals("Closed")){
            holder.textView4.setText("Completed");
            holder.textView4.setTextColor(Color.parseColor("#F7CB73"));
        }else if(item.getStatus().equals("Rejected")){
            holder.textView4.setTextColor(Color.parseColor("#E31E24"));
        }else if(item.getStatus().equals("Open")){
            holder.textView4.setTextColor(Color.parseColor("#EF7F1A"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,BookingActivity.class);
                intent.putExtra("jobId",item.getJobId());
                intent.putExtra("vendorImage",item.getVendorImage());
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
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public CircularImageView imageView;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_vendorName);
            textView2=itemView.findViewById(R.id.textView_vendorType);
            textView3=itemView.findViewById(R.id.textView_date);
            textView4=itemView.findViewById(R.id.textView_serviceStatus);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }
}
