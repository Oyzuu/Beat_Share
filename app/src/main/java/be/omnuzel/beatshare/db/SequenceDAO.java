package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.Sequence;

public class SequenceDAO implements DataAccessObject<Sequence> {

    public static String
    TABLE_NAME         = "sequence",

    COLUMN_ID          = "id",
    COLUMN_NAME        = "name",
    COLUMN_JSON        = "json",

    CREATE_TABLE       = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_JSON
    ),

    UPGRADE_TABLE      = "DROP TABLE " + TABLE_NAME + " ; " + CREATE_TABLE;

    private SQLiteDatabase   db;
    private DatabaseHelper   databaseHelper;
    private Context          context;
    private LocationDAO      locationDAO;

    public SequenceDAO(Context context) {
        this.context = context;
        locationDAO  = new LocationDAO(context);
    }

    @Override
    public void open(int openTypeConstant) {
        databaseHelper = new DatabaseHelper(context);

        if (openTypeConstant == WRITABLE)
            db = databaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = databaseHelper.getReadableDatabase();
        Log.i("SEQUENCEDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();
        Log.i("SEQUENCEDAO", "Database closed");
    }

    @Override
    public long create(Sequence sequence) throws SQLiteException {
        ContentValues cv = new ContentValues();

        try {
            cv.put(COLUMN_NAME, sequence.getName());
            cv.put(COLUMN_JSON, sequence.toJSON());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        long id = db.insertOrThrow(TABLE_NAME, null, cv);

        Log.i("SEQUENCEDAO", "Sequence : " + sequence.getName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(long id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("SEQUENCEDAO", "Sequence with id : " + id + " has been deleted");
    }

    @Override
    public Sequence get(long id) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getFromCursor(cursor);
        }

        return null;
    }

    public long alreadyPossess(String sequenceName, String userName) {
        Sequence sequence = getByName(sequenceName);
        if (sequence != null && !getByName(sequenceName).getAuthor().equals(userName))
            return -1;

        return getByName(sequenceName).getId();
    }

    public Sequence getByName(String name) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_NAME + "='" + name + "'",
                null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getFromCursor(cursor);
        }

        return null;
    }

    @Override
    public Sequence getFromCursor(Cursor cursor) {
        int    id   = cursor.getInt   (cursor.getColumnIndex(COLUMN_ID));
        String json = cursor.getString(cursor.getColumnIndex(COLUMN_JSON));


        Sequence sequence = null;
        try {
            sequence = Sequence.fromJSON(json);
            sequence.setId(id);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return sequence;
    }

    @Override
    public ArrayList<Sequence> getAll() {
        return null;
    }
}
