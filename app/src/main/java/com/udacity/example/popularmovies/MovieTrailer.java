package com.udacity.example.popularmovies;

import org.json.JSONObject;

/**
 * Created by hans.dykstra on 1/15/2017.
 */

public class MovieTrailer {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_SOURCE = "key";

    public static MovieTrailer fromJson(JSONObject trailerObj) {
        MovieTrailer trailer = new MovieTrailer();
        trailer.trailerName = trailerObj.optString(FIELD_NAME);
        trailer.trailerSource = trailerObj.optString(FIELD_SOURCE);

        return trailer;
    }

    private String trailerName;
    private String trailerSource;

    public String getTrailerName() {
        return trailerName;
    }

    public String getTrailerSource() {
        return trailerSource;
    }
}
