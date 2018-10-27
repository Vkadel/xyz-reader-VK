package com.example.xyzreader.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.xyzreader.remote.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ArticleRepository {
    private final String TAG=getClass().getSimpleName();
    private ArticleDao mArticleDao;
    private LiveData<List<Article>> mArticles;
    private List<Article> mArticleList;
    private Boolean notInitialized = false;

    public ArticleRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        Timber.d("VK:Getting Items updated");
        mArticleDao = db.ArticleDao();

        mArticles = mArticleDao.getAll();

        //If mArticles is empty call an Async Task to update online. Or if
        //is the first Time the Repository is initialized
        if (!notInitialized) {
            refreshItemsOnline(mArticles);
            Timber.d("VK: Started Online Update");
        }

    }

    public LiveData<List<Article>> getallArticles() {
        return mArticles;
    }

    public void refreshItemsOnline(LiveData<List<Article>> mArticles) {
        new getJsonArrayOnline(mArticleDao).execute(mArticles);
    }

    public void insert(Article article) {
        new insertAsyncTask(mArticleDao).execute(article);
    }

    private static class insertAsyncTask extends AsyncTask<Article, Void, Void> {

        private ArticleDao mAsyncTaskDao;

        insertAsyncTask(ArticleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Article... params) {
            mAsyncTaskDao.insertOne(params[0]);
            return null;
        }
    }

    private static class getJsonArrayOnline extends AsyncTask<LiveData<List<Article>>, Void, Void> {

        private ArticleDao mAsyncTaskDao;
        private JSONArray array;
        private List<Article> mArticles;
        getJsonArrayOnline(ArticleDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(LiveData<List<Article>>... liveData) {
            List<Article> mArticlesJSONList = new ArrayList<>();
            try {
                array = RemoteEndpointUtil.fetchJsonArray();
                int arraysize = array.length();
                if (array == null) {
                    throw new JSONException("Invalid parsed item array");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                for (int i = 0; i <= array.length() - 1; i++) {
                    Article thisArticle = new Article();
                    JSONObject object = new JSONObject();
                    object = array.getJSONObject(i);
                    thisArticle.setAuthor(object.getString("author"));
                    thisArticle.setId(Integer.parseInt(object.getString("id")));
                    thisArticle.setBody(object.getString("body"));
                    thisArticle.setPhoto(object.getString("photo"));
                    thisArticle.setAspect_ratio(object.getString("aspect_ratio"));
                    thisArticle.setPublished_date(object.getString("published_date"));
                    thisArticle.setThumb(object.getString("thumb"));
                    mAsyncTaskDao.insertOne(thisArticle);
                    Log.i("Updating_online", "doInBackground: "+i);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mArticles=mAsyncTaskDao.getAll().getValue();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

}
