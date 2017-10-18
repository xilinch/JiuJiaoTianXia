package com.tangsoft.xkr.jiujiaotianxia.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 分享信息
 * {"spreadUrl":"http://www.baidu.com/","spreadContent":"分享测试内容","imgUrl":"http://yun.t.7lk.com/workspace/assets/image/success.png","title":"分享测试标题"}
 */
public class ShareInfo implements Serializable {
    /**标题*/
    private String title = "";
    /**链接*/
    private String spreadUrl = "";
    /**内容*/
    private String spreadContent = "";
    /**图片*/
    private String imgUrl = "";

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)){
            this.imgUrl = imgUrl;
        }
    }

    public String getSpreadContent() {
        return spreadContent;
    }

    public void setSpreadContent(String spreadContent) {
        if (!TextUtils.isEmpty(spreadContent)){
            this.spreadContent = spreadContent;
        }
    }

    public String getSpreadUrl() {
        return spreadUrl;
    }

    public void setSpreadUrl(String spreadUrl) {
        if (!TextUtils.isEmpty(spreadUrl)){
            this.spreadUrl = spreadUrl;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)){
            this.title = title;
        }
    }
}