package com.tangsoft.xkr.jiujiaotianxia.model;

import java.io.Serializable;

/**
 * Created by 123 on 2017/9/23.
 */

public class UpgradeModel implements Serializable {
    public static final String TAG = "UpgradeModel";
    public String is_mandatory_update;
    public String updateContent;
    public String versionName;
    public String downloadUrl;

}
