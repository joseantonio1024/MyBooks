package com.example.android.mybooks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.mybooks.model.BookContent;
import java.text.DateFormat;


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
     * The dummy content this fragment is presenting.
     */
    private BookContent.BookItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookDetailFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = BookContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.titulo);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.book_detail, container, false);
        // Formateamos la fecha para presentarla en el formato del país de origen.
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);


        // Se muestran los detalles de un libro.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_autor)).setText(mItem.autor);
            ((TextView) rootView.findViewById(R.id.tv_fecha)).setText(df.format(mItem.fechaPublicacion));
            ((TextView) rootView.findViewById(R.id.tv_descripcion)).setText(mItem.descripcion);
            // La imagen de portada, de momento es una imagen estática.
            //((TextView) rootView.findViewById(R.id.tv_url_imagen)).setText(mItem.URLImagenPortada);
        }

        return rootView;
    }
}
