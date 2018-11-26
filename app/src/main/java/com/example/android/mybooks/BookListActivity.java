package com.example.android.mybooks;

import com.example.android.mybooks.model.BookContent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import com.facebook.stetho.Stetho;

import static com.example.android.mybooks.MyFirebaseMessagingService.ACTION_DELETE_BOOK;
import static com.example.android.mybooks.MyFirebaseMessagingService.ACTION_VIEW_DETAILS;
import static com.example.android.mybooks.MyFirebaseMessagingService.BOOK_ID;

/**
 * An activity representing a list of Books. This activity has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of books, which when touched, lead to a {@link BookDetailActivity} representing
 * book details. On tablets, the activity presents the list of books and book details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    // Reference to the server database.
    private DatabaseReference dbRef;

    // Global reference to remove the listener in onDestroy() and onRefreshListener().
    private ValueEventListener listener;

    // Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
    private boolean mTwoPane;

    // Adapter that will show the list of books.
    private SimpleItemRecyclerViewAdapter mAdapter;

    // A list with the books on the server.
    // When tapping in the notification, it creates a new instance of BookListActivity, so we need to
    // set mBooksFromServer static in order to keep its value.
    private static List<BookContent.BookItem> mBooksFromServer;

    // An object with the current user.
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // This statement helps us debug the local database
        Stetho.initializeWithDefaults(this);

        createNotificationChannel();
        initToolbar();
        initFab();
        isTwoPane();
        getNotificationActionButtons();
        initRecyclerView();
        signin();
        registerSwipeRefreshLayout();

    }//End onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GET_INTENT","onDestroy()");
        // Removes the listener in order to save resources.
        removeValueEventListenerFromFirebase();
    }



    private void createNotificationChannel() {
        Log.d("GET_INTENT", "METHOD: createNotificationChannel()");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initToolbar(){
        Log.d("GET_INTENT", "METHOD: initToolbar()");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    // We use this button to sign out in order to test the app.
    private void initFab(){
        Log.d("GET_INTENT", "METHOD: initFab()");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: remove this in the production app.
                Toast.makeText(BookListActivity.this, "signs out if long clicked", Toast.LENGTH_LONG).show();
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //TODO: remove this in the production app.
                mAuth.signOut();
                Snackbar.make(view, "signed out", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                return false;
            }
        });
    }

    private void isTwoPane(){
        Log.d("GET_INTENT", "METHOD: isTwoPane()");
        mTwoPane = findViewById(R.id.book_detail_container) != null;
    }

    private void getNotificationActionButtons(){
        Log.d("GET_INTENT", "METHOD: getNotificationActionButtons()");
        if (getIntent() != null && getIntent().getAction() != null) {

            // Retrieves the action within the intent.
            String action = getIntent().getAction();
            if(action.equalsIgnoreCase(ACTION_DELETE_BOOK) || action.equalsIgnoreCase(ACTION_VIEW_DETAILS)) {
                Log.d("GET_INTENT", action);
                Log.d("GET_INTENT","book position: " + getIntent().getStringExtra(BOOK_ID));

                // Gets the value of BOOK_ID key.
                String bookID = getIntent().getStringExtra(BOOK_ID);

                // Try if bookPosition can be converted to an int.
                try{
                    int id = Integer.parseInt(bookID);

                    // if id is not out of range
                    if(id>=0 && id<mBooksFromServer.size()){
                        // Gets the book from the server
                        BookContent.BookItem bookFromServer = mBooksFromServer.get(id);

                        // bookFromServer can be null if it has been deleted previously. So we check it.
                        if (bookFromServer != null) {
                            // Gets the title of the book.
                            String bookTitle = bookFromServer.getTitle();

                            // Searches in the local database for the book to delete or view details.
                            BookContent.BookItem bookToDeleteOrViewDetails = BookContent.lookForBookWithTitle(bookTitle);
                            if (bookToDeleteOrViewDetails != null){
                                // If the button clicked in the notification was 'DELETE'
                                if (action.equalsIgnoreCase(ACTION_DELETE_BOOK)) {
                                    bookToDeleteOrViewDetails.delete();// Deletes the book from the local database.
                                    deleteBookFromServer(bookID);// Deletes the book from the server.
                                    Log.d("GET_INTENT", "delete book: " + bookToDeleteOrViewDetails.getTitle());
                                    Toast.makeText(this, "book deleted", Toast.LENGTH_SHORT).show();
                                }
                                // If the button clicked in the notification was 'VIEW DETAILS'
                                else if (action.equalsIgnoreCase(ACTION_VIEW_DETAILS)) {
                                    // View details of the book.
                                    if (mTwoPane) {
                                        Log.d("GET_INTENT", "mTwoPane: true");
                                        Log.d("GET_INTENT", this.toString());
                                        Bundle arguments = new Bundle();
                                        arguments.putString(BookDetailFragment.ARG_ITEM_ID, bookToDeleteOrViewDetails.getTitle());
                                        BookDetailFragment fragment = new BookDetailFragment();
                                        fragment.setArguments(arguments);
                                        this.getSupportFragmentManager().beginTransaction().replace(R.id.book_detail_container, fragment).commit();
                                    } else {
                                        Log.d("GET_INTENT", "mTwoPane: false");
                                        Intent intent = new Intent(this, BookDetailActivity.class);
                                        intent.putExtra(BookDetailFragment.ARG_ITEM_ID, bookToDeleteOrViewDetails.getTitle());
                                        this.startActivity(intent);
                                    }
                                }
                            }else {
                                Log.d("GET_INTENT", "Error. El libro indicado no existe en la bbdd");
                                Toast.makeText(this, "error. El libro indicado no existe en la bbdd", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.d("GET_INTENT", "Error. El libro indicado no existe en el server");
                            Toast.makeText(this, "error. El libro indicado no existe en el server", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Log.d("GET_INTENT", "Error. index out of range");
                        Toast.makeText(this, "error. index out of range", Toast.LENGTH_SHORT).show();
                    }
                }catch(NumberFormatException e){
                    Log.d("GET_INTENT", e.getMessage());
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // Reset getIntent.getAction in order to not enter this method again when the device is rotated.
                getIntent().setAction(Intent.ACTION_MAIN);
            }
        }
    }

    private void initRecyclerView(){
        Log.d("GET_INTENT", "METHOD: initRecyclerView()");
        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        // Adds LayoutManager here instead of book_list.xml
        ((RecyclerView) recyclerView).setLayoutManager(new LinearLayoutManager(this));
        // Creates the mAdapter and pass the data.
        mAdapter = new SimpleItemRecyclerViewAdapter(this, BookContent.ITEMS, mTwoPane);
        // Attachs mAdapter to recyclerView in order to populate data.
        ((RecyclerView) recyclerView).setAdapter(mAdapter);
    }

    private void signin() {
        Log.d("GET_INTENT", "METHOD: signin()");
        String email = "jose@email.es";
        String password = "123456";
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Log.d("GET_INTENT", "Current user is null. Trying to register");
            // sign in with email and password :)
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If user is signed in, tries to download data from server.
                    if (task.isSuccessful()) {
                        Toast.makeText(BookListActivity.this, getString(R.string.user_register_successfully), Toast.LENGTH_SHORT).show();
                        Log.d("GET_INTENT", "registro: correcto");
                        downloadBooks();
                    } else {// Otherwise, shows data from local database.
                        Toast.makeText(BookListActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        Log.d("GET_INTENT", "registro: error");
                    }
                }
            });
        }else{
            Log.d("GET_INTENT", "User is already registered: " + mAuth.getCurrentUser().getEmail());
            downloadBooks();
        }
    }

    private void downloadBooks() {
        Log.d("GET_INTENT", "METHOD: downloadBooks()");
        if (mAuth.getCurrentUser() != null) { // If the user is registered.
            if (listener == null) { // If the listener is not yet registered or the previous one was removed, adds another one.
                dbRef = FirebaseDatabase.getInstance().getReference(BookContent.FIREBASE_BOOKS_REFERENCE);
                listener = dbRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    // Called if there is any change in the books on the server
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("GET_INTENT", "se lanza el event onDataChange()");
                        // Gets the books from the server as an array of types BookItem.
                        GenericTypeIndicator<ArrayList<BookContent.BookItem>> t = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {};
                        Toast.makeText(BookListActivity.this, getString(R.string.downloading_books), Toast.LENGTH_SHORT).show();
                        // mBooksFromServer holds the list of books from the server
                        // We made it static because we need its value in the method getNotificationActionButtons() which is called
                        // in a new instance of BooklistActivity.
                        mBooksFromServer = dataSnapshot.getValue(t);
                        Log.d("GET_INTENT", "books downloaded correctly");
                        sincronizeDatabases();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(BookListActivity.this, getString(R.string.error_downloading_data), Toast.LENGTH_SHORT).show();
                        // If there is any issue with the server, shows the local database instead.
                        Log.d("GET_INTENT", "Se lanza el evento onCancelled(). Se muestra la bbdd local");
                        showLocalDatabase();
                    }
                });
                Log.d("GET_INTENT", "valueEventListener added: " + listener.hashCode());
            }
        }
    }

    private void sincronizeDatabases(){

        int serverSize = 0;
        // Calculates how many books are in the server.
        if (mBooksFromServer != null) {
            for (BookContent.BookItem book : mBooksFromServer) {
                // When a book in the server is deleted, it remains at its position as null.
                // But we don't want to count null books.
                if (book != null) {
                    // If a bookFromServer is not null, increments serverSize.
                    serverSize++;
                }
            }
        }
        // Calculates how many books are in the local database.
        List<BookContent.BookItem> booksFromLocalDatabase = BookContent.getBooks();
        int databaseSize = booksFromLocalDatabase.size();

        // if serverSize > databaseSize, we need to add books to the local database.
        if (serverSize > databaseSize) {
            Log.d("GET_INTENT", "serverSize > databaseSize");
            // If there are any books to download
            if (mBooksFromServer != null) {
                for (BookContent.BookItem book : mBooksFromServer) {
                    // When a book in the server is deleted, it remains at its position as null.
                    // But we don't want to store null books in the local database.
                    if (book != null) {
                        // If a bookFromServer does not exist in the local database, adds to it.
                        if (!BookContent.exists(book)) {
                            book.save();
                        }
                    }
                }
            }
        }

        // If serverZize < databaseSize, we need to remove books from the local database
        if (serverSize < databaseSize) {
            Log.d("GET_INTENT", "serverSize < databaseSize");
            // If there are books to download
            if (mBooksFromServer != null) {
                for (BookContent.BookItem bookFromLocalDatabase : booksFromLocalDatabase) {
                    if (!BookContent.existsInServer(bookFromLocalDatabase, mBooksFromServer)) {
                        bookFromLocalDatabase.delete();
                    }
                }
            }
        }
        showLocalDatabase();
    }

    private void deleteBookFromServer(String id){
        Log.d("GET_INTENT", "METHOD: deleteBookFromServer()");
        DatabaseReference dbBook = FirebaseDatabase.getInstance().getReference(BookContent.FIREBASE_BOOKS_REFERENCE).child(id);
        Log.d("GET_INTENT","deleted book: " + dbBook.toString());
        dbBook.removeValue();

    }

    // Shows books from local database.
    public void showLocalDatabase() {
        Log.d("GET_INTENT", "METHOD: showLocalDatabase()");
        List<BookContent.BookItem> booksFromLocalDatabase = BookContent.getBooks();
        if (!booksFromLocalDatabase.isEmpty()) {// If database is not empty, shows it.
            BookContent.ITEMS.clear();
            BookContent.ITEM_MAP.clear();
            for (BookContent.BookItem book : booksFromLocalDatabase) {
                BookContent.ITEMS.add(book);
                BookContent.ITEM_MAP.put(book.getTitle(), book);
            }
            // Sets the adapter with the books from the local database.
            mAdapter.setItems(BookContent.ITEMS);
        }else{
            Toast.makeText(this, getString(R.string.no_books_in_ddbb), Toast.LENGTH_SHORT).show();
        }
    }

    // Refreshes the screen when the user swipes down.
    private void registerSwipeRefreshLayout(){
        Log.d("GET_INTENT", "METHOD: registerSwipeRefreshLayout()");
        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                Log.d("GET_INTENT","onRefresh()");
                BookContent.BookItem.deleteAll(BookContent.BookItem.class);
                Log.d("GET_INTENT","Local database deleted");
                // removes the listener because another one is going to be created in downloadBooks().
                removeValueEventListenerFromFirebase();
                signin();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void removeValueEventListenerFromFirebase() {
        Log.d("GET_INTENT", "METHOD: removeValueEventListenerFromFirebase()");
        if(listener != null) {
            dbRef.removeEventListener(listener);
            Log.d("GET_INTENT","valueEventListener removed: " + listener.hashCode());
            listener = null;
        }
    }







    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Creates the adapter who manages the insertion of books in the list.
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
