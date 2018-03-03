package com.tangsoft.xkr.jiujiaotianxia.base;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by 123 on 2018/3/3.
 */

public class BaseActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
