package com.example.xyzreader.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface ArticleDao {
    @Query("SELECT * FROM article_table")
    LiveData<List<Article>> getAll();

    @Query("SELECT * FROM article_table WHERE id IN (:articleIds)")
    List<Article> loadAllbyID(int[] articleIds);

    @Query("SELECT * FROM article_table WHERE id IN (:articleId)")
    Article loadOnebyID(int articleId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOne(Article... articles);

    @Delete
    void delete(Article user);
}
