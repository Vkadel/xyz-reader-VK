package com.example.xyzreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.ui.articlelist.ArticleListFragment;

public class ArticleList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_list_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new ArticleListFragment())
                    .commit();
        }
    }
}
