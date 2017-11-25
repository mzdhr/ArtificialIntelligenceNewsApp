package com.mzdhr.artificialintelligencenews.model;

/**
 * Created by mohammad on 11/24/17.
 */

public class Article {
    private String mTitle;
    private String mAuthor;
    private String mDate;
    private String mSection;
    private String mLink;



    public Article(String title) {
        mTitle = title;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
