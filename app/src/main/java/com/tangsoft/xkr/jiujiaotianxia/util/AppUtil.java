package com.tangsoft.xkr.jiujiaotianxia.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.UUID;

public class AppUtil {


    /**
     * 获取当前App的版本名
     */
    public static String getVersionName(Context context) {
        String versionName = "1.0.0";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return versionName;
        }
    }

    /**
     * 获取版本号(内部识别号)
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 获取UUID
     *
     * @return
     */
    public static String getMyUUID() {
        String uniqueId = null;
        String var1 = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 + Build.TAGS.length() % 10 + Build.TYPE.length() % 10 + Build.USER.length() % 10;

        try {
            uniqueId = Build.class.getField("SERIAL").get(null).toString();
            return (new UUID((long) var1.hashCode(), (long) uniqueId.hashCode())).toString();
        } catch (Exception var3) {
            uniqueId = "serial";
            return (new UUID((long) var1.hashCode(), (long) uniqueId.hashCode())).toString();
        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return statusBarHeight;
        }

    }


    /**
     * 将系统的编译版本 转化为double类型的
     * @return
     */
    public static final double parseVersion2Double(){
        double version = 0;
        String strVer = Build.VERSION.RELEASE;
        int nIndex = strVer.indexOf('.', strVer.indexOf('.') + 1);
        if (-1 != nIndex) {
            strVer = strVer.substring(0, nIndex);
        }
        if(!TextUtils.isEmpty(strVer)){
            strVer = strVer.toLowerCase();
            if(strVer.contains("android")){
                strVer = strVer.replace("android","");
            }
        }
        try{
            version = Double.parseDouble(strVer);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

            return version;
        }
    }

}