package com.udacity.example.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by hans.dykstra on 1/14/2017.
 */

public class FavoriteMovieContract {
    public static final String CONTENT_AUTHORITY = "com.udacity.example.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorite";

    public static class FavoriteMovie implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "FavoriteMovie";
        public static final String COLUMN_TMDB_ID = "IdTMDB";
        public static final String COLUMN_POSTER_PATH = "PosterPath";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_RELEASE_DATE = "ReleaseDate";
        public static final String COLUMN_RATING = "UserRating";
        public static final String COLUMN_PLOT_SYNOPSIS = "Overview";

    }
}
