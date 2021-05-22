package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterServiceList extends RecyclerView.Adapter<AdapterServiceList.PostViewHolder> {

    private ArrayList<ClassService> items;
    private Context context;
    private String state;
    private String city;
    private  String category;
    private String subCategory;

    public AdapterServiceList(ArrayList<ClassService> items, Context context,String state,String city,String category,String subCategory) {
        this.items = items;
        this.context = context;
        this.state = state;
        this.city = city;
        this.category=category;
        this.subCategory=subCategory;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.services_cardview,parent,false);
        return new PostViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {

        boolean discount=false;
        double discountedPrice=0;
        final ClassService item =items.get(position);
        holder.textView1.setText(item.getHead());
        if(item.getDuration()!=null)
            holder.textView5.setText(item.getDuration());
        holder.textView3.setText(item.getDiscount()+"% OFF");
        holder.textView4.setText("₹"+item.getPrice());
        if(item.getDiscount()!=0) {
            double price = (item.getDiscount() / 100.0) * item.getPrice();
            discountedPrice=item.getPrice()-price;
            holder.textView2.setText("₹" +discountedPrice);
            discount=true;
        }
        else {
            holder.textView2.setText("₹"+item.getPrice());
            holder.frameLayout.setVisibility(View.GONE);
            holder.textView4.setVisibility(View.INVISIBLE);
        }
        Picasso.get().load(item.getImageUrl()).into(holder.imageView);
//        holder.checkBox.setOnCheckedChangeListener(null);
  //      holder.checkBox.setSelected(item.isChecked());


        final boolean finalDiscount = discount;
        final long finalDiscountedPrice = (long) discountedPrice;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DataSnapshot dataSnapshot;
                //String matchId = dataSnapshot.getChildren().iterator().next().getKey().toString();
                // Toast.makeText(context, matchId, Toast.LENGTH_SHORT).show();
              //  Intent intent=new Intent(context,DetailActivity.class);

               /* intent.putExtra("state",state);
                intent.putExtra("city",city);
                intent.putExtra("category",category);
                intent.putExtra("subCategory",subCategory);
                context.startActivity(intent);*/
               item.setChecked(!item.isChecked());
//               holder.checkBox.setChecked(item.isChecked());
                if(item.isChecked())
                    holder.relativeLayout.setVisibility(View.VISIBLE);
                else
                    holder.relativeLayout.setVisibility(View.INVISIBLE);

               if(item.isChecked()) {
                   Intent intent = new Intent("custom-message");
                   //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                   intent.putExtra("item", item.getUid());
                   intent.putExtra("head", item.getHead());
                   if(finalDiscount) {
                       Log.d("xyzdis", String.valueOf(finalDiscountedPrice));
                       intent.putExtra("price", finalDiscountedPrice);
                   }
                   else
                       intent.putExtra("price",item.getPrice());
                   intent.putExtra("task","add");
                   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               }else{
                   Intent intent = new Intent("custom-message");
                   //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                   intent.putExtra("item", item.getUid());
                   intent.putExtra("head", item.getHead());
                   if(finalDiscount)
                       intent.putExtra("price", finalDiscountedPrice);
                   else
                       intent.putExtra("price",item.getPrice());
                   intent.putExtra("task","remove");
                   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
               }
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        public FrameLayout frameLayout;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public TextView textView5;
        public CheckBox checkBox;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public PostViewHolder(View itemView) {
            super(itemView);

            frameLayout=itemView.findViewById(R.id.frameLayout);
            textView1=itemView.findViewById(R.id.textViewHead);
            textView5=itemView.findViewById(R.id.textViewDuration);
            textView2=itemView.findViewById(R.id.textViewRateDiscount);
            textView3=itemView.findViewById(R.id.textViewOff);
            textView4=itemView.findViewById(R.id.textViewRate);
            imageView=itemView.findViewById(R.id.imageView);

//            checkBox= itemView.findViewById(R.id.checkbox);
            relativeLayout= itemView.findViewById(R.id.relativeLayout_overlay);

        }
    }
}
