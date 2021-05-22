package com.service.serveigo;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VendorListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassVendor> listItems;
    HashSet<String> listItemsSelected;
    FirebaseFirestore firebaseFirestore;
    StringBuilder services;
    ProgressBar progressBar;
    Button buttonBack;
    TextView textViewNoResult;
    ImageView imageViewBanner;
    ArrayList<String> serviceList;
    ArrayList<Long> priceList;
    ArrayList<String> headList;
    String snapshotMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);


        listItems= new ArrayList<>();
        serviceList= new ArrayList<>();
        priceList= new ArrayList<>();
        headList= new ArrayList<>();
        listItemsSelected= new HashSet<>();
        services= new StringBuilder();


        Intent intent=getIntent();
        String state= intent.getStringExtra("state").trim();
        String city= intent.getStringExtra("city").trim();
        String category= intent.getStringExtra("category").trim();
        String subCategory= intent.getStringExtra("subCategory").trim();
        String bannerUrl= intent.getStringExtra("banner");
        serviceList = (ArrayList<String>) intent.getSerializableExtra("items");
        priceList = (ArrayList<Long>) intent.getSerializableExtra("itemsPrice");
        headList = (ArrayList<String>) intent.getSerializableExtra("itemsHead");
        Log.d("xyzprice",priceList.toString());

        int n=intent.getIntExtra("number",0);
        for(int i=0;i<n;i++){
            listItemsSelected.add(intent.getStringExtra("item"+i));
            services.append(intent.getStringExtra("item"+i)).append(" ").append("\n");
        }
        //Toast.makeText(this, ""+listItemsSelected, Toast.LENGTH_SHORT).show();

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();
        imageViewBanner=findViewById(R.id.imageView_banner);
        progressBar= findViewById(R.id.progressBar);
        textViewNoResult=findViewById(R.id.textView_noResult);

        Picasso.get().load(bannerUrl).into(imageViewBanner);
        imageViewBanner.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);

        buttonBack=findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter=new AdapterVendorList((ArrayList<ClassVendor>) listItems,this, state, city, category, subCategory, services,serviceList,priceList,headList);

        recyclerView.setAdapter(adapter);

        /*for(int i=0;i<5;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
        getResult(state,city,category,subCategory);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getResult(final String state, final String city, final String category, final String subCategory){
        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).collection("Vendor").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final int[] count = {0};
                if(task.getResult().size()==0){
                    progressBar.setVisibility(View.GONE);
                    textViewNoResult.setVisibility(View.VISIBLE);
                }
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    Log.d("xyzsize", String.valueOf(task.getResult().size()));
                    for (final DocumentSnapshot snapshot : task.getResult()) {

                        firebaseFirestore.collection("Vendor").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                progressBar.setVisibility(View.VISIBLE);
                                    //hs.add(snapshot1.getId());
                                    Log.d("xyzVendor", task.getResult().getId());
                                    //snapshotMain=snapshot.getId();
                                    firebaseFirestore.collection("Vendor").document(snapshot.getId()).collection("Services").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            snapshotMain=snapshot.getId();
                                            Log.d("xyzVendor", snapshotMain);
                                            for (final DocumentSnapshot snapshot1 : task.getResult()) {
                                                hs.add(snapshot1.getId());
                                                Log.d("xyzServices", snapshot1.getId());
                                                progressBar.setVisibility(View.VISIBLE);
                                            }
                                            Log.d("xyz1", ""+hs);
                                            progressBar.setVisibility(View.VISIBLE);
                                            if(hs.containsAll(listItemsSelected) && hs.size()!=0){
                                                Log.d("xyz", "got");
                                                final ClassVendor classVendor;
                                                Log.d("xyz", snapshotMain);
                                                //Log.d("xyz1",snapshotMain.getString("name"));
                                                hs.clear();
                                                textViewNoResult.setVisibility(View.GONE);
                                                adapter.notifyDataSetChanged();
                                                firebaseFirestore.collection("Vendor").document(snapshotMain).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        final ClassVendor classVendor;
                                                        //classVendor.vendorImage=task.getResult().getString("vendorImage");
                                                        classVendor=task.getResult().toObject(ClassVendor.class);
                                                        listItems.add(classVendor);
                                                        count[0]++;
                                                        adapter.notifyDataSetChanged();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });/*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        }
                                    });*/
                                }
                        });
                        /*Log.d("xyz", snapshot.getId());
                        Log.d("xyz", snapshot.getString("Address"));
                        Log.d("xyz", snapshot.getString("Name"));*/
                    }
                    if(count[0]==0){
                        progressBar.setVisibility(View.GONE);
                        //textViewNoResult.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });

    }
}