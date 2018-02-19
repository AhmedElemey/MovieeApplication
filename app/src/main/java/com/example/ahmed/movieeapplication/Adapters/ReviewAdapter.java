package com.example.ahmed.movieeapplication.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ahmed.movieeapplication.R;
import com.example.ahmed.movieeapplication.retrofit.MovieReview;

import java.util.List;



public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<MovieReview.ResultsEntity> data;

    public ReviewAdapter(List<MovieReview.ResultsEntity> data) {
        this.data=data;

    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_custome, parent, false);
        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.ViewHolder holder, final int position) {
        MovieReview.ResultsEntity resultsEntity = data.get(position);

        holder.title.setText(resultsEntity.getAuthor());
        holder.reviewcontent.setText(resultsEntity.getContent());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title,reviewcontent;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.auther_review);
            reviewcontent = (TextView) itemView.findViewById(R.id.content_review);

        }
    }


}
