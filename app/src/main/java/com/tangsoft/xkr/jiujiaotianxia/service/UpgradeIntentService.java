package com.tangsoft.xkr.jiujiaotianxia.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tangsoft.xkr.jiujiaotianxia.R;
import com.tangsoft.xkr.jiujiaotianxia.UpgradeActivity;
import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import com.tangsoft.xkr.jiujiaotianxia.util.ToastUtils;
import com.tangsoft.xkr.jiujiaotianxia.util.UtilZipCheck;

import java.io.File;

/**
 * Created by xilinch on 2016/6/3.
 * 升级下载服务，支持断点下载
 */
public class UpgradeIntentService extends IntentService {

    private static final String TAG = "UpgradeIntentService";

    private static final int NOTIFICATION_ID = 0X345444;

    private UpgradeModel upgradeModel;

    public static boolean isDownloading = false;

    private HttpUtils httpUtils;

    private NotificationManager notificationManager;

    private Notification notification;

    private NotificationCompat.Builder builder;

    private String filePath;

    public UpgradeIntentService() {
        super("UpgradeIntentService");
    }

    public UpgradeIntentService(String name) {
        super(name);
    }

    public static final String FILE_DIRECTORY = "/jjtx/apk/";

    @Override
    protected void onHandleIntent(Intent intent) {
        upgradeModel = (UpgradeModel) intent.getSerializableExtra(UpgradeModel.TAG);
        if(upgradeModel != null){
            initNotifycation();
            if (isDownloading) {
                //正在下载
                Toast.makeText(UpgradeIntentService.this, "正在下载...", Toast.LENGTH_SHORT).show();
            } else {
                //进入下载
                begainDownload();
            }
        }
    }

    /**
     * 开始下载
     */
    private synchronized void begainDownload() {
        isDownloading = true;
        httpUtils = new HttpUtils();
        final String url = upgradeModel.downloadUrl;
        String fileName = getFileNameFromString(url);
        if(TextUtils.isEmpty(fileName)){
            ToastUtils.showShort(this,"下载地址出错!");
            return;
        }
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(FILE_DIRECTORY + fileName);
        HttpHandler handler = httpUtils.download(url,
                filePath,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        long percent = (current * 100 / total);

                        if (builder != null) {
                            builder.setProgress(100, (int) percent, false);
                            builder.setContentText("下载进度" + percent + "%");
                            notification = builder.build();
                            if (notificationManager == null) {
                                notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            }
                            notificationManager.notify(NOTIFICATION_ID, builder.build());
                        }
                        Intent intent = new Intent();
                        intent.setAction(UpgradeActivity.ACTION_UPDATE_PROGRESS);
                        intent.putExtra(UpgradeActivity.ACTION_UPDATE_PROGRESS, percent);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        isDownloading = false;

                        //增加下载完成缓存记录
                        boolean isRight = UtilZipCheck.isErrorZip(filePath);
                        if(isRight){
                            installAPK(responseInfo.result.getPath());
                            Intent intent = new Intent();
                            intent.setAction(UpgradeActivity.ACTION_UPDATE_FINISHED);
                            intent.putExtra(UpgradeActivity.ACTION_UPDATE_FINISHED, 100);
                            sendBroadcast(intent);
                            if (builder != null) {
                                builder.setProgress(100, 100, false);
                                builder.setContentText("下载进度100%");
                                notification = builder.build();
                                if (notificationManager == null) {
                                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                }
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.setAction(UpgradeActivity.ACTION_UPDATE_BROKEN);
                            sendBroadcast(intent);
                            UtilZipCheck.clearAPK();
                        }

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        isDownloading = false;
                        int code = error.getExceptionCode();
                        if (code == 416) {
                            boolean isRight = UtilZipCheck.isErrorZip(filePath);
                            if(isRight){
                                //下载已经完成
                                installAPK(filePath);
                                Intent intent = new Intent();
                                intent.setAction(UpgradeActivity.ACTION_UPDATE_FINISHED);
                                intent.putExtra(UpgradeActivity.ACTION_UPDATE_FINISHED, 100);
                                sendBroadcast(intent);
                                if (builder != null) {
                                    builder.setProgress(100, 100, false);
                                    builder.setContentText("下载进度100%");
                                    //增加下载完成缓存记录
                                    notification = builder.build();
                                    if (notificationManager == null) {
                                        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    }
                                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                                }
                            } else {
                                UtilZipCheck.clearAPK();
                                Intent intent = new Intent();
                                intent.setAction(UpgradeActivity.ACTION_UPDATE_BROKEN);
                                sendBroadcast(intent);
                            }

                        } else {
                            if (builder != null) {
                                builder.setContentText("下载失败");
                                notification = builder.build();
                                if (notificationManager == null) {
                                    notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                }
                                notificationManager.notify(NOTIFICATION_ID, builder.build());
                            }
                            Intent intent = new Intent();
                            intent.setAction(UpgradeActivity.ACTION_UPDATE_ERROR);
                            sendBroadcast(intent);
                            UtilZipCheck.clearAPK();
                        }

                    }
                });

    }


    /**
     * 初始化通知栏
     */
    private void initNotifycation() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        if (notification == null) {
            builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.icon)
                    .setTicker("开始下载")
                    .setContentTitle("下载")
                    .setContentText("下载进度 0%")
                    .setProgress(100, 0, true);
            notification = builder.build();
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 安装apk
     *
     * @param path
     */
    private void installAPK(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 获取url的文件名
     * @return
     */
    private String getFileNameFromString(String url){
        String fileName = "";
        if(url != null){
            int index = url.lastIndexOf("/");
            if(index > 0){
                fileName = url.substring(index+ 1 ,url.length());
            }
        }
        return fileName;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDownloading = false;
        upgradeModel = null;
        notificationManager = null;
    }
}
