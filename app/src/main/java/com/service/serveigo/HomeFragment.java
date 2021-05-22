package com.service.serveigo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView,recyclerViewSpecial;
    RecyclerView.Adapter adapter,adapterSpecial;
    List<ClassCategory> listItems,listItemsSpecial;
    ArrayList<String> listItemsSearch;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    TextView textViewSearch;
    TextView textViewNoResult;
    private ViewPager vp_slider;
    private LinearLayout ll_dots;
    SliderPagerAdapter sliderPagerAdapter;
    ArrayList<String> slider_image_list;
    private TextView dots,dots1,dots2,dots3,dots4;
    int page_position = 0;
    int index=0;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final FrameLayout mFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_home,
                container, false);

        View view=inflater.inflate(R.layout.fragment_home, container, false);
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        final String state = sharedpreferences.getString("stateUid", "oyWjU7sA3w7hxAX0cKFA");
        final String city = sharedpreferences.getString("cityUid","F5BfjMT86ug56gpWbmOH");

        /*HomeActivity homeActivity= (HomeActivity) getActivity();
        if(homeActivity.textViewLocation!=null){
            //city= homeActivity.textViewLocation.getText().toString();
            city= homeActivity.textViewId.getText().toString();
            Log.d("xyzCity",city);
        }else{
            city="F5BfjMT86ug56gpWbmOH";
        }*/

        recyclerView= view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewSpecial= view.findViewById(R.id.recyclerViewSpecial);
        recyclerViewSpecial.setHasFixedSize(true);
        recyclerViewSpecial.setLayoutManager(new GridLayoutManager(getContext(),1));
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        //cardView= view.findViewById(R.id.cardView);
        textViewNoResult=view.findViewById(R.id.textView_noResult);
        progressBar= view.findViewById(R.id.progressBar);
        textViewSearch=view.findViewById(R.id.searchView);
        vp_slider = view.findViewById(R.id.vp_slider);
        ll_dots = view.findViewById(R.id.ll_dots);
        slider_image_list = new ArrayList<>();
        sliderPagerAdapter = new SliderPagerAdapter(getActivity(), slider_image_list);
        vp_slider.setAdapter(sliderPagerAdapter);
        dots = view.findViewById(R.id.dots);
        dots1 = view.findViewById(R.id.dots1);
        dots2 = view.findViewById(R.id.dots2);
        dots3 = view.findViewById(R.id.dots3);
        dots4 = view.findViewById(R.id.dots4);
        dots.setText(Html.fromHtml("&#8226;"));
        dots1.setText(Html.fromHtml("&#8226;"));
        dots2.setText(Html.fromHtml("&#8226;"));
        dots3.setText(Html.fromHtml("&#8226;"));
        dots4.setText(Html.fromHtml("&#8226;"));
        dots.setTextColor(Color.parseColor("#FFFFFF"));
        dots1.setTextColor(Color.parseColor("#FFFFFF"));
        dots2.setTextColor(Color.parseColor("#FFFFFF"));
        dots3.setTextColor(Color.parseColor("#FFFFFF"));
        dots4.setTextColor(Color.parseColor("#FFFFFF"));
        dots.setTextSize(30);
        dots1.setTextSize(30);
        dots2.setTextSize(30);
        dots3.setTextSize(30);
        dots4.setTextSize(30);

       // searchView= view.findViewById(R.id.searchView);
   /*     cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),SubCategoryActivity.class);

                intent.putExtra("state",state);
                intent.putExtra("city",city);
                intent.putExtra("category","Serveigo Warriors");
                startActivity(intent);
            }
        });*/

        //Toast.makeText(HomeActivity.this, ""+reference, Toast.LENGTH_SHORT).show();
        textViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),SearchActivity.class);
                intent.putExtra("state",state);
                intent.putExtra("city",city);
                startActivity(intent);
            }
        });


        listItems= new ArrayList<>();
        listItemsSpecial= new ArrayList<>();
        listItemsSearch= new ArrayList<>();

        adapter=new AdapterCategoryList((ArrayList<ClassCategory>) listItems,getContext(),state,city);
        adapterSpecial=new AdapterCategorySpecial((ArrayList<ClassCategory>) listItemsSpecial,getContext(),state,city);

        recyclerView.setAdapter(adapter);
        recyclerViewSpecial.setAdapter(adapterSpecial);

        vp_slider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /*for(int i=0;i<5;i++){
            ClassCategory score=new ClassCategory();
            score.head="cg";
            score.subHead="pnb";
            listItems.add(score);
            adapter.notifyDataSetChanged();
        }*/
        getResult(state,city);

        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (page_position == slider_image_list.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                vp_slider.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 5000);


        return view;
    }

    private void getResult(final String state,final String city){
        Log.d("xyz",city);
        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").orderBy("Index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().size()==0){
                        progressBar.setVisibility(View.GONE);
                        textViewNoResult.setVisibility(View.VISIBLE);
                    }
                    int count = 0;
                    for (final DocumentSnapshot snapshot : task.getResult()) {
                        ClassCategory classCategory;
                        Log.d("xyz", snapshot.getId());
                        classCategory=snapshot.toObject(ClassCategory.class);
                        if(classCategory.isVisible()){
                            if(classCategory.isSpecial()) {
                                listItemsSpecial.add(classCategory);
                            }
                            else {
                                Log.d("xyz2", snapshot.getId());
                                listItems.add(classCategory);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        adapterSpecial.notifyDataSetChanged();
                        firebaseFirestore.collection("Area").document(state).collection("City").document(city).collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (final DocumentSnapshot snapshot : task.getResult()) {
                                    listItemsSearch.add(snapshot.getId());
                                    Log.d("xyz1", snapshot.getId());
                                }
                                firebaseFirestore.collection("Admin").document("Ad").collection("List").orderBy("Index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            for(DocumentSnapshot snapshot1: task.getResult()) {
                                                String url=snapshot1.getString("url");
                                                slider_image_list.add(url);
                                                sliderPagerAdapter.notifyDataSetChanged();
                                            }
                                            addBottomDots(0);
                                        }
                                    }
                                });
                            }
                        });
                        //cardView.setVisibility(View.VISIBLE);
                        count++;
                        progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    textViewNoResult.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Log.w("xyz", "Error getting documents.", task.getException());
                }
            }
        });
    }


    private void addBottomDots(int currentPage) {
        if(index==0){
            dots4.setTextColor(Color.parseColor("#FFFFFF"));
            dots.setTextColor(Color.parseColor("#000000"));
        }else if(index==1){
            dots.setTextColor(Color.parseColor("#FFFFFF"));
            dots1.setTextColor(Color.parseColor("#000000"));
        }else if(index==2){
            dots1.setTextColor(Color.parseColor("#FFFFFF"));
            dots2.setTextColor(Color.parseColor("#000000"));
        }else if(index==3){
            dots2.setTextColor(Color.parseColor("#FFFFFF"));
            dots3.setTextColor(Color.parseColor("#000000"));
        }else if(index==4){
            dots3.setTextColor(Color.parseColor("#FFFFFF"));
            dots4.setTextColor(Color.parseColor("#000000"));
        }
        index++;
        if(index==5){
            index=0;
        }
    }

}