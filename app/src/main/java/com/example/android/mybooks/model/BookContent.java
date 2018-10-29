package com.example.android.mybooks.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.mybooks.R;
import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();


    // A map (book) items, by ID.
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<String, BookItem>();

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
        List<BookItem> notChangedList = new ArrayList<>();

        //addedList.addAll(firebaseList);

        for(BookItem book: localDatabaseList){
            int firebaseIndex = addedList.indexOf(book);
            if(firebaseIndex<0){
                deletedList.add(book);
            }else{
                BookItem firebaseBook = addedList.remove(firebaseIndex);
                //if(book.compareTo(firebaseBook)){
                //    notChangedList.add(book);
                //}
            }
        }
        //notChangedList.removeAll(deletedList);
        //notChangedList.addAll(addedList);
        List<BookItem> booksFromLocalDatabase = BookItem.listAll(BookItem.class);
        booksFromLocalDatabase.removeAll(deletedList);
        booksFromLocalDatabase.addAll(addedList);
        int i=0;
    }


    /**
     * A helper method that checks if exist a bookFromServer in the local database
     * @param bookFromServer a book from our server
     * @return true if exists in the local database. False otherwise.
     */
    public static boolean exists(BookItem bookFromServer){
        // Get the local book's database.
        List<BookItem> booksFromDDBB = BookItem.listAll(BookItem.class);
        // If the local database is empty, return 'false' in order to fill it.
        // TODO: probar con books == null.
        if(booksFromDDBB.size()==0)
            return false;

        // compares all the books from the local database with the book downloaded from firebase.
        for(BookItem bookFromDDBB: booksFromDDBB) {
            Log.d(d, "id libro bbdd local: " + bookFromDDBB.getIdentificator() + " --> id libro Firebase: " + bookFromServer.getIdentificator());
            // If identificators match, the book exists on the local database.
            if (bookFromDDBB.getIdentificator() == bookFromServer.getIdentificator()) {
                Log.d(d,"identificadores iguales: " + bookFromDDBB.getIdentificator());
                // If the contents of both books are different, updates the book from the local database.
                if (!bookFromDDBB.equals(bookFromServer)) {
                    Log.d(d,"el libro " + bookFromDDBB.getTitle() + " se ha modificado en Firebase ---> actualizado en local ddbb.");
                    bookFromDDBB.setAuthor(bookFromServer.getAuthor());
                    bookFromDDBB.setDescription(bookFromServer.getDescription());
                    bookFromDDBB.setPublication_date(bookFromServer.getPublication_date());
                    bookFromDDBB.setTitle(bookFromServer.getTitle());
                    bookFromDDBB.setUrl_image(bookFromServer.getUrl_image());
                    bookFromDDBB.save();
                }
                return true;// the book exists and is updated.
            }
        }
        return false; // The book doesn't exist.
    }

    // Datos de prueba
    static {

    }
    /*static {
        addItem(new BookItem(1,"Título 1","Autor 1", setDate(2000,12,31),"Descripción 1","url 1"));
        addItem(new BookItem(2,"Título 2","Autor 2", setDate(1994,10,24),"Descripción 2","url 2"));
        addItem(new BookItem(3,"Título 3","Autor 3", setDate(1995,3,4),"Descripción 3","url 3"));
        addItem(new BookItem(4,"Título 4","Autor 4", setDate(1999,1,23),"Descripción 4","url 4"));
        addItem(new BookItem(5,"Título 5","Autor 5", setDate(1970,12,23),"Descripción 5","url 5"));
        addItem(new BookItem(6,"Título 6","Autor 6", setDate(1996,1,23),"Descripción 6","url 6"));
    }*/

    private static void addItem(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.getIdentificator()), item);
    }

    // Método helper para convertir las fechas a Date.
    private static Date setDate(int year, int mes, int dia){
        Calendar calendar = new GregorianCalendar(year,mes-1, dia);
        return calendar.getTime();
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
