package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.model.User;

// TODO create an admin user

public class UserDAO implements DataAccessObject<User> {
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

    UPGRADE_TABLE    = "DROP TABLE " + TABLE_NAME + ";" + CREATE_TABLE,

    CREATE_USER_ROLE =
            "CREATE TABLE IF NOT EXISTS user_role(" +
                    "user_id INTEGER NOT NULL," +
                    "role_id INTEGER NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES user(id)," +
                    "FOREIGN KEY (role_id) REFERENCES role(id)" +
                    "PRIMARY KEY (user_id, role_id) " +
                    ")",
    UPGRADE_USER_ROLE = "DROP TABLE user_role ; " + CREATE_USER_ROLE;


    private SQLiteDatabase db;
    private DatabaseHelper DatabaseHelper;
    private Context context;
    private RoleDAO roleDAO;

    public UserDAO(Context context) {
        this.context = context;
        this.roleDAO = new RoleDAO(context);
    }

    @Override
    public void open(int openTypeConstant) {
        DatabaseHelper = new DatabaseHelper(context);
        if (openTypeConstant == WRITABLE)
            db = DatabaseHelper.getWritableDatabase();

        if (openTypeConstant == READABLE)
            db = DatabaseHelper.getReadableDatabase();

        Log.i("USERDAO", "Database open type : " + openTypeConstant);
    }

    @Override
    public void close() {
        db.close();
        DatabaseHelper.close();

        Log.i("USERDAO", "Database closed");
    }

    @Override
    public long create(User user) throws SQLiteConstraintException {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, user.getUserName());
        cv.put(COLUMN_EMAIL,    user.getEmail());
        cv.put(COLUMN_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_NAME, null, cv);
        Log.i("USERDAO", "User : " + user.getUserName() + " @ " + id);

        return id;
    }

    @Override
    public void delete(int id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("USERDAO", "User with id : " + id + " has been deleted");
    }

    @Override
    public User get(int id) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }
        else
            return null;
    }

    public User getByName(String userName) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_USERNAME + "='" + userName + "'",
                null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }
        else
            return null;
    }

    public User getByEmail(String email) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_USERNAME + "='" + email + "'",
                null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }
        else
            return null;
    }

    @Override
    public User getFromCursor(Cursor c) {
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

    @Override
    public ArrayList<User> getAll() {
        String[] selection = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PASSWORD};
        Cursor c           = db.query(TABLE_NAME, selection, null, null, null, null, null);

        if (c.getCount() > 0) {
            ArrayList<User> users = new ArrayList<>();

            c.moveToFirst();

            do {
                users.add(getFromCursor(c));
            } while (c.moveToNext());

            c.close();

            return users;
        }

        return null;
    }
}
