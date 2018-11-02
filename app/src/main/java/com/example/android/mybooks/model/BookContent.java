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

    // Method that updates the local database with the changes done in the server.
    public static void updateLocalDatabase(){
        List<BookItem> localDatabaseList = getBooks();
        List<BookItem> addedList = new ArrayList<>(ITEMS);
        List<BookItem> deletedList = new ArrayList<>();

        for(BookItem book: localDatabaseList){
            //
            int index = addedList.indexOf(book);
            // If index<0 the book in the server has been deleted or modified.
            if(index<0){
                // so we add it to deletedList books.
                deletedList.add(book);
            }else{// otherwise the book in the server has not changed so we remove it from addedList
                addedList.remove(index);
                // at the end of the loop, addedList will only contain added books in the server.
            }
        }

        // Deletes in the local database all the books deleted and modified in the server.
        for(BookItem bookFromLocalDatabase: localDatabaseList){
            for(BookItem bookToDelete: deletedList){
                // If titles match, deletes the book.
                if (bookFromLocalDatabase.getTitle() == bookToDelete.getTitle()) {
                    bookFromLocalDatabase.delete();
                }
            }
        }

        // Adds to the local database all the books added and modified in the server.
        for(BookItem bookToAdd: addedList){
            bookToAdd.save();
        }
    }

    // Shows the books from the local database.
    public static void showLocalDatabase(){
        List<BookContent.BookItem> books = BookContent.getBooks();
        BookContent.ITEMS.clear();
        BookContent.ITEM_MAP.clear();
        for (BookContent.BookItem book : books) {
            BookContent.ITEMS.add(book);
            BookContent.ITEM_MAP.put(String.valueOf(book.getIdentificator()), book);
        }
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
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            BookItem bookItem = (BookItem) object;
            return  (mIdentificator == bookItem.mIdentificator) &&
                    (Objects.equals(mAuthor, bookItem.mAuthor)) &&
                    (Objects.equals(mDescription, bookItem.mDescription)) &&
                    (Objects.equals(mPublicationDate, bookItem.mPublicationDate)) &&
                    (Objects.equals(mTitle, bookItem.mTitle)) &&
                    (Objects.equals(mUrlImage, bookItem.mUrlImage));
        }

        @Override
         public int hashCode(){
            return Objects.hash(mAuthor,mDescription,mPublicationDate,mTitle,mUrlImage);
        }
    }// End class BookItem
}// End class BookContent
