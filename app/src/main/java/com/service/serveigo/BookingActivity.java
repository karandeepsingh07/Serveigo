package com.service.serveigo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BookingActivity extends AppCompatActivity implements PaymentResultListener {

    FirebaseFirestore firebaseFirestore;
    int REQUEST_PHONE_CALL=1;
    FirebaseUser firebaseUser;
    Button buttonAccept,buttonBack;
    ProgressBar progressBar,progressBar2;
    TextView textViewOrder,textViewPayment,textViewJob,textViewComment,textViewVendorName,textViewVendorType;
    String state,city,category,subCategory,vendorID,jobID,vendorImage,amount;
    LinearLayout linearLayoutDummy,linearLayout;
    ImageView imageViewDetail;
    String pendingPayment,contact,email;
    TextView textViewAmount,textViewDate,textViewAddress,textViewTime,textViewVendorAddress;
    RadioGroup radioGroup;
    boolean UPI=true,visible;
   // final int UPI_PAYMENT = 0;
    private static final String TAG = BookingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Booking Detail");

        Intent intent=getIntent();
        jobID=intent.getStringExtra("jobId");
        vendorImage=intent.getStringExtra("vendorImage");

        textViewVendorName=findViewById(R.id.textView_vendorName);
        textViewVendorType=findViewById(R.id.textView_vendorType);
        textViewComment=findViewById(R.id.textView_comment);
        textViewJob=findViewById(R.id.textView_job);
        textViewPayment=findViewById(R.id.textView_payment);
        textViewOrder=findViewById(R.id.textView_order);
        textViewAmount=findViewById(R.id.textView_amount);
        textViewDate=findViewById(R.id.textView_date);
        textViewAddress=findViewById(R.id.textView_address);
        textViewVendorAddress=findViewById(R.id.textView_vendorAddress);
        textViewTime=findViewById(R.id.textView_time);
        buttonAccept=findViewById(R.id.button_accept);
        linearLayoutDummy=findViewById(R.id.linearLayout_dummy);
        linearLayout=findViewById(R.id.linearLayout);
        progressBar=findViewById(R.id.progressBar);
        progressBar2=findViewById(R.id.progressBar2);
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        buttonBack=toolbar.findViewById(R.id.button_back);
        radioGroup=findViewById(R.id.radioGroup);
        imageViewDetail=findViewById(R.id.imageView_detail);

        Picasso.get().load(vendorImage).placeholder(R.drawable.plumbing)
                .error(R.drawable.plumbing).into(imageViewDetail);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /*buttonSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(BookingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BookingActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseFirestore.collection("Admin").document("Contact").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String number= task.getResult().get("number").toString();
                            //Picasso.get().load(url).into(imageViewQR);
                            progressBar.setVisibility(View.GONE);
                            if (ContextCompat.checkSelfPermission(BookingActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(BookingActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                            }
                            else
                            {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });*/

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                //buttonAccept.setVisibility(View.GONE);
                final String amount = textViewAmount.getText().toString();
                final String note=jobID;
                if(UPI) {
                    firebaseFirestore.collection("Admin").document("Payment").collection("List").document("4QEqdy0LhALrEE9rLhIk").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String upiId = task.getResult().getString("upiId");
                                String name = task.getResult().getString("name");
                                progressBar2.setVisibility(View.GONE);
                                if(isConnectionAvailable(BookingActivity.this)){
                                    startPayment();
                                }
                                //payUsingUpi(amount, upiId, name, note);
                            }
                        }
                    });
                }else{
                    DocumentReference fromPath= firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID);
                    DocumentReference toPath= firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID);
                    moveFirestoreDocument(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
                }

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.radioButtonUpi){
                    UPI=true;
                }else{
                    UPI=false;
                }
            }
        });
       /* for(int i=0;i<5;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
       getResult();
    }

    private void getResult(){
        firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Booking").document(jobID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                            final String status= task.getResult().getString("status");
                            if(status.equals("DuePayment")){
                                textViewOrder.setText("Accepted");
                                textViewOrder.setTextColor(Color.BLACK);
                                textViewOrder.setBackgroundColor(Color.GREEN);
                                textViewPayment.setText("Pending");
                                textViewPayment.setTextColor(Color.WHITE);
                                textViewPayment.setBackgroundColor(Color.RED);
                                textViewJob.setText("Processing");
                                textViewJob.setTextColor(Color.WHITE);
                                textViewJob.setBackgroundColor(Color.GRAY);
                            }
                            else if(status.equals("Rejected")){
                                textViewOrder.setText("Rejected");
                                textViewOrder.setBackgroundColor(Color.RED);
                            }
                            else if(status.equals("Closed")){
                                textViewOrder.setText("Accepted");
                                textViewOrder.setTextColor(Color.BLACK);
                                textViewOrder.setBackgroundColor(Color.GREEN);
                                textViewPayment.setText("Done");
                                textViewPayment.setBackgroundColor(Color.GREEN);
                                textViewJob.setText("Done");
                                textViewJob.setBackgroundColor(Color.GREEN);
                            }
                            firebaseFirestore.collection("Job").document(status).collection("UID").document(task.getResult().getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    ClassBooking classBooking= task.getResult().toObject(ClassBooking.class);
                                    vendorID=classBooking.getVendorID();
                                    String[] nameList=classBooking.getVendorName().split(" ");
                                    String name=nameList[0];
                                    textViewVendorName.setText(name);
                                    //textViewVendorType.setText(classBooking.getVendorType());
                                    textViewComment.setText(classBooking.getComments());
                                    amount=task.getResult().getString("amount");
                                    //amount=String.valueOf(Float.parseFloat(amount)+Float.parseFloat(task.getResult().getString("additionalCharge")));

                                    if(task.getResult().getBoolean("workDone") && status.equals("DuePayment")) {
                                        radioGroup.setVisibility(View.VISIBLE);
                                        buttonAccept.setVisibility(View.VISIBLE);
                                    }
                                    textViewAmount.setText(amount);
                                    textViewDate.setText(task.getResult().getString("date"));
                                    textViewTime.setText(task.getResult().getString("time"));
                                    textViewAddress.setText(task.getResult().getString("userAddress"));
                                    textViewVendorAddress.setText(task.getResult().getString("vendorAddress"));
                                    Log.d("xyz", task.getResult().toString());
                                    firebaseFirestore.collection("Vendor").document(vendorID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            state= task.getResult().getString("state");
                                            city=task.getResult().getString("city");
                                            category=task.getResult().getString("category");
                                            subCategory=task.getResult().getString("subCategory");
                                            pendingPayment=task.getResult().getString("paymentPending");
                                            visible=task.getResult().getBoolean("visible");
                                            linearLayoutDummy.setVisibility(View.VISIBLE);
                                            if(status.equals("Accepted")){
                                                buttonAccept.setVisibility(View.VISIBLE);
                                            }
                                            firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot snapshot=task.getResult();
                                                    textViewVendorType.setText(snapshot.getString("Head"));
                                                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            email=task.getResult().getString("altEmail");
                                                            contact=task.getResult().getString("contact");
                                                            progressBar.setVisibility(View.GONE);
                                                            linearLayout.setVisibility(View.VISIBLE);
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    else {
                        Log.w("xyz", "Error getting documents.", task.getException());
                    }
            }
        });
    }

    public void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath,final String state,final String city,final String category,final String subCategory,final String vendorID,final String jobID) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("xyz", "DocumentSnapshot successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("xyz", "DocumentSnapshot successfully deleted!");
                                                        if(UPI) {
                                                            firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID).update("status", "Closed", "uid", jobID, "payment", true,"paymentMode","UPI").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Booking").document(jobID).update("status", "Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").document(jobID).update("status", "Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    final Map<String,Object> notification=new HashMap<>();
                                                                                    notification.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                    notification.put("message","Thank you your payment have ben received");
                                                                                    final Map<String,Object> notificationVendor=new HashMap<>();
                                                                                    notificationVendor.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                    notificationVendor.put("message","Your job has been completed.");
                                                                                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Notification").document().set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            firebaseFirestore.collection("Vendor").document(vendorID).collection("Notification").document().set(notificationVendor);
                                                                                            Log.d("xyz", "Done Update!");
                                                                                            Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
                                                                                            Toast.makeText(BookingActivity.this, "Payment Done", Toast.LENGTH_SHORT).show();
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        else{
                                                            firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID).update("status", "Closed", "uid", jobID,"payment",false,"paymentMode","Cash").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Booking").document(jobID).update("status", "Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            pendingPayment=String.valueOf(Double.parseDouble(amount)+Double.parseDouble(pendingPayment));
                                                                            Log.d("xyzPP",pendingPayment);
                                                                            if(Double.parseDouble(amount)+Double.parseDouble(pendingPayment)>100){
                                                                                visible=false;
                                                                            }
                                                                            firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").document(jobID).update("status", "Closed").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    firebaseFirestore.collection("Vendor").document(vendorID).update("paymentPending",pendingPayment,"visible",visible);
                                                                                    final Map<String,Object> notification=new HashMap<>();
                                                                                    notification.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                    notification.put("message","Thank you your payment have ben received");
                                                                                    final Map<String,Object> notificationVendor=new HashMap<>();
                                                                                    notificationVendor.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                                    notificationVendor.put("message","Your job has been completed. Please submit the amount to Serveigo as soon as possible");
                                                                                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).collection("Notification").document().set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            firebaseFirestore.collection("Vendor").document(vendorID).collection("Notification").document().set(notificationVendor);
                                                                                            Log.d("xyz", "Done Update!");
                                                                                            Intent intent = new Intent(BookingActivity.this, HomeActivity.class);
                                                                                            Toast.makeText(BookingActivity.this, "Payment Done", Toast.LENGTH_SHORT).show();
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("xyz", "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("xyz", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("xyz", "No such document");
                    }
                } else {
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_P2sva0ZBNUW006");

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.ic_launcher_foreground);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Serveigo");
            options.put("description", "Test payment");
            //  options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            // options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", Float.parseFloat(amount)*100);//pass amount in currency subunits
            //options.put("prefill.email", email);
            //options.put("prefill.contact","+91"+contact);
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }
    /*void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // will always show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(BookingActivity.this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }

    }*/

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(BookingActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
        //firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID).update("amount",amount);
        DocumentReference fromPath= firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID);
        DocumentReference toPath= firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID);
        moveFirestoreDocument(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Transaction Failed "+s, Toast.LENGTH_SHORT).show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d("UPI", "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d("UPI", "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }*/

   /* private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(BookingActivity.this)) {
            String str = data.get(0);
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(BookingActivity.this, "Transaction successful.", Toast.LENGTH_SHORT).show();
                //firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID).update("amount",amount);
                DocumentReference fromPath= firebaseFirestore.collection("Job").document("DuePayment").collection("UID").document(jobID);
                DocumentReference toPath= firebaseFirestore.collection("Job").document("Closed").collection("UID").document(jobID);
                moveFirestoreDocument(fromPath,toPath,state,city,category,subCategory,vendorID,jobID);
                Log.d("UPI", "responseStr: " + approvalRefNo);
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(BookingActivity.this, "Payment cancelled by user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(BookingActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(BookingActivity.this, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }*/

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}