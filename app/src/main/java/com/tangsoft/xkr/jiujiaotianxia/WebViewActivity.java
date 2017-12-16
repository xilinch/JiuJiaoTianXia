package com.tangsoft.xkr.jiujiaotianxia;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

/**
 * Created by xilinch on 17-12-16.
 */

public class WebViewActivity extends Activity {


    private LinearLayout back;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        back = (LinearLayout) findViewById(R.id.back);
        webView = (WebView) findViewById(R.id.webView);
        initListener();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if(getIntent() != null && getIntent().getExtras() != null){
            String url = getIntent().getExtras().getString(DrawActivity.TAG_URL);
            webView.loadUrl(url);
        }
    }

    private void initListener(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
