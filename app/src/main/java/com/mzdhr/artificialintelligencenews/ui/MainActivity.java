package com.mzdhr.artificialintelligencenews.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mzdhr.artificialintelligencenews.R;
import com.mzdhr.artificialintelligencenews.adapter.ArticleAdapter;
import com.mzdhr.artificialintelligencenews.model.Article;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RAW_API = "https://content.guardianapis.com/search?q=artificialintelligenceai&show-tags=contributor&api-key=42684dad-c1f8-4d02-8d95-f2f4cedb3055";
    private ArrayList<Article> mArticleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kick off an AsyncTask
        ArticleAsyncTask task = new ArticleAsyncTask();
        task.execute();
    }


    private class ArticleAsyncTask extends AsyncTask<URL, Void, ArrayList<Article>> {   // Params, Progress, Result

        @Override
        protected ArrayList<Article> doInBackground(URL... urls) {  // ... means any number of args are welcomed (url1, url2, url3...)
            // Don't perform the request if there are no URLs, or the first URL is null.
//            if (urls.length < 1 || urls[0] == null) {
//                return null;
//            }

            // Create URL object
            URL url = createUrl(RAW_API);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);    // TODO: 11/26/17 move to -> QueryUtils class
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e);
            }
            ArrayList<Article> articleArrayList = extractFeatureFromJson(jsonResponse);
            return articleArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articleArrayList) {
            // If there is no result, do nothing.
            if (articleArrayList == null) {
                return;
            }


            ArticleAdapter articleAdapter = new ArticleAdapter(MainActivity.this, articleArrayList);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(articleAdapter);
        }


        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(TAG, "Problem building the URL ", exception);
                return null;
            }
            return url;
        }


        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                // setting the connection, then connect
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                // checking and getting the data
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.d(TAG, "makeHttpRequest Error response code: " + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                Log.d(TAG, "makeHttpRequest: " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }


        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                // InputStreamReader convert all data to 0 and 1
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                // BufferReader convert all data to what we want (text)
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }


        private ArrayList<Article> extractFeatureFromJson(String articleJSON) {
            mArticleList.clear();
            String title;
            String author;
            String date;
            String section;
            String link;

            /*
            Tree of json api response:
                response ->
                            results[] ->
                                         article data, tags[]
             */
            try {
                JSONObject baseJsonResponse = new JSONObject(articleJSON);
                JSONObject responseJsonObject = baseJsonResponse.getJSONObject("response");
                JSONArray resultsArray = responseJsonObject.getJSONArray("results");

                if (resultsArray.length() > 0) {
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject articleData = resultsArray.getJSONObject(i);
                        JSONArray tagsArray = articleData.getJSONArray("tags");

                        // extract values from json
                        title = articleData.getString("webTitle");
                        author = getSection(tagsArray);
                        date = getReadableDate(articleData.getString("webPublicationDate"));
                        section = " -" + articleData.getString("sectionName");
                        link = articleData.getString("webUrl");

                        // creating new article and add it to the article list
                        mArticleList.add(new Article(title, author, date, section, link));
                    }
                    return mArticleList;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the JSON results", e);
            }
            return null;
        }

        private String getReadableDate(String rawDate){
            String rawDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
            String readableDatePattern = "MMM d, yyy";
            SimpleDateFormat dateFormatter = new SimpleDateFormat(rawDatePattern, Locale.US);
            try {
                Date parsedJsonDate = dateFormatter.parse(rawDate);
                SimpleDateFormat formatter = new SimpleDateFormat(readableDatePattern, Locale.US);
                String readableDate = formatter.format(parsedJsonDate);
                return readableDate;
            } catch (ParseException e) {
                Log.d(TAG, "getReadableDate: Error ParseException: " + e);
            }
            return "";
        }

        private String getSection(JSONArray tags) throws JSONException {
            if (tags.length() > 0) {
                JSONObject tag = tags.getJSONObject(0);
                return tag.getString("webTitle");
            } else {
                return "Unknown";
            }
        }


    }
}