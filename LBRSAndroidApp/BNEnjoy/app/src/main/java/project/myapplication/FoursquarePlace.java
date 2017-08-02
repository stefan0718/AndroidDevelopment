package project.myapplication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoursquarePlace implements Serializable {
    private String id, name, address, city, state, postalCode, url, menuUrl, phone, openingHours;
    private Map<String, String> categories = new HashMap<>();
    private List<String> photoUrls;
    private float distance, rating;
    private double latitude, longitude;
    private int checkinsCount, priceLevel, isOpen;
    private boolean isPublicHoliday;

    //construct a new foursquareplace type
    public FoursquarePlace(String id, String name, Map<String, String> categories, float distance,
                           String address, String city, String state, String postalCode,
                           double latitude, double longitude, int checkinsCount, String url,
                           List<String> photoUrls, String menuUrl, String phone, int priceLevel,
                           float rating, String openingHours, int isOpen, boolean isPublicHoliday){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.distance = distance;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.checkinsCount = checkinsCount;
        this.url = url;
        this.photoUrls = photoUrls;
        this.menuUrl = menuUrl;
        this.phone = phone;
        this.priceLevel = priceLevel;
        this.rating = rating;
        this.openingHours = openingHours;
        this.isOpen = isOpen;
        this.isPublicHoliday = isPublicHoliday;
    }

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name; }

    public Map<String, String> getCategories(){ return categories; }

    public void setCategories(Map<String, String> categories){ this.categories = categories; }

    public float getDistance(){ return distance; }

    public void setDistance(float distance) { this.distance = distance; }

    public String getLocation(){ return address + ", " + city + " " + state + " " + postalCode; }

    public String getAddress(){ return address; }

    public void setAddress(String address){ this.address = address; }

    public String getCity(){ return city; }

    public void setCity(String city){ this.city = city; }

    public String getState(){ return state; }

    public void setState(String state){ this.state = state; }

    public String getPostalCode(){ return postalCode; }

    public void setPostalCode(String postalCode){ this.postalCode = postalCode; }

    public double getLatitude(){ return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude(){ return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public int getCheckinsCount(){ return checkinsCount; }

    public void setCheckinsCount(int checkinsCount) {this.checkinsCount = checkinsCount; }

    public String getUrl(){ return url; }

    public void setUrl(String url){ this.url = url; }

    public List<String> getPhotoUrls(){ return photoUrls; }

    public void setPhotoUrl(List<String> photoUrls){ this.photoUrls = photoUrls; }

    public String getMenuUrl(){ return menuUrl; }

    public void setMenuUrl(String menuUrl){ this.menuUrl = menuUrl; }

    public String getPhone(){ return phone; }

    public void setPhone(String phone){ this.phone = phone; }

    public int getPriceLevel(){ return priceLevel; }

    public void setPriceLevel(int priceLevel){ this.priceLevel = priceLevel; }

    public float getRating(){ return rating; }

    public void setRating(float rating){ this.rating = rating; }

    public String getOpeningHours(){ return openingHours; }

    public void setOpeningHours(String openingHours){ this.openingHours = openingHours; }

    public int getOpenStatus(){ return isOpen; }

    public void setOpenStatus(int isOpen){ this.isOpen = isOpen; }

    public boolean getIsPublicHoliday(){ return isPublicHoliday; }

    public void setIsPublicHoliday(boolean isPublicHoliday){ this.isPublicHoliday = isPublicHoliday; }
}
