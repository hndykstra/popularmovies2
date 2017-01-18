package com.udacity.example.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hans.dykstra on 1/15/2017.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "favmovies.db";

    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) { super(context, DB_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createSQL = "CREATE TABLE " + FavoriteMovieContract.FavoriteMovie.TABLE_NAME + "(" +
                FavoriteMovieContract.FavoriteMovie._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID + " INTEGER NOT NULL, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_RELEASE_DATE + " TEXT, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_RATING + " TEXT, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_POSTER_PATH + " TEXT, " +
                FavoriteMovieContract.FavoriteMovie.COLUMN_PLOT_SYNOPSIS + " TEXT, " +
                "UNIQUE (" + FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String dropSQL = "DROP TABLE IF EXISTS " + FavoriteMovieContract.FavoriteMovie.TABLE_NAME;
        db.execSQL(dropSQL);
        onCreate(db);
    }
}
