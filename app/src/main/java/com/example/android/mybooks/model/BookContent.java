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

    // An array of Books
    public static final List<BookItem> ITEMS = new ArrayList<>();

    // A map of books, by Title.
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<>();

    /**
     * A helper method that returns all the books in the local database
     * @return a list of books
     */
    public static List<BookItem> getBooks(){
        // returns a list with all the books on the local database.
        return BookItem.listAll(BookItem.class);
    }

    /**
     * A helper method that checks if a bookFromServer exists in the local database
     * @param bookFromServer a book from the server
     * @return true if the book exists in the local database. False otherwise.
     */
    public static boolean exists(BookItem bookFromServer){
        // Gets the local book's database.
        List<BookItem> booksFromLocalDatabase = getBooks();
        // If the local database is empty, return 'false' in order to fill it with the first book.
        if(booksFromLocalDatabase.isEmpty())
            return false;

        // Compares all the books from the local database with the book downloaded from firebase.
        for(BookItem bookFromDatabase: booksFromLocalDatabase) {
            // If titles match, the book exists on the local database.
            if (bookFromDatabase.getTitle().equals(bookFromServer.getTitle())) {
                return true;
            }
        }
        return false; // The book doesn't exist.
    }

    /**
     * A helper method that checks if there is a book in the local database with the title specified
     * @param title the title specified
     * @return the book if exists. Null, otherwise
     */
    public static BookItem lookForBookWithTitle(String title){
        // Gets the local book's database
        List<BookItem> booksFromLocalDatabase = getBooks();

        // Looks for the book with the title
        for(BookItem book: booksFromLocalDatabase){
            if(book.getTitle().equals(title))
                return book;
        }
        // The book does not exist in the local database.
        return null;
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
