package com.service.serveigo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionPlan;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth=FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null) {
            // This method will be executed once the timer is over
            FirebaseFirestore.getInstance().collection("Users").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (auth.getCurrentUser() != null && snapshot.exists()) {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    } else{
                        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    }
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_in);
                    finish();
                }
            });
        }else {
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }
    }
}