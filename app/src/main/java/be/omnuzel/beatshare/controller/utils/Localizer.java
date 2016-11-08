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
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import be.omnuzel.beatshare.controller.tasks.LocationJSONTask;
import be.omnuzel.beatshare.model.Location;

public class Localizer {

    private final LocationManager locationManager;
    private final Context context;
    private android.location.Location receivedLocation;
    private final LocationListener locationListener;

    public Localizer(Context context) {
        Log.i("LOCALIZER", "in constructor");
        this.context = context;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
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
    }

    /**
     * Get a hydrated location with a JSON from Google geocode API
     *
     * @param accuracy accuracy constant (Criteria.ACCURACY_FINE or Criteria.ACCURACY_COARSE)
     * @return a fully hydrated be.omnuzel.beatshare.model.Location
     */
    public Location getLocation(int accuracy) {
        Log.i("LOCALIZER", "in getLocation()");
        double[] coords = getCoords(accuracy);
        double lat = 0, lon = 0;

        if (coords != null) {
            lat = coords[0];
            lon = coords[1];
        }

        LocationJSONTask locationJSONTask = new LocationJSONTask();
        locationJSONTask.execute(lat, lon);

        String json;
        Location location = null;

        try {
            json = locationJSONTask.get();
            Log.i("LOCALIZER", "Json : " + json);
            location = Location.fromJSON(lat, lon, json);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("LOCALIZER", "Location : " + (location != null ? location.toString() : null));

        return location;
    }

    public Location getMockLocation() {
        Log.i("LOCALIZER", "in getMockLocation()");
        double
                lat = 50.837722469761964,
                lon = 4.353536367416382;

        LocationJSONTask locationJSONTask = new LocationJSONTask();
        locationJSONTask.execute(lat, lon);

        String json;
        Location location = null;

        try {
            json = locationJSONTask.get();
            Log.i("LOCALIZER", "Json : " + json);
            location = Location.fromJSON(lat, lon, json);
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("LOCALIZER", "Location : " + (location != null ? location.toString() : null));

        return location;
    }

    /**
     * Get coordinates from the location manager
     *
     * @param accuracy accuracy constant (Criteria.ACCURACY_FINE or Criteria.ACCURACY_COARSE)
     * @return coordinates in a double array
     */
    private double[] getCoords(int accuracy) {
        Log.i("LOCALIZER", "in getCoords()");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }

        String provider = locationManager.getBestProvider(getCriteria(accuracy), true);

        locationManager.requestLocationUpdates(provider, 5000, 100, locationListener);

        if (receivedLocation == null)
            receivedLocation = locationManager.getLastKnownLocation(provider);

        if (receivedLocation == null) {
            Toast.makeText(context, "Received location is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }

        double lat = receivedLocation.getLatitude();
        double lon = receivedLocation.getLongitude();

        double[] coords = {lat, lon};

        Log.i("LOCALIZER", "coords : " + Arrays.toString(coords));
        return coords;
    }

    /**
     * Return a default criteria given the accuracy parameter
     *
     * @param accuracy accuracy constant (Criteria.ACCURACY_FINE or Criteria.ACCURACY_COARSE)
     * @return Criteria
     */
    private Criteria getCriteria(int accuracy) {
        Log.i("LOCALIZER", "in getCriteria()");
        Criteria criteria = new Criteria();

        criteria.setAccuracy(accuracy);
        criteria.setSpeedAccuracy(accuracy);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

        Log.i("LOCALIZER", "criteria : " + criteria.toString());
        return criteria;
    }
}
