package be.omnuzel.beatshare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import be.omnuzel.beatshare.model.City;
import be.omnuzel.beatshare.model.Country;

// TODO create all the DAOs !

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME    = "app.db";
    private static int    DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO    .CREATE_TABLE);
        db.execSQL(RoleDAO    .CREATE_TABLE);
        db.execSQL(CountryDAO .CREATE_TABLE);
        db.execSQL(CityDAO    .CREATE_TABLE);
        db.execSQL(LocationDAO.CREATE_TABLE);

        db.execSQL(UserDAO    .CREATE_USER_ROLE);
        db.execSQL(RoleDAO    .INSERT_BASEROLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDAO    .UPGRADE_TABLE);
        db.execSQL(RoleDAO    .UPGRADE_TABLE);
        db.execSQL(CountryDAO .UPGRADE_TABLE);
        db.execSQL(CityDAO    .UPGRADE_TABLE);
        db.execSQL(LocationDAO.UPGRADE_TABLE);

        db.execSQL(UserDAO    .UPGRADE_USER_ROLE);
        db.execSQL(RoleDAO    .INSERT_BASEROLES);

        onCreate(db);
    }
}
