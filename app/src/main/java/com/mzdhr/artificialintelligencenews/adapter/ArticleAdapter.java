package com.mzdhr.artificialintelligencenews.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mzdhr.artificialintelligencenews.R;
import com.mzdhr.artificialintelligencenews.model.Article;

import java.util.ArrayList;

/**
 * Created by mohammad on 11/24/17.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {

    public ArticleAdapter(@NonNull Context context, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false
            );
        }

        final Article currentArticle = getItem(position);
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_article_text_view);
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_article_text_view);
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_article_text_view);
        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_article_text_view);

        titleTextView.setText(currentArticle.getTitle());
        authorTextView.setText(currentArticle.getAuthor());
        dateTextView.setText(currentArticle.getDate());
        sectionTextView.setText(currentArticle.getSection());

        // setting on click listener to open the clicked article item in the browser
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You click -> " + currentArticle.getLink(), Toast.LENGTH_SHORT).show();
            }
        });


        return listItemView;
    }

}
