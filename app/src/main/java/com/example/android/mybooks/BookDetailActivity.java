package com.example.android.mybooks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * An activity representing a single BookItem detail screen. This activity is only used on narrow width devices. On tablet-size devices,
 * book details are presented side-by-side with a list of books in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Shows a floating action button for buying the book
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates a webView and shows a form to buy the book
                WebView webView = new WebView(BookDetailActivity.this);
                setContentView(webView);
                MyWebViewClient webViewClient = new MyWebViewClient();
                webView.setWebViewClient(webViewClient);
                webView.loadUrl("file:///android_asset/form.html");
            }
        });

        // Shows the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // Creates the detail fragment and adds it to the activity using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_ID));
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.book_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, BookListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Inner class to intercept form's data.
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // Gets the fields in the form.
            String name = Uri.parse(url).getQueryParameter("name");
            String num = Uri.parse(url).getQueryParameter("num");
            String date = Uri.parse(url).getQueryParameter("date");

            // If there is some field empty, shows a message for the user to refill them.
            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(num) || TextUtils.isEmpty(date)) {
                Toast.makeText(view.getContext(), "You have to fill all text boxes", Toast.LENGTH_LONG).show();
                return true;
            }

            // The user has bought the book successfully. Shows a message and dismiss the webview.
            Toast.makeText(view.getContext(), "Your purchase has been successful", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
    }
}
