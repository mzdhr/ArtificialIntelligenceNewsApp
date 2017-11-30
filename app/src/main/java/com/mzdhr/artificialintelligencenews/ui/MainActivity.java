package com.mzdhr.artificialintelligencenews.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mzdhr.artificialintelligencenews.R;
import com.mzdhr.artificialintelligencenews.adapter.ArticleAdapter;
import com.mzdhr.artificialintelligencenews.loader.ArticleLoader;
import com.mzdhr.artificialintelligencenews.model.Article;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RAW_API_URL = "https://content.guardianapis.com/search?q=artificialintelligenceai&show-tags=contributor&api-key=42684dad-c1f8-4d02-8d95-f2f4cedb3055";
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter mAdapter = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private LinearLayout mEmptyStatView;
    private ImageView mEmptyStateImageView;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FindViews
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mListView = (ListView) findViewById(R.id.list);
        mEmptyStatView = (LinearLayout) findViewById(R.id.empty_view);
        mEmptyStateImageView = (ImageView) findViewById(R.id.empty_image_view);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Preparing Adapter
        mAdapter = new ArticleAdapter(this);

        // Preparing ListView, and setting Adapter and Empty-View to it.
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyStatView);

        // Setting onRefreshListener, so user can pull down and refresh the data if he needed.
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // If there is a network connection, kick off the loader to fetch data
                if (isNetworkAvailable()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    // Preparing Loader
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
                    Log.d(TAG, "onRefresh: Triggered from pull down");
                } else {
                    // If no connection
                    mProgressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_internet_connection);
                    mEmptyStateImageView.setImageResource(R.drawable.ic_wifi_off);
                    mAdapter.clear();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // If there is a network connection, kick off the loader to fetch data
        if (isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            //mAdapter.clear();
            // Preparing Loader
            Log.d(TAG, "onResume: Triggered from Resume");
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, MainActivity.this);
        } else {
            // If no connection
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            mEmptyStateImageView.setImageResource(R.drawable.ic_wifi_off);
            mAdapter.clear();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        // When user go out the app, then internet disconnect, then he return to the app.
        // the Loader trying to trigger!!! So I needed to destroyed it when app go in background.
        // then created it again in onResume.
        getLoaderManager().destroyLoader(ARTICLE_LOADER_ID);
    }


    // --------------
    // Loader Section
    // --------------
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
        return new ArticleLoader(this, RAW_API_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articlesData) {

        // Hide the ProgressBar if result come or not
        mProgressBar.setVisibility(View.GONE);

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid data, then add them to the adapter's (This will trigger the ListView to update).
        if (articlesData != null && !articlesData.isEmpty()){
            mAdapter.addAll(articlesData);
        } else {
            // Setting the Empty State view
            mEmptyStateTextView.setText(R.string.no_article_found);
            mEmptyStateImageView.setImageResource(R.drawable.ic_note_text);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        mAdapter.clear();
    }


}