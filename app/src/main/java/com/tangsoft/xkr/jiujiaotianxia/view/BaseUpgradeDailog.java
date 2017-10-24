package com.tangsoft.xkr.jiujiaotianxia.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import com.tangsoft.xkr.jiujiaotianxia.service.UpgradeIntentService;
import com.tangsoft.xkr.jiujiaotianxia.service.UpgradeSilenceIntentService;
import com.tangsoft.xkr.jiujiaotianxia.util.ToastUtils;
import com.tangsoft.xkr.jiujiaotianxia.util.UtilZipCheck;

import java.io.File;

/**
 * Created by Administrator on 2016/6/24.
 */
public abstract class BaseUpgradeDailog extends Dialog {
    protected Activity activity;

    public BaseUpgradeDailog(Activity context) {
        super(context);
    }

    public BaseUpgradeDailog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BaseUpgradeDailog(Activity context, int theme) {
        super(context, theme);
    }

    /**
     * 下载更新进度
     * @param percent
     */
    public abstract void upgradeProcess(int percent);

    /**
     * 下载错误
     * @param percent
     */
    public abstract void upgradeError(int percent);

    /**
     * 下载完成
     * @param percent
     */
    public abstract void upgradeFinish(int percent);



    protected boolean check(){
        boolean isValide = true;
        if(UpgradeIntentService.isDownloading){
            ToastUtils.showShort(activity,"正在下载...");
            isValide = false;
        }

        return isValide;
    }

    protected boolean checkAPKExist(UpgradeModel upgradeModel){
        boolean exist = false;
        boolean download = false;
        if(upgradeModel != null ){
            String url = upgradeModel.downloadUrl;
            String path = getFileNameFromString(url);
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(UpgradeIntentService.FILE_DIRECTORY + path);
            File file = new File(filePath);
            if(file != null && file.exists() && download){
                //存在
                download = UtilZipCheck.isErrorZip(filePath);
            }
        }

        return download;
    }

    public void install(UpgradeModel upgradeModel){
        if(upgradeModel == null){
            return;
        }
        String url = upgradeModel.downloadUrl;
        String fileName = getFileNameFromString(url);
        if(TextUtils.isEmpty(fileName)){
//            ToastUtils.toastShow(getContext(), "下载地址出错!");
            return;
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().concat(UpgradeSilenceIntentService.FILE_DIRECTORY + fileName);
        installAPK(filePath);
    }

    /**
     * 安装apk
     *
     * @param path
     */
    private void installAPK(String path) {
        try{
            boolean isRight = UtilZipCheck.isErrorZip(path);
            if(isRight){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    protected boolean checkBySilence(){
        boolean isValide = true;
        if(UpgradeIntentService.isDownloading){
//            ToastUtils.toastShow(activity, "正在下载...");
            isValide = false;
        }
        return isValide;
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

}
