package com.service.serveigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements  ItemListDialogFragment.ItemClickListener{

    FirebaseFirestore firebaseFirestore;
    CircleImageView imageView;
    EditText editTextDummy,editTextAddress,editTextName,editTextEmail,editTextContact;
    Button buttonSubmit;
    String name,email,address,contact;
    boolean opened=false;
    Spinner spinnerState,spinnerCity;
    String state,city;
    ArrayList<String> arrayListState,arrayListCity;
    ArrayList<String> arrayListStateUid,arrayListCityUid;
    LinearLayout linearLayout;
    String imageViewUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    String user;
    ProgressBar progressBar,progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseFirestore= FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getUid();
        editTextName=findViewById(R.id.editTextUserName);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextAddress=findViewById(R.id.editTextAddress);
        editTextContact=findViewById(R.id.editTextContact);
        progressBar=findViewById(R.id.progressBar);
        progressBar2=findViewById(R.id.progressBar2);
        imageView=findViewById(R.id.imageViewUser);
        buttonSubmit=findViewById(R.id.buttonSubmitDetails);
        linearLayout=findViewById(R.id.linearLayout);
      /*  spinnerState=findViewById(R.id.spinnerState);
        spinnerCity=findViewById(R.id.spinnerCity);*/
        arrayListStateUid=new ArrayList<>();
        arrayListState=new ArrayList<>();
        arrayListCityUid=new ArrayList<>();
        arrayListCity=new ArrayList<>();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opened=true;
                openFileChooser();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                buttonSubmit.setEnabled(false);
                name = editTextName.getText().toString();
                email = editTextEmail.getText().toString();
                address = editTextAddress.getText().toString();
                contact = editTextContact.getText().toString();
                if(opened) {
                    String filePathAndName = "User/" + contact;
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                    ref.putFile(Uri.parse(imageViewUrl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {

                                firebaseFirestore.collection("Users").document(user).update("altEmail", email,"name", name,"address",address,"imageUrl",downloadUri,"contact",contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(ProfileActivity.this, "Details updated successfully.. ", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        buttonSubmit.setEnabled(true);
                                        //Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileActivity.this, "Details not updated.. ", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                buttonSubmit.setEnabled(true);
                                            }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    firebaseFirestore.collection("Users").document(user).update("altEmail", email,"name", name,"address",address,"contact",contact).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, "Details updated successfully.. ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);
                            //Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            //startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Details not updated.. ", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);
                        }
                    });
                }
            }
        });
        getResult();
    }

    private void getResult(){
        firebaseFirestore.collection("Users").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    editTextName.setText(task.getResult().getString("name"));
                    editTextEmail.setText(task.getResult().getString("altEmail"));
                    editTextContact.setText(task.getResult().getString("contact"));
                    editTextAddress.setText(task.getResult().getString("address"));
                    state=task.getResult().getString("state");
                    city=task.getResult().getString("city");
                    Picasso.get().load(task.getResult().getString("imageUrl")).placeholder(R.drawable.plumbing)
                            .error(R.drawable.plumbing).into(imageView);
                    progressBar2.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    buttonSubmit.setVisibility(View.VISIBLE);
                    //getResultState();
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getResultState(){
        arrayListStateUid.clear();
        arrayListState.clear();
        arrayListStateUid.add("Select State");
        arrayListState.add("Select State");

        firebaseFirestore.collection("Area").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    int count=1;
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListStateUid.add(snapshot.getId());
                        arrayListState.add(snapshot.get("Head").toString());
                        if(snapshot.getId().equals(state))
                            spinnerState.setSelection(count);
                        count++;
                    }
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(ProfileActivity.this,android.R.layout.simple_spinner_item,arrayListState);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerState.setAdapter(stateAdapter);
                    getResultCity(state);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }
    private void getResultCity(final String state){
        arrayListCityUid.clear();
        arrayListCity.clear();
        arrayListCityUid.add("Select City");
        arrayListCity.add("Select City");
        firebaseFirestore.collection("Area").document(state).collection("City").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    final HashSet<String> hs=new HashSet<>();
                    int count=1;
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        Log.d("xyz", snapshot.getId());
                        arrayListCityUid.add(snapshot.getId());
                        arrayListCity.add(snapshot.get("Head").toString());
                        if(snapshot.getId().equals(city))
                            spinnerCity.setSelection(count);
                        count++;
                    }
                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(ProfileActivity.this,android.R.layout.simple_spinner_item,arrayListCity);
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(cityAdapter);
                    progressBar2.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    buttonSubmit.setVisibility(View.VISIBLE);
                }
                else {
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void editLayout(View view){
        final ItemListDialogFragment bottomSheetDialogFragment = new ItemListDialogFragment();
        editTextDummy=findViewById(view.getId());
        Bundle bundle = new Bundle();
        bundle.putString("data",editTextDummy.getText().toString());
        bottomSheetDialogFragment.setArguments(bundle);
        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imageUri = data.getData();
            imageViewUrl=imageUri.toString();

            Picasso.get().load(imageUri).into(imageView);

        }
    }
    @Override
    public void onItemClick(String item) {
        editTextDummy.setText(item);
    }
}