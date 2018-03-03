package com.tangsoft.xkr.jiujiaotianxia;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.lidroid.xutils.util.LogUtils;
import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;
import com.tangsoft.xkr.jiujiaotianxia.base.BaseAppCompatActivity;
import com.tangsoft.xkr.jiujiaotianxia.dialog.ShareDialog;
import com.tangsoft.xkr.jiujiaotianxia.fragment.PayOrderDetailFragment;
import com.tangsoft.xkr.jiujiaotianxia.model.ShareInfo;
import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import com.tangsoft.xkr.jiujiaotianxia.util.UtilCheckUpgrade;
import com.tangsoft.xkr.jiujiaotianxia.view.UpgradeChoiceSilenceDialog;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseAppCompatActivity {

    @Bind(R.id.webView)
    WebView webView;

    private ShareDialog mShareDialog;

    private String FIRST_URL = ApiConfig.getHost();
    private String SUCCESS_URL = ApiConfig.getHost() + "/WebChat/Api/ReChargeSuccess.aspx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        registerMyBroadcast();
        webView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webView.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        webView.addJavascriptInterface(this, "appContainer");
        Log.i("TAG", "url = " + FIRST_URL);


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
        checkUpgrade();
    }

    private void checkUpgrade() {
        //测试代码
//        UpgradeModel upgradeModel = new UpgradeModel();
//        upgradeModel.is_mandatory_update = "0";
//        upgradeModel.versionName = "11.2.5";
//        upgradeModel.downloadUrl = "http://gdown.baidu.com/data/wisegame/6a2d8a62ec153552/yingyongbao_7102130.apk";
//        upgradeModel.updateContent = "1.增加了微信支付，功能更强大. 2.增加了微信分享功能.3.增加了应用内下载，无需下载直接安装功能.";
//        upgradeBySilence(upgradeModel);
//        UtilCheckUpgrade.toUpgrade(this, upgradeModel);
//        测试代码---end
        UtilCheckUpgrade.checkUpgrade(this);
        initUpgradeSilenceReciver();
    }


    @JavascriptInterface
    public void addProductToCart(String txtPayOrderCode) {
        FragmentManager fm = getSupportFragmentManager();
        PayOrderDetailFragment payDetailFragment = PayOrderDetailFragment.newInstance
                (txtPayOrderCode);
        payDetailFragment.show(fm, "payDetail");
    }

    @JavascriptInterface
    public void addProductToCartImage(final String memberCode, final String productCode, final String productName, final String productDetail, final String url, final String image) {
        //分享
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setImgUrl(image);
                shareInfo.setTitle(productName);
                shareInfo.setSpreadContent(productDetail);
                shareInfo.setSpreadUrl(url);
                Log.e("my", "share: memberCode" + memberCode + " productCode:" + productCode + " productName:" + productName + " url:" + url + " image:" + image);
                Log.e("my", "ShareInfo: getImgUrl" + shareInfo.getImgUrl() + " getSpreadContent:" + shareInfo.getSpreadContent() + " getSpreadUrl:" + shareInfo.getSpreadUrl() + " getTitle:" + shareInfo.getTitle());
                showNativeShareDialog(shareInfo);
            }
        });

    }

    @JavascriptInterface
    public void invitingFriend(final String productName, final String productDetail, final String url, final String img) {
        //分享
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShareInfo shareInfo = new ShareInfo();
                shareInfo.setImgUrl(img);
                shareInfo.setTitle(productName);
                shareInfo.setSpreadContent(productDetail);
                shareInfo.setSpreadUrl(url);
                Log.e("my", "ShareInfo: getImgUrl" + shareInfo.getImgUrl() + " getSpreadContent:" + shareInfo.getSpreadContent() + " getSpreadUrl:" + shareInfo.getSpreadUrl() + " getTitle:" + shareInfo.getTitle());
                showNativeShareDialog(shareInfo);
            }
        });
    }

    @JavascriptInterface
    public void signPlayGame(final String userId,final String title, final String url) {

        //分享
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("my", " userId" + userId + " title:" + title + " url:" + url);
                Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                intent.putExtra(DrawActivity.TAG_USERID, userId);
                intent.putExtra(DrawActivity.TAG_URL, url);
                intent.putExtra(DrawActivity.TAG_TITLE, title);
                startActivity(intent);
            }
        });

    }

    private void showNativeShareDialog(ShareInfo shareInfo) {
        if (mShareDialog != null && !mShareDialog.isShowing()) {
            mShareDialog.setShareInfo(shareInfo);
            mShareDialog.show();
        }

    }

    public static final String ACTION_WX_PAY_SUCCESS = "ACTION_WX_PAY_SUCCESS";

    private BroadcastReceiver wXPayBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("my","action:" + intent.getAction());
            if(ACTION_WX_PAY_SUCCESS.equals(intent.getAction())){
                loadPayResult();
            }
        }
    };

    private void registerMyBroadcast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_WX_PAY_SUCCESS);
        registerReceiver(wXPayBroadcastReciver,filter);
    }

    private void unRegisterMyBroadcast(){
        if(wXPayBroadcastReciver != null){
            unregisterReceiver(wXPayBroadcastReciver);
        }
    }

    public void loadPayResult() {
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
        Timer timer = null;
        if (!flag) {
            flag = true;
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    flag = false;
                }
            }, 1000);
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissUpgradeChoiceSilenceDialog();
        if (myBroadcastReciver != null) {
            unregisterReceiver(myBroadcastReciver);
        }
        unRegisterMyBroadcast();
    }

    private UpgradeChoiceSilenceDialog upgradeChoiceSilenceDialog;

    public void upgradeBySilence(UpgradeModel upgradeModel) {
        upgradeChoiceSilenceDialog = new UpgradeChoiceSilenceDialog(this, upgradeModel);
        upgradeChoiceSilenceDialog.clickUpgrade();
    }

    public void dismissUpgradeChoiceSilenceDialog() {
        if (upgradeChoiceSilenceDialog != null && upgradeChoiceSilenceDialog.isShowing()) {
            upgradeChoiceSilenceDialog.dismiss();
        }
    }

    public void showUpgradeChoiceSilenceDialog() {
        if (upgradeChoiceSilenceDialog != null) {
            upgradeChoiceSilenceDialog.show();
        }
    }

    /**
     * 监听器
     */
    private void initUpgradeSilenceReciver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_ERROR);
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED);
        filter.addAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS);
        registerReceiver(myBroadcastReciver, filter);
    }

    /**
     * 广播
     */
    private BroadcastReceiver myBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS.equals(action)) {
                //更新进度
                int percent = (int) intent.getLongExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS, 0);
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeProcess(percent);
                }

            } else if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED.equals(action)) {
                //完成
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeFinish(100);
                    upgradeChoiceSilenceDialog.getDialog_upgrade_tv_upgrade_cp().getTextView().setText("无需下载，点击安装");
                    showUpgradeChoiceSilenceDialog();
                }

            } else if (UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_ERROR.equals(action)) {
                //错误
                if (upgradeChoiceSilenceDialog != null) {
                    upgradeChoiceSilenceDialog.upgradeError(0);
                }
            }

        }
    };

}
