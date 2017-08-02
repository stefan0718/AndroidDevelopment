package project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static boolean IS_FIRST_TIME_OPEN = true;
    private ImageView img_me, img_list, food, shopping, placeToGo;
    private TextView change;
    private SearchView searchView;
    private RecommendedViewAdapter adapter;
    private List<FoursquarePlace> fps = new ArrayList<>();
    private List<FoursquarePlace> fpsFood = new ArrayList<>();
    private List<FoursquarePlace> fpsShoppingMall = new ArrayList<>();
    private List<FoursquarePlace> fpsShoppingPlaza = new ArrayList<>();
    private List<FoursquarePlace> fpsPlaceToGo = new ArrayList<>();
    private LinearLayout loadingLayout;
    private Locator locator;
    private double latitude, longitude;
    private DataProcessor dp;
    private List<Boolean> buttonStatus = new ArrayList<>();
    private Animation fadeInAnimation, fadeOutAnimation;
    private final String clientId = "4Q5EN22IAX2RNFRRK0UA1VU25QGKYM4BPEAKTQ1SL3HIKATV";
    private final String clientPassword = "LEMWC2UNHM1NRWRENVPYWQ2XQERYY1M3VXLU01BHLGDBCLND";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_me = (ImageView) findViewById(R.id.me);
        img_list = (ImageView) findViewById(R.id.list);
        food = (ImageView) findViewById(R.id.food);
        shopping = (ImageView) findViewById(R.id.shopping);
        placeToGo = (ImageView) findViewById(R.id.place_togo);
        change = (TextView) findViewById(R.id.recommended_change);
        searchView = (SearchView) findViewById(R.id.searchView);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout) ;
        dp = new DataProcessor(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        if (IS_FIRST_TIME_OPEN){
            //clear DATA if open this activity by launching app, not by jumping from other activities
            sp.edit().clear().apply();
        }
        //set all button status un-clicked initially
        buttonStatus.add(true);
        food.setImageResource(R.mipmap.food_clicked2);
        buttonStatus.add(false);
        buttonStatus.add(false);
        getData();  //get data when opening this activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingLayout.setVisibility(View.GONE);  //hide this layout
                sortData();
                initializeAdapter();  //initialize the adapter
            }
        }, 4000);

        //link to list layout
        img_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsFirstTimeOpen();
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //link to login layout or user layout
        img_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIsFirstTimeOpen();
                SharedPreferences sp = getSharedPreferences("USER", Context.MODE_PRIVATE);
                if (sp.getString("user", null) == null){  //haven't logged in yet
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{  //logged in
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        searchView.setQueryHint("Anything interested?");  //set a query hint
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);    //make whole search bar clickable
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setIsFirstTimeOpen();
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                sp.edit().putString("Query", query).apply();  //save query data
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //set onclick listener on buttons
        View.OnClickListener onButtonClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
                recyclerView.startAnimation(fadeOutAnimation);
                ImageView iv = (ImageView) findViewById(v.getId());
                setButtons(iv);
                sortData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initializeAdapter();
                    }
                }, 400);
            }
        };
        food.setOnClickListener(onButtonClickListener);
        shopping.setOnClickListener(onButtonClickListener);
        placeToGo.setOnClickListener(onButtonClickListener);

        //click to change recommended items
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
                recyclerView.startAnimation(fadeOutAnimation);
                sortData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initializeAdapter();
                    }
                }, 400);
            }
        });
    }

    // set "First Time Open" status to false if jump to other activities
    public void setIsFirstTimeOpen(){
        if (IS_FIRST_TIME_OPEN){
            IS_FIRST_TIME_OPEN = false;
        }
    }

    // initialize the recommended view adapter
    public void initializeAdapter(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new RecommendedViewAdapter(fps, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();  //update new data
        recyclerView.startAnimation(fadeInAnimation);
        //set click listener to each recommended view
        adapter.setRecommendedViewOnClickListener(new RecommendedViewAdapter.
                RecommendedViewOnClickListener() {
            @Override
            public void onCardClick(View view, FoursquarePlace fp) {
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(fp);  //parse FoursquarePlace fp to json
                //save fp as json format
                sp.edit().putString("ChosenFoursquarePlace", json).apply();
                sp.edit().putBoolean("Main", true).apply();
                Intent intent = new Intent(MainActivity.this, DetailedInformationActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // get user location
    public void getLocation(){
        //get latitude and longitude
        locator = new Locator(this);
        if (locator.getLocationServiceStatus()) {    //can get location information
            latitude = locator.getLatitude();
            longitude = locator.getLongitude();
        } else {
            locator.showSettingsAlert();
        }
    }

    // get recommended data
    public void getData(){
        getLocation();  //get location
        latitude = locator.getLatitude();  //get latitude
        longitude = locator.getLongitude();  //get longitude
        if (latitude == 0.0){
            latitude = -27.4954310;
            longitude = 153.0120300;
        }
        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        //save user's location
        sp.edit().putString("Latitude", Double.toString(latitude)).apply();
        sp.edit().putString("Longitude", Double.toString(longitude)).apply();
        String urlHead = "https://api.foursquare.com/v2/venues/explore?client_id=" + clientId
                + "&client_secret=" + clientPassword + "&v=" + dp.getCurrentDate()
                + "&limit=50&radius=100000&venuePhotos=1";
        String urlTail = "&ll=" + Double.toString(latitude) + "," + Double.toString(longitude);
        final String urlFood = urlHead + "&categoryId=4d4b7105d754a06374d81259" + urlTail;
        final String urlShoppingMall = urlHead + "&categoryId=4bf58dd8d48988d1fd941735" + urlTail;
        final String urlShoppingPlaza = urlHead + "&categoryId=5744ccdfe4b0c0459246b4dc" + urlTail;
        final String urlEvent = urlHead + "&categoryId=4d4b7105d754a06373d81259" + urlTail;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fpsFood = dp.parseJsonData(dp.readDataFromFoursquare(urlFood));
                fpsShoppingMall = dp.parseJsonData(dp.readDataFromFoursquare(urlShoppingMall));
                fpsShoppingPlaza = dp.parseJsonData(dp.readDataFromFoursquare(urlShoppingPlaza));
                fpsPlaceToGo = dp.parseJsonData(dp.readDataFromFoursquare(urlEvent));
            }
        };
        new Thread(runnable).start();
    }

    //get sorted data by what the user chose
    public void sortData(){
        fps = new ArrayList<>();
        if (buttonStatus.get(0)){
            Set<FoursquarePlace> set = new HashSet<>();
            if (fpsFood.size() > 6){
                for (int i = 0; i < 6; i++){
                    fps.add(fpsFood.get(new Random().nextInt(fpsFood.size())));
                    set.addAll(fps);
                    fps.clear();
                    if (set.size() < 6){
                        i -= 1;
                    }
                    else{
                        break;
                    }
                }
                fps.addAll(set);  // remove duplicated items
            }
            else{
                fps.addAll(fpsFood);
            }
        }
        else if (buttonStatus.get(1)){
            Set<FoursquarePlace> set = new HashSet<>();
            fpsShoppingMall.addAll(fpsShoppingPlaza);
            if (fpsShoppingMall.size() > 6){
                for (int i = 0; i < 6; i++){
                    fps.add(fpsShoppingMall.get(new Random().nextInt(fpsShoppingMall.size())));
                    set.addAll(fps);
                    fps.clear();
                    if (set.size() < 6){
                        i -= 1;
                    }
                    else{
                        break;
                    }
                }
                fps.addAll(set);  // remove duplicated items
            }
            else{
                fps.addAll(fpsShoppingMall);
            }
        }
        else if (buttonStatus.get(2)){
            Set<FoursquarePlace> set = new HashSet<>();
            if (fpsPlaceToGo.size() > 6){
                for (int i = 0; i < 6; i++){
                    fps.add(fpsPlaceToGo.get(new Random().nextInt(fpsPlaceToGo.size())));
                    set.addAll(fps);
                    fps.clear();
                    if (set.size() < 6){
                        i -= 1;
                    }
                    else{
                        break;
                    }
                }
                fps.addAll(set);  // remove duplicated items
            }
            else{
                fps.addAll(fpsPlaceToGo);
            }
        }
    }

    //set button drawable icons and button status
    public void setButtons(ImageView iv) {
        if (iv == food && !buttonStatus.get(0)){
            food.setImageResource(R.mipmap.food_clicked2);
            shopping.setImageResource(R.mipmap.shopping);
            placeToGo.setImageResource(R.mipmap.place_togo);
            buttonStatus.set(0, true);
            buttonStatus.set(1, false);
            buttonStatus.set(2, false);
        }
        else if (iv == shopping && !buttonStatus.get(1)){
            food.setImageResource(R.mipmap.food);
            shopping.setImageResource(R.mipmap.shopping_clicked2);
            placeToGo.setImageResource(R.mipmap.place_togo);
            buttonStatus.set(0, false);
            buttonStatus.set(1, true);
            buttonStatus.set(2, false);
        }
        else if (iv == placeToGo && !buttonStatus.get(2)){
            food.setImageResource(R.mipmap.food);
            shopping.setImageResource(R.mipmap.shopping);
            placeToGo.setImageResource(R.mipmap.place_togo_clicked2);
            buttonStatus.set(0, false);
            buttonStatus.set(1, false);
            buttonStatus.set(2, true);
        }
    }
}
