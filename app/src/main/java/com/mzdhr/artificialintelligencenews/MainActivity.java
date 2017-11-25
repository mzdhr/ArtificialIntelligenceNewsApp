package com.mzdhr.artificialintelligencenews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.mzdhr.artificialintelligencenews.adapter.ArticleAdapter;
import com.mzdhr.artificialintelligencenews.model.Article;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ArrayList<Article> articles = new ArrayList<>();
        articles.add(new Article("Study AI: 'I believe we could see the end of cancer in our lifetime'"));
        articles.add(new Article("Master of machines: the rise of artificial intelligence calls for postgrad experts"));
        articles.add(new Article("Floods, locust farms and teens in charge: Blast Theory's vision of Hull in 2097"));
        articles.add(new Article("How aredsffds fisdlfdilufuidhsu ifsduihfu isdufsd f you today?"));
        articles.add(new Article("Experimental films? Putting movie science under the microscope"));
        articles.add(new Article("Why, exactly, would anyone want to use AI to decide whether I’m gay or straight? | Matthew Todd"));
        articles.add(new Article("Japanese company replaces office workers with artificial intelligence"));
        articles.add(new Article("Robots v experts: are any human professions safe from automation?"));
        articles.add(new Article("Life 3.0 by Max Tegmark review – we are ignoring the AI apocalypse"));


        ArticleAdapter articleAdapter = new ArticleAdapter(this, articles);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(articleAdapter);

    }
}
