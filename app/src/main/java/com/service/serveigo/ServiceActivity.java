package com.service.serveigo;

import android.animation.LayoutTransition;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassService> listItems;
    RecyclerView recyclerViewSelected;
    RecyclerView.Adapter adapterSelected;
    List<String> listItemsSelected;
    List<Long> listItemsPrice;
    List<String> listItemsHead;
    FirebaseFirestore firebaseFirestore;
    Button buttonSearch,buttonBack;
    ImageView imageView,imageViewBanner;
    TextView textViewHead,textViewNOResult,textViewDetail,textViewDetailLine1,textViewDetailLine2,textViewDetailLine3,textViewDetailLine4,textViewDetailLine5;
    ProgressBar progressBar;
    int n;
    String item;
    ImageButton imageButtonExpand,imageButtonCollapse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        //Toolbar toolbar = findViewById(R.id.toolbar);
    //    toolbar.setTitle("Service type");
      //  setSupportActionBar(toolbar);
  //      TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
//        textViewToolbar.setText("Service Type");

        Intent intent=getIntent();
        final String state= intent.getStringExtra("state").trim();
        final String city= intent.getStringExtra("city").trim();
        final String category= intent.getStringExtra("category").trim();
        final String subCategory= intent.getStringExtra("subCategory").trim();
        final String head= intent.getStringExtra("head").trim();
        final String imageUrl= intent.getStringExtra("imageUrl");
        final String bannerUrl= intent.getStringExtra("banner");
//        final ArrayList<String> list= (ArrayList<String>) getIntent().getSerializableExtra("key");
        Log.d("xyz",category);
//        Log.d("xyzSub",bannerUrl);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewSelected=findViewById(R.id.recyclerViewSelected);
//        recyclerViewSelected.setHasFixedSize(true);
  //      recyclerViewSelected.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();
        buttonSearch=findViewById(R.id.buttonSearch);
        imageView= findViewById(R.id.imageView);
        imageViewBanner=findViewById(R.id.imageView_banner);
        textViewHead= findViewById(R.id.textViewHead);
        textViewNOResult= findViewById(R.id.textView_noService);
        textViewDetail=findViewById(R.id.description_text);
        textViewDetailLine1=findViewById(R.id.description_text1);
        textViewDetailLine2=findViewById(R.id.description_text2);
        textViewDetailLine3=findViewById(R.id.description_text3);
        textViewDetailLine4=findViewById(R.id.description_text4);
        textViewDetailLine5=findViewById(R.id.description_text5);
        progressBar=findViewById(R.id.progressBar);
        imageButtonExpand=findViewById(R.id.plus);
        imageButtonCollapse=findViewById(R.id.minus);

        buttonSearch.setEnabled(false);
        textViewHead.setText(head);
        Picasso.get().load(bannerUrl).into(imageViewBanner);
        imageViewBanner.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        buttonBack=findViewById(R.id.button_back);

       buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listItems= new ArrayList<>();
        listItemsSelected= new ArrayList<>();
        listItemsPrice= new ArrayList<>();
        listItemsHead= new ArrayList<>();

        adapter=new AdapterServiceList((ArrayList<ClassService>) listItems,this,state,city,category,subCategory);
        adapterSelected=new AdapterSelectedServiceList((ArrayList<String>) listItemsSelected,this);

        recyclerView.setAdapter(adapter);
 //       recyclerViewSelected.setAdapter(adapterSelected);

        /*for(int i=0;i<7;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
        getResult(state,city,category,subCategory);
        Log.d("abc",category+" "+subCategory);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ((ViewGroup) findViewById(R.id.linearLayout_description)).getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(n==0){
                    buttonSearch.setEnabled(false);
                }else {
                    Intent intent = new Intent(ServiceActivity.this, VendorListActivity.class);
                    intent.putExtra("state", state);
                    intent.putExtra("city", city);
                    intent.putExtra("category", category);
                    intent.putExtra("subCategory", subCategory);
                    intent.putExtra("head", head);
                    intent.putExtra("items", (ArrayList<String>) listItemsSelected);
                    intent.putExtra("itemsPrice", (ArrayList<Long>) listItemsPrice);
                    intent.putExtra("itemsHead", (ArrayList<String>) listItemsHead);
                    intent.putExtra("number", n);
                    intent.putExtra("banner", bannerUrl);
                    for (int i = 0; i < n; i++) {
                        intent.putExtra("item" + i, listItemsSelected.get(i));
                    }
                    startActivity(intent);
                }
            }
        });

        imageButtonExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonExpand.setVisibility(View.GONE);
                imageButtonCollapse.setVisibility(View.VISIBLE);
                textViewDetail.setMaxLines(Integer.MAX_VALUE);
                textViewDetailLine1.setVisibility(View.VISIBLE);
                textViewDetailLine2.setVisibility(View.VISIBLE);
                textViewDetailLine3.setVisibility(View.VISIBLE);
                textViewDetailLine4.setVisibility(View.VISIBLE);
                textViewDetailLine5.setVisibility(View.VISIBLE);
            }
        });

        imageButtonCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonExpand.setVisibility(View.VISIBLE);
                imageButtonCollapse.setVisibility(View.GONE);
                textViewDetail.setMaxLines(2);
                textViewDetailLine1.setVisibility(View.GONE);
                textViewDetailLine2.setVisibility(View.GONE);
                textViewDetailLine3.setVisibility(View.GONE);
                textViewDetailLine4.setVisibility(View.GONE);
                textViewDetailLine5.setVisibility(View.GONE);
            }
        });
    }
    private void getResult(final String state,final String city,final String category,final String subCategory){
        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot=task.getResult();
                textViewDetail.setText(snapshot.getString("Description"));
                textViewDetailLine1.setText(snapshot.getString("Description1"));
                textViewDetailLine2.setText(snapshot.getString("Description2"));
                textViewDetailLine3.setText(snapshot.getString("Description3"));
                textViewDetailLine4.setText(snapshot.getString("Description4"));
                textViewDetailLine5.setText(snapshot.getString("Description5"));
                firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d("xyz", String.valueOf(task.getResult().size()));
                            int count=0;
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                if(snapshot.getBoolean("visible")) {
                                    Log.d("xyz", snapshot.getId());
                                    ClassService item;
                                    item = snapshot.toObject(ClassService.class);
                                    item.checked = false;
                                    listItems.add(item);
                                }
                                adapter.notifyDataSetChanged();
                                count++;
                            }
                            if(count==0){
                                textViewNOResult.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            Log.w("xyz", "Error getting documents.", task.getException());
                        }
                    }
                });
            }
        });

    }
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String itemName = intent.getStringExtra("item");
            String head = intent.getStringExtra("head");
            long price = intent.getLongExtra("price",0);
            Log.d("xyzdis", String.valueOf(price));
            String checkItem= intent.getStringExtra("task");

            if(checkItem.equals("add")) {
                listItemsSelected.add(itemName);
                listItemsHead.add(head);
                listItemsPrice.add(price);
            }
            HashSet<String> listToSet = new HashSet<>(listItemsSelected);
            HashSet<Long> listToSet2 = new HashSet<>(listItemsPrice);
            HashSet<String> listToSet3 = new HashSet<>(listItemsHead);
            if(checkItem.equals("remove")) {
                listToSet.remove(itemName);
                listToSet2.remove(price);
                listToSet3.remove(head);
            }
            listItemsSelected.clear();
            listItemsHead.clear();
            listItemsPrice.clear();
            if(listToSet.size()!=0)
                for (String i : listToSet) {
                    listItemsSelected.add(i);
                    Collections.reverse(listItemsSelected);
                    adapterSelected.notifyDataSetChanged();
                }
            if(listToSet2.size()!=0)
                for (Long i : listToSet2) {
                    listItemsPrice.add(i);
                    Collections.reverse(listItemsPrice);
                    adapterSelected.notifyDataSetChanged();
                }
            if(listToSet3.size()!=0)
                for (String i : listToSet3) {
                    listItemsHead.add(i);
                    Collections.reverse(listItemsHead);
                    adapterSelected.notifyDataSetChanged();
                }
            adapterSelected.notifyDataSetChanged();
            n=listItemsSelected.size();
            Log.d("xyzsize",String.valueOf(n));
            Log.d("xyzItem", String.valueOf(listItemsSelected));
            if(n!=0){
                buttonSearch.setEnabled(true);
            }
        }
    };
}