package project.myapplication;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.os.IBinder;

public class Locator extends Service implements LocationListener{

    private final Context context;
    private LocationManager locationManager;
    private boolean gps_status = false;
    private boolean network_status = false;
    private boolean location_service_status = false;
    private Location location;
    private static double latitude;
    private static double longitude;

    public Locator(Context context){
        this.context = context;
        getLocation();
    }

    public Location getLocation(){
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        //get GPS status
        gps_status = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //get network status
        network_status = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        long min_meters = 10;  //minimum distance that need to update location
        long min_milliseconds = 5000;  //minimum time that need to update location
        try{
            if (network_status){  //commonly, network is faster than gps to get location
                location_service_status = true;
                if(gps_status) {  //if got gps location
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, min_milliseconds, min_meters, this);
                    //get last known location from the user's device
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null){
                        latitude = location.getLatitude();  //get latitude
                        longitude = location.getLongitude();  //get longitude
                    }
                }
                else{  //if gps hasn't got location yet, use network instead
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, min_milliseconds, min_meters, this);
                    //get last known location from the user's device
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null){
                        latitude = location.getLatitude();  //get latitude
                        longitude = location.getLongitude();  //get longitude
                    }
                }
            }
        }
        catch (SecurityException e){
            e.getMessage();
        }
    return location;
    }

    /*
    //stop using gps
    public void stopGPS() {
        try{
            locationManager.removeUpdates(Locator.this);
        }
        catch (SecurityException e){
            e.getMessage();
        }
    }*/

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Location Service Settings");    //set dialog title
        //set dialog message
        alertDialog.setMessage("Location service is not enabled. Do you want to open it in settings?");
        //set the button "Settings" if the user want to go to settings
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                    }
                });
        //set the button "Cancel" if the user doesn't want to go to settings
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();  //show this dialog
    }

    public boolean getLocationServiceStatus(){
        return this.location_service_status;
    }

    //get latitude
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //get longitude
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location){
        getLatitude();
        getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        location_service_status = false;
    }

    @Override
    public void onProviderEnabled(String provider) {
        location_service_status = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
