package com.udacity.example.popularmovies;

import org.json.JSONObject;

/**
 * Created by hans.dykstra on 1/15/2017.
 */

public class MovieReview {
    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_REVIEW = "content";
    private static final String FIELD_URL = "url";
    private static final String FIELD_ID = "id";

    public static MovieReview fromJson(JSONObject reviewObj) {
        MovieReview theReview = new MovieReview();
        theReview.author = reviewObj.optString(FIELD_AUTHOR);
        theReview.review = reviewObj.optString(FIELD_REVIEW);
        theReview.id = reviewObj.optString(FIELD_ID);

        return theReview;
    }

    private String id;
    private String author;
    private String review;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getReview() {
        return review;
    }

    @Override
    public String toString() {
        return author + ": " + review;
    }
}
