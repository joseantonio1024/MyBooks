package com.example.android.mybooks.model;

import android.support.annotation.NonNull;
import com.orm.SugarRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for providing content for user interfaces.
 */
public class BookContent {

    public static final String d = "DEBUG_JOSE: ";

    // The Firebase reference to download the books.
    public static final String FIREBASE_BOOKS_REFERENCE = "books";

    // An array (book) items.
    public static final List<BookItem> ITEMS = new ArrayList<>();

    // A map (book) items, by ID.
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<>();

    /**
     * A helper method that returns all the books in the local database
     * @return a list of books
     */
    public static List<BookItem> getBooks(){
        // returns a list with all the books on the local database.
        return BookItem.listAll(BookItem.class);
    }

    public static void updateLocalDatabase(){
        List<BookItem> firebaseList = ITEMS;
        List<BookItem> localDatabaseList = getBooks();
        List<BookItem> addedList = new ArrayList<>(firebaseList);
        List<BookItem> deletedList = new ArrayList<>();

        for(BookItem book: localDatabaseList){
            int firebaseIndex = addedList.indexOf(book);
            if(firebaseIndex<0){
                deletedList.add(book);
            }
        }




        //List<BookItem> booksFromLocalDatabase = BookItem.listAll(BookItem.class);
        //booksFromLocalDatabase.removeAll(deletedList);
        //booksFromLocalDatabase.addAll(addedList);
    }

     /**
     * Esta clase representa un libro.
     */
    public static class BookItem extends SugarRecord {
        private int mIdentificator;
        private String mAuthor;
        private String mDescription;
        private String mPublicationDate;
        private String mTitle;
        private String mUrlImage;

        public BookItem(){}
        public BookItem(int identificator,String author, String description, String publicationDate, String title, String url_image ) {
            mAuthor = author;
            mDescription = description;
            mPublicationDate = publicationDate;
            mTitle = title;
            mUrlImage = url_image;
            mIdentificator = identificator;
        }

        // OJO: los getters y setters tienen que tener el mismo nombre que las keys de la database de Firebase.
        public int getIdentificator(){
            return mIdentificator;
        }
        public String getAuthor() {
            return mAuthor;
        }
        public String getDescription() {
            return mDescription;
        }
        public String getPublication_date() {
            return mPublicationDate;
        }
        public String getTitle() {
            return mTitle;
        }
        public String getUrl_image() {
            return mUrlImage;
        }
        public void setIdentificator(int identificator){
            mIdentificator = identificator;
        }
        public void setAuthor(String author) {
            this.mAuthor = author;
        }
        public void setDescription(String description) {
            mDescription = description;
        }
        public void setPublication_date(String publicationDate) {
            mPublicationDate = publicationDate;
        }
        public void setTitle(String title) {
            mTitle = title;
        }
        public void setUrl_image(String url_image) {
            mUrlImage = url_image;
        }

        @Override @NonNull
        public String toString() {
            return mTitle + ". Written by " + mAuthor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookItem bookItem = (BookItem) o;
            return mIdentificator == bookItem.mIdentificator &&
                    Objects.equals(mAuthor, bookItem.mAuthor) &&
                    Objects.equals(mDescription, bookItem.mDescription) &&
                    Objects.equals(mPublicationDate, bookItem.mPublicationDate) &&
                    Objects.equals(mTitle, bookItem.mTitle) &&
                    Objects.equals(mUrlImage, bookItem.mUrlImage);
        }

        public boolean compareTo(Object o){
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BookItem bookItem = (BookItem) o;
            return mIdentificator == bookItem.mIdentificator &&
                    Objects.equals(mAuthor, bookItem.mAuthor) &&
                    Objects.equals(mDescription, bookItem.mDescription) &&
                    Objects.equals(mPublicationDate, bookItem.mPublicationDate) &&
                    Objects.equals(mTitle, bookItem.mTitle) &&
                    Objects.equals(mUrlImage, bookItem.mUrlImage);
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }// End class BookItem
}// End class BookContent
