package com.mzdhr.artificialintelligencenews.helper;

import android.util.Log;

import com.mzdhr.artificialintelligencenews.model.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mohammad on 11/26/17.
 * A class that help with making network requests.
 */

public class QueryUtils {
    private static final String TAG = QueryUtils.class.getSimpleName();

    // convert any string url into URL object, used in ArticleLoader.class
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "Problem building the URL ", exception);
            return null;
        }
        return url;
    }

    // making the connection request URL object, used in ArticleLoader.class
    public static String makeHttpRequest(URL url) throws IOException {
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

    // helper method to convert stream into readable string to used in makeHttpRequest();
    public static String readFromStream(InputStream inputStream) throws IOException {
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
        Log.d(TAG, "readFromStream: " + output.toString());
        return output.toString();
    }

    // helper method to convert  string json into Array List Object, used in ArticleLoader.class
    public static ArrayList<Article> extractFeatureFromJson(String articleJSON) {
        ArrayList<Article> articleArrayList = new ArrayList<>();
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
        try {// TODO: 11/29/17 no internet it will crash here! handel if intenrt is available then use this method
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responseJsonObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseJsonObject.getJSONArray("results");

            if (resultsArray.length() > 0) {
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject articleData = resultsArray.getJSONObject(i);
                    JSONArray tagsArray = articleData.getJSONArray("tags");

                    // extract values from json
                    title = articleData.getString("webTitle");
                    author = getAuthor(tagsArray);
                    date = getReadableDate(articleData.getString("webPublicationDate"));
                    section = " -" + articleData.getString("sectionName");
                    link = articleData.getString("webUrl");

                    // creating new article and add it to the article list
                    articleArrayList.add(new Article(title, author, date, section, link));
                }
                return articleArrayList;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the JSON results", e);
        }
        return null;
    }

    // helper method to return a nice format date to used it in extractFeatureFromJson();
    public static String getReadableDate(String rawDate){
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
        return "Unknown Date";
    }

    // helper method to return author name to used it in extractFeatureFromJson();
    public static String getAuthor(JSONArray tags) throws JSONException {
        if (tags.length() > 0) {
            JSONObject tag = tags.getJSONObject(0);
            return tag.getString("webTitle");
        } else {
            return "Unknown Author";
        }
    }

}
