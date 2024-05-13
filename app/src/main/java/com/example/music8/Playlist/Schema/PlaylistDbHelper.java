package com.example.music8.Playlist.Schema;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class PlaylistDbHelper extends SQLiteOpenHelper {
    private Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "playlist.db";

    public PlaylistDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PlaylistAllSongs.CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PlaylistAllSongs.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean addPlaylistName(String name){
        boolean res = true;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // A variable place holder for the values that we want to insert into our table
            ContentValues values = new ContentValues();

            // Add data for each column in the table
            values.put(PlaylistAllSongs.FeedEntry.COLUMN_PLAYLIST_NAME, name);

            // Short hand way of inserting data...
            db.insert(PlaylistAllSongs.FeedEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            res = false;
        } finally {
            db.endTransaction();
            db.close();
        }
        return res;
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + PlaylistAllSongs.FeedEntry.TABLE_NAME;

        // Here, we call getReadableDatabase() since we only want to fetch data from the database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){cursor = db.rawQuery(query, null);}
        return cursor;
    }

    public Cursor readNewPlaylist() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + PlaylistAllSongs.FeedEntry.TABLE_NAME +
                " ORDER BY " + PlaylistAllSongs.FeedEntry._ID + " DESC LIMIT 1";
        return db.rawQuery(query, null);
    }
}
