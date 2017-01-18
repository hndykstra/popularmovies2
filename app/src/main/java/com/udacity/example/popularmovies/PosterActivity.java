package com.udacity.example.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.example.popularmovies.data.MovieUtils;

import java.io.IOException;

public class PosterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]> {
    public static final String LOG_TAG = "PosterActivity";

    private static final int POPULAR_LOADER_ID = 2893;
    private static final int TOPRATED_LOADER_ID = 4892;
    private static final int FAVORITE_LOADER_ID = 9284;

    /**
     * Associates an enumerated value with the loader ID for use with SharedPreferences.
     */
    enum QueryType {
        POPULAR(POPULAR_LOADER_ID),
        TOPRATED(TOPRATED_LOADER_ID),
        FAVORITE(FAVORITE_LOADER_ID);

        private int loaderId;
        private QueryType(int loader) { loaderId = loader; }

        public int getLoaderId() { return loaderId; }
    }

    private QueryType currentQuery;
    private RecyclerView postersRecycler;
    private PosterListAdapter adapter;
    private ProgressBar loadingIndicator;
    private TextView onlineErrorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        loadingIndicator = (ProgressBar)findViewById(R.id.loading_indicator);
        onlineErrorView = (TextView)findViewById(R.id.online_error);

        postersRecycler = (RecyclerView)findViewById(R.id.recycler_posters);
        int colCount = (isLandscapeMode() ? 3 : 2);
        RecyclerView.LayoutManager lm = new GridLayoutManager(this, colCount);
        postersRecycler.setLayoutManager(lm);
        adapter = new PosterListAdapter(new PosterListAdapter.MovieSelectionListener() {
            @Override
            public void movieSelected(Movie selectedMovie) {
                PosterActivity.this.selectMovie(selectedMovie);
            }
        });
        postersRecycler.setAdapter(adapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String queryType = preferences.getString(getString(R.string.pref_query), getString(R.string.pref_query_default));
        QueryType preferredQuery = QueryType.POPULAR;
        try {
            preferredQuery = QueryType.valueOf(queryType);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Invalid pref_query value " + queryType, e);
        }
        initialize(QueryType.valueOf(queryType));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poster_options, menu);
        MenuItem selected = null;
        switch (currentQuery) {
            case POPULAR:
                selected = menu.findItem(R.id.action_popular);
                break;
            case TOPRATED:
                selected = menu.findItem(R.id.action_top_rated);
                break;
            case FAVORITE:
                selected = menu.findItem(R.id.action_favorites);
                break;
        }
        if (selected != null)
            selected.setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_popular:
                item.setChecked(true);
                requery(QueryType.POPULAR);
                return true;
            case R.id.action_top_rated:
                item.setChecked(true);
                requery(QueryType.TOPRATED);
                return true;
            case R.id.action_favorites:
                item.setChecked(true);
                requery(QueryType.FAVORITE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() {
        // Code based on stack overflow http://stackoverflow.com/questions/30343011/how-to-check-if-an-android-device-is-online
        boolean online = false;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            online = netInfo != null && netInfo.isConnectedOrConnecting();
        }
        Log.d(LOG_TAG, "Checked online: " + online);
        return online;
    }

    private void initialize(QueryType type) {
        if (this.isOnline()) {
            Log.d(LOG_TAG, "Initializing loader " + type);
            int loaderId = type.getLoaderId();
            currentQuery = type;
            showLoading(true);
            getSupportLoaderManager().initLoader(loaderId, null, this);
            showPosterView();
        } else {
            currentQuery = type;
            showErrorView(R.string.not_online);
        }
    }

    private void setTitle(QueryType type) {
        int id = R.string.app_name;
        if (type != null) {
            switch (type) {
                case POPULAR:
                    id = R.string.app_name_popular;
                    break;
                case TOPRATED:
                    id = R.string.app_name_toprated;
                    break;
                case FAVORITE:
                    id = R.string.app_name_favorite;
                    break;
            }
        }
        getSupportActionBar().setTitle(id);
    }

    private void requery(QueryType query) {
        LoaderManager mgr = getSupportLoaderManager();

        if (query != currentQuery) {
            Log.d(LOG_TAG, "Requerying " + query);
            if (currentQuery != null) {
                mgr.destroyLoader(currentQuery.getLoaderId());
            }
            if (query != null) {
                mgr.restartLoader(query.getLoaderId(), null, this);
            }
            currentQuery = query;
            setTitle(currentQuery);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getString(R.string.pref_query), currentQuery.toString());
            editor.commit();
        } else {
            Log.d(LOG_TAG, "Restarting " + currentQuery);
            if (currentQuery != null) {
                mgr.restartLoader(currentQuery.getLoaderId(), null, this);
            }
        }
    }

    private void selectMovie(Movie selectedMovie) {
        Log.d(LOG_TAG, "Selecting movie");
        Class<?> aClass = MovieDetailActivity.class;
        Intent viewDetailIntent = new Intent(this, aClass);
        if (selectedMovie != null) {
            viewDetailIntent.putExtra(MovieDetailActivity.MOVIE_PARCEL, selectedMovie);
            startActivity(viewDetailIntent);
        }
    }

    private void showLoading(boolean loading) {
        loadingIndicator.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
    }

    private void showPosterView() {
        this.onlineErrorView.setVisibility(View.INVISIBLE);
        this.postersRecycler.setVisibility((View.VISIBLE));
    }

    private void showErrorView(int resourceId) {
        this.onlineErrorView.setVisibility(View.VISIBLE);
        this.onlineErrorView.setText(resourceId);
        this.postersRecycler.setVisibility(View.INVISIBLE);
    }

    private boolean isLandscapeMode() {
        int orient = getResources().getConfiguration().orientation;
        return orient == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, Bundle args) {
        final Context context = this;
        Log.d(LOG_TAG, "onCreateLoader " + id);
        switch (id) {
            case POPULAR_LOADER_ID:
                return new AsyncTaskLoader<Movie[]>(context) {
                    private Movie[] results = null;

                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading " + getId());
                        super.onStartLoading();
                        if (results != null) {
                            deliverResult(results);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Movie[] loadInBackground() {
                        Log.d(LOG_TAG, "Popular loader running");
                        String apiKey = PosterActivity.this.getString(R.string.api_key);
                        TMDBMovieService service = new TMDBMovieService(apiKey);
                        try {
                            results = service.getPopularMovies();
                            return results;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            case TOPRATED_LOADER_ID:
                return new AsyncTaskLoader<Movie[]>(context) {
                    Movie[] results = null;

                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading " + getId());
                        super.onStartLoading();
                        if (results != null) {
                            deliverResult(results);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Movie[] loadInBackground() {
                        Log.d(LOG_TAG, "Top rated loader running");
                        String apiKey = PosterActivity.this.getString(R.string.api_key);
                        TMDBMovieService service = new TMDBMovieService(apiKey);
                        try {
                            results = service.getTopRatedMovies();
                            return results;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            case FAVORITE_LOADER_ID:
                return new AsyncTaskLoader<Movie[]>(context) {
                    Movie[] results = null;

                    @Override
                    protected void onStartLoading() {
                        Log.d(LOG_TAG, "Start loading " + getId());
                        super.onStartLoading();
                        if (results != null) {
                            deliverResult(results);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public Movie[] loadInBackground() {
                        Log.d(LOG_TAG, "Favorites loader running");
                        results = MovieUtils.queryAllFavorites(getContext());
                        return results;
                    }
                };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        Log.d(LOG_TAG, "Loader finished " + loader.getId());
        showLoading(false);
        if (data != null) {
            if (data.length > 0){
                adapter.setMovies(data);
                showPosterView();
            } else {
                adapter.setMovies(data);
                showErrorView(R.string.no_data);
            }
            setTitle(PosterActivity.this.currentQuery);
        } else {
            showErrorView(R.string.service_failed);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {
        // nothing to do here
    }
}
