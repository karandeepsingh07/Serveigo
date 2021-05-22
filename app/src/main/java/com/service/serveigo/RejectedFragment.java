package com.service.serveigo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RejectedFragment extends Fragment {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    RecyclerView.Adapter adapter;
    List<ClassBooking> listItems;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_rejected, container, false);

        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        progressBar= view.findViewById(R.id.progressBar);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listItems= new ArrayList<>();

        adapter=new AdapterBookingList((ArrayList<ClassBooking>) listItems, getContext());

        recyclerView.setAdapter(adapter);

        getResult();
        return view;
    }

    private void getResult(){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Booking").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d("xyz", ""+task.getResult());
                    final int[] count = {0};
                    if(task.getResult().size()==0){
                        progressBar.setVisibility(View.GONE);
                    }
                    int size=task.getResult().size();
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        String status= snapshot.getString("status");
                        firebaseFirestore.collection("Job").document(status).collection("UID").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task!=null) {
                                    final ClassBooking classBooking = task.getResult().toObject(ClassBooking.class);
                                    if(classBooking!=null && classBooking.getStatus().equals("Rejected")) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        Log.d("xyz", classBooking.getVendorName());
                                        classBooking.setJobId(task.getResult().getId());
                                        firebaseFirestore.collection("Vendor").document(classBooking.getVendorID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                classBooking.vendorImage=task.getResult().getString("vendorImage");
                                                String state= task.getResult().getString("state");
                                                String city= task.getResult().getString("city");
                                                String category= task.getResult().getString("category");
                                                String subCategory= task.getResult().getString("subCategory");
                                                firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        String vendorType=task.getResult().getString("Head");
                                                        if(vendorType!=null) {
                                                            classBooking.setVendorType(vendorType);
                                                            Log.d("xyzVT",classBooking.vendorType);
                                                        }
                                                        listItems.add(classBooking);
                                                        Collections.sort(listItems,new SortbyTimestamp());
                                                        adapter.notifyDataSetChanged();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        count[0]++;
                    }
                    count[0]++;
                    if(count[0]>size) {
                        //   textViewNoResult.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
}