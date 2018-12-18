package com.example.android.mybooks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        Log.d("MI_DEBUG","BookDetailFragment.onCreate()");
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Loads the book specified by the fragment arguments.
            mItem = BookContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final LayoutInflater inflater1 = inflater;
        final ViewGroup container1 = container;

        Log.d("MI_DEBUG","BookDetailFragment.onCreateView()");

        // Shows the title of the book in the toolbar.
        final Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(mItem.getTitle());
        }

        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        // Shows book details.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_autor)).setText(mItem.getAuthor());
            ((TextView) rootView.findViewById(R.id.tv_fecha)).setText(mItem.getPublication_date());
            ((TextView) rootView.findViewById(R.id.tv_descripcion)).setText(mItem.getDescription());
            // Uses Picasso library to show the book image.
            Picasso.get().load(mItem.getUrl_image()).into((ImageView)rootView.findViewById(R.id.iv_imagen_libro));
        }




            // adds a listener to the floating action button for buying the book
            FloatingActionButton fab = rootView.findViewById(R.id.fragment_fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    WebViewFragment fragment = new WebViewFragment();
                    Log.d("MI_DEBUG","BookDetailFragment - getActivity.getLocalClassName(): " + getActivity().getLocalClassName());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment).commit();



                }
            });


        return rootView;
    }
}
