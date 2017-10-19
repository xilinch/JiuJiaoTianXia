package com.tangsoft.xkr.jiujiaotianxia;


import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;
import com.tangsoft.xkr.jiujiaotianxia.dialog.ShareDialog;
import com.tangsoft.xkr.jiujiaotianxia.fragment.PayOrderDetailFragment;
import com.tangsoft.xkr.jiujiaotianxia.model.ShareInfo;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.webView)
    WebView webView;

    private ShareDialog mShareDialog;

    private String FIRST_URL = ApiConfig.getHost() ;
    private String SUCCESS_URL = ApiConfig.getHost()+"/WebChat/Api/ReChargeSuccess.aspx";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.addJavascriptInterface(this, "appContainer");
        Log.i("TAG","url = "+FIRST_URL);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        webView.addJavascriptInterface(this, "appContainer");

        webView.loadUrl(FIRST_URL);
        mShareDialog = new ShareDialog(this);
    }


    @JavascriptInterface
    public void addProductToCart(String txtPayOrderCode){
        FragmentManager fm = getSupportFragmentManager();
        PayOrderDetailFragment payDetailFragment = PayOrderDetailFragment.newInstance
                (txtPayOrderCode);
        payDetailFragment.show(fm,"payDetail");
    }

    @JavascriptInterface
    public void addProductToCart(final String memberCode,final String productCode,final String productName,final String url,final String image){
        //分享
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShareInfo shareInfo =  new ShareInfo();
                shareInfo.setImgUrl(ApiConfig.getHost() + image);
                shareInfo.setTitle("邀请有礼！" + productName);
                shareInfo.setSpreadContent("您的好友"+ "为您送来TA的专属邀请，赶快点击查看惊喜吧～");
                shareInfo.setSpreadUrl(ApiConfig.getHost() + url);
                Log.e("my", "share: memberCode" + memberCode + " productCode:" + productCode + " productName:" + productName + " url:" + url + " image:" + image);
                Log.e("my", "ShareInfo: getImgUrl" + shareInfo.getImgUrl() + " getSpreadContent:" + shareInfo.getSpreadContent() + " getSpreadUrl:"+  shareInfo.getSpreadUrl() + " getTitle:"+ shareInfo.getTitle());
                showNativeShareDialog(shareInfo);
            }
        });

    }

    private void showNativeShareDialog(ShareInfo shareInfo){
        if(mShareDialog != null && !mShareDialog.isShowing()){
            mShareDialog.setShareInfo(shareInfo);
            mShareDialog.show();
        }

    }


    public void loadPayResult(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(SUCCESS_URL);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //双击退出应用
                exit2Click();
                break;
            default:
                break;
        }
        return false;
    }


    private boolean flag;  //用来判断是否连续双击了屏幕
    //双击退出方法
    private void exit2Click() {
        Timer timer=null;
        if(!flag){
            flag=true;
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            timer=new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    flag=false;
                }
            }, 1000);
        }else{
            finish();
            System.exit(0);
        }
    }


}
