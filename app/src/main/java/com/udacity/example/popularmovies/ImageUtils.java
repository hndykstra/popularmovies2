package com.udacity.example.popularmovies;

import android.net.Uri;

/**
 * Shared utilities for generating URI to load TMDB images. Encapsulates details of the base
 * URL and desired image sizes in one place.
 * Created by Hans on 12/27/2016.
 */

public class ImageUtils {
    private static final String IMAGE_ROOT_URL = "http://image.tmdb.org/t/p";
    private static final String POSTER_IMAGE_SIZE = "w185";
    private static final String THUMBNAIL_IMAGE_SIZE = "w185";

    /**
     * Build a URI appropriate for the Poster view grid
     * @param posterPath
     * @return
     */
    public static Uri buildPosterUri(String posterPath) {
        if (posterPath == null || posterPath.length() == 0) {
            return null;
        } else {
            if (posterPath.startsWith("/")) {
                posterPath = posterPath.substring(1);
            }
            Uri uri = Uri.parse(IMAGE_ROOT_URL).buildUpon()
                    .appendPath(POSTER_IMAGE_SIZE)
                    .appendPath(posterPath)
                    .build();
            return uri;
        }
    }

    /**
     * Build a URI appropriate for the thumbnail imag in the detail view.
      * @param posterPath
     * @return
     */
    public static Uri buildThumbnailUri(String posterPath) {
        if (posterPath == null || posterPath.length() == 0) {
            return null;
        } else {
            if (posterPath.startsWith("/")) {
                posterPath = posterPath.substring(1);
            }
            Uri uri = Uri.parse(IMAGE_ROOT_URL).buildUpon()
                    .appendPath(THUMBNAIL_IMAGE_SIZE)
                    .appendPath(posterPath)
                    .build();
            return uri;
        }
    }

}
