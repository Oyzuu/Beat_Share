package be.omnuzel.beatshare.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO !!! IMPORTANT !!! Sequence and SequenceDAO when able

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME    = "app.db";
    private static int    DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserDAO         .CREATE_TABLE);
        db.execSQL(UserDAO         .CREATE_USER_ROLE);
        db.execSQL(RoleDAO         .CREATE_TABLE);
        db.execSQL(RoleDAO         .INSERT_BASEROLES);
        db.execSQL(CountryDAO      .CREATE_TABLE);
        db.execSQL(CityDAO         .CREATE_TABLE);
        db.execSQL(NeighbourhoodDAO.CREATE_TABLE);
        db.execSQL(LocationDAO     .CREATE_TABLE);
        db.execSQL(SequenceDAO     .CREATE_TABLE);

        UserDAO.createAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UserDAO         .UPGRADE_TABLE);
        db.execSQL(UserDAO         .UPGRADE_USER_ROLE);
        db.execSQL(RoleDAO         .UPGRADE_TABLE);
        db.execSQL(CountryDAO      .UPGRADE_TABLE);
        db.execSQL(CityDAO         .UPGRADE_TABLE);
        db.execSQL(NeighbourhoodDAO.UPGRADE_TABLE);
        db.execSQL(LocationDAO     .UPGRADE_TABLE);
        db.execSQL(SequenceDAO     .UPGRADE_TABLE);

        onCreate(db);
    }
}
