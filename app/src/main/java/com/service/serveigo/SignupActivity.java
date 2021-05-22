package com.service.serveigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    Button buttonSignUp;
    EditText editTextName,editTextEmail,editTextAddress;
    private String mVerificationId;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    CircleImageView imageView;
    TextView textViewHint;
    boolean opened=false;
    String imageViewUrl="";
    Uri imageUri;
    String contact;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Intent intent=getIntent();
        contact=intent.getStringExtra("contact");
        auth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        progressBar=findViewById(R.id.progressBar);
        buttonSignUp=findViewById(R.id.button_signup);
        editTextName=findViewById(R.id.editText_name);
        editTextEmail=findViewById(R.id.editText_email);
        editTextAddress=findViewById(R.id.editText_address);
        textViewHint=findViewById(R.id.textViewProfile);
        imageView=findViewById(R.id.imageViewUser);

        /*buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                buttonSignUp.setEnabled(false);
                sendVerificationCode(editTextContact.getText().toString());
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formCheck(editTextName,editTextContact,editTextEmail,editTextOTP,editTextAddress)) {
                    if (editTextContact.getText().toString().length() != 10) {
                        Toast.makeText(SignupActivity.this, "Contact Number must be of 10 digits", Toast.LENGTH_SHORT).show();
                    } else if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".com")) {
                        editTextEmail.setError("Invalid Email address");
                        editTextEmail.requestFocus();
                        Toast.makeText(SignupActivity.this, "please, enter valid email address", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        buttonSignUp.setEnabled(false);
                        verifyVerificationCode(editTextOTP.getText().toString());
                    }
                }

            }
        });*/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(formCheck(editTextName,editTextEmail,editTextAddress)) {
                    if (!editTextEmail.getText().toString().contains("@") || !editTextEmail.getText().toString().contains(".com")) {
                        editTextEmail.setError("Invalid Email address");
                        editTextEmail.requestFocus();
                        Toast.makeText(SignupActivity.this, "please, enter valid email address", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        buttonSignUp.setEnabled(false);
                        uploadData();
                    }
                }
            }
        });
    }

    public void uploadData(){
        String filePathAndName = "User/" + contact;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(Uri.parse(imageViewUrl)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // image uploaded to firebase storage. get uri
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();
                imageViewUrl=downloadUri;

                if (uriTask.isSuccessful()) {
                    addDetail(editTextName.getText().toString(),editTextEmail.getText().toString(),editTextAddress.getText().toString(),contact,imageViewUrl);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addDetail(final String name, final String email, final String address, final String contact,final String imageViewUrl){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token_id = instanceIdResult.getToken();
                String current_id = auth.getCurrentUser().getUid();

                final HashMap<String, Object> UserHashMap = new HashMap<>();
                UserHashMap.put("altEmail", email);
                UserHashMap.put("name", name);
                UserHashMap.put("address",address);
                UserHashMap.put("contact", contact);
                UserHashMap.put("imageUrl",imageViewUrl);
                UserHashMap.put("newUser",true);
                UserHashMap.put("uid", current_id);
                UserHashMap.put("token_id", token_id);

                firebaseFirestore.collection("Users").document(current_id).set(UserHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this, "Details updated successfully.. ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Details not updated.. ", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        buttonSignUp.setEnabled(true);
                    }
                });
            }
        });
    }
    /*private void sendVerificationCode(String mobile) {
        Log.d("xyz","inside 1st method");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setEnabled(false);
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            Log.d("xyz","code1st"+phoneAuthCredential);
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextOTP.setText(code);
                //verifying the code
                progressBar.setVisibility(View.GONE);
                buttonSignUp.setEnabled(true);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            buttonSignUp.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            Log.d("xyz","inside oncodesent");
            //storing the verification id that is sent to the user
            mVerificationId = s;
            progressBar.setVisibility(View.GONE);
            buttonSignUp.setEnabled(true);
        }
    };*/


    /*private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        Log.d("xyz","code"+code);
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //String uid= auth.getUid();

                            //progressBar.setVisibility(View.GONE);
                            //verification successful we will start the profile activity
                        } else {
                            buttonSignUp.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }*/
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

    public boolean formCheck(EditText editTextName,EditText editTextEmail,EditText editTextAddress){
        if(imageViewUrl.isEmpty()){
            textViewHint.setVisibility(View.VISIBLE);
            return false;
        }
        if(TextUtils.isEmpty(editTextName.getText().toString())){
            editTextName.setText("");
            editTextName.setHint("Name");
            editTextName.setError("Field should not be empty");
            return false;
        }if(TextUtils.isEmpty(editTextEmail.getText().toString())){
            editTextEmail.setText("");
            editTextEmail.setHint("Email");
            editTextEmail.setError("Field should not be empty");
            return false;
        }if(TextUtils.isEmpty(editTextAddress.getText().toString())){
            editTextAddress.setText("");
            editTextAddress.setHint("Address");
            editTextAddress.setError("Field should not be empty");
            return false;
        }
        return true;
    }
}