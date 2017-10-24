package com.tangsoft.xkr.jiujiaotianxia.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tangsoft.xkr.jiujiaotianxia.UpgradeSilenceActivity;
import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import com.tangsoft.xkr.jiujiaotianxia.util.UtilZipCheck;

import java.io.File;

/**
 * Created by xilinch on 2016/6/3.
 * 升级下载服务，支持断点下载
 */
public class UpgradeSilenceIntentService extends IntentService {

    private static final String TAG = "UpgradeSilenceIntentService";

    private UpgradeModel upgradeModel;

    public static boolean isDownloading = false;

    private HttpUtils httpUtils;

    private String filePath;

    public UpgradeSilenceIntentService() {
        super(TAG);
    }

    public UpgradeSilenceIntentService(String name) {
        super(name);
    }

    public static final String FILE_DIRECTORY = "/jjtx/apk/";

    @Override
    protected void onHandleIntent(Intent intent) {
        upgradeModel = (UpgradeModel) intent.getSerializableExtra(UpgradeModel.TAG);
        if(upgradeModel != null){
            if (isDownloading) {
                //正在下载
//                Toast.makeText(UpgradeSilenceIntentService.this, "正在下载...", Toast.LENGTH_SHORT).show();
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
        String url = upgradeModel.downloadUrl;
        String fileName = getFileNameFromString(url);
        if(TextUtils.isEmpty(fileName)){
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

                        Intent intent = new Intent();
                        intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS);
                        intent.putExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_PROGRESS, percent);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        isDownloading = false;
//                        installAPK(responseInfo.result.getPath());
                        boolean isRight = UtilZipCheck.isErrorZip(filePath);
                        if(isRight){
                            Intent intent = new Intent();
                            intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED);
                            intent.putExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED, 100);
                            sendBroadcast(intent);
                        } else {
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
                                Intent intent = new Intent();
                                intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED);
                                intent.putExtra(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_FINISHED, 100);
                                sendBroadcast(intent);
                            } else {
                                UtilZipCheck.clearAPK();
                            }
                            //下载已经完成
//                            installAPK(filePath);
                        } else {
//                            Intent intent = new Intent();
//                            intent.setAction(UpgradeSilenceActivity.ACTION_UPDATE_SILENCE_ERROR);
//                            sendBroadcast(intent);
                            UtilZipCheck.clearAPK();
                        }
                    }
                });

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
    }
}
