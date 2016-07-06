package be.omnuzel.beatshare.controller.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

import be.omnuzel.beatshare.controller.tasks.LocationJSONTask;
import be.omnuzel.beatshare.model.Location;

public class Localizer {

    private LocationManager locationManager;
    private Context context;

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

            } else {
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

        String bestProvider = locationManager.getBestProvider(criteria, true);

//        locationManager.requestLocationUpdates();

        android.location.Location receivedLocation =
                locationManager.getLastKnownLocation(bestProvider);

        if (receivedLocation == null) {
            Toast.makeText(context, "Received location is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }
        Toast.makeText(context, receivedLocation.toString(), Toast.LENGTH_SHORT).show();

        double lat = receivedLocation.getLatitude();
        double lon = receivedLocation.getLongitude();

        LocationJSONTask locationJSONTask = new LocationJSONTask();
        locationJSONTask.execute(lat, lon);

        String json = "";

        try {
            json = locationJSONTask.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Location location = null;

        try {
            location = Location.hydrateFromJSON(lat, lon, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return location;
    }
}
