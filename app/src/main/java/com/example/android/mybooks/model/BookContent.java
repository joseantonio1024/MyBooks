package com.example.android.mybooks.model;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces.
 */
public class BookContent {

    /**
     * An array of sample (book) items.
     */
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();

    /**
     * A map of sample (book) items, by ID.
     */
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<String, BookItem>();

    // Datos de prueba
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
    public static class BookItem {
        private int mIdentificator;
        private String mAuthor;
        private String mDescription;
        private String mPublicationDate;
        private String mTitle;
        private String mUrlImage;

        public BookItem(String author, String description, String publicationDate, String title, String url_image) {
            mAuthor = author;
            mDescription = description;
            mPublicationDate = publicationDate;
            mTitle = title;
            mUrlImage = url_image;
        }

        public BookItem(){}

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
}
