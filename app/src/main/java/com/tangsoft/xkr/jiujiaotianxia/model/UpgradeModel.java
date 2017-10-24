package com.tangsoft.xkr.jiujiaotianxia.model;

import java.io.Serializable;

/**
 * Created by 123 on 2017/9/23.
 */

public class UpgradeModel implements Serializable {
    public static final String TAG = "UpgradeModel";
    public String phoneOS;
    public String versionNo;
    public String versionName;
    public String downloadUrl;
    public String releaseTime;
    public String fileRsurl;
    public String updateContent;
    public String copyright;
    public String isForcedUpdate;
    public String createTime;
}
