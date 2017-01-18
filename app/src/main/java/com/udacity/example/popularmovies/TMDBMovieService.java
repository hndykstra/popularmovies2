package com.udacity.example.popularmovies;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Encapsulates the logic of performing the movie queries. A movie service instance requires an
 * api key to successfully query the service.
 * Created by Hans on 12/26/2016.
 */

public class TMDBMovieService {
    public static final String LOG_TAG = TMDBMovieService.class.getSimpleName();

    private static final String MOVIE_SERVICE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String MOVIE_TRAILER_PATH = "videos";
    private static final String MOVIE_REVEIW_PATH = "reviews";
    private static final String API_KEY_PARAM = "api_key";

    private String apiKey;

    /**
     * Construct an instance of the service to run movie queies.
     * @param apiKey The movie DB API key required to run queries.
     */
    public TMDBMovieService(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Executes the popular movies query (returns page 1 of N).
     * @return Array of Movies returned from the query, or null if the service fails.
     * @throws IOException If there is a problem querying or reading results.
     */
    public Movie[] getPopularMovies() throws IOException{
        URL popularSearchUrl = buildURL(POPULAR_PATH);
        return executeMovieQuery(popularSearchUrl);
    }

    /**
     * Executes the top-rated movies query (returns page 1 of N).
     * @return Array of Movies returned from the query, or null if the service fails.
     * @throws IOException If there is a proglem querying or reading results.
     */
    public Movie[] getTopRatedMovies() throws IOException {
        URL topRatedSearchUrl = buildURL(TOP_RATED_PATH);
        return executeMovieQuery(topRatedSearchUrl);
    }

    public Movie getMovie(int tmdbId) throws IOException {
        // just appending the movie ID to the base URL
        URL movieURL = buildMovieURL(tmdbId, null);
        return executeSingleMovie(movieURL);
    }

    /**
     * Fetches the trailer information for the specified movie.
     * @param tmdbId TMDB movie identifier.
     * @return Array of movie trailer information.
     * @throws IOException If there is an error communicating with the service.
     */
    public MovieTrailer[] getMovieTrailers(int tmdbId) throws IOException {
        URL trailersURL = buildMovieURL(tmdbId, MOVIE_TRAILER_PATH);
        return executeTrailersQuery(trailersURL);
    }

    /**
     * Fetches the reviews for the specifice movie.
     * @param tmdpId TMDB movie identifier.
     * @return Array of movie reviews.
     * @throws IOException If there is an error communicating with the service.
     */
    public MovieReview[] getMovieReviews(int tmdpId) throws IOException {
        URL reviewsURL = buildMovieURL(tmdpId, MOVIE_REVEIW_PATH);
        return executeReviewQuery(reviewsURL);
    }

    private URL buildURL(String path) {
        Uri uri = Uri.parse(MOVIE_SERVICE_BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            // no real handling here, need to do better
            e.printStackTrace();
        }

        return url;
    }

    private URL buildMovieURL(int tmdbId, String path) {
        Uri.Builder bldr = Uri.parse(MOVIE_SERVICE_BASE_URL).buildUpon()
                .appendPath(String.valueOf(tmdbId));
        if (path != null) {
            bldr.appendPath(path);
        }
        bldr.appendQueryParameter(API_KEY_PARAM, apiKey);
        Uri uri = bldr.build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private String retrieveJson(URL url) throws IOException {
        Log.d(LOG_TAG, "Query " + url.toString());
        String jsonResult = "";
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = connection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                jsonResult = scanner.next();
            }
        } finally {
            connection.disconnect();
        }

        Log.d(LOG_TAG, "Return " + jsonResult);
        return jsonResult;
    }
    private MovieReview[] executeReviewQuery(URL url) throws IOException {
        String jsonResult = retrieveJson(url);
        MovieReview[] results = null;
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                results = extractMovieReviews(jsonResult);
            } catch (JSONException e) {
                e.printStackTrace();;
            }
        }
        return results;
    }

    private Movie executeSingleMovie(URL url) throws IOException {
        String jsonResult = retrieveJson(url);
        Movie result = null;
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                result = Movie.fromJson(new JSONObject(jsonResult));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private Movie[] executeMovieQuery(URL url) throws IOException {
        String jsonResult = retrieveJson(url);

        Movie[] results = null;
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                results = extractMovies(jsonResult);
            } catch (JSONException e) {
                // really need better error handling
                // but this is a borked result, e.g. wrong URL
                e.printStackTrace();
            }
        }
        return results;
    }

    private MovieTrailer[] executeTrailersQuery(URL url) throws IOException {
        String jsonResult = retrieveJson(url);

        MovieTrailer[] results = null;
        if (jsonResult != null && jsonResult.length() > 0) {
            try {
                results = extractMovieTrailers(jsonResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private Movie[] extractMovies(String jsonResultSet) throws JSONException {
        Movie[] parsedResults = null;
        JSONObject resultSet = new JSONObject(jsonResultSet);
        JSONArray array = (JSONArray)resultSet.optJSONArray("results");
        if (array != null) {
            parsedResults = new Movie[array.length()];
            for (int i=0 ; i < array.length() ; ++i) {
                parsedResults[i] = Movie.fromJson(array.getJSONObject(i));
            }
        }
        return parsedResults;
    }

    private MovieTrailer[] extractMovieTrailers(String jsonResultSet) throws JSONException {
        MovieTrailer[] parsedResults = null;
        JSONObject resultSet = new JSONObject(jsonResultSet);
        JSONArray array = (JSONArray)resultSet.optJSONArray("results");
        if (array != null) {
            parsedResults = new MovieTrailer[array.length()];
            for (int i=0 ; i < array.length() ; ++i) {
                parsedResults[i] = MovieTrailer.fromJson(array.getJSONObject(i));
            }
        }

        return parsedResults;
    }

    private MovieReview[] extractMovieReviews(String jsonResultSet) throws JSONException {
        MovieReview[] parsedResults = null;
        JSONObject resultSet = new JSONObject(jsonResultSet);
        JSONArray array = (JSONArray)resultSet.optJSONArray("results");
        if (array != null) {
            parsedResults = new MovieReview[array.length()];
            for (int i=0 ; i < array.length() ; ++i) {
                parsedResults[i] = MovieReview.fromJson(array.getJSONObject(i));
            }
        }

        return parsedResults;
    }
}
