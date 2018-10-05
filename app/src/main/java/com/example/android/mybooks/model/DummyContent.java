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
public class DummyContent {

    /**
     * An array of sample (book) items.
     */
    public static final List<BookItem> ITEMS = new ArrayList<BookItem>();

    /**
     * A map of sample (book) items, by ID.
     */
    public static final Map<String, BookItem> ITEM_MAP = new HashMap<String, BookItem>();

    // Datos de prueba
    static {
        addItem(new BookItem(1,"Título 1","Autor 1", setDate(2000,12,31),"Descripción 1","url 1"));
        addItem(new BookItem(2,"Título 2","Autor 2", setDate(1994,10,24),"Descripción 2","url 2"));
        addItem(new BookItem(3,"Título 3","Autor 3", setDate(1995,3,4),"Descripción 3","url 3"));
        addItem(new BookItem(4,"Título 4","Autor 4", setDate(1999,1,23),"Descripción 4","url 4"));
        addItem(new BookItem(5,"Título 5","Autor 5", setDate(1970,12,23),"Descripción 5","url 5"));
        addItem(new BookItem(6,"Título 6","Autor 6", setDate(1996,1,23),"Descripción 6","url 6"));
    }

    private static void addItem(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.identificador), item);
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
        public final int identificador;
        public final String titulo;
        public final String autor;
        public final Date fechaPublicacion;
        public final String descripcion;
        public final String URLImagenPortada;

        public BookItem(int id, String titulo, String autor, Date fecha, String descripcion, String URL) {
            this.identificador = id;
            this.titulo = titulo;
            this.autor = autor;
            this.fechaPublicacion = fecha;
            this.descripcion = descripcion;
            this.URLImagenPortada = URL;
        }

        @Override @NonNull
        public String toString() {
            return titulo + " escrito por " + autor;
        }
    }
}
