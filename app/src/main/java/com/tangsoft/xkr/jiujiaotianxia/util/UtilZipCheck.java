package com.tangsoft.xkr.jiujiaotianxia.util;

import android.os.Environment;
import android.util.Log;

import com.tangsoft.xkr.jiujiaotianxia.service.UpgradeIntentService;

import java.io.File;
import java.util.zip.ZipFile;

/**
 * Created by 123 on 2017/9/23.
 */

public class UtilZipCheck {

    public static boolean isErrorZip(String filePath) {
        File file = new File(filePath);
        boolean isRight = true;

        try {
            new ZipFile(file);
        } catch (Exception var4) {
            var4.printStackTrace();
            Log.e("my", "installAPK zipFile error");
            isRight = false;
            if(file != null && file.exists()) {
                file.delete();
            }
        }

        return isRight;
    }

    /**
     * 清除缓存
     * @return
     */
    public static boolean clearAPK(){
        boolean success = false;
        try{
            File file = Environment.getExternalStorageDirectory();
            if(file != null){
                String filePath = file.getAbsolutePath().concat(UpgradeIntentService.FILE_DIRECTORY );
                File deretory = new File(filePath);
                if(deretory != null && deretory.exists()){
                    for (File delFile:deretory.listFiles()) {
                        delFile.delete();
                    }
                    success = true;
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return success;
    }
}
