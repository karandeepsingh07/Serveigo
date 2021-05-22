package com.service.serveigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SupportFragment extends Fragment {

    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    String website,instagram,twitter,facebook,email;
    TextView textViewEmail,textViewWebsite;
    ImageView imageViewInstagram,imageViewTwitter,imageViewFacebook;
    RelativeLayout relativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_support, container, false);

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressBar=view.findViewById(R.id.progressBar);
        textViewEmail=view.findViewById(R.id.textView_email);
        textViewWebsite=view.findViewById(R.id.textView_website);
        imageViewFacebook=view.findViewById(R.id.imageView_facebook);
        imageViewInstagram=view.findViewById(R.id.imageView_instagram);
        imageViewTwitter=view.findViewById(R.id.imageView_twitter);
        relativeLayout=view.findViewById(R.id.relativeLayout);


        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(browserIntent);
            }
        });
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mailClient = new Intent(Intent.ACTION_VIEW);
                mailClient.setClassName("com.google.android.gm", "com.google.android.gm.ConversationListActivity");
                mailClient.setData(Uri.parse(email));
                startActivity(mailClient);
            }
        });
        imageViewTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(twitter)));
            }
        });
        imageViewInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(instagram)));
            }
        });
        imageViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebook)));
            }
        });

        getResult();

        return view;
    }
    private void getResult() {
        progressBar.setVisibility(View.VISIBLE);
        relativeLayout.setVisibility(View.GONE);
        firebaseFirestore.collection("Admin").document("Contact").collection("List").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    if (snapshot.getId().equals("g1MjzoEmQ1mvS5KO80Lk")) {
                        email = snapshot.get("email").toString();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textViewEmail.setText(Html.fromHtml(email, Html.FROM_HTML_MODE_COMPACT));
                        } else{
                            textViewEmail.setText(email);
                        }
                    } else if (snapshot.getId().equals("zmbVpw3sVTNlHczsT7uc")) {
                        website = snapshot.get("website").toString();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textViewWebsite.setText(Html.fromHtml(website, Html.FROM_HTML_MODE_COMPACT));
                        } else{
                            textViewWebsite.setText(website);
                        }
                    } else if (snapshot.getId().equals("UviH8qu39FIuomGjyJlN")) {
                        instagram = snapshot.get("instagram").toString();
                    } else if (snapshot.getId().equals("e87XcwUrV526HnE7EmQ1")) {
                        twitter = snapshot.get("twitter").toString();
                    } else if (snapshot.getId().equals("Jc3NgoHSn8eYyeFIFrc1")) {
                        facebook = snapshot.get("facebook").toString();
                    }
                    progressBar.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}