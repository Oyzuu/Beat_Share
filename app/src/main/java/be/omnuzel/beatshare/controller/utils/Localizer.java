package be.omnuzel.beatshare.controller.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import be.omnuzel.beatshare.model.*;

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

        android.location.Location receivedLocation =
                locationManager.getLastKnownLocation(bestProvider);

        if (receivedLocation == null) {
            Toast.makeText(context, "Received location is NULL", Toast.LENGTH_SHORT).show();
            return null;
        }

        Location location = new be.omnuzel.beatshare.model.Location();

        location.setLatitude (receivedLocation.getLatitude());
        location.setLongitude(receivedLocation.getLongitude());

        return location;
    }
}
