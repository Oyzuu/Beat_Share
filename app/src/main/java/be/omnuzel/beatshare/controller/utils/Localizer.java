package be.omnuzel.beatshare.controller.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import be.omnuzel.beatshare.controller.tasks.LocationJSONTask;
import be.omnuzel.beatshare.model.Location;

// TODO reorganize this mess --- IF TIME FOR IT
// TODO change this for Play Games Services
// https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderApi

public class Localizer {

    private LocationManager           locationManager;
    private Context                   context;
    private android.location.Location receivedLocation;
    private final LocationListener    locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            receivedLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public Localizer(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLocation(int accuracy) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.READ_CONTACTS)) {

            }
            else {
                ActivityCompat.requestPermissions((Activity)context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }

        Criteria criteria = new Criteria();

        criteria.setAccuracy(accuracy);
        criteria.setSpeedAccuracy(accuracy);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        String provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(provider, 10000, 500, locationListener);

        if (receivedLocation == null)
            receivedLocation = locationManager.getLastKnownLocation(provider);

        if (receivedLocation == null) {
            Toast.makeText(context, "Received location is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }

        double lat = receivedLocation.getLatitude();
        double lon = receivedLocation.getLongitude();

        LocationJSONTask locationJSONTask = new LocationJSONTask();
        locationJSONTask.execute(lat, lon);

        String json;
        Location location = null;

        try {
            json = locationJSONTask.get();
            location = Location.hydrateFromJSON(lat, lon, json);
        }
        catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return location;
    }
}
