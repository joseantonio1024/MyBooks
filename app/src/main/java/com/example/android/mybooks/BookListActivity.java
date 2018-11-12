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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        createNotificationChannel();
        initToolbar();
        initFab();
        isTwoPane();
        initRecyclerView();
        signin();
        registerSwipeRefreshLayout();

    }//End onCreate()

    private void createNotificationChannel() {
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
        // Adds LayoutManager here instead of book_list.xml
        ((RecyclerView) recyclerView).setLayoutManager(new LinearLayoutManager(this));
        // Creates the mAdapter and pass the data.
        mAdapter = new SimpleItemRecyclerViewAdapter(this, BookContent.ITEMS, mTwoPane);
        // Attachs mAdapter to recyclerView in order to populate data.
        ((RecyclerView) recyclerView).setAdapter(mAdapter);
    }

    private void signin(){
        String email = "jose@email.es";
        String password = "123456";
        mAuth = FirebaseAuth.getInstance();
        // sign in with email and password :)
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // If user is signed in, tries to download data from server.
                if (task.isSuccessful()) {
                    Toast.makeText(BookListActivity.this,getString(R.string.user_register_successfully),Toast.LENGTH_SHORT).show();
                    downloadBooksFromServer();
                } else {// Otherwise, shows data from local database.
                    Toast.makeText(BookListActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                    showLocalDatabase();
                }
            }
        });
    }


    private void downloadBooksFromServer(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference(BookContent.FIREBASE_BOOKS_REFERENCE);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            // Called if there is any change in the books on the server
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(BookListActivity.this, getString(R.string.downloading_books), Toast.LENGTH_SHORT).show();
                // Gets the books from the server as an array of types BookItem.
                GenericTypeIndicator<ArrayList<BookContent.BookItem>> t = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {};
                List<BookContent.BookItem> booksFromServer = dataSnapshot.getValue(t);
                // If a bookFromServer does not exist in the local database, adds to it.
                for (BookContent.BookItem book: booksFromServer){
                    if(!BookContent.exists(book)){
                        book.save();
                    }
                }
                showLocalDatabase();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BookListActivity.this, getString(R.string.error_downloading_data), Toast.LENGTH_SHORT).show();
                // If there is any issue with the server, shows the local database instead.
                showLocalDatabase();
            }
        });
    }

    // Shows books from local database.
    public void showLocalDatabase() {
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
        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                signin();
                swipeContainer.setRefreshing(false);
            }
        });
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
