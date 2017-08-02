package project.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListActivity extends AppCompatActivity{

    private ImageView img_me, img_discover;
    private SearchView searchView;
    private Button food, shopping, placeToGo, popularity, rating, distance, price, openNow;
    private Locator locator;
    private double latitude, longitude;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private DataViewAdapter adapter;
    private DividerItemDecoration decoration;
    private DataProcessor dp;
    private List<FoursquarePlace> fps = new ArrayList<>();
    private int lastVisibleItem, numberOfShownItems, numberOfRemainingItems;
    private int userChosenListPosition = 0;
    private final String clientId = "4Q5EN22IAX2RNFRRK0UA1VU25QGKYM4BPEAKTQ1SL3HIKATV";
    private final String clientPassword = "LEMWC2UNHM1NRWRENVPYWQ2XQERYY1M3VXLU01BHLGDBCLND";
    private List<FoursquarePlace> fpsFood = new ArrayList<>();
    private List<FoursquarePlace> fpsShoppingMall = new ArrayList<>();
    private List<FoursquarePlace> fpsShoppingPlaza = new ArrayList<>();
    private List<FoursquarePlace> fpsPlaceToGo = new ArrayList<>();
    private List<FoursquarePlace> fpsSearched = new ArrayList<>();
    private ArrayList<Integer> bigButtonStatus = new ArrayList<>();
    private ArrayList<Integer> smallButtonStatus = new ArrayList<>();
    private List<List<String>> categoriesIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        img_me = (ImageView)findViewById(R.id.me);
        img_discover = (ImageView)findViewById(R.id.discover);
        searchView = (SearchView)findViewById(R.id.searchView);
        food = (Button)findViewById(R.id.food_list);
        shopping = (Button)findViewById(R.id.shopping_list);
        placeToGo = (Button)findViewById(R.id.place_to_go_list);
        popularity = (Button)findViewById(R.id.popularity);
        rating = (Button)findViewById(R.id.rating);
        distance = (Button)findViewById(R.id.distance);
        price = (Button)findViewById(R.id.price);
        openNow = (Button)findViewById(R.id.opennow);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        dp = new DataProcessor(this);
        locator = new Locator(ListActivity.this);

        //set list view divider. number 1 means vertical orientation (horizontal divider)
        decoration = new DividerItemDecoration(recyclerView.getContext(), 1);
        Drawable verticalDivider = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.line_divider);
        decoration.setDrawable(verticalDivider);
        recyclerView.addItemDecoration(decoration);  //add line to divide items
        refreshLayout.setRefreshing(true);  //start refreshing when opening this page

        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        // the condition that user entered search query
        if (sp.contains("Query")){
            if (!sp.getString("Query", null).equals(null)){
                CharSequence cs = sp.getString("Query", null);  //get query
                loadSearchedData(cs.toString());
                searchView.setQuery(cs, true);  //boolean "true" means submit the query right now
            }
        }
        else{
            loadData();  //load data when opening this page
        }

        categoriesIds.add(getFoodCategoryIdsFromShared());
        categoriesIds.add(getShoppingCategoryIdsFromShared());
        categoriesIds.add(getPlaceToGoCategoryIdsFromShared());
        //condition that nothing saved on shared preference
        if (categoriesIds.get(0).size() == 0 && categoriesIds.get(1).size() == 0
                && categoriesIds.get(2).size() == 0){
            loadCategoriesIds();  //load all categories' id when opening this page
        }
        numberOfShownItems = 0;  //initially 0 item has been shown

        //set adapter and display data in recycler view after refreshing in 3 seconds,
        //as getting user's image url data will cost a little bit time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);  //stop refreshing
                showData(numberOfShownItems, getSortType());  //get 15 fps initially
                jumpToClickedItemView();  //determine if needs jumping to certain item view
                initializeAdapter();  //initialize the adapter
                adapter.notifyDataSetChanged();  //update new data
                loadingNotice();  //show if loaded data or not
                //jump to certain position if needed
                recyclerView.scrollToPosition(userChosenListPosition);
                saveAllCategoriesData();  //record all category ids data
            }
        }, 4000);

        getAllButtonStatus();
        initializeButtons();

        //link to main layout
        img_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
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
                    Intent intent = new Intent(ListActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{  //logged in
                    Intent intent = new Intent(ListActivity.this, UserActivity.class);
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
                searchView.setIconified(false);  //make whole search bar clickable
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                //SharedPreferences.Editor editor = sp.edit();
                sp.edit().putString("Query", query).apply();  //save query data
                loadSearchedData(query);
                refreshLayout.setRefreshing(true);  //start refreshing
                numberOfShownItems = 0;  //reset
                fps = new ArrayList<>();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showData(numberOfShownItems, getSortType());
                        initializeAdapter();
                        loadingNotice();  //show if loaded data or not
                        refreshLayout.setRefreshing(false);  //stop refreshing
                    }
                }, 3000);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });

        //set onClickListener on three big buttons
        View.OnClickListener onBigButtonClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!refreshLayout.isRefreshing()){
                    Button b = (Button)findViewById(v.getId());
                    setBigButtonEffect(b);
                    numberOfShownItems = 0;
                    fps = new ArrayList<>();  //initialize list fps
                    showData(numberOfShownItems, getSortType());
                    initializeAdapter();
                    adapter.notifyDataSetChanged();  //update new data
                    SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    //save big button status
                    sp.edit().putString("BigButtonStatus", getBigButtonStatus()).apply();
                }
            }
        };
        food.setOnClickListener(onBigButtonClickListener);
        shopping.setOnClickListener(onBigButtonClickListener);
        placeToGo.setOnClickListener(onBigButtonClickListener);

        //set onClickListener on six small buttons
        View.OnClickListener onSmallButtonClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!refreshLayout.isRefreshing()){
                    Button b = (Button)findViewById(v.getId());
                    setSmallButtonEffect(b);
                    setUpOrDown(b);
                    numberOfShownItems = 0;  //clear all shown items
                    fps = new ArrayList<>();  //initialize list fps
                    showData(numberOfShownItems, getSortType());
                    initializeAdapter();
                    adapter.notifyDataSetChanged();  //update new data
                    SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    //save small button status
                    sp.edit().putString("SmallButtonStatus", getSmallButtonStatus()).apply();
                }
            }
        };

        popularity.setOnClickListener(onSmallButtonClickListener);
        rating.setOnClickListener(onSmallButtonClickListener);
        distance.setOnClickListener(onSmallButtonClickListener);
        price.setOnClickListener(onSmallButtonClickListener);
        openNow.setOnClickListener(onSmallButtonClickListener);

        //set onrefresh listener so that pull down to refresh
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (searchView.getQuery().toString().equals(null)
                        || searchView.getQuery().toString().equals("")){
                    loadData();  //reload updated data since location might changed
                }
                else{
                    loadSearchedData(searchView.getQuery().toString());
                }
                numberOfShownItems = 0;  //reset
                fps = new ArrayList<>();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showData(numberOfShownItems, getSortType());
                        initializeAdapter();
                        loadingNotice();  //show if loaded data or not
                        refreshLayout.setRefreshing(false);  //stop refreshing
                    }
                }, 4000);
            }
        });

        //set onscroll listener so that pull up to load more
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                //condition that not currently scrolling && at last visible item position
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == numberOfShownItems) {
                    //condition that there are still un-shown items exist
                    if (numberOfRemainingItems > 0) {
                        adapter.showFootView();  //show foot view
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showData(numberOfShownItems, getSortType());
                                adapter.notifyDataSetChanged();  //update new data
                                Toast.makeText(getApplicationContext(),
                                        "Loaded more items. ", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                    }

                    //if there are no more items remaining
                    else if(lastVisibleItem == fps.size() && fps.size() != 0){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.hideFootView();
                                adapter.notifyDataSetChanged();  //update new data
                                Toast.makeText(getApplicationContext(),
                                        "No more items here. ", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //get position of the last visible item when scrolling recyclerview
                lastVisibleItem = ((LinearLayoutManager) layoutManager)
                        .findLastVisibleItemPosition();
            }
        });

        //set refreshing colors
        refreshLayout.setColorSchemeResources(android.R.color.secondary_text_dark,
                android.R.color.darker_gray, android.R.color.tertiary_text_dark);
    }

    //jump to user clicked item view when the user comes back from other activities
    public void jumpToClickedItemView(){
        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        //determine if get string from other activities
        if (sp.contains("ChosenFoursquarePlace")){
            Gson gson = new Gson();
            String json = sp.getString("ChosenFoursquarePlace", null);
            //get fp id
            String focusedFoursquarePlaceId = gson.fromJson(json, FoursquarePlace.class).getId();
            for (int i = 0; i < fps.size(); i++){  //initially there are only 15 fp in fps
                //condition that the clicked item is in these 15 fp
                if (focusedFoursquarePlaceId.equals(fps.get(i).getId())){
                    userChosenListPosition = i;  //get this position
                    break;  //stop looping
                }
                //condition that the clicked item is not in these 15 fp
                else if (!focusedFoursquarePlaceId.equals(fps.get(i).getId())
                        && i == fps.size() - 1){
                    showData(numberOfShownItems, getSortType());  //load 15 more fp
                }
            }
        }
    }

    //determine if user left customized button status after leaving this activity
    //then get all button status
    public void getAllButtonStatus(){
        SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        if (sp.contains("BigButtonStatus")){
            String[] sBigbuttons = sp.getString("BigButtonStatus", null).split(",");
            bigButtonStatus.add(Integer.parseInt(sBigbuttons[0]));
            bigButtonStatus.add(Integer.parseInt(sBigbuttons[1]));
            bigButtonStatus.add(Integer.parseInt(sBigbuttons[2]));
        }
        else{
            for (int i = 0; i < 3; i++){
                bigButtonStatus.add(0);  //set all big button status un-clicked initially
            }
        }
        if (sp.contains("SmallButtonStatus")){
            String[] sSmallButtons = sp.getString("SmallButtonStatus", null).split(",");
            smallButtonStatus.add(Integer.parseInt(sSmallButtons[0]));
            smallButtonStatus.add(Integer.parseInt(sSmallButtons[1]));
            smallButtonStatus.add(Integer.parseInt(sSmallButtons[2]));
            smallButtonStatus.add(Integer.parseInt(sSmallButtons[3]));
            smallButtonStatus.add(Integer.parseInt(sSmallButtons[4]));
        }
        else{
            smallButtonStatus.add(1);  //set popularity status clicked initially
            for (int i = 0; i < 5; i++){
                smallButtonStatus.add(0);  //set other buttons status un-clicked initially
            }
        }
    }

    //set buttons as recorded status
    public void initializeButtons(){
        List<Button> bigButtonList = new ArrayList<>();
        bigButtonList.add(food);
        bigButtonList.add(shopping);
        bigButtonList.add(placeToGo);
        //set all button as recorded status
        for (int i = 0; i < bigButtonList.size(); i++){
            if (bigButtonStatus.get(i) == 1){  //the ith big button is clicked
                bigButtonList.get(i).setBackgroundResource(R.drawable.button_onclick_shape);
                bigButtonList.get(i).setTextColor(
                        ContextCompat.getColor(this, R.color.colorPrimaryGrey));
                bigButtonList.get(i).setCompoundDrawables(
                        setBigButtonDrawables(bigButtonList.get(i), 1), null, null, null);
            }
            else{  //the ith big button is un-clicked
                bigButtonList.get(i).setBackgroundResource(R.drawable.button_shape);
                bigButtonList.get(i).setTextColor(
                        ContextCompat.getColor(this, R.color.colorWhite));
                bigButtonList.get(i).setCompoundDrawables(
                        setBigButtonDrawables(bigButtonList.get(i), 0), null, null, null);
            }
        }
        List<Button> smallButtonList = new ArrayList<>();
        smallButtonList.add(popularity);
        smallButtonList.add(rating);
        smallButtonList.add(distance);
        smallButtonList.add(price);
        smallButtonList.add(placeToGo);
        for (int i = 0; i < smallButtonList.size(); i++){
            if (smallButtonStatus.get(i) == 1){  //the ith button is clicked
                if (i == 0 || i == 4){  //button popularity or placeToGo
                    smallButtonList.get(i).setBackgroundResource(R.drawable.button_onclick_shape);
                    smallButtonList.get(i).setTextColor(ContextCompat.getColor(
                            this, R.color.colorPrimaryLightGrey2));
                    smallButtonList.get(i).setTypeface(Typeface.DEFAULT_BOLD);
                    smallButtonList.get(i).setEnabled(false);  //set button un-clickable
                }
                else{  //i == 1 || i == 2 || i == 3, which implies button rating, distance and price
                    smallButtonList.get(i).setCompoundDrawables(
                            null, null, setSmallButtonDrawables(1), null);
                    setSmallButtonEffect(smallButtonList.get(i));
                }
            }
            //could only happen in button rating, distance and price as they have 3 status
            else if (smallButtonStatus.get(i) == 2){
                smallButtonList.get(i).setCompoundDrawables(
                        null, null, setSmallButtonDrawables(2), null);
                setSmallButtonEffect(smallButtonList.get(i));
            }
            else if (smallButtonStatus.get(i) == 0){  //the ith button is un-clicked
                if (i == 1 || i == 2 || i == 3){  //button rating, distance and price
                    smallButtonList.get(i).setCompoundDrawables(
                            null, null, setSmallButtonDrawables(0), null);
                }
            }
        }
    }

    //get latitude and longitude
    public void getLocation(){
        locator = new Locator(this);
        if (locator.getLocationServiceStatus()){  //can get location information
            latitude = locator.getLatitude();
            longitude = locator.getLongitude();
        }
        else{
            locator.showSettingsAlert();
        }
    }

    //load foursquare venues' data
    public void loadData(){
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
        //item request limitation is 50, radius range is 100km, request one photo
        String urlHead = "https://api.foursquare.com/v2/venues/explore?client_id=" + clientId
                + "&client_secret=" + clientPassword + "&v=" + dp.getCurrentDate()
                + "&limit=50&radius=100000&venuePhotos=1";
        String urlTail = "&ll=" + Double.toString(latitude) + "," + Double.toString(longitude);
        final String urlFood = urlHead + "&categoryId=4d4b7105d754a06374d81259" + urlTail;
        final String urlShoppingMall = urlHead + "&categoryId=4bf58dd8d48988d1fd941735" + urlTail;
        final String urlShoppingPlaza = urlHead + "&categoryId=5744ccdfe4b0c0459246b4dc" + urlTail;
        final String urlEvent = urlHead + "&categoryId=4d4b7105d754a06373d81259" + urlTail;

        //methods related to network connection have to run in another thread
        Runnable runnable = new Runnable(){
            public void run() {
                fpsFood = dp.parseJsonData(dp.readDataFromFoursquare(urlFood));
                fpsShoppingMall = dp.parseJsonData(dp.readDataFromFoursquare(urlShoppingMall));
                fpsShoppingPlaza = dp.parseJsonData(dp.readDataFromFoursquare(urlShoppingPlaza));
                fpsPlaceToGo = dp.parseJsonData(dp.readDataFromFoursquare(urlEvent));
            }
        };
        new Thread(runnable).start();
    }

    //load foursquare venue data based on what the user want to search
    public void loadSearchedData(String query){
        getLocation();  //get location
        latitude = locator.getLatitude();  //get latitude
        longitude = locator.getLongitude();  //get longitude
        if (latitude == 0.0){
            latitude = -27.4954310;
            longitude = 153.0120300;
        }
        String urlHead = "https://api.foursquare.com/v2/venues/explore?client_id=" + clientId
                + "&client_secret=" + clientPassword + "&v=" + dp.getCurrentDate()
                + "&limit=50&venuePhotos=1";  //item request limitation is 50, request one photo
        String urlTail = "&ll=" + Double.toString(latitude) + "," + Double.toString(longitude);
        String[] strings = query.split(" ");
        String string = "";
        if (strings.length == 1){  //the query is a single word
            string += strings[0];
        }
        else if (strings.length > 1){  //the query contains more than one word, splitted by space
            for (int i = 0; i < strings.length - 1; i++){
                string += strings[i];
                string += "%20";  //"%20" represents as a space symbol
            }
            string += strings[strings.length - 1];  //no need to add "%20" behind the last word
        }

        final String urlSearched = urlHead + "&query=" + string + urlTail;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fpsSearched = dp.parseJsonData(dp.readDataFromFoursquare(urlSearched));
            }
        };
        new Thread(runnable).start();
    }

    //get all categorys' id via foursquare API
    public void loadCategoriesIds(){
        final String urlCategories = "https://api.foursquare.com/v2/venues/categories?client_id="
                + clientId + "&client_secret=" + clientPassword + "&v=" + dp.getCurrentDate();
        Runnable runnable = new Runnable(){
            public void run() {
                categoriesIds = dp.parseFoursquareCategories(dp.readDataFromFoursquare(urlCategories));
            }
        };
        new Thread(runnable).start();
    }

    //save all categories data
    //in order to reduce unnecessary Foursquare API accessing
    public void saveAllCategoriesData(){
        SharedPreferences sp = getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        if (sp.getInt("FoodCategoryIdSize", 0) == 0 || sp.getInt("ShoppingCategoryIdSize", 0) == 0
                || sp.getInt("PlaceToGoCategoryIdSize", 0) == 0){
            SharedPreferences.Editor editor = sp.edit();
            for (int i = 0; i < categoriesIds.get(0).size(); i++){
                //save each food category ids
                editor.putString("FoodCategoryId_" + i, categoriesIds.get(0).get(i));
            }
            //record the size of FoodCategoryId List
            editor.putInt("FoodCategoryIdSize", categoriesIds.get(0).size());
            for (int i = 0; i < categoriesIds.get(1).size(); i++){
                //save each shopping category ids
                editor.putString("ShoppingCategoryId_" + i, categoriesIds.get(1).get(i));
            }
            //record the size of ShoppingCategoryId List
            editor.putInt("ShoppingCategoryIdSize", categoriesIds.get(1).size());
            for (int i = 0; i < categoriesIds.get(2).size(); i++){
                //save each placeToGo category ids
                editor.putString("PlaceToGoCategoryId_" + i, categoriesIds.get(2).get(i));
            }
            //record the size of PlaceToGoCategoryId List
            editor.putInt("PlaceToGoCategoryIdSize", categoriesIds.get(2).size());
            editor.apply();
        }
    }

    //fetch food category ids from shared preference
    public List<String> getFoodCategoryIdsFromShared(){
        List<String> list = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        for (int i = 0; i < sp.getInt("FoodCategoryIdSize", 0); i++){
            //get each food category id
            list.add(sp.getString("FoodCategoryId_" + i, null));
        }
        return list;
    }

    //fetch shopping category ids from shared preference
    public List<String> getShoppingCategoryIdsFromShared(){
        List<String> list = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        for (int i = 0; i < sp.getInt("ShoppingCategoryIdSize", 0); i++){
            //get each shopping category id
            list.add(sp.getString("ShoppingCategoryId_" + i, null));
        }
        return list;
    }

    //fetch placeToGo category ids from shared preference
    public List<String> getPlaceToGoCategoryIdsFromShared(){
        List<String> list = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("CATEGORIES", Context.MODE_PRIVATE);
        for (int i = 0; i < sp.getInt("PlaceToGoCategoryIdSize", 0); i++){
            //get each placeToGo category id
            list.add(sp.getString("PlaceToGoCategoryId_" + i, null));
        }
        return list;
    }

    //put data into adapter and show in recycler view
    public void showData(int numberOfShownItems, List<FoursquarePlace> list){
        if (list.size() - numberOfShownItems > 15){  //if there are more than 15 un-shown items
            for (int i = numberOfShownItems; i < numberOfShownItems + 15; i++){
                fps.add(list.get(i));  //only show 15 items once
            }
            this.numberOfShownItems += 15;  //15 additional items has been shown
            numberOfRemainingItems = list.size() - this.numberOfShownItems;  //fewer items remaining
        }
        else{    //if there are less than or equal to 15 un-shown items
            for (int i = numberOfShownItems; i < list.size(); i++){
                fps.add(list.get(i));  //show all of them
            }
            this.numberOfShownItems = list.size();  //all items have been shown
            numberOfRemainingItems = 0;  //0 item remaining
        }
    }

    //filter data with user's personalized choice
    public List<FoursquarePlace> filterData(String status){
        List<FoursquarePlace> newList = new ArrayList<>();
        List<FoursquarePlace> all = new ArrayList<>();
        List<FoursquarePlace> foods = new ArrayList<>(fpsFood);
        List<FoursquarePlace> shoppingMall = new ArrayList<>(fpsShoppingMall);
        List<FoursquarePlace> shoppingPlaza = new ArrayList<>(fpsShoppingPlaza);
        List<FoursquarePlace> places = new ArrayList<>(fpsPlaceToGo);
        List<FoursquarePlace> searched = new ArrayList<>(fpsSearched);  //user searched result
        //the user doesn't search anything, so provide him recommended items
        if (searchView.getQuery().toString().equals("")
                || searchView.getQuery().toString().equals(null)){
            all.addAll(foods);
            all.addAll(shoppingMall);
            all.addAll(shoppingPlaza);
            all.addAll(places);
        }
        //the user has searched a certain thing, so provide him searched items
        else{
            all.addAll(searched);
        }
        String[] s = status.split(",");
        if (s[0].equals("0") && s[1].equals("0") && s[2].equals("0")){  //no filter choice
            newList.addAll(all);
        }
        else{
            for (int i = 0; i < all.size(); i++){  //iterate every fp
                for(Iterator<Map.Entry<String, String>> it =
                    all.get(i).getCategories().entrySet().iterator();
                    it.hasNext(); ){  //iterate the HashMap getCategories()
                    Map.Entry<String, String> entry = it.next();
                    if (s[0].equals("1")){  //the user has chosen the filter "food"
                        for (int j = 0; j < categoriesIds.get(0).size(); j++){
                            if (entry.getKey().equals(categoriesIds.get(0).get(j))){  //if matched
                                newList.add(all.get(i));  //add the category-matched fp
                            }
                        }
                    }
                    if (s[1].equals("1")){  //the user has chosen the filter "shopping"
                        for (int j = 0; j < categoriesIds.get(1).size(); j++){
                            if (entry.getKey().equals(categoriesIds.get(1).get(j))){  //if matched
                                newList.add(all.get(i));  //add the category-matched fp
                            }
                        }
                    }
                    if (s[2].equals("1")){  //the user has chosen the filter "placeToGo"
                        for (int j = 0; j < categoriesIds.get(2).size(); j++){
                            if (entry.getKey().equals(categoriesIds.get(2).get(j))){  //if matched
                                newList.add(all.get(i));  //add the category-matched fp
                            }
                        }
                    }
                }
            }
        }
        return newList;
    }

    //get sort type by determining which small button is clicked, in order to update sorted data
    public List<FoursquarePlace> getSortType(){
        List<FoursquarePlace> list = new ArrayList<>();
        //condition that the button popularity is clicked
        if(popularity.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            list = sortDataByPopularity(filterData(getBigButtonStatus()));
        }
        //condition that the button rating is clicked
        if(rating.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            if(rating.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(1).getConstantState())){
                list = sortDataByRating(filterData(getBigButtonStatus()));
            }
            else if(rating.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(2).getConstantState())){
                list = sortDataByRatingReverse(filterData(getBigButtonStatus()));
            }
        }
        //condition that the button distance is clicked
        if(distance.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            if(distance.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(1).getConstantState())){
                list = sortDataByDistance(filterData(getBigButtonStatus()));
            }
            else if(distance.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(2).getConstantState())){
                list = sortDataByDistanceReverse(filterData(getBigButtonStatus()));
            }
        }
        //condition that the button price is clicked
        if(price.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            if(price.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(1).getConstantState())){
                list = sortDataByPriceLevel(filterData(getBigButtonStatus()));
            }
            else if(price.getCompoundDrawables()[2].getConstantState().equals(
                    setSmallButtonDrawables(2).getConstantState())){
                list = sortDataByPriceLevelReverse(filterData(getBigButtonStatus()));
            }
        }
        //condition that the button openNow is clicked
        if(openNow.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            list = sortDataByOpenNow(filterData(getBigButtonStatus()));
        }
        return list;
    }

    //sort data by popularity from highest to lowest
    public List<FoursquarePlace> sortDataByPopularity(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(list.get(i));  //get all foursquare place together
        }
        //sort all data by popularity
        Collections.sort(newList, new Comparator<FoursquarePlace>() {
            @Override
            public int compare(FoursquarePlace fp_1, FoursquarePlace fp_2)
            {
                return Float.compare(fp_1.getCheckinsCount(), fp_2.getCheckinsCount());
            }
        });
        Collections.reverse(newList);
        return newList;
    }

    //sort data by rating from highest to lowest
    public List<FoursquarePlace> sortDataByRatingReverse(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(list.get(i));  //get all foursquare place together
        }
        //sort all data by rating
        Collections.sort(newList, new Comparator<FoursquarePlace>() {
            @Override
            public int compare(FoursquarePlace fp_1, FoursquarePlace fp_2)
            {
                return Float.compare(fp_1.getRating(), fp_2.getRating());
            }
        });
        return newList;
    }

    //sort data by rating from lowest to highest
    public List<FoursquarePlace> sortDataByRating(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = sortDataByRatingReverse(list);
        Collections.reverse(newList);
        return newList;
    }

    //sort data by distance from nearest to farthest
    public List<FoursquarePlace> sortDataByDistance(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(list.get(i));  //get all foursquare place together
        }
        //sort all data by distance
        Collections.sort(newList, new Comparator<FoursquarePlace>() {
            @Override
            public int compare(FoursquarePlace fp_1, FoursquarePlace fp_2)
            {
                return Float.compare(fp_1.getDistance(), fp_2.getDistance());
            }
        });
        return newList;
    }

    //sort data by distance from farthest to nearest
    public List<FoursquarePlace> sortDataByDistanceReverse(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = sortDataByDistance(list);
        Collections.reverse(newList);
        return newList;
    }

    //sort data by price level from lowest to highest
    public List<FoursquarePlace> sortDataByPriceLevel(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            newList.add(list.get(i));  //get all foursquare place together
        }
        //sort all data by price level
        Collections.sort(newList, new Comparator<FoursquarePlace>() {
            @Override
            public int compare(FoursquarePlace fp_1, FoursquarePlace fp_2)
            {
                return Float.compare(fp_1.getPriceLevel(), fp_2.getPriceLevel());
            }
        });
        return newList;
    }

    //sort data by price level from highest to lowest
    public List<FoursquarePlace> sortDataByPriceLevelReverse(List<FoursquarePlace> list){
        List<FoursquarePlace> newList = sortDataByPriceLevel(list);
        Collections.reverse(newList);
        return newList;
    }

    //sort data by current open status of each venues
    public List<FoursquarePlace> sortDataByOpenNow(List<FoursquarePlace> list){
        List<FoursquarePlace> all = sortDataByPopularity(list);
        List<FoursquarePlace> newList = new ArrayList<>();
        List<FoursquarePlace> opening = new ArrayList<>();
        List<FoursquarePlace> closed = new ArrayList<>();
        List<FoursquarePlace> unknown = new ArrayList<>();
        for (int i = 0; i < all.size(); i++){
            if (all.get(i).getOpenStatus() == 1){  //condition that the venue is opening
                opening.add(all.get(i));
            }
            else if (all.get(i).getOpenStatus() == 2){  //condition that the venue is now closed
                closed.add(all.get(i));
            }
            else{  //condition that the open status of this venue is undefined
                unknown.add(all.get(i));
            }
        }
        newList.addAll(opening);
        newList.addAll(closed);
        newList.addAll(unknown);
        return newList;
    }

    //shows loading notice to user
    public void loadingNotice(){
        Toast toast;
        if (adapter.getItemCount() == 0){  //there is no data item in DataViewAdapter
            toast = Toast.makeText(getApplicationContext(),
                    "Updating failed, please try again. ", Toast.LENGTH_SHORT);
        }
        else {
            toast = Toast.makeText(getApplicationContext(),
                    "Data updating completed!", Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.TOP, 0, 500);
        toast.show();
    }

    //initialize the item adapter after page opening or refreshing
    public void initializeAdapter(){
        adapter = new DataViewAdapter(fps, this);  //bind foursquare place data and view adapter
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);  //set layout manager
        recyclerView.setAdapter(adapter);  //set adapter
        if (fps.size() >= 15){  //need to show more items
            adapter.showFootView();  //show foot view
        }
        else{  //no need to show more items
            adapter.hideFootView();  //hide foot view
        }
        //condition that one of the last three item has been clicked
        //need to hide foot view, otherwise it will keep showing
        if (userChosenListPosition == adapter.getItemCount() - 2
                || userChosenListPosition == adapter.getItemCount() - 3
                || userChosenListPosition == adapter.getItemCount() - 4){
            adapter.hideFootView();  //hide foot view
        }
        //set click listener to each item view
        adapter.setDataViewOnClickListener(new DataViewAdapter.DataViewOnClickListener() {
            @Override
            public void onItemClick(View view, FoursquarePlace fp) {
                SharedPreferences sp = getSharedPreferences("DATA", Context.MODE_PRIVATE);
                Gson gson = new Gson();
                String json = gson.toJson(fp);  //parse FoursquarePlace fp to json
                //save fp as json format
                sp.edit().putString("ChosenFoursquarePlace", json).apply();
                sp.edit().putBoolean("List", true).apply();
                Intent intent = new Intent(ListActivity.this, DetailedInformationActivity.class);
                startActivity(intent);
                //make activity transition effect fade in and fade out
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    //get clicked or unclicked status of three big buttons
    public String getBigButtonStatus(){
        String foodStatus, shoppingStatus, placeToGoStatus;
        String bigButtonStatus = "";
        if(this.food.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite))
            { foodStatus = "1"; }
        else{ foodStatus = "0"; }
        if(this.shopping.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite))
            { shoppingStatus = "1"; }
        else{ shoppingStatus = "0"; }
        if(this.placeToGo.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite))
            { placeToGoStatus = "1"; }
        else{ placeToGoStatus = "0"; }
        bigButtonStatus += foodStatus;
        bigButtonStatus += "," + shoppingStatus;
        bigButtonStatus += "," + placeToGoStatus;
        return bigButtonStatus;
    }

    //if click big button, add or remove button effect
    //also include icon style inside big button
    public void setBigButtonEffect(Button b){
        //condition that button b is clicked
        if(b.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite)){
            //change button background to button_shape
            b.setBackgroundResource(R.drawable.button_shape);
            //change text color to white
            b.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            //change icon to un-click style
            b.setCompoundDrawables(setBigButtonDrawables(b, 0), null, null, null);
        }
        //button b is un-clicked
        else{
            //change button background to button_onclick_shape
            b.setBackgroundResource(R.drawable.button_onclick_shape);
            //change text color to primaryLight2
            b.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryGrey));
            //change icon to on-click style
            b.setCompoundDrawables(setBigButtonDrawables(b, 1), null, null, null);
        }
    }

    //set big button drawable icons
    public Drawable setBigButtonDrawables(Button b, int status){
        Drawable setbigbuttonIcon = null;
        if (status == 0){  //condition un-click
            if (b == food){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.food);
            }
            else if (b == shopping){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.shopping);
            }
            else if (b == placeToGo){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.place_togo);
            }
        }
        else if (status == 1){  //condition click
            if (b == food){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.food_clicked);
            }
            else if (b == shopping){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.shopping_clicked);
            }
            else if (b == placeToGo){
                setbigbuttonIcon = ContextCompat.getDrawable(this, R.mipmap.place_togo_clicked);
            }
        }
        setbigbuttonIcon.setBounds(0, 0, 150, 150);
        return setbigbuttonIcon;
    }

    //get clicked or unclicked status of six small buttons
    public String getSmallButtonStatus(){
        String popularityStatus, ratingStatus, distanceStatus, priceStatus, likedStatus, openNowStatus;
        String smallButtonStatus = "";
        if(this.popularity.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite))
            { popularityStatus = "1"; }
        else{ popularityStatus = "0"; }
        smallButtonStatus += popularityStatus;
        if (this.rating.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(0).getConstantState())) { ratingStatus = "0"; }
        else if (this.rating.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(1).getConstantState())){ ratingStatus = "1"; }
        else{ ratingStatus = "2"; }
        smallButtonStatus += "," + ratingStatus;
        if (this.distance.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(0).getConstantState())) { distanceStatus = "0"; }
        else if (this.distance.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(1).getConstantState())){ distanceStatus = "1"; }
        else{ distanceStatus = "2"; }
        smallButtonStatus += "," + distanceStatus;
        if (this.price.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(0).getConstantState())) { priceStatus = "0"; }
        else if (this.price.getCompoundDrawables()[2].getConstantState()
                .equals(setSmallButtonDrawables(1).getConstantState())){ priceStatus = "1"; }
        else{ priceStatus = "2"; }
        smallButtonStatus += "," + priceStatus;
        if(this.openNow.getCurrentTextColor() != ContextCompat.getColor(this, R.color.colorWhite))
            { openNowStatus = "1"; }
        else{ openNowStatus = "0"; }
        smallButtonStatus += "," + openNowStatus;
        return smallButtonStatus;
    }

    //if click small button, add button effect and remove other buttons' effect
    public void setSmallButtonEffect(Button b){
        List<Button> bs = new ArrayList<>();
        bs.add(popularity);
        bs.add(rating);
        bs.add(distance);
        bs.add(price);
        bs.add(openNow);
        if(b.getTypeface() != Typeface.DEFAULT_BOLD){  //button b is un-clicked
            b.setTypeface(Typeface.DEFAULT_BOLD);  //make text style bold
            //change button background to button_onclick_shape
            b.setBackgroundResource(R.drawable.button_onclick_shape);
            //change text color to primaryLight2
            b.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryLightGrey2));
            if (b == popularity || b == openNow){
                b.setEnabled(false);  //set the button un-clickable
            }
            for (int i = 0; i < bs.size(); i++){
                //if another clicked button exists
                if(b.getTypeface() == Typeface.DEFAULT_BOLD && bs.get(i) != b){
                    bs.get(i).setTypeface(Typeface.DEFAULT);  //make text style not bold
                    //change button background to button_shape
                    bs.get(i).setBackgroundResource(R.drawable.button_shape);
                    //change text color to white
                    bs.get(i).setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    bs.get(i).setEnabled(true);  //set button clickable
                }
            }
        }
    }

    //the condition that a button includes icon such as rating, distance and price
    //when user click these buttons, the icon up or down should be lighted or un-lighted
    public void setUpOrDown(Button b){
        List<Button> bs = new ArrayList<>();
        bs.add(rating);
        bs.add(distance);
        bs.add(price);
        if(b.getCompoundDrawables()[2] == null){
            //condition that clicked the button that does not have up or down arrow
            for(int i = 0; i < bs.size(); i++){
                //reset button arrows
                bs.get(i).setCompoundDrawables(null, null, setSmallButtonDrawables(0), null);
            }
        }
        else{
            //condition that clicked the button that has up or down arrow
            //both up and down are un-lighted, or down is lighted
            //[2] stands for the right direction
            if (b.getCompoundDrawables()[2].getConstantState().equals(setSmallButtonDrawables(0)
                    .getConstantState()) || b.getCompoundDrawables()[2].getConstantState()
                    .equals(setSmallButtonDrawables(2).getConstantState())){
                //light the up icon
                b.setCompoundDrawables(null, null, setSmallButtonDrawables(1), null);
            }
            //up is lighted
            else if (b.getCompoundDrawables()[2].getConstantState()
                    .equals(setSmallButtonDrawables(1).getConstantState())) {
                //light the down icon
                b.setCompoundDrawables(null, null, setSmallButtonDrawables(2), null);
            }
            for(int i = 0; i < bs.size(); i++){
                if(b != bs.get(i)) {
                    //reset button arrows which are not been clicked this time
                    bs.get(i).setCompoundDrawables(null, null, setSmallButtonDrawables(0), null);
                }
            }
        }
    }

    //set small button drawable icon up_down
    public Drawable setSmallButtonDrawables(int status){
        Drawable addButtonUpAndDown = ContextCompat.getDrawable(this, R.mipmap.up_down);
        if (status == 0){
            //there is no change to d
        }
        else if (status == 1){
            addButtonUpAndDown = ContextCompat.getDrawable(this, R.mipmap.up);
        }
        else if (status == 2){
            addButtonUpAndDown = ContextCompat.getDrawable(this, R.mipmap.down);
        }
        addButtonUpAndDown.setBounds(0, 0, 50, 75);
        return addButtonUpAndDown;
    }
}
