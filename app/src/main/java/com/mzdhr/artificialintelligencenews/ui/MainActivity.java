package com.mzdhr.artificialintelligencenews.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mzdhr.artificialintelligencenews.R;
import com.mzdhr.artificialintelligencenews.adapter.ArticleAdapter;
import com.mzdhr.artificialintelligencenews.loader.ArticleLoader;
import com.mzdhr.artificialintelligencenews.model.Article;
import android.widget.ListView;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {
    private static final String RAW_API_URL = "https://content.guardianapis.com/search?q=artificialintelligenceai&show-tags=contributor&api-key=42684dad-c1f8-4d02-8d95-f2f4cedb3055";
    private static final int ARTICLE_LOADER_ID = 1;
    private ArticleAdapter mAdapter = null;
    private ListView mListView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FindViews
        mListView = (ListView) findViewById(R.id.list);

        // Preparing Adapter
        mAdapter = new ArticleAdapter(this);

        // Preparing Loader, and kick it off
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
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
        mAdapter.clear();

        if (articlesData != null && !articlesData.isEmpty()){
            mAdapter.addAll(articlesData);
            mListView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        mAdapter.clear();
    }

}