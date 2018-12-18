package com.example.android.mybooks;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyWebViewClient extends WebViewClient {


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

        return false;
    }
}
