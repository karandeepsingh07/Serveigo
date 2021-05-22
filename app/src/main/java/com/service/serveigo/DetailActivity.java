package com.service.serveigo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alespero.expandablecardview.ExpandableCardView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    private int mYear, mMonth, mDay, mHour, mMinute,day1,month1,year1;
    long tax,extraAmount=0;
    TextView textViewDate, textViewTime,textViewVendorName,textViewVendorType,textViewTotalTax,textViewTotalAmountTax,textViewGst,textViewGstAmount,textViewExtra,textViewExtraAmount;
    Button buttonBookDetail,buttonInstant,buttonNormal,buttonBack;
    ImageView imageViewVendor;
    EditText editTextComment,editTextAddress;
    ProgressBar progressBar,progressBar2;
    String userName,tokenId,contact;
    StringBuilder services=null,prices=null;
    boolean buttonCheck=true;
    LinearLayout linearLayout;
    ArrayList<String> serviceList;
    ArrayList<Long> priceList;
    ArrayList<String> headList;
    ArrayList<ClassService> listService;
    ArrayList<ClassCoupon> listCoupon;
    AdapterServiceFinal adapterServiceFinal;
    AdapterCouponList adapterCouponList;
    ExpandableCardView expandableCardView;
    RecyclerView recyclerView,recyclerViewCoupon;
    float totalBill;
    float totalBillAfterDiscount;
    boolean newUser;
    TextView textViewTotalAmountAfterDiscount,textViewTotalDiscount,textViewDiscount,textViewTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textViewToolbar=toolbar.findViewById(R.id.textView_location);
        textViewToolbar.setText("Booking Details");

        Intent intent=getIntent();
        final String state= intent.getStringExtra("state");
        final String city= intent.getStringExtra("city");
        final String category= intent.getStringExtra("category");
        final String subCategory= intent.getStringExtra("subCategory");
        final String vendorID= intent.getStringExtra("vendorID");
        final String vendorAddress= intent.getStringExtra("vendorAddress");
        final String vendorName= intent.getStringExtra("vendorName");
        //final String services= intent.getStringExtra("services");
        final String vendorImage=intent.getStringExtra("vendorImage");
        serviceList = (ArrayList<String>) intent.getSerializableExtra("serviceList");
        priceList = (ArrayList<Long>) intent.getSerializableExtra("priceList");
        headList = (ArrayList<String>) intent.getSerializableExtra("headList");

        Log.d("xyzprice",priceList.toString());

        listService= new ArrayList<>();
        listCoupon= new ArrayList<>();

        services=new StringBuilder();
        prices=new StringBuilder();
        for(int i=0;i<serviceList.size();i++){
            ClassService service=new ClassService();
            service.head=headList.get(i);
            service.price=priceList.get(i);
            totalBill+=service.price;
            totalBillAfterDiscount=totalBill;
            listService.add(service);
            services.append(service.getHead());
            prices.append(service.getPrice());
        }

        textViewDate= findViewById(R.id.textView_date);
        textViewTime= findViewById(R.id.textView_time);
        textViewVendorType=findViewById(R.id.textView_vendorType);
        textViewTotalDiscount=findViewById(R.id.textView_total_discount);
        textViewDiscount=findViewById(R.id.textView_discount);
        textViewTotalAmount=findViewById(R.id.textView_total_price);
        textViewTotalAmountAfterDiscount=findViewById(R.id.textView_total_price_discount);
        textViewTotalTax=findViewById(R.id.textView_final_title);
        textViewTotalAmountTax=findViewById(R.id.textView_final_price);
        textViewGst=findViewById(R.id.textView_gst);
        textViewGstAmount=findViewById(R.id.textView_gst_amount);
        textViewExtra=findViewById(R.id.textView_extra);
        textViewExtraAmount=findViewById(R.id.textView_extraAmount);
        imageViewVendor=findViewById(R.id.imageView_detail);
        editTextComment = findViewById(R.id.editText_comment);
        editTextAddress = findViewById(R.id.editText_address);
        buttonBookDetail= findViewById(R.id.button_book_detail);
        buttonInstant=findViewById(R.id.button_instantService);
        buttonNormal=findViewById(R.id.button_normalService);
        textViewVendorName= findViewById(R.id.textView_vendorName);
        progressBar= findViewById(R.id.progressBar);
        progressBar2= findViewById(R.id.progressBar2);
        linearLayout= findViewById(R.id.linearLayout);
        expandableCardView=findViewById(R.id.coupon_cardView);
        adapterServiceFinal=new AdapterServiceFinal(listService,this);
        adapterCouponList=new AdapterCouponList(listCoupon,this,totalBill);
        recyclerView=findViewById(R.id.recyclerViewService);
        expandableCardView=findViewById(R.id.coupon_cardView);
        recyclerViewCoupon= expandableCardView.findViewById(R.id.recyclerViewCoupon);

        textViewTotalAmount.setText(String.valueOf(totalBill));
        textViewTotalAmountAfterDiscount.setText(String.valueOf(totalBill));

        firebaseFirestore= FirebaseFirestore.getInstance();
        Log.d("xyz1",vendorID);
        buttonBack=toolbar.findViewById(R.id.button_back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonInstant.setPadding(60,60,60,60);
        buttonNormal.setPadding(10,0,10,0);
        buttonInstant.setBackgroundResource(R.drawable.round_button_orange);
        buttonNormal.setBackgroundResource(R.drawable.round_edittext_grey);
        buttonNormal.setTextColor(Color.BLACK);
        buttonInstant.setTextColor(Color.WHITE);
        textViewDate.setVisibility(View.GONE);
        textViewTime.setVisibility(View.GONE);
        buttonCheck=true;

        String[] nameList=vendorName.split(" ");
        String name=nameList[0];
        textViewVendorName.setText(name);
        textViewVendorType.setText(subCategory);
        Picasso.get().load(vendorImage).placeholder(R.drawable.plumbing)
                .error(R.drawable.plumbing).into(imageViewVendor);
        getResult(state,city,category,subCategory);

        final Calendar c = Calendar.getInstance();
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm a");
        final String localDate = date.format(c.getTime());
        final String localTime = time.format(c.getTime());
        textViewDate.setText(localDate);
        textViewTime.setText(localTime);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCoupon.setHasFixedSize(true);
        recyclerViewCoupon.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapterServiceFinal);
        adapterServiceFinal.notifyDataSetChanged();
        recyclerViewCoupon.setAdapter(adapterCouponList);
        adapterCouponList.notifyDataSetChanged();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("coupon-message"));

        buttonInstant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonInstant.setPadding(60,60,60,60);
                buttonNormal.setPadding(10,0,10,0);
                buttonInstant.setBackgroundResource(R.drawable.round_button_orange);
                buttonNormal.setBackgroundResource(R.drawable.round_edittext_grey);
                buttonInstant.setTextColor(Color.WHITE);
                buttonNormal.setTextColor(Color.BLACK);
                textViewDate.setVisibility(View.GONE);
                textViewTime.setVisibility(View.GONE);
                textViewDate.setText(localDate);
                textViewTime.setText(localTime);
                buttonCheck=true;
            }
        });
        buttonNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNormal.setPadding(60,60,60,60);
                buttonInstant.setPadding(10,0,10,0);
                buttonNormal.setBackgroundResource(R.drawable.round_button_orange);
                buttonInstant.setBackgroundResource(R.drawable.round_edittext_grey);
                buttonInstant.setTextColor(Color.BLACK);
                buttonNormal.setTextColor(Color.WHITE);
                textViewDate.setVisibility(View.VISIBLE);
                textViewTime.setVisibility(View.VISIBLE);
                textViewDate.setHint("Select Date");
                textViewTime.setHint("Select Time");
                buttonCheck=false;
            }
        });

        buttonBookDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detailCheck()) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonBookDetail.setEnabled(false);
                    Map<String, Object> job = new HashMap<>();
                    job.put("vendorID", vendorID);
                    job.put("vendorAddress", vendorAddress);
                    job.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (buttonCheck) {
                        job.put("date", textViewDate.getText().toString());
                        job.put("instant", true);
                    } else {
                        job.put("date", textViewDate.getText().toString());
                        job.put("instant", false);
                    }
                    job.put("amount",textViewTotalAmountTax.getText().toString());
                    job.put("payment",false);
                    job.put("time", textViewTime.getText().toString());
                    job.put("timeStamp", textViewDate.getText().toString()+" "+textViewTime.getText().toString());
                    job.put("services", services.toString());
                    job.put("prices", prices.toString());
                    job.put("userAddress", editTextAddress.getText().toString());
                    job.put("status", "Open");
                    job.put("workDone", false);
                    job.put("token_id",tokenId);
                    job.put("userName", userName);
                    job.put("contact", contact);
                    job.put("comments", editTextComment.getText().toString());
                    job.put("vendorName", textViewVendorName.getText().toString());
                    job.put("vendorType", subCategory);

                   // job.put("vendorNumber",);

                    firebaseFirestore.collection("Job").document("Open").collection("UID")
                            .add(job)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Log.d("xyz", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    String status = "Open";
                                    Map<String, Object> jobId = new HashMap<>();
                                    jobId.put("jobId", documentReference.getId());
                                    jobId.put("status", status);
                                    jobId.put("date", textViewDate.getText().toString());

                                    final Map<String,Object> notification=new HashMap<>();
                                    notification.put("from",vendorID);
                                    notification.put("message","Booking Done");
                                    //notification.put("notification_uid",);
                                    //.put("uid",);
                                    final Map<String,Object> notificationVendor=new HashMap<>();
                                    notificationVendor.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    notificationVendor.put("message","You got a new order by "+userName+" at "+editTextAddress.getText().toString());
                                    final Map<String,Object> notificationVendorOrder=new HashMap<>();
                                    notificationVendorOrder.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    notificationVendorOrder.put("clickAction","order");
                                    notificationVendorOrder.put("jobId",documentReference.getId());
                                    notificationVendorOrder.put("message","You got a new order click to view "+userName+" at "+editTextAddress.getText().toString());
                                    //notificationVendor.put("notification_uid",);
                                    //notificationVendor.put("uid",);
                                    String id = documentReference.getId();
                                    firebaseFirestore.collection("Vendor").document(vendorID).collection("Job").document(documentReference.getId()).set(jobId);
                                    firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Booking").document(documentReference.getId()).set(jobId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Notification").document(documentReference.getId()).set(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    firebaseFirestore.collection("Vendor").document(vendorID).collection("Notification").document().set(notificationVendor).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            firebaseFirestore.collection("Vendor").document(vendorID).collection("NotificationOrder").document().set(notificationVendorOrder);
                                                            if(newUser)
                                                                firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("newUser",false);
                                                            Intent intent1 = new Intent(DetailActivity.this, ThankYouActivity.class);
                                                            startActivity(intent1);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("xyz", "Error adding document", e);
                                }
                            });
                }
            }
        });
        showPromoCode();
    }

    private void getResult(final String state,final String city,final String category,final String subCategory){
        firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userName= document.getString("name");
                        tokenId=document.getString("token_id");
                        contact=document.getString("contact");
                        newUser=document.getBoolean("newUser");
                        editTextAddress.setText(document.getString("address"));
                        firebaseFirestore.collection("Admin").document("tax").collection("List").document("haqYQ0JXkuGLSxHPfQID").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                tax=task.getResult().getLong("tax");
                                String title=task.getResult().getString("taxTitle");
                                String extraTitle=task.getResult().getString("extraTitle");
                                Log.d("xyzExtra",extraTitle);
                                extraAmount=task.getResult().getLong("extraAmount");
                                double extraCharges=0;
                                if(extraAmount!=0){
                                    textViewExtra.setVisibility(View.VISIBLE);
                                    textViewExtraAmount.setVisibility(View.VISIBLE);
                                    Log.d("xyzExtra2",extraTitle);
                                    textViewExtra.setText(extraTitle);
                                    extraCharges=(totalBillAfterDiscount/100.0f)*extraAmount;
                                    textViewExtraAmount.setText("₹"+extraAmount);
                                }

                                double taxAmount=(totalBillAfterDiscount/100.0f)*tax;
                                double discountAmount=totalBillAfterDiscount+taxAmount+extraAmount;
                                textViewGst.setText(title);
                                textViewGstAmount.setText(""+tax+"%");
                                textViewTotalAmountTax.setText(String.format("%.2f", discountAmount));
                                textViewTotalAmountTax.setVisibility(View.VISIBLE);
                                textViewTotalTax.setVisibility(View.VISIBLE);
                                textViewGst.setVisibility(View.VISIBLE);
                                textViewGstAmount.setVisibility(View.VISIBLE);
                                progressBar2.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        progressBar2.setVisibility(View.GONE);
                        Log.d("xyz", "No such document");
                    }
                } else {
                    progressBar2.setVisibility(View.GONE);
                    Log.d("xyz", "get failed with ", task.getException());
                }
            }
        });
        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").document(category).collection("Subcategory").document(subCategory).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot=task.getResult();
                textViewVendorType.setText(snapshot.getString("Head"));

            }
        });
    }

    private void showPromoCode(){
        firebaseFirestore.collection("Coupons").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot: task.getResult()){
                        ClassCoupon classCoupon=snapshot.toObject(ClassCoupon.class);
                        listCoupon.add(classCoupon);
                        adapterCouponList.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    public void datePick(final View v){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        textViewDate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                        day1=dayOfMonth;
                        month1=monthOfYear+1;
                        Log.d("getCurrentDateTime", String.valueOf(day1));
                        Log.d("getCurrentDateTime", String.valueOf(month1));
                        timePick(v);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    public void timePick(View v){
        final Calendar c = Calendar.getInstance();
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                        String getCurrentDateTime = sdf.format(c.getTime());
                        String getCurrentDate = sdf2.format(c.getTime());
                        String minuteLocal= String.valueOf(minute);
                        String hourLocal= String.valueOf(hourOfDay);
                        String dayLocal= String.valueOf(day1);
                        Log.d("getCurrentDateTime", String.valueOf(day1));
                        String monthLocal= String.valueOf(month1);
                        if (minute < 10)
                            minuteLocal = "0" + minute;
                        if (hourOfDay < 10)
                            hourLocal = "0" + hourOfDay;
                        if ((mMonth+1) < 10)
                            monthLocal = "0" + month1;
                        if (mDay < 10)
                            dayLocal = "0" + day1;
                        String getMyTime=mYear+"/"+monthLocal+"/"+dayLocal+" "+hourLocal+":"+minuteLocal;
                        String getMyDate=mYear+"/"+monthLocal+"/"+dayLocal;
                        Log.d("getCurrentDateTime",getCurrentDateTime);
                        Log.d("getCurrentDateTime",getMyTime);

                        if(getCurrentDate.compareTo(getMyDate)<0) {
                            updateTime(hourOfDay,minute);
                        }else if (getCurrentDateTime.compareTo(getMyTime) < 0){
                            updateTime(hourOfDay,minute);
                        }
                        else
                        {
                            Toast.makeText(DetailActivity.this, "Select correct date and time", Toast.LENGTH_SHORT).show();
                            textViewDate.setText("Select Date");
                            textViewTime.setText("Select Time");
                        }
                        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date selDate= null;
                        try {
                            selDate = sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(System.currentTimeMillis() < selDate.getTime())
                            textViewDate.setText("Select Date");
                        else
                            Toast.makeText(DetailActivity.this, "Select date equal to or greater than today's date", Toast.LENGTH_SHORT).show();*/
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        textViewTime.setText(aTime);
    }
    public boolean detailCheck(){
        if(TextUtils.isEmpty(editTextAddress.getText().toString().trim())){
            editTextAddress.setError("Enter full address");
            editTextComment.setHint("Enter Address");
            return false;
        }/*if(TextUtils.isEmpty(editTextComment.getText().toString().trim())){
            editTextComment.setError("Describe the issue");
            editTextComment.setHint("Comment");
            return false;
        }*/if(textViewDate.getText().toString().equals("Select Date") && !buttonCheck){
            Toast.makeText(this, "Enter Date", Toast.LENGTH_SHORT).show();
            return false;
        }if(textViewTime.getText().toString().equals("Select Time") && !buttonCheck){
            Toast.makeText(this, "Enter Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            float discount=0;
            int discountValue=intent.getIntExtra("item",0);
            String type=intent.getStringExtra("type");
            String uid=intent.getStringExtra("uid");
            Log.d("xyzUid",""+discountValue);
            if(uid.equals("0jOgIRQfe4KjsipTa2Gd") && !newUser){
                Toast.makeText(context, "Offer applicable for new users only", Toast.LENGTH_SHORT).show();
            }else {
                if (type.equals("value")) {
                    discount = discountValue;
                    totalBillAfterDiscount = totalBill - discount;
                    double taxAmount = (totalBillAfterDiscount / 100.0f) * tax;
                    double extraCharges = (totalBillAfterDiscount / 100.0f) * extraAmount;
                    double discountAmount = totalBillAfterDiscount + taxAmount + extraAmount;
                    textViewTotalAmountTax.setText(String.format("%.2f", discountAmount));
                    textViewDiscount.setText("Flat ₹"+discountValue+" Discount");
                    //totalBill= totalBillAfterDiscount/discountValue;
                } else {
                    if (discountValue != 0) {
                        discount = ((totalBill / 100.0f) * discountValue);
                    }
                    totalBillAfterDiscount = totalBill - discount;
                    double taxAmount = (totalBillAfterDiscount / 100.0f) * tax;
                    double extraCharges = (totalBillAfterDiscount / 100.0f) * extraAmount;
                    double discountAmount = totalBillAfterDiscount + taxAmount + extraAmount;
                    textViewTotalAmountTax.setText(String.format("%.2f", discountAmount));
                    textViewDiscount.setText(discountValue + "% Discount");
                    //totalBill= totalBillAfterDiscount/discountValue;
                }
                Log.d("xyzCoupon", String.valueOf(discountValue));

                textViewTotalAmountAfterDiscount.setText("₹" + String.format("%.2f", totalBillAfterDiscount));
                textViewDiscount.setVisibility(View.VISIBLE);
                textViewTotalAmountAfterDiscount.setVisibility(View.VISIBLE);
                textViewTotalDiscount.setVisibility(View.VISIBLE);
            }
        }
    };
}