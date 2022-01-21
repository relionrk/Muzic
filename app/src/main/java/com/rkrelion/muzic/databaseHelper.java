package com.rkrelion.muzic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class databaseHelper extends SQLiteOpenHelper {
    public static final String song = "SONG" ;
    public static final String id = "ID" ;
    Context context ;
    public databaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableString = "CREATE TABLE " + song + "(" + id + " INTEGER PRIMARY KEY)" ;
        db.execSQL(createTableString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + song);
        onCreate(db);
    }

    public boolean addItem (int idx) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues() ;
        cv.put(id,idx);

        long insert = db.insert(song, null, cv);

        if (insert == -1)
            return false ;
        else {
            return true;
        }
    }

    public boolean removeItem (int idx) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.delete(song,"ID=?",new String[]{Integer.toString(idx)});
            Toast.makeText(context, "Removed From Playlist", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "can't delete", Toast.LENGTH_SHORT).show();
        }

        return true ;

    }

    public ArrayList<Integer> getItems() {
        ArrayList<Integer> results = new ArrayList<>() ;

        String queryString = "SELECT * FROM SONG" ;

        SQLiteDatabase db = this.getReadableDatabase() ;
        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToFirst() ;
        int size = cursor.getCount() ;

        while (results.size() != size) {
            int item = cursor.getInt(0) ;
            results.add(item) ;
            cursor.moveToNext() ;
        }
        //Toast.makeText(context, "size is " + results.size(), Toast.LENGTH_SHORT).show();
        cursor.close();
        db.close();
        return results ;
    }

    public void display() {
        String queryString = "SELECT * FROM SONG" ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString , null) ;
        boolean b = cursor.moveToFirst();
        if (b) {
            Toast.makeText(context, "does work the count is " + cursor.getInt(0) , Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "doesn't works", Toast.LENGTH_SHORT).show();
        }

    }

}
