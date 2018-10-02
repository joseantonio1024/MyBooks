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
        addItem(new BookItem(1,"titulo1","autor1","fecha1","descripcion1","url1"));
        addItem(new BookItem(2,"titulo2","autor2","fecha2","descripcion2","url2"));
        addItem(new BookItem(3,"titulo3","autor3","fecha3","descripcion3","url3"));
    }

    private static void addItem(BookItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.identificador), item);
    }

    /**
     * A book item representing a piece of content.
     */
    public static class BookItem {
        public final int identificador;
        public final String titulo;
        public final String autor;
        public final String fechaPublicacion;
        public final String descripcion;
        public final String URLImagenPortada;

        public BookItem(int id, String tit, String aut, String fecha, String desc, String URL) {
            this.identificador = id;
            this.titulo = tit;
            this.autor = aut;
            this.fechaPublicacion = fecha;
            this.descripcion = desc;
            this.URLImagenPortada = URL;
        }

        @Override
        public String toString() {
            return titulo + " escrito por " + autor;
        }
    }
}
