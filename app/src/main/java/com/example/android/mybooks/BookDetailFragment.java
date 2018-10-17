package com.example.android.mybooks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.mybooks.model.Book;
import com.squareup.picasso.Picasso;


/**
 * A fragment representing a single Book detail screen. This fragment is either contained in a {@link BookListActivity}
 * in two-pane mode (on tablets) or a {@link BookDetailActivity} on handsets.
 */
public class BookDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private Book mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the books specified by the fragment arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = BookListActivity.mBooksMap.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);
        // TODO: arreglar el formato de fecha y mirar por qué la descripción no se muestra hasta el fondo de la pantalla.
        // Formateamos la fecha para presentarla en el formato del país de origen.
        //DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        //String fechaDebug = df.format(mItem.getDate());
        //Log.d("FRAGMENTOOOO: ", fechaDebug);
        //Log.d("BOOKDETAILFRAGMENT: " ,mItem.getPublication_date());

        // Se muestran los detalles de un libro.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_autor)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.tv_fecha)).setText(mItem.getPublication_date());
            ((TextView) rootView.findViewById(R.id.tv_descripcion)).setText(mItem.getDescription());
            // Utilizamos la librería Picasso para mostrar la imagen del ítem.
            Picasso.get().load(mItem.getUrl_image()).into((ImageView)rootView.findViewById(R.id.iv_imagen_libro));
        }

        return rootView;
    }
}
