package com.tangsoft.xkr.jiujiaotianxia;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import com.tangsoft.xkr.jiujiaotianxia.view.BaseUpgradeDailog;
import com.tangsoft.xkr.jiujiaotianxia.view.UpgradeChoiceDialog;
import com.tangsoft.xkr.jiujiaotianxia.view.UpgradeForceDialog;


/**
 * Created by xilinch on 2016/6/3.
 */
public class UpgradeActivity extends Activity {


    /**
     * 升级对话框
     */
    private BaseUpgradeDailog upgradeDialog;

    /**
     * 升级
     */
    private UpgradeModel upgradeModel;


    public static final String ACTION_UPDATE_PROGRESS = "com.nfdaily.nfplus.ACTION_UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_ERROR = "com.nfdaily.nfplus.ACTION_UPDATE_ERROR";
    public static final String ACTION_UPDATE_FINISHED = "com.nfdaily.nfplus.ACTION_UPDATE_FINISHED";
    public static final String ACTION_UPDATE_BROKEN = "com.nfdaily.nfplus.ACTION_UPDATE_BROKEN";

    private BroadcastReceiver myBroadcastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_UPDATE_PROGRESS.equals(action)) {
                //更新进度
                int percent = (int) intent.getLongExtra(ACTION_UPDATE_PROGRESS, 0);
                if(upgradeDialog != null){
                    upgradeDialog.upgradeProcess(percent);
                }

            } else if (ACTION_UPDATE_FINISHED.equals(action)) {
                //完成
                if(upgradeDialog != null){

                    upgradeDialog.upgradeFinish(100);
                }
            } else if (ACTION_UPDATE_ERROR.equals(action)) {
                //错误
                if(upgradeDialog != null){

                    upgradeDialog.upgradeError(0);
                }
            } else if(ACTION_UPDATE_BROKEN.equals(action)){
                //下载错误
                if(upgradeDialog != null){
                    upgradeDialog.upgradeError(0);
                    upgradeDialog.dismiss();
                }
                if(UpgradeActivity.this != null){
                    UpgradeActivity.this.finish();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_ERROR);
        filter.addAction(ACTION_UPDATE_FINISHED);
        filter.addAction(ACTION_UPDATE_PROGRESS);
        filter.addAction(ACTION_UPDATE_BROKEN);
        registerReceiver(myBroadcastReciver, filter);


        upgradeModel = (UpgradeModel) getIntent().getSerializableExtra(UpgradeModel.TAG);
        //测试代码
//        upgradeModel.setContent("提示：\n1.修复了peter的bug\n2.修复了bob的bug\n3.修复了lucy的bug");
//        upgradeModel.setTitle("升级提示");
//        upgradeModel.setUpgradeFlag("2");
//        upgradeModel.setVersion("2.3.3");
//        upgradeModel.setDownloadUrl("http://gdown.baidu.com/data/wisegame/74cc5f397f25b197/yingyongbao_7042130.apk");
        //测试代码---end

        if ("1".equals(upgradeModel.isForcedUpdate)) {
            upgradeDialog = new UpgradeForceDialog(this, upgradeModel);
            upgradeDialog.show();
        } else {
            upgradeDialog = new UpgradeChoiceDialog(this, upgradeModel);
            upgradeDialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReciver);
    }


}
