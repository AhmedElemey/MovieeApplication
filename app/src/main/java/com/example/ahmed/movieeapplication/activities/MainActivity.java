package com.example.ahmed.movieeapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ahmed.movieeapplication.R;
import com.example.ahmed.movieeapplication.fragments.Detail_Fragment;
import com.example.ahmed.movieeapplication.fragments.Movie_Fragment;
import com.example.ahmed.movieeapplication.retrofit.Result;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements Movie_Fragment.callback ,
         NavigationView.OnNavigationItemSelectedListener{

    private boolean mTwoPane = false;

    private DrawerLayout mDrawerLayout ;
    Toolbar toolbar;
    NavigationView navigationView;

    Movie_Fragment fragment;
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    SharedPreferences sharedPref;
    public static final String KEY_PREFS = "MY_KEY" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        fragment = new Movie_Fragment();
        fragment.setListener(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment1,fragment);
        transaction.commit();


        if (findViewById(R.id.detail_fragment) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment, new Detail_Fragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        if(savedInstanceState!=null){
            fragment.positionIndex = savedInstanceState.getInt(BUNDLE_RECYCLER_LAYOUT);
        }



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,toolbar,
                R.string.drawer_open,R.string.drawer_close);

        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null){
            outState.putInt(BUNDLE_RECYCLER_LAYOUT, fragment.positionIndex);
        }
    }



    @Override
    public void onItemSelected(Result result) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("result",result);
            Detail_Fragment fragment = new Detail_Fragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment, fragment)
                    .commit();


        } else {
            Intent intent = new Intent(this, DetailsActivity.class)
                    .putExtra("result", (Serializable) result);
            startActivity(intent);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        String titleItem = (String) item.getTitle();
        toolbar.setTitle(titleItem);

        int itemX = item.getItemId();

        switch (itemX) {


            case R.id.pop_movie:

                String value1 = "popular";
                SetSharedPreferences(value1);
                CallFragment();
                break;

            case R.id.top_movie:

                String value2 = "top_rated";
                SetSharedPreferences(value2);
                CallFragment();
                break;


            case R.id.fav_movie:

                String value4 = "Favourite";
                SetSharedPreferences(value4);
                CallFragment();
                break;




        }
            return true;
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            hideDrawer();

        else
            super.onBackPressed();
    }

    public void SetSharedPreferences (String type){

        sharedPref = getSharedPreferences(MY_PREFS_NAME ,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_PREFS , type);
        editor.commit();

    }

    public void CallFragment (){
        Movie_Fragment fragment = new Movie_Fragment();
        fragment.setListener(this);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment1,fragment);
        transaction.commit();
        hideDrawer();

    }
}
