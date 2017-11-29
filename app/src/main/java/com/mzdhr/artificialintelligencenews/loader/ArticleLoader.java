package com.mzdhr.artificialintelligencenews.loader;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import com.mzdhr.artificialintelligencenews.helper.QueryUtils;
import com.mzdhr.artificialintelligencenews.model.Article;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mohammad on 11/29/17.
 */

public class ArticleLoader extends AsyncTaskLoader<ArrayList<Article>>{

    private static final String TAG = ArticleLoader.class.getSimpleName();
    private String mUrl;

    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // create url object
        URL url = QueryUtils.createUrl(mUrl);

        // making an internet request, and getting json format from it
        String jsonResponse = "";
        try {
            jsonResponse = QueryUtils.makeHttpRequest(url);
        } catch (IOException e) {
            Log.d(TAG, "doInBackground: " + e);
        }

        // converting that json into ArrayList, and return it to Loader caller
        ArrayList<Article> articleArrayList = QueryUtils.extractFeatureFromJson(jsonResponse);
        return articleArrayList;
    }

}
