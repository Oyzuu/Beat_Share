package be.omnuzel.beatshare.db;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by student on 4/07/2016.
 */
public interface DataAccessObject<T> {
    int WRITABLE = 1;
    int READABLE = 0;

    void open(int openTypeConstant);

    void close();

    long create(T t);

    void delete(int id);

    T get(int id);

    T getFromCursor(Cursor cursor);

    ArrayList<T> getAll();
}
