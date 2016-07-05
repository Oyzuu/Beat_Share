package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.Role;

public class RoleDAO implements DataAccessObject<Role> {

    public static String
    TABLE_NAME    = "role",

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
    private Context context;

    public RoleDAO(Context context) {
        this.context = context;
    }

    @Override
    public void open(int openTypeConstant) {
        databaseHelper = new DatabaseHelper(context);
        if (openTypeConstant == WRITABLE)
            db = databaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = databaseHelper.getReadableDatabase();

        Log.i("ROLEDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        databaseHelper.close();

        Log.i("ROLEDAO", "Database closed");
    }

    @Override
    public long create(Role role) throws SQLiteConstraintException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, role.getName());

        long id = db.insert(TABLE_NAME, null, cv);
        Log.i("ROLEDAO", "Role : " + role.getName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("ROLEDAO", "Role with id : " + id + " has been deleted");
    }

    @Override
    public Role get(int id) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id,
                null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }
        else
            return null;
    }

    @Override
    public Role getFromCursor(Cursor cursor) {
        int id      = cursor.getInt   (cursor.getColumnIndex(COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));

        Role role = new Role();

        role.setId  (id);
        role.setName(name);

        return role;
    }

    @Override
    public ArrayList<Role> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_NAME};
        Cursor cursor      = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            ArrayList<Role> roles = new ArrayList<>();

            cursor.moveToFirst();

            do {
                roles.add(getFromCursor(cursor));
            } while (cursor.moveToNext());

            cursor.close();

            return roles;
        }

        return null;
    }
}
