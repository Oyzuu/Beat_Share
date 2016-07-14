package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.Country;

public class CountryDAO implements DataAccessObject<Country> {

    public static String
    TABLE_NAME    = "country",

    COLUMN_ID     = "id",
    COLUMN_NAME   = "name",

    CREATE_TABLE  = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL UNIQUE)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME
    ),

    UPGRADE_TABLE = "DROP TABLE " + TABLE_NAME + " ; " + CREATE_TABLE;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Context        context;

    public CountryDAO(Context context) {
        this.context = context;
    }

    @Override
    public void open(int openTypeConstant) {
        databaseHelper = new DatabaseHelper(context);

        if (openTypeConstant == WRITABLE)
            db = databaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = databaseHelper.getReadableDatabase();
        Log.i("COUNTRYDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();
        Log.i("COUNTRYDAO", "Database closed");
    }

    @Override
    public long create(Country country) throws SQLiteException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, country.getName());

        long id = db.insertOrThrow(TABLE_NAME, null, cv);
        Log.i("COUNTRYDAO", "Country : " + country.getName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(long id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("COUNTRYDAO", "Country with id : " + id + " has been deleted");
    }

    @Override
    public Country get(long id) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }

        return null;
    }

    @Override
    public Country getFromCursor(Cursor cursor) {
        int    id   = cursor.getInt   (cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        Country country = new Country();

        country.setId(id);
        country.setName(name);

        return country;
    }

    @Override
    public ArrayList<Country> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_NAME};
        Cursor c           = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (c.getCount() > 0) {
            ArrayList<Country> countries = new ArrayList<>();

            c.moveToFirst();

            do {
                countries.add(getFromCursor(c));
            } while (c.moveToNext());

            c.close();

            return countries;
        }

        return null;
    }
}
