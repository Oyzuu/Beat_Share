package be.omnuzel.beatshare.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import java.util.ArrayList;

public interface DataAccessObject<T> {
    int WRITABLE = 1;
    int READABLE = 0;

    void open(int openTypeConstant);

    void close();

    long create(T t) throws SQLiteConstraintException;

    void delete(int id);

    T get(int id);

    T getFromCursor(Cursor cursor);

    ArrayList<T> getAll();
}
