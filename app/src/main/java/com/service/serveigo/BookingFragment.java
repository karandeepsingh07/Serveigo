package com.service.serveigo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class BookingFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassBooking> listItems;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    private TabAdapter adaptertab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FragmentActivity myContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_booking,
                container, false);

        View view=inflater.inflate(R.layout.fragment_booking, container, false);


        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        adaptertab = new TabAdapter(fragManager,1);
        adaptertab.addFragment(new OpenFragment(), "Open");
        adaptertab.addFragment(new AcceptedFragment(), "Accepted");
        adaptertab.addFragment(new CompletedFragment(), "Completed");
        adaptertab.addFragment(new RejectedFragment(), "Rejected");
        viewPager.setAdapter(adaptertab);
        tabLayout.setupWithViewPager(viewPager);


        final String state="Chhatisgarh";
        final String city= "Bhilai";

        progressBar= view.findViewById(R.id.progressBar);
        recyclerView=view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();



        listItems= new ArrayList<>();

        adapter=new AdapterBookingList((ArrayList<ClassBooking>) listItems,getContext());

        recyclerView.setAdapter(adapter);

       /* for(int i=0;i<5;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
     //   getResult();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        myContext=(FragmentActivity) context;
        super.onAttach(context);
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
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        String status= snapshot.getString("status");
                        firebaseFirestore.collection("Job").document(status).collection("UID").document(snapshot.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task!=null) {
                                    final ClassBooking classBooking = task.getResult().toObject(ClassBooking.class);
                                    if(classBooking!=null) {
                                        Log.d("xyzCB", classBooking.vendorType);
                                        classBooking.setJobId(task.getResult().getId());
                                        firebaseFirestore.collection("Vendor").document(classBooking.getVendorID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                                                        adapter.notifyDataSetChanged();
                                                        progressBar.setVisibility(View.GONE);
                                                        count[0]++;
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        if(count[0]==0) {
                            //   textViewNoResult.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
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