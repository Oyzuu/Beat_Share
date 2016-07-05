package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.City;
import be.omnuzel.beatshare.model.Country;

public class CityDAO implements DataAccessObject<City> {

    public static String
    TABLE_NAME = "city",

    COLUMN_ID = "id",
    COLUMN_NAME = "name",
    COLUMN_COUNTRY_ID = "country_ID",

    CREATE_TABLE = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_COUNTRY_ID
    ),

    UPGRADE_TABLE = "DROP TABLE " + TABLE_NAME + " ; " + CREATE_TABLE;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Context        context;
    private CountryDAO     countryDAO;

    public CityDAO(Context context) {
        this.context = context;
        countryDAO   = new CountryDAO(context);
    }

    @Override
    public void open(int openTypeConstant) {
        databaseHelper = new DatabaseHelper(context);

        if (openTypeConstant == WRITABLE)
            db = databaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = databaseHelper.getReadableDatabase();
        Log.i("CITYDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();
        Log.i("CITYDAO", "Database closed");
    }

    @Override
    public long create(City city) throws SQLiteConstraintException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, city.getName());
        cv.put(COLUMN_COUNTRY_ID, city.getCountry().getId());

        long id = db.insert(TABLE_NAME, null, cv);
        Log.i("CITYDAO", "City : " + city.getName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("CITYDAO", "City with id : " + id + " has been deleted");
    }

    @Override
    public City get(int id) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }

        return null;
    }

    @Override
    public City getFromCursor(Cursor cursor) {
        int id         = cursor.getInt   (cursor.getColumnIndex(COLUMN_ID));
        String name    = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        int country_id = cursor.getInt   (cursor.getColumnIndex(COLUMN_COUNTRY_ID));

        countryDAO.open(READABLE);
        Country country = countryDAO.get(country_id);
        countryDAO.close();

        City city = new City();
        city.setId(id);
        city.setName(name);
        city.setCountry(country);

        return city;
    }

    @Override
    public ArrayList<City> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_NAME, COLUMN_COUNTRY_ID};
        Cursor c           = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (c.getCount() > 0) {
            ArrayList<City> cities = new ArrayList<>();

            c.moveToFirst();

            do {
                cities.add(getFromCursor(c));
            } while (c.moveToNext());

            c.close();

            return cities;
        }

        return null;
    }
}
