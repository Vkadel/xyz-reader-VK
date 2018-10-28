package com.example.xyzreader.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "article_table")
public class Article {
    @PrimaryKey
    int id;
    @ColumnInfo(name = "author")
    String author;
    @ColumnInfo(name = "body")
    String body;
    @ColumnInfo(name = "aspect_ratio")
    String aspect_ratio;
    @ColumnInfo(name = "photo")
    String photo;
    @ColumnInfo(name = "published_date")
    String published_date;
    @ColumnInfo(name = "thumb")
    String thumb;
    @ColumnInfo(name = "title")
    String title;

    public Article(int id,String author,String Body,String thumb,
                   String aspect_ratio,String photo, String title){
    }
    public Article(){}

    public int getId() {
        return id;
    }

    public String getAspect_ratio() {
        return aspect_ratio;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPublished_date() {
        return published_date;
    }

    public String getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setAspect_ratio(String aspect_ratio) {
        this.aspect_ratio = aspect_ratio;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setPublished_date(String published_date) {
        this.published_date = published_date;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
