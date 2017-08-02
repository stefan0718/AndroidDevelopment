package project.myapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataProcessor {

    private Context context;
    private StringBuilder result;  //used to record returned data from php
    private StringBuilder data;
    private Date date;
    private User user;

    //construct DataProcessor with context
    public DataProcessor(Context c){
        this.context = c;
    }

    //read foursquare data via url request
    public String readDataFromFoursquare(String url){
        data = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try{
            URL u = new URL(url);
            urlConnection = (HttpURLConnection)u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                data.append(line);
            }
            br.close();
            isr.close();
        }
        catch (IOException e){
            e.getMessage();
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.getMessage();
        }
        finally {
            urlConnection.disconnect();  //disconnect url connection anyway
        }
        return data.toString();
    }

    //parse foursquare venue image url from json
    public List<String> parseFoursquareImageUrls(String data){
        List<String> imageUrls = new ArrayList<>();
        String prefix = "";
        String suffix = "";
        try{
            JSONObject jb = new JSONObject(data);
            JSONArray ja = jb.getJSONObject("response").getJSONObject("photos")
                    .getJSONArray("items");  //get photo items
            for(int i = 0; i < ja.length(); i++){
                if(!ja.getJSONObject(i).isNull("prefix")){
                    prefix = ja.getJSONObject(i).getString("prefix");  //get prefix
                }

                if(!ja.getJSONObject(i).isNull("suffix")){
                    suffix = ja.getJSONObject(i).getString("suffix");  //get suffix
                }
                String imageUrl = prefix + "original" + suffix;  //whole image url
                imageUrls.add(imageUrl);
            }
        }
        catch (JSONException e) {
            e.getMessage();
        }
        return imageUrls;
    }

    //parse json data of 4 foursquare categories: food, shoppingMall, shoppingPlaza, event
    public List<List<String>> parseFoursquareCategories(String data){
        List<List<String>> categoriesList = new ArrayList<>();
        List<String> foodList = new ArrayList<>();
        List<String> shoppingList = new ArrayList<>();
        List<String> eventList = new ArrayList<>();
        try{
            JSONObject jb = new JSONObject(data);
            JSONArray ja = jb.getJSONObject("response").getJSONArray("categories");  //get categories
            for (int i = 0; i < ja.length(); i++){
                if (ja.getJSONObject(i).getString("name").equals("Food")){
                    //get parent category "Food"
                    foodList.add(ja.getJSONObject(i).getString("id"));
                    if (!ja.getJSONObject(i).isNull("categories")) {
                        JSONArray jaSub = ja.getJSONObject(i).getJSONArray("categories");
                        for (int j = 0; j < jaSub.length(); j++){
                            //get 1st layer sub category. eg. "Asian Restaurant"
                            foodList.add(jaSub.getJSONObject(j).getString("id"));
                            if (!jaSub.getJSONObject(j).isNull("categories")){
                                JSONArray jaSubSub = jaSub.getJSONObject(j).getJSONArray("categories");
                                for (int k = 0; k < jaSubSub.length(); k++){
                                    //get 2nd layer sub category. eg. "Chinese Restaurant"
                                    foodList.add(jaSubSub.getJSONObject(k).getString("id"));
                                    if (!jaSubSub.getJSONObject(k).isNull("categories")){
                                        JSONArray jaSubSubSub = jaSubSub.
                                                getJSONObject(k).getJSONArray("categories");
                                        for (int l = 0; l < jaSubSubSub.length(); l++){
                                            //get 3rd layer sub category. eg. "Anhui Restaurant"
                                            foodList.add(jaSubSubSub.getJSONObject(l).getString("id"));
                                            if (!jaSubSubSub.getJSONObject(l).isNull("categories")){
                                                JSONArray jaSubSubSubSub = jaSubSubSub.
                                                        getJSONObject(l).getJSONArray("categories");
                                                for (int m = 0; m < jaSubSubSubSub.length(); m++){
                                                    //get 4th layer sub category.
                                                    //eg. "Acai House" from "Brazilian Restaurant"
                                                    foodList.add(jaSubSubSubSub.getJSONObject(m).
                                                            getString("id"));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if (ja.getJSONObject(i).getString("name").equals("Shop & Service")
                        && !ja.getJSONObject(i).isNull("categories")){
                    JSONArray jaSub = ja.getJSONObject(i).getJSONArray("categories");
                    for (int j = 0; j < jaSub.length(); j++) {
                        //get 1st layer sub category. eg. "Shopping Mall"
                        if (jaSub.getJSONObject(j).getString("name").equals("Shopping Mall")){
                            //get parent category "Shopping Mall"
                            shoppingList.add(jaSub.getJSONObject(j).getString("id"));
                        }
                        if (jaSub.getJSONObject(j).getString("name").equals("Shopping Plaza")){
                            //get parent category "Shopping Plaza"
                            shoppingList.add(jaSub.getJSONObject(j).getString("id"));
                        }
                    }
                }
                else if (ja.getJSONObject(i).getString("name").equals("Event")){
                    //get parent category "Event"
                    eventList.add(ja.getJSONObject(i).getString("id"));
                    if (!ja.getJSONObject(i).isNull("categories")) {
                        JSONArray jaSub = ja.getJSONObject(i).getJSONArray("categories");
                        for (int j = 0; j < jaSub.length(); j++){
                            //get 1st layer sub category. eg. "Christmas Market"
                            eventList.add(jaSub.getJSONObject(j).getString("id"));
                        }
                    }
                }
            }
            categoriesList.add(foodList);
            categoriesList.add(shoppingList);
            categoriesList.add(eventList);
        }
        catch (JSONException e) {
            e.getMessage();
        }
        return categoriesList;
    }

    //parse json data of foursquare place
    public List<FoursquarePlace> parseJsonData(String data){
        List<FoursquarePlace> fps = new ArrayList<>();
        try{
            JSONObject jb = new JSONObject(data);
            JSONArray ja = jb.getJSONObject("response").getJSONArray("groups").getJSONObject(0)
                    .getJSONArray("items");  //get items;
            for(int i = 0; i < ja.length(); i++){
                FoursquarePlace fp = new FoursquarePlace(null, "Unknown", null, 0, null, null, null,
                        null, 0, 0, 0, null, null, null, null, 0, 0, null, 0, false);
                if (!ja.getJSONObject(i).isNull("venue")){
                    JSONObject jbVenue = ja.getJSONObject(i).getJSONObject("venue");  //get venues
                    if (!jbVenue.isNull("id")){
                        fp.setId(jbVenue.getString("id"));  //set id
                    }
                    if (!jbVenue.isNull("name")){
                        fp.setName(jbVenue.getString("name"));  //set name
                    }
                    if (!jbVenue.isNull("contact")) {
                        JSONObject jbContact = jbVenue.getJSONObject("contact");  //get contact
                        if (!jbContact.isNull("phone")) {
                            //set phone number
                            fp.setPhone("0" + jbContact.getString("phone").substring(3, 6) + " "
                                    + jbContact.getString("phone").substring(6, 9) + " "
                                    + jbContact.getString("phone").substring(9));
                        }
                    }
                    if (!jbVenue.isNull("location")){
                        JSONObject jbLocation = jbVenue.getJSONObject("location");  //get location
                        if (!jbLocation.isNull("address")){
                            fp.setAddress(jbLocation.getString("address"));  //set address
                        }
                        if (!jbLocation.isNull("lat")){
                            //set latitude
                            fp.setLatitude(Double.parseDouble(jbLocation.getString("lat")));
                        }
                        if (!jbLocation.isNull("lng")){
                            //set longitude
                            fp.setLongitude(Double.parseDouble(jbLocation.getString("lng")));
                        }
                        if (!jbLocation.isNull("postalCode")){
                            //set postal code
                            fp.setPostalCode(jbLocation.getString("postalCode"));
                        }
                        if (!jbLocation.isNull("distance")){
                            //set distance
                            fp.setDistance(Float.parseFloat(jbLocation.getString("distance")));
                        }
                        if (!jbLocation.isNull("city")){
                            fp.setCity(jbLocation.getString("city"));  //set city
                        }
                        if (!jbLocation.isNull("state")){
                            fp.setState(jbLocation.getString("state"));  //set state
                        }
                    }
                    if (!jbVenue.isNull("categories")) {
                        //get categories
                        JSONArray jaCategories = jbVenue.getJSONArray("categories");
                        Map<String, String> categoryMap = new HashMap<>();
                        for (int j = 0; j < jaCategories.length(); j++) {
                            if (!jaCategories.getJSONObject(j).isNull("id")
                                    && !jaCategories.getJSONObject(j).isNull("name")) {
                                categoryMap.put(jaCategories.getJSONObject(j).getString("id"),
                                        jaCategories.getJSONObject(j).getString("name"));
                            }
                            fp.setCategories(categoryMap);  //save category id and name as hashmap
                        }
                    }
                    if (!jbVenue.isNull("stats")){
                        JSONObject jbStats = jbVenue.getJSONObject("stats");  //get stats
                        if (!jbStats.isNull("checkinsCount")){
                            //get checkinsCount number
                            fp.setCheckinsCount(Integer.parseInt(jbStats.getString("checkinsCount")));
                        }
                    }
                    if (!jbVenue.isNull("url")){
                        fp.setUrl(jbVenue.getString("url"));  //set venue's website
                    }
                    if (!jbVenue.isNull("price")){
                        JSONObject jbPrice = jbVenue.getJSONObject("price");  //get price
                        if (!jbPrice.isNull("tier")){
                            //set price level
                            fp.setPriceLevel(Integer.parseInt(jbPrice.getString("tier")));
                        }
                    }
                    if (jbVenue.isNull("price")){
                        fp.setPriceLevel(0);  //no price level
                    }
                    if (!jbVenue.isNull("rating")){
                        fp.setRating(Float.parseFloat(jbVenue.getString("rating")));  //set rating
                    }
                    if (!jbVenue.isNull("menu")){
                        JSONObject jbMenu = jbVenue.getJSONObject("menu");  //get menu
                        if (!jbMenu.isNull("url")){
                            fp.setMenuUrl(jbMenu.getString("url"));  //set menu url
                        }
                    }
                    if (!jbVenue.isNull("hours")){
                        JSONObject jbHours = jbVenue.getJSONObject("hours");  //get opening hours
                        if (!jbHours.isNull("status")){
                            fp.setOpeningHours(jbHours.getString("status"));  //set opening hours
                        }
                        if (!jbHours.isNull("isOpen")){
                            if (jbHours.getBoolean("isOpen")){
                                fp.setOpenStatus(1);  //1 means now open
                            }
                            else if (!jbHours.getBoolean("isOpen")){
                                fp.setOpenStatus(2);  //2 means closed
                            }
                        }
                        if (!jbHours.isNull("isLocalHoliday")){
                            //set if today is public holiday
                            fp.setIsPublicHoliday(jbHours.getBoolean("isLocalHoliday"));
                        }
                    }
                    if (!jbVenue.isNull("photos")){
                        JSONObject jbPhotos = jbVenue.getJSONObject("photos");
                        if (!jbPhotos.isNull("groups")){
                            JSONArray jaGroups = jbPhotos.getJSONArray("groups");
                            if (!jaGroups.getJSONObject(0).isNull("items")){
                                JSONArray jaItems = jaGroups.getJSONObject(0).getJSONArray("items");
                                String prefix = "";
                                String suffix = "";
                                String url = "";
                                List<String> l = new ArrayList<>();
                                if (!jaItems.getJSONObject(0).isNull("prefix")){
                                    prefix = jaItems.getJSONObject(0).getString("prefix");
                                }
                                if (!jaItems.getJSONObject(0).isNull("suffix")){
                                    suffix = jaItems.getJSONObject(0).getString("suffix");
                                }
                                url = prefix + "original" + suffix;
                                l.add(url);
                                fp.setPhotoUrl(l);  //set one photo url
                            }
                        }
                    }
                }
                fps.add(fp);
            }
        }
        catch (JSONException e) {
            e.getMessage();
        }
        return fps;
    }

    //parse the user data that got from php
    public User parseJson(String result){
        user = new User(null, null, null, null, null, null, null, null);
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jb = ja.getJSONObject(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!jb.isNull("name")){
                user.setName(jb.getString("name"));
            }
            if (!jb.isNull("email")){
                user.setEmail(jb.getString("email"));
            }
            if (!jb.isNull("password")){
                user.setPassword(jb.getString("password"));
            }
            if (!jb.isNull("phone")){
                user.setPhone(jb.getString("phone"));
            }
            if (!jb.isNull("gender")){
                user.setGender(jb.getString("gender"));
            }
            if (!jb.isNull("location")){
                user.setLocation(jb.getString("location"));
            }
            if (!jb.isNull("date_of_birth")){
                user.setDateOfBirth(dateFormat.parse(jb.getString("date_of_birth")));
            }
            if (!jb.isNull("signup_datetime")){
                user.setSignupDatetime(datetimeFormat.parse(jb.getString("signup_datetime")));
            }
        }
        catch (JSONException e) {
            e.getMessage();
        }
        catch (ParseException e) {
            e.getMessage();
        }
        return user;
    }

    //send json POST request to php and read result
    public void postDataToPhp(String parameters, String url){
        HttpURLConnection urlConnection = null;
        result = new StringBuilder();
        try{
            URL u = new URL(url);
            urlConnection = (HttpURLConnection)u.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");  //Http headers
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000);
            OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream());
            osw.write(parameters);
            osw.flush();
            osw.close();
            InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            br.close();
            isr.close();
        }
        catch (IOException e){
            e.getMessage();
        }
        finally {
            urlConnection.disconnect();  //disconnect url connection anyway
        }
    }

    //get returned data from php
    public String getData(){
        return result.toString();
    }

    //get current time (use this method to record every login time of every user)
    public String getDateTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = df.format(new Date());
        return s;
    }

    public Date parseDateTime(String s){
        try{
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.parse(s);
        }
        catch (ParseException e){
            e.getMessage();
        }
        return date;
    }

    //get current date that corresponds foursquare api request format
    public String getCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String s = df.format(new Date());
        return s;
    }
}
