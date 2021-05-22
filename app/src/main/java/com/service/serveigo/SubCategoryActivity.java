package com.service.serveigo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubCategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassSubCategory> listItems;
    FirebaseFirestore firebaseFirestore;
    TextView textViewCategory,textViewNoResult;
    ProgressBar progressBar;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Our Services");

        Intent intent=getIntent();
        String state= intent.getStringExtra("state").trim();
        String city= intent.getStringExtra("city").trim();
        String category= intent.getStringExtra("category");
        Log.d("xyz",category);
        Log.d("xyz",state);
        Log.d("xyz",city);

        textViewCategory=findViewById(R.id.textView_serviceType);
        progressBar= findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore= FirebaseFirestore.getInstance();

        /*textViewCategory.setText(category);*/
        textViewNoResult=findViewById(R.id.textView_noResult);
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listItems= new ArrayList<>();

        adapter=new AdapterSubCategoryList((ArrayList<ClassSubCategory>) listItems,this,state,city,category);

        recyclerView.setAdapter(adapter);

        /*for(int i=0;i<5;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
        getResult(state,city,category);

    }

    private void getResult(final String state,final String city,final String category){
        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").orderBy("Index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    int count=0;
                    Log.d("xyz", String.valueOf(task.getResult().size()));
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        ClassSubCategory classCategory;
                        Log.d("xyz", snapshot.getId());
                        classCategory=snapshot.toObject(ClassSubCategory.class);
                        if(classCategory.visible) {
                            listItems.add(classCategory);
                        }
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        count++;
                    }
                    if(count==0) {
                        textViewNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }

                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
}