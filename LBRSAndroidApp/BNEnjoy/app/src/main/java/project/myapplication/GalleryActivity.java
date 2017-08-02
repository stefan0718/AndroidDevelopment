package project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager layoutManager;
    private GalleryAdapter galleryAdapter;
    private RecyclerView recyclerViewGallery;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private ImageView img_discover, img_list, img_me, back, zoomedInImage;
    private TextView fpName;
    private FoursquarePlace fp;
    private LinearLayout backgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        recyclerViewGallery = (RecyclerView) findViewById(R.id.recyclerViewGallery);
        img_discover = (ImageView) findViewById(R.id.discover);
        img_list = (ImageView) findViewById(R.id.list);
        img_me = (ImageView) findViewById(R.id.me);
        back = (ImageView) findViewById(R.id.back_to_last_page);
        zoomedInImage = (ImageView) findViewById(R.id.zoomed_in_image);
        fpName = (TextView) findViewById(R.id.fp_name);
        backgroundLayout = (LinearLayout) findViewById(R.id.background_layout);

        //Intent intent = this.getIntent();
        /*
        //get image urls from DetailedInformationActivity
        imageUrls = intent.getStringArrayListExtra("ImageUrls");
        //get fp data from ListActivity
        fp = (FoursquarePlace) intent.getSerializableExtra("ChosenFoursquarePlace");*/
        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String[] s = sp.getString("ImageUrls", null).split(",");  //get image urls
        for (int i = 0; i < s.length; i++){
            imageUrls.add(s[i]);
        }
        Gson gson = new Gson();
        String json = sp.getString("ChosenFoursquarePlace", null);
        fp = gson.fromJson(json, FoursquarePlace.class);  //get fp data
        fpName.setText(fp.getName());
        decorateRecyclerViewGallery();
        initializeGalleryAdapter();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, DetailedInformationActivity.class);
                /*
                //pass fp back to DetailedInformationActivity
                intent.putExtra("ChosenFoursquarePlace", fp);*/
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        zoomedInImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backgroundLayout.getVisibility() == View.VISIBLE){
                    backgroundLayout.animate().alpha(0.0f);  //fade in effect
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            backgroundLayout.setVisibility(View.GONE);  //hide zoomed image
                        }
                    }, 300);
                }
            }
        });

        //link to main layout
        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to list layout
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, ListActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to login layout or user layout
        img_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                if (sp.getString("user", null) == null){  //haven't logged in yet
                    Intent intent = new Intent(GalleryActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{  //logged in
                    Intent intent = new Intent(GalleryActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    public void initializeGalleryAdapter(){
        galleryAdapter = new GalleryAdapter(imageUrls, this);  //bind image urls to gallery adapter
        layoutManager = new GridLayoutManager(this, 3);  //the number 3 means set three columns
        recyclerViewGallery.setLayoutManager(layoutManager);  //set layout manager
        recyclerViewGallery.setAdapter(galleryAdapter);  //set gallery adapter
        galleryAdapter.setGalleryOnClickListener(new GalleryAdapter.GalleryOnClickListener() {
            @Override
            public void onGalleryItemClick(View v, int position) {
                backgroundLayout.setVisibility(View.VISIBLE);  //make image layout visible
                Picasso.with(getApplicationContext()).load(imageUrls.get(position))
                        .into(zoomedInImage);
                backgroundLayout.animate().alpha(1.0f);  //fade in effect
            }
        });
    }

    //set horizontal divide lines to divide images
    //(vertical divider is set in GalleryAdapter with "ImageView.setVisibility(View.GONE);"
    public void decorateRecyclerViewGallery(){
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(
                recyclerViewGallery.getContext(), 1);  //1 means horizontal divider
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
        horizontalDecoration.setDrawable(verticalDivider);
        recyclerViewGallery.addItemDecoration(horizontalDecoration);  //add horizontal line
    }
}
