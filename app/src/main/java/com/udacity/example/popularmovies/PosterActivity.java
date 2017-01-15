package com.udacity.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

public class PosterActivity extends AppCompatActivity {
    public static final String LOG_TAG = "PosterActivity";

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

        fetchPopularMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.poster_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_popular:
                fetchPopularMovies();
                item.setChecked(true);
                return true;
            case R.id.action_top_rated:
                fetchTopRatedMovies();
                item.setChecked(true);
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

    private void selectMovie(Movie selectedMovie) {
        Log.d(LOG_TAG, "Selecting movie");
        Class<?> aClass = MovieDetailActivity.class;
        Intent viewDetailIntent = new Intent(this, aClass);
        if (selectedMovie != null) {
            viewDetailIntent.putExtra(Intent.EXTRA_TEXT, selectedMovie.toString());
            startActivity(viewDetailIntent);
        }
    }

    private void fetchPopularMovies() {
        AsyncTask<String, Void, Movie[]> task = new AsyncTask<String, Void, Movie[]>() {
            @Override
            protected Movie[] doInBackground(String... ignored) {
                String apiKey = PosterActivity.this.getString(R.string.api_key);
                MovieService service = new MovieService(apiKey);
                try {
                    return service.getPopularMovies();
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                PosterActivity.this.loadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Movie[] movies) {
                PosterActivity.this.loadingIndicator.setVisibility(View.INVISIBLE);
                PosterActivity.this.adapter.setMovies(movies);
                if (movies != null) {
                    Log.d(LOG_TAG, "Retrieved " + movies.length + "  movies");
                    PosterActivity.this.showPosterView();
                } else {
                    Log.d(LOG_TAG, "Movie service returned null");
                    PosterActivity.this.showErrorView(R.string.service_failed);
                }
            }
        };

        if (this.isOnline()) {
            showPosterView();
            task.execute();
        } else {
            showErrorView(R.string.not_online);
        }
    }

    private void fetchTopRatedMovies() {
        AsyncTask<String, Void, Movie[]> task = new AsyncTask<String, Void, Movie[]>() {
            @Override
            protected Movie[] doInBackground(String... params) {
                String apiKey = PosterActivity.this.getString(R.string.api_key);
                MovieService service = new MovieService(apiKey);
                try {
                    return service.getTopRatedMovies();
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                PosterActivity.this.loadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Movie[] movies) {
                PosterActivity.this.loadingIndicator.setVisibility(View.INVISIBLE);
                PosterActivity.this.adapter.setMovies(movies);
                if (movies != null) {
                    Log.d(LOG_TAG, "Retrieved " + movies.length + "  movies");
                    PosterActivity.this.showPosterView();
                } else {
                    Log.d(LOG_TAG, "Movie service returned null");
                    PosterActivity.this.showErrorView(R.string.service_failed);
                }
            }
        };

        if (this.isOnline()) {
            showPosterView();
            task.execute();
        } else {
            showErrorView(R.string.not_online);
        }
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
}
