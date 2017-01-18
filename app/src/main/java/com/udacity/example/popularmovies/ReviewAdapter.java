package com.udacity.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by hans.dykstra on 1/16/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private MovieReview[] reviews = null;

    public void swapMovieReviews(MovieReview[] reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    void showReview(Context context, MovieReview review) {
        // not implemented (could store the review URL in review object but no requirement
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.review_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < 0 || position >= reviews.length) {
            throw new IllegalArgumentException("Invalid view position " + position);
        }

        holder.bind(reviews[position]);
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView author;
        private TextView content;
        private MovieReview thisReview;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.review_author);
            content = (TextView)itemView.findViewById(R.id.review_content);
        }

        void bind(MovieReview review) {
            thisReview = review;
            author.setText(thisReview.getAuthor());
            content.setText(thisReview.getReview());
        }

        @Override
        public void onClick(View v) {
            if (thisReview != null) {
                ReviewAdapter.this.showReview(v.getContext(), thisReview);
            }
        }
    }
}
