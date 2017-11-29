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

    public Article(String title, String author, String date, String section, String link) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mSection = section;
        mLink = link;
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getSection() {
        return mSection;
    }

    public void setSection(String section) {
        mSection = section;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
