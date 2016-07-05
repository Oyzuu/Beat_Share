package be.omnuzel.beatshare.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;

/**
 * DAO interface with must-have methods
 * @param <T> Type of the object for the DAO
 */
public interface DataAccessObject<T> {
    int WRITABLE = 1;
    int READABLE = 0;

    /**
     * Open the database and the DatabaseHelper in read or write
     * @param openTypeConstant WRITABLE or READABLE
     */
    void open(int openTypeConstant);

    /**
     * Close the database and the DatabaseHelper
     */
    void close();

    /**
     * Create a row in the table from your <T> object
     * @param t the object you want to insert
     * @return  indentifier of the table row
     * @throws  SQLiteConstraintException
     */
    long create(T t) throws SQLiteConstraintException;

    void delete(int id);

    T get(int id);

    T getFromCursor(Cursor cursor);

    ArrayList<T> getAll();
}
