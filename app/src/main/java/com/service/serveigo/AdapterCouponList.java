package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCouponList extends RecyclerView.Adapter<AdapterCouponList.PostViewHolder>{
    private ArrayList<ClassCoupon> items;
    private Context context;
    private float totalBill;

    public AdapterCouponList(ArrayList<ClassCoupon> items, Context context, float totalBill) {
        this.items = items;
        this.context = context;
        this.totalBill = totalBill;
    }

    @NonNull
    @Override
    public AdapterCouponList.PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.coupon_cardview,parent,false);
        return new AdapterCouponList.PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final AdapterCouponList.PostViewHolder holder, int position) {
        final ClassCoupon item=items.get(position);

        holder.textView1.setText(item.getHead());
        Log.d("xyz",item.getHead());
        String type="";
        if(item.getValue()==0)
            type="percentage";
        else
            type="value";
        final String finalType = type;

        final String finalType1 = type;
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getMinValue()<totalBill) {
                    Intent intent = new Intent("coupon-message");
                    intent.putExtra("type", finalType);
                    if (finalType1.equals("percentage"))
                        intent.putExtra("item", (int) item.getPercentage());
                    else
                        intent.putExtra("item", (int) item.getValue());
                    intent.putExtra("uid", item.getUid().trim());
                    Log.d("xyzDiscount", String.valueOf(item.percentage));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }else{
                    Toast.makeText(context, "Minimum bill should be "+item.getMinValue(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView button;

        public PostViewHolder(View itemView) {
            super(itemView);

            textView1=itemView.findViewById(R.id.textView);
            button=itemView.findViewById(R.id.button);
        }
    }
}
