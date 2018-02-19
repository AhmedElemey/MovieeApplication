package com.example.ahmed.movieeapplication.fragments;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ahmed.movieeapplication.R;
import com.example.ahmed.movieeapplication.Adapters.ReviewAdapter;
import com.example.ahmed.movieeapplication.Adapters.TrailerAdapter;
import com.example.ahmed.movieeapplication.activities.DetailsActivity;
import com.example.ahmed.movieeapplication.database.MoviesCPContract;
import com.example.ahmed.movieeapplication.retrofit.Api;
import com.example.ahmed.movieeapplication.retrofit.MovieReview;
import com.example.ahmed.movieeapplication.retrofit.MovieTrailer;
import com.example.ahmed.movieeapplication.retrofit.Result;
import com.example.ahmed.movieeapplication.retrofit.TrailerResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Detail_Fragment extends Fragment {
    String TAG = DetailsActivity.class.getSimpleName();
    ImageView poster,poster1,btnFav;

    TextView title,date,vote,over;

    RadioGroup rdGroup;

    RecyclerView trailerList;

    RecyclerView peopleList;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    Result movie;
    String imageURL;
    Api api;
    List<TrailerResult> trailerResultslist;
    List<MovieReview.ResultsEntity> reviewsResultslist;
    Context context;
    int id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail,container,false);
        poster=(ImageView)view.findViewById(R.id.iv_detail_poster);
        poster1=(ImageView)view.findViewById(R.id.iv_detail_poster1);
        btnFav=(ImageView)view.findViewById(R.id.btn_fav);
        title=(TextView)view.findViewById(R.id.tv_title);
        date=(TextView)view.findViewById(R.id.tv_date);
        over=(TextView)view.findViewById(R.id.tv_over);
        vote=(TextView)view.findViewById(R.id.tv_vote);
        rdGroup=(RadioGroup)view.findViewById(R.id.radio_group);
        trailerList=(RecyclerView)view.findViewById(R.id.trailer_list);
        peopleList=(RecyclerView)view.findViewById(R.id.people_list);
        ButterKnife.bind(this,view);
        trailerResultslist = new ArrayList<>();
        reviewsResultslist = new ArrayList<>();
        movie = (Result) getArguments().getSerializable("result");
        if (isFavorite(movie)) {
            btnFav.setImageResource(R.drawable.ic_favourite);
        } else {
            btnFav.setImageResource(R.drawable.ic_unfavourite);
        }

        title.setText(movie.getTitle());
        date.setText(movie.getReleaseDate());
        over.setText(movie.getOverview());
        vote.setText(""+movie.getVoteAverage());
        id = movie.getId();
        Log.wtf("dasd", id + "");
        imageURL = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();


        Picasso.with(getActivity()).load(imageURL).into(poster);
        Picasso.with(getActivity()).load(imageURL).into(poster1);


        trailerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        trailerAdapter = new TrailerAdapter(trailerResultslist,getActivity());
        trailerList.setAdapter(trailerAdapter);

        reviewAdapter = new ReviewAdapter(reviewsResultslist);
        peopleList.setLayoutManager(new LinearLayoutManager(getActivity()));
        peopleList.setAdapter(reviewAdapter);



        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFavorite(movie)){
                    removeMovieFromContentProvider(movie);
                } else {
                    addMovieToContentProvider(movie);
                }
            }
        });

        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (i == R.id.reviews_radio){
                    peopleList.setVisibility(View.VISIBLE);
                    trailerList.setVisibility(View.GONE);
                }
                else {
                    peopleList.setVisibility(View.GONE);
                    trailerList.setVisibility(View.VISIBLE);

                }
            }
        });

        review();
        trailers();
        return view;
    }


    private void trailers() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        final Api api = retrofit.create(Api.class);
        api.trailer(""+id).enqueue(new Callback<MovieTrailer>() {
            @Override
            public void onResponse(Call<MovieTrailer> call, Response<MovieTrailer> response) {
                Log.i(TAG,"onResponse");
                Log.i(TAG,"onResponse response.body()");
                MovieTrailer movieTrailer = response.body();
                trailerResultslist.addAll(movieTrailer.getResults());
                trailerAdapter.notifyDataSetChanged();

            }
            @Override
            public void onFailure(Call<MovieTrailer> call, Throwable t) {
                Log.i(TAG," trailer onFailure "+t.getMessage());
            }
        });
    }

    private void review() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        final Api api = retrofit.create(Api.class);
        api.reviews(id + "", 1).enqueue(new Callback<MovieReview>() {
            @Override
            public void onResponse(Call<MovieReview> call, Response<MovieReview> response) {
                Log.i(TAG, "url "+response.raw().toString());
                MovieReview movieReview = response.body();
                reviewsResultslist.addAll(movieReview.getResults());
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieReview> call, Throwable t) {


            }


        });
    }
    private void addMovieToContentProvider(Result movieModel) {

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesCPContract.MovieEntry._ID, movieModel.getId());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, movieModel.getOriginalLanguage());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_TITLE, movieModel.getTitle());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movieModel.getOriginalTitle());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_ADULT, movieModel.getAdult());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_POPULARITY, movieModel.getPopularity());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_POSTER_PATH, movieModel.getPosterPath());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_OVERVIEW, movieModel.getOverview());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_RELEASE_DATE, movieModel.getReleaseDate());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_VOTE_AVERAGE, movieModel.getVoteAverage());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_VOTE_COUNT, movieModel.getVoteCount());
            contentValues.put(MoviesCPContract.MovieEntry.COLUMN_VIDEO, movieModel.getVideo());
            Uri uri = getActivity().getContentResolver().insert(MoviesCPContract.MovieEntry.CONTENT_URI, contentValues);

            btnFav.setImageResource(R.drawable.ic_favourite);

            Log.w("URI: ", uri.toString());
        }catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
    }

    private void removeMovieFromContentProvider(Result movieModel) {
        try {
            int id = movieModel.getId();
            String stringId = Integer.toString(id);
            Uri uri = MoviesCPContract.MovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            getActivity().getContentResolver().delete(uri, null, null);
            btnFav.setImageResource(R.drawable.ic_unfavourite);
        }
        catch (UnsupportedOperationException e){
            e.printStackTrace();
        }
    }

    private boolean isFavorite(Result movieModel) {
        Uri singleUri = ContentUris.withAppendedId(MoviesCPContract.MovieEntry.CONTENT_URI,movieModel.getId());
        String[] titleColumn = {MoviesCPContract.MovieEntry.COLUMN_TITLE};
        Cursor coverCursor = getActivity().getContentResolver().query(singleUri,titleColumn , null, null, null);

        boolean isFavorite = coverCursor.getCount() > 0 ? true : false;
        coverCursor.close();
        return isFavorite;
    }
}

