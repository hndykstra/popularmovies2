package com.udacity.example.popularmovies;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Crappy ersion of JSON record.
 */

public class Movie {
    private JSONObject movieJson;

    public Movie(JSONObject object) {
        this.movieJson = object;
    }

    public Movie(String jsonText) throws JSONException {
        movieJson = new JSONObject(jsonText);
    }

    public boolean isValid() {
        return movieJson != null;
    }
    public String getOriginalTitle() {
        return movieJson.optString("original_title");
    }

    public String getReleastDate() {
        return movieJson.optString("release_date");
    }

    public String getOverview() {
        return movieJson.optString("overview");
    }

    public String getPosterPath() {
        return movieJson.optString("poster_path");
    }

    public String getTitle() {
        return movieJson.optString("title");
    }

    public String getUserRating() {
        double rating = movieJson.optDouble("vote_average");
        if (rating <= 0.0 || rating > 10.0) {
            return "";
        } else {
            NumberFormat fmt = new DecimalFormat("##.#");
            return fmt.format(rating);
        }
    }

    @Override
    public String toString() {
        if (movieJson == null)
            return "(null)";
        else
            return movieJson.toString();
    }
}
