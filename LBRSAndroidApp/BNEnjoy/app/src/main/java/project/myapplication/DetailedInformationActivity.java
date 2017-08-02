package project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DetailedInformationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FoursquarePlace fp;
    private DataProcessor dp;
    private ImageView img_discover, img_list, img_me, lastImage, nextImage, back, share, likeThisItem,
            ratingStarOne, ratingStarTwo, ratingStarThree, ratingStarFour, ratingStarFive;
    private TextView title, placeName, placeCategories, placePriceLevel,
            placePriceLevelSuffix, placeMenuUrl, placeRating, placeOpeningHours, placeIsOpen,
            placeIsPublicHoliday, placeDistance, placeLocation, placePhone, commentContent;
    private EditText commentEditText;
    private Button commentSubmit;
    private LinearLayout loadingNotice, ratingButton, commentButton, ratingLayout, commentLayout;
    private RelativeLayout hiddenLayout;
    private ViewPager viewPager;
    private ImageSlideShowAdapter issAdapter;
    private final String clientId = "4Q5EN22IAX2RNFRRK0UA1VU25QGKYM4BPEAKTQ1SL3HIKATV";
    private final String clientPassword = "LEMWC2UNHM1NRWRENVPYWQ2XQERYY1M3VXLU01BHLGDBCLND";
    private ArrayList<String> imageUrls = new ArrayList<>();
    private Animation fadeInAnimation, fadeOutAnimation;
    private ScrollView scrollView;
    private GoogleMapFragment mapFragment;
    private double latitude, longitude;
    private User user;
    private String jsonComment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        img_discover = (ImageView) findViewById(R.id.discover);
        img_list = (ImageView) findViewById(R.id.list);
        img_me = (ImageView) findViewById(R.id.me);
        lastImage = (ImageView) findViewById(R.id.back_to_last_image);
        nextImage = (ImageView) findViewById(R.id.go_to_next_image);
        back = (ImageView) findViewById(R.id.back_to_last_page);
        share = (ImageView) findViewById(R.id.share);
        likeThisItem = (ImageView) findViewById(R.id.like_this_item);
        ratingStarOne = (ImageView) findViewById(R.id.rating_star_1);
        ratingStarTwo = (ImageView) findViewById(R.id.rating_star_2);
        ratingStarThree = (ImageView) findViewById(R.id.rating_star_3);
        ratingStarFour = (ImageView) findViewById(R.id.rating_star_4);
        ratingStarFive = (ImageView) findViewById(R.id.rating_star_5);
        title = (TextView) findViewById(R.id.fp_name);
        placeName = (TextView) findViewById(R.id.place_name);
        placeCategories = (TextView) findViewById(R.id.place_categories);
        placePriceLevel = (TextView) findViewById(R.id.place_price_level);
        placePriceLevelSuffix = (TextView) findViewById(R.id.place_price_level_suffix);
        placeMenuUrl = (TextView) findViewById(R.id.place_menu);
        placeRating = (TextView) findViewById(R.id.place_rating);
        placeOpeningHours = (TextView) findViewById(R.id.place_opening_hours);
        placeIsOpen = (TextView) findViewById(R.id.place_is_open);
        placeIsPublicHoliday = (TextView) findViewById(R.id.place_is_public_holiday);
        placeDistance = (TextView) findViewById(R.id.place_distance);
        placeLocation = (TextView) findViewById(R.id.place_location);
        placePhone = (TextView) findViewById(R.id.place_phone);
        commentContent = (TextView) findViewById(R.id.comment_content);
        loadingNotice = (LinearLayout) findViewById(R.id.loading_image_layout);
        ratingButton = (LinearLayout) findViewById(R.id.rating_button);
        commentButton = (LinearLayout) findViewById(R.id.comment_button);
        ratingLayout = (LinearLayout) findViewById(R.id.rating_layout);
        commentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        hiddenLayout = (RelativeLayout) findViewById(R.id.hidden_layout);
        commentEditText = (EditText) findViewById(R.id.comment_edittext);
        commentSubmit = (Button) findViewById(R.id.comment_submit);
        viewPager = (ViewPager) findViewById(R.id.image_show);
        scrollView = (ScrollView) findViewById(R.id.information_layout);
        mapFragment = (GoogleMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        dp = new DataProcessor(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        mapFragment.getMapAsync(this);  //get google map
        mapFragment.setOnTouchListener(new GoogleMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                //this scrollView cannot intercept touch events of its child
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString("ChosenFoursquarePlace", null);
        fp = gson.fromJson(json, FoursquarePlace.class);  //get fp data
        SharedPreferences sp2 = getSharedPreferences("USER", Context.MODE_PRIVATE);
        Gson gson2 = new Gson();
        String json2 = sp2.getString("user", null);
        user = gson2.fromJson(json2, User.class);  //get user
        latitude = Double.parseDouble(sp.getString("Latitude", null));  //get latitude
        longitude = Double.parseDouble(sp.getString("Longitude", null));  //get longitude
        getImageUrls();
        /*
          set place details
         */
        title.setText(fp.getName());  //set title
        placeName.setText(fp.getName());  //set place name
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : fp.getCategories().entrySet()){
            sb.append(entry.getValue());
            sb.append(" / ");
        }
        sb.setLength(sb.length() - 3);  //remove last string " / "
        placeCategories.setText(sb.toString());  //set place categories
        //set place price level
        if (fp.getPriceLevel() == 1){
            placePriceLevel.setText("$");
            placePriceLevelSuffix.setText("$$$");
        }
        else if (fp.getPriceLevel() == 2){
            placePriceLevel.setText("$$");
            placePriceLevelSuffix.setText("$$");
        }
        else if (fp.getPriceLevel() == 3){
            placePriceLevel.setText("$$$");
            placePriceLevelSuffix.setText("$");
        }
        else if (fp.getPriceLevel() == 4){
            placePriceLevel.setText("$$$$");
            placePriceLevelSuffix.setText("");
        }
        else{
            placePriceLevel.setText("");
            placePriceLevelSuffix.setText("$$$$");
        }
        if(fp.getMenuUrl() == null){
            placeMenuUrl.setText("No menu provided.");  //set place menu link
        }
        else{
            placeMenuUrl.setText("Check menu here");  //set place menu link
            placeMenuUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(fp.getMenuUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            //set text underlined
            placeMenuUrl.setPaintFlags(placeMenuUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        placeRating.setText(Float.toString(fp.getRating()));  //set place rating
        placeOpeningHours.setText(fp.getOpeningHours());  //set place opening hours
        if (fp.getOpenStatus() == 2){
            placeIsOpen.setText("Closed");  //set place open status
        }
        else if (fp.getOpenStatus() != 1){
            placeIsOpen.setText("");  //set place open status
        }
        if (fp.getIsPublicHoliday()){
            placeIsPublicHoliday.setText("Public Holiday");  //set place if today is public holiday
        }
        //set distance between place and the user's location
        if (fp.getDistance() >= 1000) {
            BigDecimal bd = new BigDecimal(fp.getDistance() / 1000);
            String notice = "km away from your location";
            placeDistance.setText(bd.setScale(1, BigDecimal.ROUND_HALF_UP) + notice);
        }
        else{
            BigDecimal bd = new BigDecimal(fp.getDistance());
            String notice = "m away from your location";
            placeDistance.setText(bd.setScale(0, BigDecimal.ROUND_HALF_UP) + notice);
        }
        placeLocation.setText(fp.getLocation());  //set place location
        placePhone.setText(fp.getPhone());  //set place phone number
        //set text underlined
        placePhone.setPaintFlags(placePhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        placePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:" + fp.getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //click this button to show or hide rating layout
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentLayout.setVisibility(View.GONE);
                if (ratingLayout.getVisibility() == View.GONE){
                    hiddenLayout.startAnimation(fadeInAnimation);
                    ratingLayout.startAnimation(fadeInAnimation);
                    ratingLayout.setVisibility(View.VISIBLE);
                }
                else{
                    hiddenLayout.startAnimation(fadeOutAnimation);
                    ratingLayout.startAnimation(fadeOutAnimation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ratingLayout.setVisibility(View.GONE);
                        }
                    }, 400);
                }
            }
        });
        List<ImageView> stars = new ArrayList<>();
        stars.add(ratingStarOne);
        stars.add(ratingStarTwo);
        stars.add(ratingStarThree);
        stars.add(ratingStarFour);
        stars.add(ratingStarFive);
        rating(stars);
        //click this button to show or hide comment layout
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingLayout.setVisibility(View.GONE);
                if (commentLayout.getVisibility() == View.GONE){
                    hiddenLayout.startAnimation(fadeInAnimation);
                    commentLayout.startAnimation(fadeInAnimation);
                    commentLayout.setVisibility(View.VISIBLE);
                }
                else{
                    hiddenLayout.startAnimation(fadeOutAnimation);
                    commentLayout.startAnimation(fadeOutAnimation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            commentLayout.setVisibility(View.GONE);
                        }
                    }, 400);
                }
            }
        });
        readComments();
        if (user != null){
            //click this button to submit user comments
            commentSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String comment = user.getName() + ": "
                            + commentEditText.getText().toString() + "\n" + "\n";
                    final String url_write_comments = "http://10.89.208.155/MyProjectBNEnjoy/write_comments.php";
                    final String parameters_2 = "place_id=" + fp.getId() + "&comment=" + comment;
                    if (commentEditText.getText().toString().equals("")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailedInformationActivity.this, "Comment cannot be empty!",
                                        Toast.LENGTH_SHORT).show();
                                commentEditText.setText("");
                            }
                        }, 400);
                    }
                    else{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //post comments to database
                                dp.postDataToPhp(parameters_2, url_write_comments);
                            }
                        }).start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailedInformationActivity.this,
                                        "Comment posted successfully!", Toast.LENGTH_SHORT).show();
                                commentEditText.setText("");
                                commentContent.setVisibility(View.VISIBLE);
                                commentContent.append(comment);
                            }
                        }, 400);
                    }
                }
            });
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingNotice.startAnimation(fadeOutAnimation);  //make loading notice fade out
                loadingNotice.setVisibility(View.GONE);  //hide loading notice
                viewPager.startAnimation(fadeInAnimation);  //make images fade in
                issAdapter = new ImageSlideShowAdapter(
                        DetailedInformationActivity.this, imageUrls);
                viewPager.setAdapter(issAdapter);  //set adapter to viewPager
                //set zoom-out animation on viewPager
                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                if (imageUrls.size() > 1){  //the number of images is more than 1
                    nextImage.setVisibility(View.VISIBLE);  //show the right button on viewPager
                }
                setAdapterListener();  //listen slide show images on click event
            }
        }, 3000);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                if (sp.getBoolean("UserAct", false)){
                    Intent intent = new Intent(DetailedInformationActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                else if (sp.getBoolean("List", false)){
                    Intent intent = new Intent(DetailedInformationActivity.this, ListActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(DetailedInformationActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        if (user != null){
            final String content = "Hi I'm " + user.getName() + ", I'm using 'BNEnjoy' to find interest"
                    + " places now. Would you like to join me?";
            //click this button to share information
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
                    startActivity(Intent.createChooser(intent, "Share via"));
                }
            });
        }

        //un-clicked drawable
        final Drawable unClicked = ContextCompat.getDrawable(this, R.mipmap.like);
        //clicked drawable
        final Drawable clicked = ContextCompat.getDrawable(this, R.mipmap.liked);
        final SharedPreferences sp3 = getSharedPreferences("FAVORITES",
                Context.MODE_PRIVATE);
        if (sp3.contains(fp.getId())){  //this fp has been liked
            likeThisItem.setImageDrawable(clicked);
        }
        else{  //this fp has not been liked yet
            likeThisItem.setImageDrawable(unClicked);
        }
        likeThisItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable current = likeThisItem.getDrawable();
                //condition that the heart button is clicked
                if (current.getConstantState().equals(clicked.getConstantState())){
                    likeThisItem.setImageDrawable(unClicked);
                    sp3.edit().remove(fp.getId()).apply();  //remove this sp
                }
                else{  //condition that the heart button is un-clicked
                    likeThisItem.setImageDrawable(clicked);
                    Gson gson = new Gson();
                    String json = gson.toJson(fp);  //parse FoursquarePlace fp to json
                    //save fp as json format
                    sp3.edit().putString(fp.getId(), json).apply();
                }
            }
        });

        //listen if the image has been changed by the user
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //will be invoked if scroll state is changed
            public void onPageScrollStateChanged(int state){ }
            //will be invoked if the page is scrolling
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){ }
            //will be invoked if current page is selected
            public void onPageSelected(int position) {
                if (position != 0){  //not at the first image page
                    lastImage.setVisibility(View.VISIBLE);  //show the left button on viewPager
                }
                else{  //at the first image page
                    lastImage.setVisibility(View.GONE);  //hide the left button on viewPager
                }
                if (position != imageUrls.size() - 1){  //at the last image page
                    nextImage.setVisibility(View.VISIBLE);  //show the right button on viewPager
                }
                else{  //at the last image page
                    nextImage.setVisibility(View.GONE);  //hide the right button on viewPager
                }
            }
        });

        //click this button to turn to the last page
        lastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();  //get current image position
                viewPager.setCurrentItem(position - 1);  //back to last image
            }
        });

        //click this button to turn to the next page
        nextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewPager.getCurrentItem();  //get current image position
                viewPager.setCurrentItem(position + 1);  //go to next image
            }
        });

        //link to main layout
        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedInformationActivity.this, MainActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to list layout
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedInformationActivity.this, ListActivity.class);
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
                    Intent intent = new Intent(DetailedInformationActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{  //logged in
                    Intent intent = new Intent(DetailedInformationActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    public void rating(final List<ImageView> stars){
        final List<Boolean> isClicked = new ArrayList<>();
        for (int a = 0; a < 5; a++){
            isClicked.add(false);  //add five 'false's
        }
        for (int i = 0; i < stars.size(); i++){
            final int index = i;
            stars.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isClicked.get(index)){  //star unclicked
                        isClicked.set(index, true);  //change the star status to 'clicked'
                        for (int j = 0; j <= index; j++){
                            stars.get(j).setImageResource(R.mipmap.rating_star_clicked);
                        }
                        for (int k = 0; k < stars.size(); k++){
                            if (k != index){
                                stars.get(k).setClickable(false);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Rating Succeeded!", Toast.LENGTH_SHORT).show();
                            }
                        }, 500);
                    }
                    else{  //star clicked
                        isClicked.set(index, false);  //change the star status to 'unclicked'
                        for (int j = 0; j <= index; j++){
                            stars.get(j).setImageResource(R.mipmap.rating_star);
                        }
                        for (int k = 0; k < stars.size(); k++){
                            stars.get(k).setClickable(true);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Unrating Succeeded!", Toast.LENGTH_SHORT).show();
                            }
                        }, 500);
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //check if fine or coarse location permission is granted
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);  //enable the my-location layer
        }
        final GoogleMap MAP = map;
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //center the user's current location if the user click the "MyLocation" button
                //The float "12" means zoom level. The integer "500" means moving duration
                MAP.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitude, longitude), 12), 500, null);
                return true;
            }
        });
        LatLng latLng = new LatLng(fp.getLatitude(), fp.getLongitude());
        //add a marker for the user's current location
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Me"));
        //show this fp position on the map. The float "10" means zoom level
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
        //add a marker for a place that the user chose
        //change the marker color as AZURE that differentiate with the user's location marker
        map.addMarker(new MarkerOptions().position(latLng).title(fp.getName())
                .snippet(fp.getLocation()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        map.setTrafficEnabled(true);
        map.setIndoorEnabled(true);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    //get all image urls of one venue via its id
    public void getImageUrls(){
        final String imageRequestUrl = "https://api.foursquare.com/v2/venues/" + fp.getId()
                + "/photos?client_id=" + clientId + "&client_secret=" + clientPassword
                + "&v=" + dp.getCurrentDate();
        //methods related to network connection have to run in another thread
        new Thread(new Runnable(){
            public void run() {
                imageUrls.addAll(dp.parseFoursquareImageUrls(
                        dp.readDataFromFoursquare(imageRequestUrl)));
            }
        }).start();
    }

    //click slide show layout to turn to GalleryActivity
    public void setAdapterListener(){
        issAdapter.setSlideShowOnClickListener(new ImageSlideShowAdapter.SlideShowOnClickListener(){
            @Override
            public void onSlideShowClick(View view){
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                String s = "";
                if (imageUrls.size() == 1){
                    s += imageUrls.get(0);
                }
                else if (imageUrls.size() > 1){
                    for (int i = 0; i < imageUrls.size() - 1; i++){
                        s += imageUrls.get(i) + ",";
                    }
                    s += imageUrls.get(imageUrls.size() - 1);
                }
                sp.edit().putString("ImageUrls", s).apply();  //save image urls as string format
                Intent intent = new Intent(DetailedInformationActivity.this, GalleryActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    //read comments
    public void readComments(){
        final String url_read_comments = "http://172.27.42.1/MyProjectBNEnjoy/read_comments.php";
        final String parameters_1 = "place_id=" + fp.getId();
        new Thread(new Runnable() {
            public void run() {
                //post comments to database
                dp.postDataToPhp(parameters_1, url_read_comments);
                jsonComment = dp.getData();
                try{
                    JSONArray ja = new JSONArray(jsonComment);
                    JSONObject jb = ja.getJSONObject(0);
                    if (!jb.isNull("comment")){
                        commentContent.setText(jb.getString("comment"));
                    }
                    else{
                        commentContent.setVisibility(View.GONE);
                    }
                }
                catch (JSONException e) {
                    e.getMessage();
                }
            }
        }).start();
    }

    //about share menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.share_menu) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
