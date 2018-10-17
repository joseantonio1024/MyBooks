package com.example.android.mybooks;

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

import com.example.android.mybooks.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Books. This activity has different presentations for handset and tablet-size devices.
 * On handsets, the activity presents a list of items, which when touched, lead to a {@link BookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and item details side-by-side using two vertical panes.
 */
public class BookListActivity extends AppCompatActivity {

    /** Whether or not the activity is in two-pane mode, i.e. running on a tablet device. */
    private boolean mTwoPane;

    // Declaramos la lista y el mapa de libros que poblaremos desde la base de datos.
    List<Book> mBooks;
    public static final Map<String,Book> mBooksMap = new HashMap<>();

    // Declaramos el recyclerViewAdapter global.
    SimpleItemRecyclerViewAdapter mAdapter;

    // Declaramos los objetos FirebaseAuth y AuthStateListener.
    //TODO: convertir en variables locales.
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        // Inicializamos la instancia de FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        // Inicializamos el método AuthStateListener para ver cuando el usuario hace sign in o sign out.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("AUTH_DEBUG", "onAuthStateChanged:signed_in: " + user.getUid());
                    //TODO: seguir con el proceso
                } else {
                    // User is signed out
                    Log.d("AUTH_DEBUG", "onAuthStateChanged:signed_out");
                }
            }
        };

        // credenciales de un usuario de la base de datos.
        String email = "jose@email.es";
        String password = "123456";
        // Método que crea un nuevo usuario con email y password.
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH_DEBUG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds the auth state listener
                        // will be notified and logic to handle the signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(BookListActivity.this, "Creación de user fallida", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        // Método que inicia sesion de un usuario con email y password.
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUTH_DEBUG", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds the auth state listener
                        // will be notified and logic to handle the signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("AUTH_DEBUG", "signInWithEmail:failed", task.getException());
                            Toast.makeText(BookListActivity.this, "login de user correcto", Toast.LENGTH_LONG).show();
                        }
                    }
                });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            mTwoPane = true;
        }

        mBooks = new ArrayList<>();

        // Buscamos el recyclerView en el layout.
        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        // Añadimos el LayoutManager aquí en vez de en book_list.xml
        ((RecyclerView) recyclerView).setLayoutManager(new LinearLayoutManager(this));
        // Creamos el mAdapter y le pasamos los datos de ejemplo.
        mAdapter = new SimpleItemRecyclerViewAdapter(this, mBooks, mTwoPane);
        // Unimos el mAdapter al recyclerView para ingresar los datos
        ((RecyclerView) recyclerView).setAdapter(mAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("books");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mBooks.removeAll(mBooks);
                mBooksMap.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Book bookItem = snapshot.getValue(Book.class);
                    String id = snapshot.getKey();
                    bookItem.setIdentificator(Integer.parseInt(id));
                    mBooksMap.put(id,bookItem);

                    //Log.d(AUTH_DEBUG, bookItem.getPublicationDate());
                    mBooks.add(bookItem);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("AUTH_DEBUG","onCreate()");
    }//End onCreate()

    @Override
    protected void onStart() {
        super.onStart();
        // cuando ejecutamos la app añadimos el listener a nuestra instancia mAuth.
        mAuth.addAuthStateListener(mAuthListener);
        Log.d("AUTH_DEBUG","onStart()");
    }//End onStart()

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            // Cuando la app se cierra eliminamos el listener de nuestra instancia mAuth
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.d("AUTH_DEBUG","onStop()");
    }//End onStop()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Crea el adaptador que extiende de RecyclerView.Adapter
    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final BookListActivity mParentActivity;
        private final List<Book> mValues;
        private final boolean mTwoPane;

        // constantes utilizadas para alternar los colores de las cardViews
        private static final int LAYOUT_PAR = 0;
        private static final int LAYOUT_IMPAR = 1;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book item = (Book) view.getTag();
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

        SimpleItemRecyclerViewAdapter(BookListActivity parent, List<Book> items, boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        // Se necesita sobreescribir este método para acceder a la posición actual del libro en la lista.
        // De esta forma podemos identificar si la posición es par o impar.
        @Override
        public int getItemViewType(int position){
            if(position%2 == 0)
                return LAYOUT_PAR;
            else
                return LAYOUT_IMPAR;
        }

        // Normalmente infla un layout de XML y retorna un holder.
        @Override @NonNull
        public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            View view;

            // Se infla un layout diferente en función de si es par o impar.
            if(viewType == LAYOUT_PAR) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content_pares, parent, false);
            }else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content_impares, parent, false);
            }
            return new ViewHolder(view);
        }

        // ingresa los datos en el item a través del holder.
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.mTituloView.setText(mValues.get(position).getTitle());
            holder.mAutorView.setText(mValues.get(position).getAuthor());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        // Retorna el número total de ítems de la lista.
        @Override
        public int getItemCount() {
            return mValues.size();
        }



        //////////////////////////////////////////////////
        // Proporciona una referencia a cada una de las views dentro de un item.
        // Se utiliza como cache de las views para acceso más rápido.
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
