package be.omnuzel.beatshare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO create RoleDAO and the corresponding link table USER_ROLE
// TODO create all the DAOs !

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME    = "app.db";
    private static int    DB_VERSION = 1;

    private String
    CREATE_USER_ROLE =
            "CREATE TABLE IF NOT EXISTS user_role(" +
                    "user_id INTEGER NOT NULL," +
                    "role_id INTEGER NOT NULL," +
                    "FOREIGN KEY (user_id) REFERENCES user(id)," +
                    "FOREIGN KEY (role_id) REFERENCES role(id)" +
                    ")",
    UPGRADE_USER_ROLE = "DROP TABLE user_role ; " + CREATE_USER_ROLE;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(RoleDAO.CREATE_TABLE);
        db.execSQL(CREATE_USER_ROLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDAO.UPGRADE_TABLE);
        db.execSQL(RoleDAO.UPGRADE_TABLE);
        db.execSQL(UPGRADE_USER_ROLE);
        onCreate(db);
    }
}
