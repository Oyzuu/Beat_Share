package be.omnuzel.beatshare.controller.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Return a JSON file from Google geocode API based on given coordinates
 */
public class LocationJSONTask extends AsyncTask<Double, String, String> {

    @Override
    protected String doInBackground(Double... coords) {
        HttpURLConnection connection;
        String json = "";

        @SuppressLint("DefaultLocale")
        String urlString = String.format(
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f",
                coords[0], coords[1]
        );

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream is = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder sb = new StringBuilder("");
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            is.close();
            reader.close();

            json = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}
