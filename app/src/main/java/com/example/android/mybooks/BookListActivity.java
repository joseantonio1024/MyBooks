package com.example.android.mybooks;

import com.example.android.mybooks.model.BookContent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import static com.example.android.mybooks.model.BookContent.d;

/**
 * An activity representing a list of Books. This activity has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of books, which when touched, lead to a {@link BookDetailActivity} representing
 * book details. On tablets, the activity presents the list of books and book details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device. */
    private boolean mTwoPane;

    private SimpleItemRecyclerViewAdapter mAdapter;

    // object to authenticate users.
    private FirebaseAuth mAuth;

    // variables with the identification of a database user.
    // it would be better to convert them to local, but we put them here for the purpose of clarity.
    private String email = "jose@email.es";
    private String password = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Stetho.initializeWithDefaults(this);

        initToolbar();
        initFab();
        isTwoPane();
        initRecyclerView();
        signin(email,password);
    }//End onCreate()

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }
    private void initFab(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }
    private void isTwoPane(){
        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    private void initRecyclerView(){
        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        // add LayoutManager here instead of book_list.xml
        ((RecyclerView) recyclerView).setLayoutManager(new LinearLayoutManager(this));
        // create the mAdapter and pass the data.
        mAdapter = new SimpleItemRecyclerViewAdapter(this, BookContent.ITEMS, mTwoPane);
        // attach mAdapter to recyclerView for populate data
        ((RecyclerView) recyclerView).setAdapter(mAdapter);
    }
    private void signin(String email, String password){
        // initialize FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        // sign in with email and password :)
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // If sign in is successfull, download data from Firebase.
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(BookListActivity.this,getString(R.string.user_register_with_email) + user.getEmail(),Toast.LENGTH_SHORT).show();
                    downloadData();
                } else {
                    // If sign in fails, show local database instead and notify the user.
                    Toast.makeText(BookListActivity.this, getString(R.string.auth_failed_showing_ddbb), Toast.LENGTH_LONG).show();
                    //TODO: show local database if exists.
                }
            }
        });
    }
    private void downloadData(){
        Log.d(d, "se ejecuta la funcion downloadData()");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference(BookContent.FIREBASE_BOOKS_REFERENCE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(d, "se lanza el evento onDataChange()");

                // If there is any change in data on the server, downloads a map with the keys and corresponding books
                GenericTypeIndicator<Map<String, BookContent.BookItem>> t = new GenericTypeIndicator<Map<String, BookContent.BookItem>>() {};
                Map<String, BookContent.BookItem> map = dataSnapshot.getValue(t);

                if(dataSnapshot.exists()) {
                    BookContent.ITEMS.clear();
                    BookContent.ITEM_MAP.clear();

                    for (Map.Entry<String, BookContent.BookItem> entry : map.entrySet()) {
                        Log.d(d, entry.getKey() + ": " + entry.getValue().getTitle());
                        BookContent.BookItem bookFromServer = entry.getValue();
                        bookFromServer.setIdentificator(Integer.parseInt(entry.getKey()));
                        // assign the books to our list (BookContent.ITEMS)
                        BookContent.ITEMS.add(bookFromServer);
                        // assign the keys and books to our map (BookContent.ITEM_MAP)
                        BookContent.ITEM_MAP.put(entry.getKey(),entry.getValue());
                    }
                }
                BookContent.updateLocalDatabase();

             // Updates the adapter in order to show the new books from Firebase.
                mAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(d, "se lanza el evento onCancelled()");
                Log.d(d,databaseError.getMessage());
                Toast.makeText(BookListActivity.this, "Error downloading data!", Toast.LENGTH_LONG).show();
                // Shows the books from the local database.
                List<BookContent.BookItem> books = BookContent.getBooks();
                BookContent.ITEMS.clear();
                BookContent.ITEM_MAP.clear();
                for (BookContent.BookItem book : books) {
                    BookContent.ITEMS.add(book);
                    BookContent.ITEM_MAP.put(String.valueOf(book.getIdentificator()), book);
                }
                // Updates the adapter in order to show the new books from the local database.
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create the adapter who manages the insertion of books in the list.
    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        // constants used to swap cardView colors.
        private static final int LAYOUT_PAR = 0;
        private static final int LAYOUT_IMPAR = 1;
        private final BookListActivity mParentActivity;
        private final List<BookContent.BookItem> mValues;
        private final boolean mTwoPane;
        // Event listener to show the details of a book when a user clicks in a book in the list.
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookContent.BookItem item = (BookContent.BookItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(BookDetailFragment.ARG_ITEM_ID, String.valueOf(item.getIdentificator()));
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment).commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, String.valueOf(item.getIdentificator()));
                    context.startActivity(intent);
                }
            }
        };

        /**
         * Overloaded constructor
         * @param parent Needed to create the fragment.
         * @param items The list of books.
         * @param twoPane Whether or not the activity is in two-pane mode.
         */
        SimpleItemRecyclerViewAdapter(BookListActivity parent, List<BookContent.BookItem> items, boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        // We need to override this method in order to accees the current position of the book in the list.
        // This way, we can indentify whether the position is even or odd and paint them different.
        @Override
        public int getItemViewType(int position){
            if(position%2 == 0)
                return LAYOUT_PAR;
            else
                return LAYOUT_IMPAR;
        }

        // Usually inflates an XML layout and returns a holder.
        @Override @NonNull
        public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            View view;

            // inflates a different layout since they are even or odd.
            if(viewType == LAYOUT_PAR) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content_pares, parent, false);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content_impares, parent, false);
            }
            return new ViewHolder(view);
        }

        // populates data in the item through the holder.
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mTituloView.setText(mValues.get(position).getTitle());
            holder.mAutorView.setText(mValues.get(position).getAuthor());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        // Returns the total amount of items in the list.
        @Override
        public int getItemCount() {
            return mValues.size();
        }



        //////////////////////////////////////////////////
        // Provides a reference to each of the views within an item.
        // Used as views cache for a faster access.
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTituloView;
            final TextView mAutorView;

            ViewHolder(View view) {
                super(view);
                mTituloView = view.findViewById(R.id.book_list_content_tv_titulo);
                mAutorView = view.findViewById(R.id.book_list_content_tv_autor);
            }
        }//End class ViewHolder
    }//End class SimpleItemRecyclerViewAdapter


}//End class BookListActivity
