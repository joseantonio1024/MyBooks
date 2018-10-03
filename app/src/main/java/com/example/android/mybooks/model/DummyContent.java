package com.example.android.mybooks.model;

import java.util.ArrayList;
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

    static {
        addItem(new BookItem(1,"Título 1","Autor 1","30/06/1952","Descripción 1","url 1"));
        addItem(new BookItem(2,"Título 2","Autor 2","26/12/2010","Descripción 2","url 2"));
        addItem(new BookItem(3,"Título 3","Autor 3","14/05/2001","Descripción 3","url 3"));
        addItem(new BookItem(4,"Título 4","Autor 4","08/11/2000","Descripción 4","url 4"));
    }

    private static void addItem(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.identificador), item);
    }

    /**
     * Esta clase representa un libro.
     */
    public static class BookItem {
        public final int identificador;
        public final String titulo;
        public final String autor;
        public final String fechaPublicacion;
        public final String descripcion;
        public final String URLImagenPortada;

        public BookItem(int id, String titulo, String autor, String fecha, String descripcion, String URL) {
            this.identificador = id;
            this.titulo = titulo;
            this.autor = autor;
            this.fechaPublicacion = fecha;
            this.descripcion = descripcion;
            this.URLImagenPortada = URL;
        }

        @Override
        public String toString() {
            return titulo + " escrito por " + autor;
        }
    }
}
