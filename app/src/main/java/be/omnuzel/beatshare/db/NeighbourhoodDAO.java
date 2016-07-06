package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.City;
import be.omnuzel.beatshare.model.Neighbourhood;

public class NeighbourhoodDAO implements DataAccessObject<Neighbourhood> {

    public static String
    TABLE_NAME     = "neighbourhood",

    COLUMN_ID      = "id",
    COLUMN_NAME    = "name",
    COLUMN_CITY_ID = "city_id",

    CREATE_TABLE   = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s INTEGER NOT NULL)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_CITY_ID
    ),

    UPGRADE_TABLE  = "DROP TABLE " + TABLE_NAME + " ; " + CREATE_TABLE;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;
    private Context        context;
    private CityDAO        cityDAO;

    public NeighbourhoodDAO(Context context) {
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
        Log.i("NEIGHDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();
        Log.i("NEIGHDAO", "Database closed");
    }

    @Override
    public long create(Neighbourhood neighbourhood) throws SQLiteException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME,    neighbourhood.getName());
        cv.put(COLUMN_CITY_ID, neighbourhood.getCity().getId());

        long id = db.insertOrThrow(TABLE_NAME, null, cv);
        Log.i("NEIGHDAO", "Neighbourhood : " + neighbourhood.getName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("NEIGHDAO", "Neighbourhood with id : " + id + " has been deleted");
    }

    @Override
    public Neighbourhood get(int id) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getFromCursor(cursor);
        }

        return null;
    }

    @Override
    public Neighbourhood getFromCursor(Cursor cursor) {
        int id      = cursor.getInt   (cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        int city_id = cursor.getInt   (cursor.getColumnIndex(COLUMN_CITY_ID));

        cityDAO.open(READABLE);
        City city = cityDAO.get(city_id);
        cityDAO.close();

        Neighbourhood neighbourhood = new Neighbourhood();

        neighbourhood.setId(id);
        neighbourhood.setName(name);
        neighbourhood.setCity(city);

        return neighbourhood;
    }

    @Override
    public ArrayList<Neighbourhood> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_NAME, COLUMN_CITY_ID};
        Cursor c           = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (c.getCount() > 0) {
            ArrayList<Neighbourhood> neighbourhoods = new ArrayList<>();

            c.moveToFirst();

            do {
                neighbourhoods.add(getFromCursor(c));
            } while (c.moveToNext());

            c.close();

            return neighbourhoods;
        }

        return null;
    }
}













