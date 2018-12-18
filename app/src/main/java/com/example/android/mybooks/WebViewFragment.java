package com.example.android.mybooks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebViewFragment extends Fragment {


    public WebViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_web_view, container, false);

        // Creates a webView and shows a form to buy the book
        WebView webView = view.findViewById(R.id.web_view);
        MyWebViewClient webViewClient = new MyWebViewClient();
        webView.setWebViewClient(webViewClient);
        webView.loadUrl("file:///android_asset/form.html");


        return view;
    }

}
