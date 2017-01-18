package com.udacity.example.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.example.popularmovies.data.MovieUtils;
import com.udacity.example.popularmovies.databinding.ActivityMovieDetailBinding;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    private static final int REVIEW_LOADER_ID = 5992;
    private static final int TRAILER_LOADER_ID = 12098;
    private static final int FAVORITE_LOADER_ID = 904;

    public static final String MOVIE_PARCEL = "com.udacity.example.popularmovies.MovieParcel";
    public static final String MOVIE_IS_FAVORITE = "com.udacity.example.popularmovies.MovieIsFavorit";

    private ActivityMovieDetailBinding binding;
    private Movie displayingMovie = null;
    private boolean isFavorite = false;
    private TrailerAdapter trailerAdapter = new TrailerAdapter();
    private ReviewAdapter reviewAdapter = new ReviewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent startingIntent = getIntent();
        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_PARCEL)) {
            Log.d(LOG_TAG, "Restoring from saved instance state");
            try {
                Movie movieData = savedInstanceState.getParcelable(MOVIE_PARCEL);
                boolean isFavorite = savedInstanceState.getBoolean(MOVIE_IS_FAVORITE);
                displayMovie(movieData, isFavorite);
                fetchAdditionalDetails();
            } catch (Exception e) {
                e.printStackTrace();
                displayError();
            }
        } else if (startingIntent.hasExtra(MOVIE_PARCEL)) {
            Log.d(LOG_TAG, "Loading from intent parcel");
            try {
                Movie movieData = startingIntent.getExtras().getParcelable(MOVIE_PARCEL);
                boolean isFavorite = startingIntent.getBooleanExtra(MOVIE_IS_FAVORITE, false);
                displayMovie(movieData, isFavorite);
                fetchAdditionalDetails();
            } catch (Exception e) {
                e.printStackTrace();
                displayError();
            }
        } else {
            displayError();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (displayingMovie != null){
            Log.d(LOG_TAG, "saving instance state");
            outState.putParcelable(MOVIE_PARCEL, displayingMovie);
            outState.putBoolean(MOVIE_IS_FAVORITE, isFavorite);
        }
        super.onSaveInstanceState(outState);
    }
    public void toggleFavorite(View button) {
        final ImageButton imageButton = binding.detailHeader.addFavoriteButton;
        final boolean favorite = isFavorite;
        AsyncTask<Movie, Void, Boolean> aTask = new AsyncTask<Movie, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Movie... params) {
                    if (params != null && params.length > 0) {
                        Movie toToggle = params[0];
                        if (favorite) {
                            MovieUtils.deleteFavoriteMovie(MovieDetailActivity.this, toToggle.getTmdbId());
                        } else {
                            MovieUtils.insertAsFavorite(MovieDetailActivity.this, toToggle);
                        }
                        return Boolean.TRUE;
                    }

                    return Boolean.FALSE;
                }

            @Override
            protected void onPostExecute(Boolean performed) {
                super.onPostExecute(performed);
                if (performed.booleanValue()) {
                    MovieDetailActivity.this.isFavorite = !favorite;
                    updateFavoriteButton(!favorite);
                    Toast.makeText(MovieDetailActivity.this, favorite ? R.string.removed_favorite : R.string.added_favorite, Toast.LENGTH_LONG).show();
                }
            }
        };

        aTask.execute(this.displayingMovie);
    }

    private void fetchAdditionalDetails() {
        // if this was initiated from the favorites view, we already know and can skip this.
        // if this was initiated from another view, it might or might not be a favorite so we need to check.
        LoaderManager lm = getSupportLoaderManager();
        if (!this.isFavorite) {
            lm.initLoader(FAVORITE_LOADER_ID, null, this);
        }
        lm.initLoader(REVIEW_LOADER_ID, null, this);
        lm.initLoader(TRAILER_LOADER_ID, null, this);
    }

    private void displayError() {
        binding.detailHeader.titleView.setText(null);
        binding.detailHeader.releaseDateView.setText(null);
        binding.detailHeader.userRatingView.setText(null);
        binding.detailHeader.thumbnailPoster.setImageDrawable(null);
        binding.textviewOverview.setText(getString(R.string.movie_missing));
    }


    private void displayMovie(Movie movie, boolean isFavorite) {
        displayingMovie = movie;
        this.isFavorite = isFavorite;
        if (movie != null) {
            Log.d(LOG_TAG, "Displaying movie favorite=" + isFavorite);
            binding.detailHeader.titleView.setText(movie.getTitle());
            binding.detailHeader.releaseDateView.setText(getString(R.string.release_date, movie.getReleaseDate()));
            binding.detailHeader.userRatingView.setText(getString(R.string.user_rating, movie.getUserRating()));
            ImageButton b = binding.detailHeader.addFavoriteButton;
            if (isFavorite) {
                b.setImageResource(R.drawable.ic_53415);
                b.setContentDescription(getString(R.string.a11y_remove_favorite));
            } else {
                b.setImageResource(R.drawable.ic_53415_x);
                b.setContentDescription(getString(R.string.a11y_add_favorite));
            }
            String synopsis = movie.getOverview();
            if (synopsis == null || synopsis.length() == 0) {
                binding.textviewOverview.setText(R.string.overview_blank);
            } else {
                binding.textviewOverview.setText(synopsis);
            }
            Uri posterUrl = ImageUtils.buildThumbnailUri(movie.getPosterPath());
            if (posterUrl == null) {
                binding.detailHeader.thumbnailPoster.setImageDrawable(null);
            } else {
                Picasso.Builder builder = new Picasso.Builder(this);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        binding.detailHeader.thumbnailPoster.setImageDrawable(null);
                    }
                });
                builder.build().load(posterUrl).into(binding.detailHeader.thumbnailPoster);
            }
        }
    }

    private void bindTrailers(MovieTrailer[] trailers) {
        LinearLayout trailerLayout = binding.trailerView.trailerList;
        trailerLayout.removeAllViews();
        trailerAdapter.swapMovieTrailers(trailers);
        if (trailerAdapter.getItemCount() == 0) {
            TextView tv = new TextView(this);
            tv.setText(R.string.no_trailers);
            tv.setPadding(8, 0, 8, 16);
            trailerLayout.addView(tv);
        } else {
            // now we need to rebuild, using a recycler view like pattern
            for (int i = 0; i < trailerAdapter.getItemCount(); ++i) {
                TrailerAdapter.ViewHolder holder = trailerAdapter.onCreateViewHolder(trailerLayout, 0);
                trailerAdapter.bindViewHolder(holder, i);
                trailerLayout.addView(holder.itemView);
            }
        }
    }

    private void bindReviews(MovieReview[] reviews) {
        Log.d(LOG_TAG, "binding reviews");
        LinearLayout reviewLayout = binding.reviewView.reviewList;
        reviewLayout.removeAllViews();
        reviewAdapter.swapMovieReviews(reviews);
        if (reviewAdapter.getItemCount() == 0) {
            TextView tv = new TextView(this);
            tv.setText(R.string.no_reviews);
            tv.setPadding(8, 0, 8, 16);
            reviewLayout.addView(tv);
        } else {
            // now rebuild the view, using recycler view like pattern but show all
            for (int i = 0; i < reviewAdapter.getItemCount(); ++i) {
                Log.d(LOG_TAG, "Binding view to review " + reviews[i]);
                ReviewAdapter.ViewHolder holder = reviewAdapter.onCreateViewHolder(reviewLayout, 0);
                reviewAdapter.bindViewHolder(holder, i);
                reviewLayout.addView(holder.itemView);
            }
        }
    }

    void updateFavoriteButton(boolean favorite) {
        this.isFavorite = favorite;
        binding.detailHeader.addFavoriteButton.setImageResource(
                this.isFavorite ? R.drawable.ic_53415 : R.drawable.ic_53415_x
        );
        binding.detailHeader.addFavoriteButton.setContentDescription(getString(
                this.isFavorite ? R.string.a11y_remove_favorite : R.string.a11y_add_favorite
        ));
    }

    /**
     * Using Object here is lame but need to
     * A) check if the movie is a favorite
     * B) load the reviews
     * C) load the trailers
     * Could do all in one go but then the return type is still weird. Like to use the Loader
     * so that if the activity is interrupted the loader will work better.
     * Implementing callbacks for all different types probably runs afoul of type erasure.
     * Upshot - just return Object and check the type on load finish.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        final int movieId = displayingMovie.getTmdbId();
        final TMDBMovieService tmdbService = new TMDBMovieService(getString(R.string.api_key));
        switch (id) {
            case REVIEW_LOADER_ID:
                return new AsyncTaskLoader<Object>(this) {
                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading reviews");
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Object loadInBackground() {
                        try {
                            Log.d(LOG_TAG, "Query reviews");
                            return tmdbService.getMovieReviews(movieId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            case TRAILER_LOADER_ID:
                return new AsyncTaskLoader<Object>(this) {
                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading trailers");
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Object loadInBackground() {
                        try {
                            Log.d(LOG_TAG, "Query trailers");
                            return tmdbService.getMovieTrailers(movieId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            case FAVORITE_LOADER_ID:
                return new AsyncTaskLoader<Object>(this) {
                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading favorite");
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Object loadInBackground() {
                        Log.d(LOG_TAG, "Query favorites");
                        Movie m = MovieUtils.getFavoriteMovie(getContext(), movieId);
                        if (m != null) {
                            try {
                                // get latest details from TMDB and update the local copy
                                m = tmdbService.getMovie(movieId);
                                MovieUtils.updateFavoriteMovie(getContext(), m);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return m;
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        int loaderId = loader.getId();
        Log.d(LOG_TAG, "Load finished " + loaderId + " " + data);
        switch (loaderId) {
            case FAVORITE_LOADER_ID:
                Movie m = (Movie)data;
                if (m != null) {
                    // this might be an updated copy of the movie, so update the UI
                    displayingMovie = m;
                    displayMovie(m, true);
                } else {
                    updateFavoriteButton(false);
                }
                break;
            case REVIEW_LOADER_ID:
                bindReviews((MovieReview[])data);
                break;
            case TRAILER_LOADER_ID:
                bindTrailers((MovieTrailer[])data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
