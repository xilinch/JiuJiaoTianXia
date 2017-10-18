package com.tangsoft.xkr.jiujiaotianxia.config;

/**
 * @author xiaocoder on 2016/5/7.
 * @modifier xiaocoder 2016/5/7 18:33.
 * @description
 */
public class ConfigFile {

    public static String APP_NAME = "xtyx";
    /**
     * app的根目录
     */
    public static String APP_ROOT = "app_" + APP_NAME;
    /**
     * crash日志目录
     */
    public static String CRASH_DIR = APP_ROOT + "/crash";
    /**
     * 图片加载缓存目录的目录
     */
    public static String CACHE_DIR = APP_ROOT + "/imgCache";
    /**
     * chat目录
     */
    public static String CHAT_DIR = APP_ROOT + "/chat";
    /**
     * 目录
     */
    public static String CHAT_PHOTO_DIR = CHAT_DIR + "/photo";
    /**
     * 目录
     */
    public static String CHAT_VIDEO_DIR = CHAT_DIR + "/voice";
    /**
     * 目录
     */
    public static String CHAT_MOIVE_DIR = CHAT_DIR + "/moive";
    /**
     * 日志文件名
     */
    public static String LOG_FILE = APP_ROOT + "_log";
    /**
     * sp文件名
     */
    public static String SP_FILE = APP_ROOT + "_sp";

}
