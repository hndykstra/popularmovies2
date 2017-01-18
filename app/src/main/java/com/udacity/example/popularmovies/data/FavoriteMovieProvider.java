package com.udacity.example.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by hans.dykstra on 1/15/2017.
 */

public class FavoriteMovieProvider extends ContentProvider {

    private static final int CODE_FAVORITES = 100;
    private static final int CODE_FAVORITE_BY_TMDBID = 101;

    private static final UriMatcher matcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE, CODE_FAVORITES);
        matcher.addURI(authority, FavoriteMovieContract.PATH_FAVORITE + "/#", CODE_FAVORITE_BY_TMDBID);
        return matcher;
    }

    private FavoritesDbHelper dbHelper;

    public FavoriteMovieProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoritesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor queryResult = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int code = matcher.match(uri);
        switch (code) {
            case CODE_FAVORITES:
                queryResult = db.query(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FAVORITE_BY_TMDBID:
                String[] byTmdbId = new String[]{uri.getLastPathSegment()};
                String selectionClause = FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID + "=?";
                queryResult = db.query(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, projection, selectionClause, byTmdbId, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized query " + uri);
        }
        return queryResult;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = matcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri resultUri = null;
        switch (code) {
            case CODE_FAVORITES:
                long id = db.insert(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, null, values);
                int tmdbId = values.getAsInteger(FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID);
                resultUri = FavoriteMovieContract.FavoriteMovie.CONTENT_URI.buildUpon()
                        .appendPath(String.valueOf(tmdbId)).build();
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized insert " + uri);
        }
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = matcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleted = 0;
        switch (code) {
            case CODE_FAVORITE_BY_TMDBID: // delete one
                String[] byTmdbId = new String[]{uri.getLastPathSegment()};
                String deleteClause = FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID + "=?";
                deleted = db.delete(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, deleteClause, byTmdbId);
                break;
            case CODE_FAVORITES: // delete all
                deleted = db.delete(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized delete " + uri);
        }
        return deleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = matcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updated = 0;
        switch (code) {
            case CODE_FAVORITE_BY_TMDBID:
                String[] byTmdbId = new String[]{uri.getLastPathSegment()};
                String updateClause = FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID + "=?";
                updated = db.update(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, values, updateClause, byTmdbId);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized update " + uri);
        }
        return updated;
    }

}
