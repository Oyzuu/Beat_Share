package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import be.omnuzel.beatshare.classes.User;

// TODO Add UserDAO to helper
// TODO adapt methods to User

public class UserDAO {
    public static String
            TABLE_NAME        = "User",

            COLUMN_ID         = "id",
            COLUMN_USERNAME   = "firstName",
            COLUMN_EMAIL      = "email",
            COLUMN_PASSWORD   = "password",

            CREATE_TABLE      = String.format(
                    "create table if not exists %s(" +
                            "%s INTEGER PRIMARY KEY," +
                            "%s TEXT NOT NULL UNIQUE," +
                            "%s TEXT NOT NULL UNIQUE," +
                            "%s TEXT NOT NULL)",
                    TABLE_NAME,
                    COLUMN_ID,
                    COLUMN_USERNAME,
                    COLUMN_EMAIL,
                    COLUMN_PASSWORD),

            UPGRADE_TABLE    = "DROP TABLE " + TABLE_NAME + ";" + CREATE_TABLE;

    private SQLiteDatabase db;
    private DatabaseHelper DatabaseHelper;
    private Context context;

    public UserDAO(Context context) {
        this.context = context;
    }

    public UserDAO openWritable() {
        DatabaseHelper = new DatabaseHelper(context);
        db = DatabaseHelper.getWritableDatabase();
        return this;
    }

    public UserDAO openReadable() {
        DatabaseHelper = new DatabaseHelper(context);
        db = DatabaseHelper.getReadableDatabase();
        return this;
    }

    public void close() {
        db.close();
        DatabaseHelper.close();
    }

    public long createUser(User user) throws SQLiteConstraintException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, user.getUserName());
        cv.put(COLUMN_EMAIL,    user.getEmail   ());
        cv.put(COLUMN_PASSWORD, user.getPassword());

        return db.insert(TABLE_NAME, null, cv);
    }

    public void deleteUser(User user) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + user.getId(), null);
    }

    public User getUserById(int userId) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + userId, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getUserFromCursor(c);
        }
        else
            return null;
    }

    private User getUserFromCursor(Cursor c) {
        int    id       = c.getInt   (c.getColumnIndex(COLUMN_ID));
        String userName = c.getString(c.getColumnIndex(COLUMN_USERNAME));
        String email    = c.getString(c.getColumnIndex(COLUMN_EMAIL));
        String password = c.getString(c.getColumnIndex(COLUMN_PASSWORD));

        User user = new User();
        user.setId      (id);
        user.setUserName(userName);
        user.setEmail   (email);
        user.setPassword(password);

        return user;
    }

    public ArrayList<User> getUsers() {
        String[] selection = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL};
        Cursor c = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (c.getCount() > 0) {
            ArrayList<User> users = new ArrayList<>();

            c.moveToFirst();

            do {
                User user = new User();

                user.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
                user.setUserName(c.getString(c.getColumnIndex(COLUMN_USERNAME)));

                users.add(user);
            } while (c.moveToNext());

            return users;
        }
        else
            return null;
    }
}
