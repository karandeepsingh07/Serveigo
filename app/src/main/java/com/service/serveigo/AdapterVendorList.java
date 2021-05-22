package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterVendorList extends RecyclerView.Adapter<AdapterVendorList.PostViewHolder> {

    private ArrayList<ClassVendor> items;
    private Context context;
    private String state;
    private String city;
    private String category;
    private String subCategory;
    private StringBuilder services;
    private ArrayList<String> serviceList;
    private ArrayList<Long> priceList;
    private ArrayList<String> headList;

    public AdapterVendorList(ArrayList<ClassVendor> items, Context context, String state, String city, String category, String subCategory, StringBuilder services, ArrayList<String> serviceList, ArrayList<Long> priceList, ArrayList<String> headList) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
        this.category = category;
        this.subCategory = subCategory;
        this.services = services;
        this.serviceList = serviceList;
        this.priceList = priceList;
        this.headList = headList;
    }

    @NonNull
    @Override
    public AdapterVendorList.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.vendor_cardviewsecond, parent, false);
            return new AdapterVendorList.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AdapterVendorList.PostViewHolder holder, int position) {

        final ClassVendor item =items.get(position);
        final String vendorID=item.getVendorID();
        Log.d("xyzUid",vendorID);
        final String vendorAddress=item.getAddress();
        final String vendorName=item.getName();
        holder.textView1.setText(vendorName);
      //  holder.textView2.setText(item.getSubHead());
        Picasso.get().load(item.getVendorImage()).placeholder(R.drawable.plumbing)
                .error(R.drawable.plumbing).into(holder.imageView);

        if(!item.isVisible()) {
            holder.itemView.setAlpha(0.4f);
            holder.itemView.setEnabled(false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                // Toast.makeText(context, matchId, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,DetailActivity.class);

                intent.putExtra("state",state);
                intent.putExtra("city",city);
                intent.putExtra("category",category);
                intent.putExtra("subCategory",subCategory);
                intent.putExtra("vendorID",vendorID);
                intent.putExtra("vendorAddress",vendorAddress);
                intent.putExtra("vendorName",vendorName);
                intent.putExtra("services",services.toString());
                intent.putExtra("vendorImage",item.getVendorImage());
                intent.putExtra("serviceList",serviceList);
                intent.putExtra("priceList",priceList);
                intent.putExtra("headList",headList);
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
        public TextView textView2;
        CircleImageView imageView;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView_vendorName);
          //  textView2=itemView.findViewById(R.id.textViewSubHead);
            imageView=itemView.findViewById(R.id.imageView);


        }
    }
}
