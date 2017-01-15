package com.udacity.example.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView titleView;
    private TextView originalTitleView;
    private TextView releaseDateView;
    private TextView overviewView;
    private TextView userRatingView;
    private ImageView thumbnailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        this.titleView = (TextView)findViewById(R.id.title_movie);
        this.originalTitleView = (TextView)findViewById(R.id.original_title_view);
        this.releaseDateView = (TextView)findViewById(R.id.release_date_view);
        this.userRatingView = (TextView)findViewById(R.id.user_rating_view);
        this.overviewView = (TextView)findViewById(R.id.textview_overview);
        this.thumbnailView= (ImageView)findViewById(R.id.thumbnail_poster);

        Intent startingIntent = getIntent();
        if (startingIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieData = startingIntent.getStringExtra(Intent.EXTRA_TEXT);
            try {
                Movie bindToMovie = new Movie(movieData);
                displayMovie(bindToMovie);
            } catch (JSONException e) {
                displayError();
            }
        } else {
            displayError();
        }
    }

    private void displayMovie(Movie movie) {
        if (movie != null) {
            this.titleView.setText(movie.getTitle());
            this.originalTitleView.setText(getString(R.string.original_title) + " " + movie.getOriginalTitle());
            this.releaseDateView.setText(getString(R.string.release_date) + " " + movie.getReleastDate());
            this.userRatingView.setText(getString(R.string.user_rating) + " " + movie.getUserRating());
            String synopsis = movie.getOverview();
            if (synopsis == null || synopsis.length() == 0) {
                this.overviewView.setText(R.string.overview_blank);
            } else {
                this.overviewView.setText(synopsis);
            }
            Uri posterUrl = ImageUtils.buildThumbnailUri(movie.getPosterPath());
            if (posterUrl == null) {
                this.thumbnailView.setImageDrawable(null);
            } else {
                Picasso.with(this).load(posterUrl).into(this.thumbnailView);
            }
        }
    }

    private void displayError() {
        this.titleView.setText(null);
        this.originalTitleView.setText(null);
        this.releaseDateView.setText(null);
        this.userRatingView.setText(null);
        this.thumbnailView.setImageDrawable(null);
        this.overviewView.setText(getString(R.string.movie_missing));
    }
}
