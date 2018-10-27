package com.example.xyzreader.ui.articlelist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.xyzreader.room.Article;
import com.example.xyzreader.room.ArticleRepository;

import java.util.List;

public class ArticleListViewModel extends AndroidViewModel {
    final String TAG = getClass().getSimpleName();
    private LiveData<List<Article>> articles;
    private ArticleRepository mRepository;

    @Override
    protected void onCleared() {
        super.onCleared();
        mRepository = null;
        articles = null;
    }

    public ArticleListViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ArticleRepository(application);
        articles = mRepository.getallArticles();
    }


    public LiveData<List<Article>> getArticles() {
        return articles;
    }

    public void insert(Article article) {
        mRepository.insert(article);
    }

    public void loadArticlesOnline() {
        // Do an asynchronous operation to fetch Articles.
        //Getting instance of Room DataBase
        String author = articles.getValue().get(0).getAuthor();
        Log.e(TAG, "ArticleListViewModel: " + author);
        mRepository.refreshItemsOnline(articles);
    }
}

