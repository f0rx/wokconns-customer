package com.wokconns.customer.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityWebViewCommonBinding;
import com.wokconns.customer.interfaces.Const;

public class WebViewCommon extends AppCompatActivity {
    ActivityWebViewCommonBinding binding;
    String url = "";
    String header = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view_common);

        binding.rlclose.setOnClickListener(v -> finish());

        if (getIntent().hasExtra(Const.URL)) {
            url = getIntent().getStringExtra(Const.URL);
            header = getIntent().getStringExtra(Const.HEADER);
            binding.tvTitle.setText(header);
        }

        binding.mWebView.setWebViewClient(new MyBrowser());
        binding.mWebView.getSettings().setLoadsImagesAutomatically(true);
        binding.mWebView.getSettings().setJavaScriptEnabled(true);
        binding.mWebView.loadUrl(url);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


}