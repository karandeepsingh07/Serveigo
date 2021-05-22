package com.service.serveigo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ClassCategory> listItems;
    FirebaseFirestore firebaseFirestore;
    CardView cardView;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    Button buttonBack;
    ArrayAdapter<String> arrayAdapter,arrayAdapterUid;
    TextView textViewLocation,textViewState;
    Fragment fragment=null;
    FragmentManager manager;
    FragmentTransaction fragmentTransaction;
    TextView textViewId,textViewIdState;
    String uidCity,uidState;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    //mTextMessage.setText(R.string.title_home);
                    //
                    /*Bundle bundle = new Bundle();
                    bundle.putString("city", "Bhilai");*/
                    fragment = new HomeFragment();
                    /*fragment.setArguments(bundle);*/
                    manager = getSupportFragmentManager();
                    fragmentTransaction = manager.beginTransaction();
                   // fragmentTransaction.setCustomAnimations(R.anim.anim_lr,R.anim.anim_rl);
                    fragmentTransaction.replace(R.id.fragmentBasic, fragment);
                    fragmentTransaction.commit();
                    /**/
                    //fragmentTransaction.addToBackStack(null);
                    //fragmentTransaction.commit();
                    return true;
                case R.id.action_booking:
                    // mTextMessage.setText(R.string.title_dashboard);
                    fragment = new BookingFragment();
                    manager = getSupportFragmentManager();
                    fragmentTransaction = manager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.anim_lr,R.anim.anim_rl);
                    fragmentTransaction.replace(R.id.fragmentBasic, fragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.action_support:
                    //mTextMessage.setText(R.string.title_WebsiteRB);
                    fragment = new SettingsFragment();
                    manager = getSupportFragmentManager();
                    fragmentTransaction = manager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentBasic, fragment);
                    fragmentTransaction.commit();
                    return true;

            }
            manager.popBackStack();
            /*FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=manager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentBasic,fragment);
            fragmentTransaction.commit();*/
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String state = sharedpreferences.getString("state", "x");
        final String city = sharedpreferences.getString("city","x");
        final String stateName = sharedpreferences.getString("state", "x");
        final String cityName = sharedpreferences.getString("city","x");

        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "subscribes";
                        if (!task.isSuccessful()) {
                            msg = "not";
                        }
                        Log.d("xyz", msg);
                        //Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        arrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapterUid = new ArrayAdapter<>(HomeActivity.this, android.R.layout.select_dialog_singlechoice);

        textViewLocation = toolbar.findViewById(R.id.textView_location);
        textViewId = toolbar.findViewById(R.id.textView_id);
        textViewIdState = toolbar.findViewById(R.id.textView_id_state);
        textViewState = toolbar.findViewById(R.id.textView_state);

        if(state.equals("x")){
            dialog();
        }else {

            textViewState.setText(stateName);
            textViewLocation.setText(cityName + ",");
        }
        textViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });
        textViewState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

      //  Intent intent=getIntent();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




    }


    public void dialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(HomeActivity.this);
        builderSingle.setCancelable(false);
        builderSingle.setTitle("Select One State:-");
        arrayAdapter.clear();
        arrayAdapterUid.clear();
        firebaseFirestore.collection("Area").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot: task.getResult()){
                        if(snapshot.getBoolean("visible")) {
                            Log.d("xyz", snapshot.get("Head").toString());
                            arrayAdapter.add(snapshot.get("Head").toString());
                            arrayAdapterUid.add(snapshot.get("uid").toString());
                        }
                        arrayAdapter.notifyDataSetChanged();
                        arrayAdapterUid.notifyDataSetChanged();
                    }
                }
            }
        });
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                textViewState.setText(strName);
                textViewIdState.setText(arrayAdapterUid.getItem(which));
                uidState=arrayAdapterUid.getItem(which).trim();
                dialogCity();
            }
        });
        builderSingle.show();
    }public void dialogCity(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(HomeActivity.this);
        builderSingle.setCancelable(false);
        builderSingle.setTitle("Select One City:-");
        arrayAdapter.clear();
        arrayAdapterUid.clear();
        Log.d("xyz",uidState);
        firebaseFirestore.collection("Area").document(uidState).collection("City").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot snapshot: task.getResult()){
                        if(snapshot.getBoolean("visible")) {
                            Log.d("xyz", snapshot.get("Head").toString());
                            arrayAdapter.add(snapshot.get("Head").toString());
                            arrayAdapterUid.add(snapshot.get("uid").toString());
                            uidCity = snapshot.get("uid").toString();
                        }
                        arrayAdapter.notifyDataSetChanged();
                        arrayAdapterUid.notifyDataSetChanged();
                    }
                }
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                textViewLocation.setText(strName+",");
                textViewId.setText(arrayAdapterUid.getItem(which));
                uidCity=arrayAdapterUid.getItem(which);

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("stateUid", uidState);
                editor.putString("cityUid", uidCity);
                editor.putString("state", textViewState.getText().toString());
                editor.putString("city", arrayAdapter.getItem(which));
                editor.apply();
                fragment = new HomeFragment();
                /*fragment.setArguments(bundle);*/
                manager = getSupportFragmentManager();
                fragmentTransaction = manager.beginTransaction();
                // fragmentTransaction.setCustomAnimations(R.anim.anim_lr,R.anim.anim_rl);
                fragmentTransaction.replace(R.id.fragmentBasic, fragment);
                fragmentTransaction.commit();
            }
        });
        builderSingle.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item1) {
            Intent intent1=new Intent(HomeActivity.this,NotificationActivity.class);
            startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public String getMyData() {
        return textViewLocation.getText().toString();
    }
}