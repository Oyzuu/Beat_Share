package be.omnuzel.beatshare.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import be.omnuzel.beatshare.controller.utils.ChocolateSaltyBalls;
import be.omnuzel.beatshare.model.Role;
import be.omnuzel.beatshare.model.User;

public class UserDAO implements DataAccessObject<User> {
    public static String
    TABLE_NAME        = "User",

    COLUMN_ID         = "id",
    COLUMN_USERNAME   = "firstName",
    COLUMN_EMAIL      = "email",
    COLUMN_PASSWORD   = "password",
    COLUMN_SALT       = "salt",

    CREATE_TABLE      = String.format(
            "CREATE TABLE IF NOT EXISTS %s(" +
                    "%s INTEGER PRIMARY KEY," +
                    "%s TEXT NOT NULL UNIQUE," +
                    "%s TEXT NOT NULL UNIQUE," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL)",
            TABLE_NAME,
            COLUMN_ID,
            COLUMN_USERNAME,
            COLUMN_EMAIL,
            COLUMN_PASSWORD,
            COLUMN_SALT),

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
    private Context        context;
    private RoleDAO        roleDAO;

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
    public long create(User user) throws SQLiteException {
        ContentValues       cv  = new ContentValues();
        ChocolateSaltyBalls csb = ChocolateSaltyBalls.getInstance();

        String salt            = csb.generateSalt();
        String hashedPassword  = "";

        try {
            hashedPassword  = csb.hash(user.getPassword() + salt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cv.put(COLUMN_USERNAME, user.getName());
        cv.put(COLUMN_EMAIL,    user.getEmail());
        cv.put(COLUMN_PASSWORD, hashedPassword);
        cv.put(COLUMN_SALT,     salt);

        long id = db.insertOrThrow(TABLE_NAME, null, cv);
        user.setId(id);

        Role role = new Role();
        role.setId(RoleDAO.MEMBER);
        role.setName("member");

        createUserRole(user, role);

        Log.i("USERDAO", "User : " + user.getName() + " @ " + id);

        return id;
    }

    /**
     * Create the administrator
     * @param db the database of your application
     */
    public static void createAdmin(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();

        User user = new User();
        user.setId      (1);
        user.setName    ("admin");
        user.setEmail   ("admin@admin.admin");
        user.setPassword("admin");

        ChocolateSaltyBalls csb = ChocolateSaltyBalls.getInstance();
        String salt             = csb.generateSalt();
        String hashedPassword   = "";

        try {
            hashedPassword  = csb.hash(user.getPassword() + salt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        cv.put(COLUMN_ID,       user.getId());
        cv.put(COLUMN_USERNAME, user.getName());
        cv.put(COLUMN_EMAIL,    user.getEmail());
        cv.put(COLUMN_PASSWORD, hashedPassword);
        cv.put(COLUMN_SALT,     salt);

        db.insert(TABLE_NAME, null, cv);

        cv = new ContentValues();

        cv.put("user_id", user.getId());
        cv.put("role_id", RoleDAO.ADMIN);

        db.insert("user_role", null, cv);

        Log.i("USERDAO", "Admin created");
    }

    /**
     * Insert a row "user_id", "role_id" in the link table user_role
     * @param user User to get an id from
     * @param role Role to get an id from
     */
    private void createUserRole(User user, Role role) {
        ContentValues cv = new ContentValues();

        cv.put("user_id", user.getId());
        cv.put("role_id", role.getId());

        db.insert("user_role", null, cv);
    }

    @Override
    public void delete(long id) {
        db.delete(TABLE_NAME, COLUMN_ID + "=" + id, null);
        Log.i("USERDAO", "User with id : " + id + " has been deleted");
    }

    @Override
    public User get(long id) {
        Cursor c = db.query(TABLE_NAME, null, COLUMN_ID + "=" + id, null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }

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
        Cursor c = db.query(TABLE_NAME, null, COLUMN_EMAIL + "='" + email + "'",
                null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            return getFromCursor(c);
        }
        else
            return null;
    }

    public String getSalt(User user) {
        Cursor c = db.query(TABLE_NAME, new String[]{COLUMN_SALT}, COLUMN_USERNAME + "='" +
                user.getName() + "'", null, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();

            return c.getString(c.getColumnIndex(COLUMN_SALT));
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

        ArrayList<Role> roles = getRolesFromId(id);

        User user = new User();
        user.setId      (id);
        user.setName(userName);
        user.setEmail   (email);
        user.setPassword(password);
        user.setRoles   (roles);

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

    private ArrayList<Role> getRolesFromId(long userId) {
        Cursor cursor = db.query("user_role", null, "user_id=" + userId, null, null, null, null);

        if (cursor.getCount() > 0) {
            ArrayList<Role> roles = new ArrayList<>();

            cursor.moveToFirst();

            do {
                int role_id = cursor.getInt(cursor.getColumnIndex("role_id"));

                roleDAO.open(READABLE);
                Role role = roleDAO.get(role_id);
                roleDAO.close();

                roles.add(role);
            } while (cursor.moveToNext());
            cursor.close();

            return roles;
        }

        return null;
    }

    public ArrayList<String> getAllUserRoles() {
        Cursor cursor = db.query("user_role", null, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            ArrayList<String> user_roles = new ArrayList<>();

            cursor.moveToFirst();

            do {
                int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                int role_id = cursor.getInt(cursor.getColumnIndex("role_id"));

                user_roles.add(user_id + " : " + role_id);
            } while (cursor.moveToNext());

            cursor.close();
            return user_roles;
        }

        return null;
    }
}
