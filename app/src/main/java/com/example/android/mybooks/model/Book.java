package com.example.android.mybooks.model;

import android.support.annotation.NonNull;

// clase para guardar todos los atributos de un libro.
public class Book {
    private int mIdentificator;
    private String mAuthor;
    private String mDescription;
    private String mPublicationDate;
    private String mTitle;
    private String mUrlImage;

    public Book(String author, String description, String publicationDate, String title, String url_image) {
        mAuthor = author;
        mDescription = description;
        mPublicationDate = publicationDate;
        mTitle = title;
        mUrlImage = url_image;
    }

    public Book(){}


    // OJO: los getters y setters tienen que tener el mismo nombre que las keys de la database.
    public void setIdentificator(int id){
        mIdentificator = id;
    }

    public int getIdentificator(){
        return mIdentificator;
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
        this.mAuthor = author;
    }

    public String getPublication_date() {
        return mPublicationDate;
    }

    public void setPublication_date(String publicationDate) {
        mPublicationDate = publicationDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrl_image() {
        return mUrlImage;
    }

    public void setUrl_image(String url_image) {
        mUrlImage = url_image;
    }



    @Override @NonNull
    public String toString() {
        return mTitle + " escrito por " + mAuthor;
    }
}
