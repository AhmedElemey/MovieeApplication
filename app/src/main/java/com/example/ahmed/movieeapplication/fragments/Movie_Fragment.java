package com.example.ahmed.movieeapplication.fragments;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.ahmed.movieeapplication.Adapters.MainAdapter;
import com.example.ahmed.movieeapplication.R;
import com.example.ahmed.movieeapplication.database.MoviesCPContract;
import com.example.ahmed.movieeapplication.retrofit.Api;
import com.example.ahmed.movieeapplication.retrofit.Movies;
import com.example.ahmed.movieeapplication.retrofit.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;



public class Movie_Fragment extends Fragment implements MainAdapter.onMovieClick  {
    List<Result> results;
    Movies movies;
    MainAdapter adapter;
    RecyclerView recyclerview;
    Movie_Fragment.callback callback;

    @Override
    public void onMovie(int position) {
        callback.onItemSelected(results.get(position));
    }


    public void setListener(Movie_Fragment.callback call){
        callback = call;
    }

    public static int positionIndex = 0;


    public interface callback{
        void onItemSelected(Result result);
    }

    public Movie_Fragment(){}

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Log.e("testing", positionIndex+"");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMovies();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        positionIndex= ((GridLayoutManager)recyclerview.getLayoutManager()).findLastCompletelyVisibleItemPosition();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_items,container,false);
        results = new ArrayList<>();
        recyclerview = (RecyclerView) v.findViewById(R.id.rv_recycler);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }
        else {
            recyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        }
        adapter = new MainAdapter(results,getActivity());
        adapter.setListener(this);
        recyclerview.setAdapter(adapter);

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
           results = savedInstanceState.getParcelableArrayList("arrayList");
        }
    }


    String getsort(){
        String MY_PREFS_NAME = "MyPrefsFile";
        String KEY_PREFS = "MY_KEY" ;
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME ,MODE_PRIVATE);
        final String sort = sharedPref.getString( KEY_PREFS ,"popular");
        return sort;

    }

    void getMovies(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        final Api api = retrofit.create(Api.class);


        api.getJson(1).enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                movies = response.body();
                results.clear();
                results.addAll(movies.getResults());

                    if (getsort() == "popular") {
                        api.sortByPopularity(1, "popular").enqueue(new Callback<Movies>() {
                        @Override
                        public void onResponse(Call<Movies> call, Response<Movies> response) {
                            results.clear();
                            movies = response.body();
                            results.addAll(movies.getResults());
                            adapter.notifyDataSetChanged();
                            recyclerview.scrollToPosition(positionIndex);

                        }

                        @Override
                        public void onFailure(Call<Movies> call, Throwable t) {
                            Toast.makeText(getActivity(), "sort by popular faild !", Toast.LENGTH_SHORT).show();

                        }
                    });

                    } else if (getsort() == "top_rated") {

                        api.sortByRate(1, "top_rated").enqueue(new Callback<Movies>() {
                            @Override
                            public void onResponse(Call<Movies> call, Response<Movies> response) {
                                results.clear();
                                movies = response.body();
                                results.addAll(movies.getResults());
                                adapter.notifyDataSetChanged();
                                recyclerview.scrollToPosition(positionIndex);
                            }

                            @Override
                            public void onFailure(Call<Movies> call, Throwable t) {
                                Toast.makeText(getActivity(), "sort by top rated faild !", Toast.LENGTH_SHORT).show();
                                checkFavorites();
                            }
                        });

                    } else if (getsort() == "upcoming") {

                        api.sortByUpComing(1, "upcoming").enqueue(new Callback<Movies>() {
                            @Override
                            public void onResponse(Call<Movies> call, Response<Movies> response) {
                                results.clear();
                                movies = response.body();
                                results.addAll(movies.getResults());
                                adapter.notifyDataSetChanged();
                                recyclerview.scrollToPosition(positionIndex);
                            }

                            @Override
                            public void onFailure(Call<Movies> call, Throwable t) {
                                Toast.makeText(getActivity(), "sort by top rated faild !", Toast.LENGTH_SHORT).show();
                                checkFavorites();
                            }
                        });

                    } else if (getsort() == "now_playing") {

                        api.sortByPlaying(1, "now_playing").enqueue(new Callback<Movies>() {
                            @Override
                            public void onResponse(Call<Movies> call, Response<Movies> response) {
                                results.clear();
                                movies = response.body();
                                results.addAll(movies.getResults());
                                adapter.notifyDataSetChanged();
                                recyclerview.scrollToPosition(positionIndex);
                            }

                            @Override
                            public void onFailure(Call<Movies> call, Throwable t) {
                                Toast.makeText(getActivity(), "sort by top rated faild !", Toast.LENGTH_SHORT).show();
                                checkFavorites();
                            }
                        });


                    }else if (getsort() == "Favourite") {

                    checkFavorites();
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {

                Toast.makeText(getActivity(), R.string.alert, Toast.LENGTH_LONG).show();
                checkFavorites();
            }
        });



    }


    private void checkFavorites() {
        new AsyncTask<Void,Void,ArrayList<Result>>() {

            @Override
            protected ArrayList<Result> doInBackground(Void... params) {
                ArrayList<Result>  favoriteMovies = new ArrayList<Result>();
                try {
                    Cursor cursor =  getActivity().getContentResolver().query(MoviesCPContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesCPContract.MovieEntry.COLUMN_TITLE);

                    favoriteMovies.addAll(convertCursorToMovies(cursor));

                }
                catch (Exception e) {
                    Log.e("DERP", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return favoriteMovies;
                }

                return favoriteMovies;
            }

            @Override
            protected void onPostExecute(ArrayList<Result> mresult) {
                results.clear();
                results.addAll(mresult);
                adapter.notifyDataSetChanged();
            }
        }.execute();


    }
    private ArrayList<Result> convertCursorToMovies(Cursor cursor) {

        int idIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry._ID);
        int originalLanguageIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        int titleIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_TITLE);
        int originalTitleIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int adultIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_ADULT);
        int popularityIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_POPULARITY);
        int posterPathIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_POSTER_PATH);
        int overviewIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int voteCountIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_VOTE_COUNT);
        int videoIndex = cursor.getColumnIndex(MoviesCPContract.MovieEntry.COLUMN_VIDEO);

        ArrayList<Result> favoriteMovies = new ArrayList<Result>();

        while(cursor.moveToNext()) {

            Result movieModel = new Result();
            movieModel.setId(cursor.getInt(idIndex));
            movieModel.setOriginalLanguage(cursor.getString(originalLanguageIndex));
            movieModel.setOriginalTitle(cursor.getString(originalTitleIndex));
            movieModel.setTitle(cursor.getString(titleIndex));
            movieModel.setAdult(cursor.getInt(adultIndex) > 0);
            movieModel.setPopularity(cursor.getDouble(popularityIndex));
            movieModel.setPosterPath(cursor.getString(posterPathIndex));
            movieModel.setOverview(cursor.getString(overviewIndex));
            movieModel.setReleaseDate(cursor.getString(releaseDateIndex));
            movieModel.setVoteAverage(cursor.getDouble(voteAverageIndex));
            movieModel.setVoteCount(cursor.getInt(voteCountIndex));
            movieModel.setVideo(cursor.getInt(videoIndex) > 0);

            if(!favoriteMovies.contains(movieModel)) favoriteMovies.add(movieModel);
        }
        cursor.close();
        return favoriteMovies;

    }

}
