package com.example.android.mybooks;

import com.example.android.mybooks.model.BookContent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Books. This activity has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of books, which when touched, lead to a {@link BookDetailActivity} representing
 * book details. On tablets, the activity presents the list of books and book details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device. */
    private boolean mTwoPane;

    // Adapter that will show the list of books.
    private SimpleItemRecyclerViewAdapter mAdapter;

    // Object to authenticate users.
    private FirebaseAuth mAuth;

    // Indicates wether the user is signed or not.
    private boolean mUserSigned = false;

    // Variables with the identification of a database user.
    // It would be better to convert them to local, but we put them here for the purpose of clarity.
    private String email = "jose@email.es";
    private String password = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        // This statement helps us debug the local database
        Stetho.initializeWithDefaults(this);
        //BookContent.BookItem.deleteAll(BookContent.BookItem.class);
        initToolbar();
        initFab();
        isTwoPane();
        initRecyclerView();
        signin(email,password);
        registerSwipeRefreshLayout();
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
        mAuth = FirebaseAuth.getInstance();
        // sign in with email and password :)
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {// If sign in is successful.
                    mUserSigned = true;
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(BookListActivity.this,getString(R.string.user_register_with_email) + user.getEmail(),Toast.LENGTH_SHORT).show();
                } else {// If sign in fails.
                    mUserSigned = false;
                    Toast.makeText(BookListActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                }
                // If user is signed in, tries to show books from the server. Otherwise, shows books from local database.
                showData();
            }
        });
    }

    // If user is signed in, tries to show data from server. Otherwise, shows data from local database.
    private void showData(){
        if(mUserSigned){ // If user is signed in, download data from Firebase.

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dbRef = database.getReference(BookContent.FIREBASE_BOOKS_REFERENCE);
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                // Called if there is any change in the data on the server
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    showDataFromServer(dataSnapshot);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(BookListActivity.this, getString(R.string.error_downloading_data), Toast.LENGTH_SHORT).show();
                    // If there is any issue with the server, shows the local database instead.
                    showLocalDatabase();
                }
            });
        }else{ // If user is not signed in, show data from the local database
            Toast.makeText(this, getString(R.string.user_is_not_signedin), Toast.LENGTH_SHORT).show();
            showLocalDatabase();
        }

    }

    // Downloads data from the server and stores them in 'BookContent.ITEMS'
    private void showDataFromServer(@NonNull DataSnapshot dataSnapshot) {
        // Gets the books from the server as an array of types BookItem.
        GenericTypeIndicator<ArrayList<BookContent.BookItem>> t = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {};
        ArrayList<BookContent.BookItem> booksFromServer = dataSnapshot.getValue(t);

        // Clears the global list and map in order to fill them again because there has been changes in the server.
        BookContent.ITEMS.clear();
        BookContent.ITEM_MAP.clear();

        if (booksFromServer != null) { // If there is a null book, skip it. This happen when deleting an in between book from the server.
            Toast.makeText(this, getString(R.string.downloading_books), Toast.LENGTH_SHORT).show();
            // Fills 'ITEMS' with all the books from the server Firebase
            for (BookContent.BookItem bookFromServer : booksFromServer) {
                if (bookFromServer != null) {// if book is null (because it has been deleted), skip it.
                    // identificator won't be used, so we initialize it to zero.
                    bookFromServer.setIdentificator(0);
                    BookContent.ITEMS.add(bookFromServer);
                    BookContent.ITEM_MAP.put(bookFromServer.getTitle(), bookFromServer);
                }
            }
        }
        // Set the adapter with the new downloaded books.
        mAdapter.setItems(BookContent.ITEMS);
        // Updates the local database with the list downloaded from server
        updateLocalDatabase();
    }

    // Updates the local database with the changes done in the server.
    public void updateLocalDatabase(){
        Toast.makeText(this, "Updating local Database", Toast.LENGTH_SHORT).show();
        List<BookContent.BookItem> localDatabaseList = BookContent.getBooks();
        List<BookContent.BookItem> addedList = new ArrayList<>(BookContent.ITEMS);
        List<BookContent.BookItem> deletedList = new ArrayList<>();

        for(BookContent.BookItem book: localDatabaseList){
            //
            int index = addedList.indexOf(book);
            // If index<0 the book in the server has been deleted or modified.
            if(index<0){
                // so we add it to deletedList books.
                deletedList.add(book);
            }else{// otherwise the book in the server has not changed so we remove it from addedList
                addedList.remove(index);
                // at the end of the loop, addedList will only contain added books in the server.
            }
        }

        // Deletes in the local database all the books deleted and modified in the server.
        for(BookContent.BookItem bookFromLocalDatabase: localDatabaseList){
            for(BookContent.BookItem bookToDelete: deletedList){
                // If titles match, deletes the book.
                if (bookFromLocalDatabase.getTitle().equals(bookToDelete.getTitle())) {
                    bookFromLocalDatabase.delete();
                }
            }
        }

        // Adds to the local database all the books added and modified in the server.
        for(BookContent.BookItem bookToAdd: addedList){
            bookToAdd.save();
        }
    }

    // Shows books from local database.
    public void showLocalDatabase(){
        Toast.makeText(this,getString(R.string.showing_books_from_local_database),Toast.LENGTH_SHORT).show();
        List<BookContent.BookItem> books = BookContent.getBooks();
        BookContent.ITEMS.clear();
        BookContent.ITEM_MAP.clear();
        for (BookContent.BookItem book : books) {
            BookContent.ITEMS.add(book);
            BookContent.ITEM_MAP.put(book.getTitle(), book);
        }
        // Sets the adapter with the books from the local database.
        mAdapter.setItems(BookContent.ITEMS);
    }

    private void registerSwipeRefreshLayout(){
        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                showData();
                swipeContainer.setRefreshing(false);
                //TODO: quitar el toast una vez testeada la app.
                Toast.makeText(BookListActivity.this, "List refreshed", Toast.LENGTH_SHORT).show();
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
        private List<BookContent.BookItem> mValues;
        private final boolean mTwoPane;
        // Event listener to show the details of a book when a user clicks in a book in the list.
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookContent.BookItem item = (BookContent.BookItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(BookDetailFragment.ARG_ITEM_ID, item.getTitle());
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment).commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.getTitle());
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

        public void setItems(List<BookContent.BookItem> items){
            mValues = items;
            notifyDataSetChanged();
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
        // Class that provides a reference to each of the views within an item.
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
