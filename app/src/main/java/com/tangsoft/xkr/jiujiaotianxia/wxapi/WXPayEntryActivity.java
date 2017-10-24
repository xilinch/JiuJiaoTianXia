package com.tangsoft.xkr.jiujiaotianxia.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tangsoft.xkr.jiujiaotianxia.MainActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by wayne on 7/31/16.
 * 微信支付回调入口Activity
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    public static final String WX_APPID = "wx0b0180abce364a9d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_APPID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Toast.makeText(this,"start pay wx", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(this.getClass().getSimpleName(),  "微信返回结果errCode=" + resp.errCode + ", errStr=" + resp.errStr);
        // 微信支付
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    toastShow(WXPayEntryActivity.this, "通过微信支付成功");
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_WX_PAY_SUCCESS);
                    WXPayEntryActivity.this.sendBroadcast(intent);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    toastShow(WXPayEntryActivity.this, "通过微信支付取消");
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    toastShow(WXPayEntryActivity.this, "通过微信支付认证失败");
                    break;
                default:
                    toastShow(WXPayEntryActivity.this, "通过微信支付失败");
                    break;
            }
//            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            resp.toBundle(bundle);
//            intent.putExtras(bundle);
//            intent.setAction(WXPayEntity.ACTION_PAY_RESULT_WECHAT);
//            sendBroadcast(intent);
        }
        finish();
    }

    private void toastShow(Context context, String msg){
        Toast.makeText(WXPayEntryActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
