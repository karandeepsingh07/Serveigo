package com.service.serveigo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    ArrayList<String> listItemsSearch,listItemsSearchUid;
    ArrayList<String> categoryList;
    ArrayList<String> imageList;
    ArrayList<String> bannerList;
    ArrayAdapter<String> itemsAdapter;
    FirebaseFirestore firebaseFirestore;
    ListView listView;
    ProgressBar progressBar;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Intent intent=getIntent();
        final String state= intent.getStringExtra("state");
        final String city= intent.getStringExtra("city");
        searchView=findViewById(R.id.searchView);

        firebaseFirestore=FirebaseFirestore.getInstance();

        listView=findViewById(R.id.listView);
        progressBar=findViewById(R.id.progressBar);
        listItemsSearch= new ArrayList<>();
        listItemsSearchUid= new ArrayList<>();
        categoryList= new ArrayList<>();
        imageList= new ArrayList<>();
        bannerList= new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(SearchActivity.this, android.R.layout.simple_list_item_1, listItemsSearch);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s=itemsAdapter.getItem(i);
                String category = categoryList.get(i);
                String banner= bannerList.get(i);
                imageUrl=imageList.get(i);
                Intent intent1=new Intent(SearchActivity.this,ServiceActivity.class);
                intent1.putExtra("state",state);
                intent1.putExtra("city",city);
                intent1.putExtra("category",category);
                intent1.putExtra("imageUrl",imageUrl);
                intent1.putExtra("banner",banner);
                intent1.putExtra("head",listItemsSearch.get(i));
                intent1.putExtra("subCategory",listItemsSearchUid.get(i));
                startActivity(intent1);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(listItemsSearch.contains(s)){
                    itemsAdapter.getFilter().filter(s);
                }else{
                    Toast.makeText(SearchActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                itemsAdapter.getFilter().filter(s);
                return false;
            }
        });

        getResult(state,city);
    }
    private void getResult(final String state,final String city){

        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    int count = 0;
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        itemsAdapter.notifyDataSetChanged();
                        if(snapshot.getBoolean("visible")!=null && snapshot.getBoolean("visible"))
                        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(snapshot.getId()).collection("Subcategory").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (final DocumentSnapshot snapshot1 : task.getResult()) {
                                    if(snapshot1.getBoolean("visible")!=null && snapshot1.getBoolean("visible")) {
                                        listItemsSearch.add(snapshot1.getString("Head"));
                                        listItemsSearchUid.add(snapshot1.getId());
                                        categoryList.add(snapshot.getId());
                                        bannerList.add(snapshot1.getString("Banner"));
                                        if (snapshot1.getString("ImageUrl") != null)
                                            //Log.d("xyz2",snapshot1.getString("ImageUrl"));
                                            imageList.add(snapshot1.getString("ImageUrl"));
                                        else
                                            imageList.add("x");
                                    }
                                    itemsAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    Log.d("xyz1", snapshot1.getId());
                                }
                            }
                        });
                        count++;
                        //progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
}