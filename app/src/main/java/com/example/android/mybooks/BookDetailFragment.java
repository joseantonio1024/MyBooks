package com.example.android.mybooks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.mybooks.model.BookContent;
import com.squareup.picasso.Picasso;


/**
 * A fragment representing a single BookItem detail screen. This fragment is either contained in a {@link BookListActivity}
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
            // Loads the book specified by the fragment arguments.
            mItem = BookContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Shows the title of the book in the toolbar.
        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getTitle());
        }

        View rootView = inflater.inflate(R.layout.book_detail, container, false);
        // TODO: Mirar por qué la descripción no se muestra hasta el fondo de la pantalla.
        // Shows book details.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_autor)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.tv_fecha)).setText(mItem.getPublication_date());
            ((TextView) rootView.findViewById(R.id.tv_descripcion)).setText(mItem.getDescription());
            // Uses Picasso library to show the book image.
            Picasso.get().load(mItem.getUrl_image()).into((ImageView)rootView.findViewById(R.id.iv_imagen_libro));
        }
        return rootView;
    }
}
