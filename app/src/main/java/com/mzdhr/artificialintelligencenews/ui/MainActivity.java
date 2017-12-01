package com.mzdhr.artificialintelligencenews.ui;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mzdhr.artificialintelligencenews.R;
import com.mzdhr.artificialintelligencenews.adapter.ArticleAdapter;
import com.mzdhr.artificialintelligencenews.loader.ArticleLoader;
import com.mzdhr.artificialintelligencenews.model.Article;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Building the API Query with Settings
        // There are two Settings -> select article section, and select article number.
        String selectSection = sharedPrefs.getString(
                getString(R.string.settings_section_by_key),
                getString(R.string.settings_section_by_default)
        );

        String articleNumber = sharedPrefs.getString(
                getString(R.string.settings_article_number_key),
                getString(R.string.setting_article_number_default)
        );

        Uri baseUri = Uri.parse(RAW_API_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // "all" is default value to show all articles in all sections.
        // But the api does not support this value "all", so I used this way to jump it.
        if (!selectSection.equals("all")){
            uriBuilder.appendQueryParameter("section", selectSection);
        }
        // "page-size" is supported with all its values, default is 10.
        uriBuilder.appendQueryParameter("page-size", articleNumber);

        return new ArticleLoader(this, uriBuilder.toString());
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


    // --------------
    // Menu Setting Section
    // --------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}