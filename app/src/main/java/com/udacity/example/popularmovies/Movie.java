package com.udacity.example.popularmovies;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.example.popularmovies.data.FavoriteMovieContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Standardizes access to movie attributes across cursors, parcels, and JSON. This object is
 * immutable since (for now) the properties are always originated from the TMDB.
 */

public class Movie implements Parcelable {
    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_RELEASE_DATE = "release_date";
    private static final String FIELD_OVERVIEW = "overview";
    private static final String FIELD_POSTER_PATH = "poster_path";
    private static final String FIELD_VOTE = "vote_average";

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            Movie theMovie = new Movie();
            theMovie.tmdbId = source.readInt();
            theMovie.title = source.readString();
            theMovie.releaseDate = source.readString();
            theMovie.userRating = source.readString();
            theMovie.overview = source.readString();
            theMovie.posterPath = source.readString();
            return theMovie;
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private int tmdbId;
    private String title;
    private String releaseDate;
    private String userRating;
    private String posterPath;
    private String overview;

    public static Movie fromJson(JSONObject object) throws JSONException {
        Movie theMovie = new Movie();
        theMovie.tmdbId = object.optInt(FIELD_ID);
        theMovie.overview = object.optString(FIELD_OVERVIEW);
        theMovie.releaseDate = object.optString(FIELD_RELEASE_DATE);
        double rating = object.optDouble(FIELD_VOTE, -1.);
        if (rating < 0.0 || rating > 10.0) {
            theMovie.userRating = "";
        } else {
            NumberFormat fmt = new DecimalFormat("#.#");
            theMovie.userRating = fmt.format(rating);
        }
        theMovie.posterPath = object.optString(FIELD_POSTER_PATH);
        theMovie.title = object.optString(FIELD_TITLE);

        return theMovie;
    }

    public static Movie fromCursor(Cursor cursor) {
        Movie theMovie = new Movie();
        theMovie.tmdbId = cursor.getInt(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_TMDB_ID));
        theMovie.title = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_TITLE));
        theMovie.overview = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_PLOT_SYNOPSIS));
        theMovie.releaseDate = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_RELEASE_DATE));
        theMovie.userRating = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_RATING));
        theMovie.posterPath = cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_POSTER_PATH));

        return theMovie;
    }

    public Movie()  {
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getUserRating() {
        return userRating;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append(Movie.class.getSimpleName())
                .append("id=").append(tmdbId)
                .append(", title=").append(title);
        return bldr.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tmdbId);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(userRating);
        dest.writeString(overview);
        dest.writeString(posterPath);
    }
}
