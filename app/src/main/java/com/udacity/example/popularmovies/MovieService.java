package com.udacity.example.popularmovies;

import android.net.Uri;

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

public class MovieService {
    private static final String MOVIE_SERVICE_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String API_KEY_PARAM = "api_key";

    private String apiKey;

    /**
     * Construct an instance of the service to run movie queies.
     * @param apiKey The movie DB API key required to run queries.
     */
    public MovieService(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Executes the popular movies query (returns page 1 of N).
     * @return Array of Movies returned from the query, or null if the service fails.
     * @throws IOException If there is a problem querying or reading results.
     */
    public Movie[] getPopularMovies() throws IOException{
        URL popularSearchUrl = buildURL(POPULAR_PATH);
        return executeQuery(popularSearchUrl);
    }

    /**
     * Executes the top-rated movies query (returns page 1 of N).
     * @return Array of Movies returned from the query, or null if the service fails.
     * @throws IOException If there is a proglem querying or reading results.
     */
    public Movie[] getTopRatedMovies() throws IOException {
        URL topRatedSearchUrl = buildURL(TOP_RATED_PATH);
        return executeQuery(topRatedSearchUrl);
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
            // no real handling here, need to
        }

        return url;
    }

    private Movie[] executeQuery(URL url) throws IOException {
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

    private Movie[] extractMovies(String jsonResultSet) throws JSONException {
        Movie[] parsedResults = null;
        JSONObject resultSet = new JSONObject(jsonResultSet);
        JSONArray array = (JSONArray)resultSet.optJSONArray("results");
        if (array != null) {
            parsedResults = new Movie[array.length()];
            for (int i=0 ; i < array.length() ; ++i) {
                parsedResults[i] = new Movie(array.getJSONObject(i));
            }
        }
        return parsedResults;
    }
}
