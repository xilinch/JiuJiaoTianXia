package com.tangsoft.xkr.jiujiaotianxia.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangsoft.xkr.jiujiaotianxia.R;
import com.tangsoft.xkr.jiujiaotianxia.model.ShareInfo;
import com.tangsoft.xkr.jiujiaotianxia.util.UtilShare;

/**
 * @author 赖善琦
 * @description
 */
public class ShareDialog extends Dialog implements View.OnClickListener {
    Activity context;
    /**新浪微博 */
    private LinearLayout ll_share_weibo;
    /**微信好友 */
    private LinearLayout ll_share_wx;
    /**微信朋友圈 */
    private LinearLayout ll_share_wxpyq;
    /**QQ好友 */
    private LinearLayout ll_share_qq;
    /**取消按钮 */
    private TextView tv_share_cancle;

    private ShareInfo shareInfo;


    public ShareDialog(Activity context) {
        super(context, R.style.TransDialog);
        this.context = context;
        init();
    }

    public void init(){
        View view = LayoutInflater.from(context).inflate(R.layout.sk_dialog_share,null);
        ll_share_weibo = (LinearLayout)view.findViewById(R.id.ll_share_weibo);
        ll_share_wx = (LinearLayout)view.findViewById(R.id.ll_share_wx);
        ll_share_wxpyq = (LinearLayout)view.findViewById(R.id.ll_share_wxpyq);
        ll_share_qq = (LinearLayout)view.findViewById(R.id.ll_share_qq);
        tv_share_cancle = (TextView)view.findViewById(R.id.tv_share_cancle);


        ll_share_weibo.setOnClickListener(this);
        ll_share_wx.setOnClickListener(this);
        ll_share_wxpyq.setOnClickListener(this);
        ll_share_qq.setOnClickListener(this);
        tv_share_cancle.setOnClickListener(this);

        setContentView(view);
        setWindowLayoutStyleAttr();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            // 新浪微博
            case R.id.ll_share_weibo :
                new UtilShare(context,shareInfo, UtilShare.WEIBO);
                dismiss();
                break;
            // 微信好友
            case R.id.ll_share_wx :
                new UtilShare(context,shareInfo,UtilShare.WEIXIN);
                dismiss();
                break;
            // 微信朋友圈
            case R.id.ll_share_wxpyq :
                new UtilShare(context,shareInfo,UtilShare.PYQ);
                dismiss();
                break;
            // QQ好友
            case R.id.ll_share_qq :
                new UtilShare(context,shareInfo,UtilShare.QQ);
                dismiss();
                break;
            // 取消按钮
            case R.id.tv_share_cancle :
                dismiss();
                break;

        }
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    private void setWindowLayoutStyleAttr() {
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}
