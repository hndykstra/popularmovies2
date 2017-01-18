package com.udacity.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Patterned off the recycler view so it could be reused this way, but won't actually be used with
 * a recycler view. But it does separate the view intricacies from the overall detail activity.
 * Created by hans.dykstra on 1/16/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private MovieTrailer[] trailers;

    public void swapMovieTrailers(MovieTrailer[] trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    private void showTrailer(Context context, MovieTrailer trailer) {
        String src = trailer.getTrailerSource();
        if (src != null && src.length() > 0) {
            Uri trailerUri = Uri.parse(context.getString(R.string.trailer_base_url)).buildUpon()
                    .appendQueryParameter(context.getString(R.string.trailer_source_query), src)
                    .build();
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, trailerUri);
            context.startActivity(videoIntent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < 0 || position >= trailers.length) {
            throw new IllegalArgumentException("Invalid view position " + position);
        }
        holder.bind(trailers[position]);
    }

    @Override
    public int getItemCount() {
        return trailers == null ? 0 : trailers.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView trailerName;
        private MovieTrailer thisTrailer;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerName = (TextView)itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        void bind(MovieTrailer trailer) {
            trailerName.setText(trailer.getTrailerName());
            thisTrailer = trailer;
        }

        @Override
        public void onClick(View v) {
            TrailerAdapter.this.showTrailer(itemView.getContext(), this.thisTrailer);
        }
    }
}
