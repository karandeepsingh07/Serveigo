
package com.service.serveigo;

import android.os.Bundle;
import android.util.Log;

import com.alespero.expandablecardview.ExpandableCardView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentActivity extends AppCompatActivity {
    double total,discount,total_after_discount;
    long percentage;
    String newUser;
    FirebaseFirestore firebaseFirestore;
    ExpandableCardView expandableCardView;
    RecyclerView recyclerView;
    List<ClassCoupon> couponList;
    AdapterCouponList adapterCouponList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        expandableCardView=findViewById(R.id.profile);
        recyclerView= expandableCardView.findViewById(R.id.recyclerViewCoupon);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseFirestore=FirebaseFirestore.getInstance();

        couponList=new ArrayList<>();
        adapterCouponList=new AdapterCouponList((ArrayList<ClassCoupon>) couponList,this,0);
        recyclerView.setAdapter(adapterCouponList);

     /*   if(newUser.equals("true")){
            discount=0;
        }*/

     /*for(int i=0;i<5;i++){
         ClassCoupon classCoupon=new ClassCoupon();
         classCoupon.Head="HEad";
         classCoupon.percentage=20;
         couponList.add(classCoupon);
         adapterCouponList.notifyDataSetChanged();
         Log.d("xyz",couponList.get(0).Head);
     }*/
        getResult();
    }

    private void getResult(){
        firebaseFirestore.collection("Coupons").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d("xyz",task.toString());
                if(task.isSuccessful()) {
                    for(DocumentSnapshot snapshot:task.getResult()){
//                        Log.d("xyz",snapshot.get("percentage").toString());
                        ClassCoupon classCoupon=snapshot.toObject(ClassCoupon.class);
                        Log.d("xyz", String.valueOf(classCoupon.getPercentage()));
                        percentage=classCoupon.getPercentage();
                        couponList.add(classCoupon);
                        adapterCouponList.notifyDataSetChanged();
                    }
                  //  calculate();
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void calculate(){
        total=109;
        if(percentage!=0) {
            discount = ((total / 100.0f) * percentage);
        }
        total_after_discount = total-discount;
        Log.d("xyz", String.valueOf(total_after_discount));
    }
}