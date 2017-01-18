package com.udacity.example.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.udacity.example.popularmovies.Movie;

/**
 * Created by hans.dykstra on 1/15/2017.
 */

public class MovieUtils {
    public static ContentValues toContentValues(Movie m) {
        ContentValues values = new ContentValues();
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID, m.getTmdbId());
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_TITLE, m.getTitle());
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_RELEASE_DATE, m.getReleaseDate());
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_RATING, m.getUserRating());
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_PLOT_SYNOPSIS, m.getOverview());
        values.put(FavoriteMovieContract.FavoriteMovie.COLUMN_POSTER_PATH, m.getPosterPath());

        return values;
    }

    public static Movie[] fromCursor(Cursor cursor) {
        int count = cursor.getCount();
        Movie[] movies = new Movie[count];
        for (int i=0 ; i < count ; ++i) {
            if (i == 0) {
                cursor.moveToFirst();
            } else {
                cursor.moveToNext();
            }
            movies[i] = Movie.fromCursor(cursor);
        }

        return movies;
    }

    public static Movie[] queryAllFavorites(Context context) {
        ContentResolver resolver = context.getContentResolver();
        Uri queryUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI;
        Cursor cursor = resolver.query(queryUri, null, null, null, null);
        return fromCursor(cursor);
    }

    public static int updateFavoriteMovie(Context context, Movie movie) {
        ContentResolver resolver = context.getContentResolver();
        Uri updateUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movie.getTmdbId()))
                .build();
        return resolver.update(updateUri, toContentValues(movie), null, null);
    }

    public static Movie getFavoriteMovie(Context context, int tmdbId) {
        ContentResolver resolver = context.getContentResolver();
        Uri queryUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(tmdbId))
                .build();
        Cursor cursor = resolver.query(queryUri, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                Movie m = Movie.fromCursor(cursor);
                return m;
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    public static Uri insertAsFavorite(Context context, Movie movie) {
        ContentResolver resolver = context.getContentResolver();
        Uri insertUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI;
        return resolver.insert(insertUri, toContentValues(movie));
    }

    public static int deleteFavoriteMovie(Context context, int tmdbId) {
        ContentResolver resolver = context.getContentResolver();
        Uri deleteUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(tmdbId))
                .build();
        return resolver.delete(deleteUri, null, null);
    }
}
