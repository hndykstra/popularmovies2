package com.udacity.example.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Adajpter class to create and manage view holders for the poster view recycler.
 * Created by Hans on 12/26/2016.
 */

public class PosterListAdapter extends RecyclerView.Adapter<PosterListAdapter.PosterItem> {

    public interface MovieSelectionListener {
        public void movieSelected(Movie selectedMovie);
    }

    private Movie[] movies;
    private MovieSelectionListener listener;

    public PosterListAdapter(MovieSelectionListener listener) {
        this.listener = listener;
    }

    @Override
    public PosterItem onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int resourceId = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(resourceId, parent, false);
        return new PosterItem(view);
    }

    @Override
    public void onBindViewHolder(PosterItem holder, int position) {
        Movie movie = null;
        if (position >= 0 && position < movies.length) {
            movie = movies[position];
        }

        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.length;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    class PosterItem extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleView;
        private ImageView posterView;
        private Movie boundMovie;

        public PosterItem(View view) {
            super(view);

            titleView = (TextView)view.findViewById(R.id.title_poster);
            posterView = (ImageView)view.findViewById(R.id.image_poster);
            view.setOnClickListener(this);
        }

        public Movie getMovie() {
            return boundMovie;
        }

        public void bind(Movie aMovie) {
            this.boundMovie = aMovie;
            Log.d(PosterActivity.LOG_TAG, "Binding " + aMovie);
            if (this.boundMovie != null) {
                this.titleView.setText(this.boundMovie.getTitle());
                String path = this.boundMovie.getPosterPath();
                Uri posterUri = ImageUtils.buildPosterUri(path);
                Log.d(PosterActivity.LOG_TAG, "Poster path " + path + ", Uri built " + posterUri);
                if (posterUri == null) {
                    this.posterView.setImageDrawable(null);
                } else {
                    Picasso.with(this.posterView.getContext()).load(posterUri).into(this.posterView);
                }
            } else {
                this.titleView.setText("");
                this.posterView.setImageDrawable(null);
            }
        }

        @Override
        public void onClick(View v) {
            PosterListAdapter.this.listener.movieSelected(this.boundMovie);
        }
    }
}
