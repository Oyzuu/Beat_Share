package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.City;
import be.omnuzel.beatshare.model.Location;

public class LocationDAO implements DataAccessObject<Location> {

    public static String
    TABLE_NAME       = "location",

    COLUMN_ID        = "id",
    COLUMN_LATITUDE  = "latitude",
    COLUMN_LONGITUDE = "longitude",
    COLUMN_CITY_ID   = "city_id",

    CREATE_TABLE     = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s REAL NOT NULL," +
                    "%s REAL NOT NULL," +
                    "%s INTEGER NOT NULL)",
            TABLE_NAME, COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_CITY_ID
    ),

    UPGRADE_TABLE    = "DROP TABLE " + TABLE_NAME + " ; " + CREATE_TABLE;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Context        context;
    private CityDAO        cityDAO;

    public LocationDAO(Context context) {
        this.context = context;
        cityDAO      = new CityDAO(context);
    }

    @Override
    public void open(int openTypeConstant) {
        databaseHelper = new DatabaseHelper(context);

        if (openTypeConstant == WRITABLE)
            db = databaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = databaseHelper.getReadableDatabase();
        Log.i("LOCATIONDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();
        Log.i("LOCATIONDAO", "Database closed");
    }

    @Override
    public long create(Location location) throws SQLiteException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LATITUDE,  location.getLatitude());
        cv.put(COLUMN_LONGITUDE, location.getLongitude());
        cv.put(COLUMN_CITY_ID,   location.getCity().getId());

        long id = db.insertOrThrow(TABLE_NAME, null, cv);
        double latitude  = location.getLatitude();
        double longitude = location.getLongitude();
        Log.i("LOCATIONDAO", "Location : " + latitude + ", " + longitude + " @ " + id);

        return id;
    }

    @Override
    public void delete(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("LOCATIONDAO", "Location with id : " + id + " has been deleted");
    }

    @Override
    public Location get(int id) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getFromCursor(cursor);
        }

        return null;
    }

    @Override
    public Location getFromCursor(Cursor cursor) {
        int id           = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        double latitude  = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE));
        int city_id      = cursor.getInt(cursor.getColumnIndex(COLUMN_CITY_ID));

        cityDAO.open(READABLE);
        City city = cityDAO.get(city_id);
        cityDAO.close();

        Location location = new Location();
        location.setId(id);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setCity(city);

        return location;
    }

    @Override
    public ArrayList<Location> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_CITY_ID};
        Cursor cursor      = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            ArrayList<Location> locations = new ArrayList<>();

            cursor.moveToFirst();

            do {
                locations.add(getFromCursor(cursor));
            } while (cursor.moveToNext());

            cursor.close();

            return locations;
        }

        return null;
    }
}
