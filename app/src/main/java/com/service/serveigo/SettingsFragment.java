package com.service.serveigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    LinearLayout linearLayoutAbout,linearLayoutPrivacyPolicy,linearLayoutSignOut,linearLayoutSupport,linearLayoutAll;
    CircleImageView imageViewProfile;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    TextView textViewUserName;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_settings, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        imageViewProfile=view.findViewById(R.id.imageView_profile);
        linearLayoutAbout=view.findViewById(R.id.linearLayout_aboutUs);
        linearLayoutPrivacyPolicy=view.findViewById(R.id.linearLayout_privacyPolicy);
        linearLayoutSignOut=view.findViewById(R.id.linearLayout_signOut);
        linearLayoutSupport=view.findViewById(R.id.linearLayout_support);
        linearLayoutAll=view.findViewById(R.id.linearLayout_all);
        textViewUserName=view.findViewById(R.id.textView_username);
        progressBar=view.findViewById(R.id.progressBar);

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                textViewUserName.setText(task.getResult().getString("name"));
                String url=task.getResult().getString("imageUrl");
                Log.d("xyz",url);
                Picasso.get().load(url).placeholder(R.drawable.plumbing)
                        .error(R.drawable.plumbing).into(imageViewProfile);
                progressBar.setVisibility(View.GONE);
                linearLayoutAll.setVisibility(View.VISIBLE);
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });linearLayoutSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),SupportActivity.class);
                startActivity(intent);
            }
        });linearLayoutAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Admin").document("About Us").collection("URL").document("About Page").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String url=task.getResult().getString("url");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
        });linearLayoutPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Admin").document("Privacy Policy").collection("List").document("Policies Page").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String url=task.getResult().getString("url");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
        });linearLayoutSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> tokenRemoveMap= new HashMap<>();
                tokenRemoveMap.put("token_id","");
                firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).update(tokenRemoveMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        Intent intent1=new Intent(getContext(),LoginActivity.class);
                        startActivity(intent1);
                        getActivity().finish();
                    }
                });
            }
        });
        return view;
    }
}