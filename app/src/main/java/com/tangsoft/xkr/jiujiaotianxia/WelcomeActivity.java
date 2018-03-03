package com.tangsoft.xkr.jiujiaotianxia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tangsoft.xkr.jiujiaotianxia.base.BaseAppCompatActivity;


/**
 * Created by Administrator on 2017-05-09.
 */

public class WelcomeActivity extends BaseAppCompatActivity {

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int n = msg.arg1;
            //获得SharedPreferences目录
            //通过键值对获得数据判断是否是第一次
            SharedPreferences sp = getSharedPreferences("welcome", MODE_PRIVATE);
            boolean flag = sp.getBoolean("welcome", false);
            if (!flag) {
                Intent intent = new Intent(WelcomeActivity.this,
                        StartupActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                Intent intent = new Intent(WelcomeActivity.this,
                        MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Welcome();
    }

    public void Welcome() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message message = handler.obtainMessage();
                message.arg1 = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

}
