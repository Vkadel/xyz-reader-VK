package com.example.xyzreader.ui.articlelist;

import android.app.Fragment;
import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.room.Article;
import com.example.xyzreader.ui.ArticleListActivity;

import java.util.List;

public class ArticleListFragment extends Fragment {

    private ArticleListViewModel mViewModel;
    private List<Article> myListofArticles=null;
    private LiveData<List<Article>> myListofArticlesLive;
    public RecyclerView mRecyclerView;
    private ViewGroup viewGroup;
    private  ArticleListActivity.myArticleAdapter mAdapter;
    private List<Article> mArticles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.article_list_fragment, container, false);
        mRecyclerView =view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }
    public void SetTheGoodies(ArticleListActivity.myArticleAdapter adapter, List<Article> articles){
        mAdapter=adapter;
        mArticles=articles;
    }
    public ArticleListActivity.myArticleAdapter GetTheGoodies(){
        return mAdapter;
    }
    public List<Article> getSecondGoodie(){
        return mArticles;
    }

}
