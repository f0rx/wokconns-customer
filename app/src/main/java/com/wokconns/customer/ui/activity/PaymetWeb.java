package com.wokconns.customer.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wokconns.customer.databinding.ActivityPaymetWebBinding;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.R;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;


public class PaymetWeb extends AppCompatActivity {
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private Context mContext;
    private static String url = "";
    private static String surl = Consts.PAYMENT_SUCCESS;
    private static String furl = Consts.PAYMENT_FAIL;
    private static String surl_paypal = Consts.PAYMENT_SUCCESS_paypal;
    private static String furl_paypal = Consts.PAYMENT_FAIL_Paypal;

    private static String surl_stripe_book = Consts.INVOICE_PAYMENT_SUCCESS_Stripe;
    private static String furl_stripe_book = Consts.INVOICE_PAYMENT_FAIL_Stripe;
    ActivityPaymetWebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paymet_web);
        mContext = PaymetWeb.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.PAYMENT_URL)) {
            url = getIntent().getStringExtra(Consts.PAYMENT_URL);

        }

        binding.wvPayment.getSettings().setJavaScriptEnabled(true);
        binding.wvPayment.getSettings().setDomStorageEnabled(true);
        binding.wvPayment.addJavascriptInterface(new JsObject(), "injectedObject");
        binding.wvPayment.loadData("", "text/html", null);
        binding.wvPayment.loadUrl(url);

        binding.wvPayment.setWebViewClient(new SSLTolerentWebViewClient());
        init();
    }

    class JsObject {
        @JavascriptInterface
        public String toString() {
            return "injectedObject";
        }
    }

    private void init() {
        binding.IVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_event));
                finish();
            }
        });
    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            // this will ignore the Ssl error and will go forward to your site
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("TAG", "shouldOverrideUrlLoading:url " + url);
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("TAG", "onPageFinished:url " + url);
            ProjectUtils.pauseProgressDialog();
            //Page load finished
            if (url.equals(surl)) {
                ProjectUtils.showToast(mContext, "Payment was successful.");
                super.onPageFinished(view, surl);
                prefrence.setValue(Consts.SURL, surl);
                finish();

                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else if (url.equals(furl)) {
                ProjectUtils.showToast(mContext, "Payment fail.");
                //view.loadUrl("https://www.youtube.com");
                super.onPageFinished(view, furl);
                prefrence.setValue(Consts.FURL, furl);
                finish();
                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else if (url.equals(surl_stripe_book)) {
                ProjectUtils.showToast(mContext, "Payment was successful.");
                super.onPageFinished(view, surl_stripe_book);
                prefrence.setValue(Consts.SURL, surl_stripe_book);
                finish();

                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else if (url.equals(furl_stripe_book)) {
                ProjectUtils.showToast(mContext, "Payment fail.");
                //view.loadUrl("https://www.youtube.com");
                super.onPageFinished(view, furl_stripe_book);
                prefrence.setValue(Consts.FURL, furl_stripe_book);
                finish();
                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else if (url.contains(surl_paypal)) {
                ProjectUtils.showToast(mContext, "Payment was successful.");
                //view.loadUrl("https://www.youtube.com");
                super.onPageFinished(view, surl_paypal);
                prefrence.setValue(Consts.SURL, surl_paypal);
                finish();
                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else if (url.equals(furl_paypal)) {
                ProjectUtils.showToast(mContext, "Payment fail.");
                //view.loadUrl("https://www.youtube.com");
                super.onPageFinished(view, furl_paypal);
                prefrence.setValue(Consts.FURL, furl_paypal);
                finish();
                binding.wvPayment.clearCache(true);

                binding.wvPayment.clearHistory();

                binding.wvPayment.destroy();
            } else {
                super.onPageFinished(view, url);
            }

        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


}
