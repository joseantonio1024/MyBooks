package com.example.android.mybooks.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

/*
    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }
*/
    static {
        addItem(new DummyItem("1","titulo1","autor1","fecha1","descripcion1","url1"));
        addItem(new DummyItem("2","titulo2","autor2","fecha2","descripcion2","url2"));
        addItem(new DummyItem("3","titulo3","autor3","fecha3","descripcion3","url3"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getIdentificador(), item);
    }

/*
    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
*/

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        private final String mIdentificador;
        private final String mTitulo;
        private final String mAutor;
        private final String mFechaPublicacion;
        private final String mDescripcion;
        private final String mURLImagenPortada;

        public DummyItem(String id, String tit, String aut, String fecha, String desc, String URL) {
            this.mIdentificador = id;
            this.mTitulo = tit;
            this.mAutor = aut;
            this.mFechaPublicacion = fecha;
            this.mDescripcion = desc;
            this.mURLImagenPortada = URL;
        }

        public String getIdentificador(){
            return mIdentificador;
        }

        public String getTitulo(){
            return mTitulo;
        }

        public String getAutor(){
            return mAutor;
        }

        public String getFechaPublicacion(){
            return mFechaPublicacion;
        }

        public String getDescripcion(){
            return mDescripcion;
        }

        public String getURLImagenPortada(){
            return mURLImagenPortada;
        }

        @Override
        public String toString() {
            return mTitulo + " escrito por " + mAutor;
        }
    }
}
