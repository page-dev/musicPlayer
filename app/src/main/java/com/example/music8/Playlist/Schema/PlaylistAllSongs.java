package com.example.music8.Playlist.Schema;

import android.provider.BaseColumns;

public class PlaylistAllSongs {
    public PlaylistAllSongs(){}

    public static class FeedEntry implements BaseColumns{
        // Define table and column names
        public static final String TABLE_NAME = "playlist";
        public static final String COLUMN_PLAYLIST_NAME = "playlist_name";
    }

    public static final String CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + FeedEntry.TABLE_NAME
            /*
                INTEGER means that this column will have a data type of integer.

                PRIMARY KEY means that this column will act as the primary key. Also, in addition to that,
                all columns set as PRIMARY KEY automatically are set as NOT NULL (therefore, no need to specify NOT NULL here).

                AUTOINCREMENT means that the value will start from 1 and automatically increases it every time
                a new record is added (no need to specify it during insert operation).
             */
            + " (" + FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            /*
                TEXT means that this column will have a data type of TEXT (similar to VARCHAR, STRING, etc.)

                NOT NULL means that we always need to supply a value into this column whenever we insert a new record. Otherwise, the database will
                return an error.
             */
            + FeedEntry.COLUMN_PLAYLIST_NAME + " TEXT NOT NULL)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
