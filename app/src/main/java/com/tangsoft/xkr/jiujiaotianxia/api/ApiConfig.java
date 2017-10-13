package com.tangsoft.xkr.jiujiaotianxia.api;

/**
 * Created by Administrator on 2017-02-04.
 */
public class ApiConfig {
    public static int PAGE_SIZE=10;
    public static boolean DEBUG = true;
    private static String host = "http://app.jiujtx.com";
    public static void setHost(String host1) {
        host = host1;
    }

    public static String getHost() {
        return host;
    }

    public static final String convert(byte[] bytes) {
        if (bytes == null)
            return null;
        return new String(bytes);
    }

    public static void  setPageSize(int pageSize){
        PAGE_SIZE = pageSize;
    }
}
