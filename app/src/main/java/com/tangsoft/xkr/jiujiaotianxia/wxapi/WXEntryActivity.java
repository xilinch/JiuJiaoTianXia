package com.tangsoft.xkr.jiujiaotianxia.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Iterator;

/**
 *
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    private IWXAPI mWxAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("my", "WXEntryActivity onCreate ------- "+getIntent().getExtras());
        Bundle bundleExtras = getIntent().getExtras();
        if(bundleExtras != null){
            Iterator iterator = bundleExtras.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next().toString();
//                Log.i("my", "iterator key: ." + key+ "  value:" + bundleExtras.getString(key));
            }
            if(null == bundleExtras.getString("_wxapi_baseresp_errcode")
                    && null == bundleExtras.getString("_wxapi_baseresp_errstr")){
                finish();
                return;
            }
        }

        mWxAPI = WXAPIFactory.createWXAPI(this, WXPayEntryActivity.WX_APPID,false);
        mWxAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        Log.d("my", "WXEntryActivity onCreate ------- "+getIntent().getExtras());
        mWxAPI.handleIntent(intent,this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
//        Log.e("my", "onReq:" + baseReq.openId);
        finish();
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("my", baseResp.errStr);
        Log.e("my", "9错误码 : " + baseResp.errCode + "");
        if (baseResp.getType() == 1) {
            switch (baseResp.errCode) {

                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    if (RETURN_MSG_TYPE_SHARE == baseResp.getType()) {
                        Toast.makeText(WXEntryActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                case BaseResp.ErrCode.ERR_OK:
                    switch (baseResp.getType()) {
                        case RETURN_MSG_TYPE_LOGIN:
                            //拿到了微信返回的code,再去请求access_token
                            String code = ((SendAuth.Resp) baseResp).code;
                            Log.e("my", "code = " + code);
//                        requestWXToken(code);
//                        requestUserInfo(code);
                            finish();
                            break;

                        case RETURN_MSG_TYPE_SHARE:
                            Toast.makeText(WXEntryActivity.this, "微信分享成功", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                    break;
                default:
                    finish();
            }
        } else if (baseResp.getType() == 2) {
            finish();
        } else {
            finish();
        }

    }

//    public void requestWXToken(String code){
//        Map<String,Object> map = new HashMap<>();
//        Http.get("https://api.weixin.qq.com/sns/oauth2/access_token?" +
//                "appid="+ Config.WX_AppID +
//                "&secret="+ Config.WX_AppSecret +
//                "&code="+ code +
//                "&grant_type=authorization_code",map,new JsonRespHandler(){
//
//            @Override
//            public boolean onMatchAppStatusCode(ReqInfo reqInfo, RespInfo respInfo, JsonModel resultBean) {
//                return true;
//            }
//
//            @Override
//            public void onSuccessAll(ReqInfo reqInfo, RespInfo respInfo, JsonModel resultBean) {
//                super.onSuccessAll(reqInfo, respInfo, resultBean);
//                if(!TextUtils.isEmpty(resultBean.getString("access_token"))) {
//                    //requestUserInfo(resultBean.getString("access_token"));
//                    Log.shortToast("微信登录成功 ：" + resultBean.getString("access_token"));
//                }else {
//                    Log.shortToast("微信登录错误码 ：" + resultBean.getString("errcode"));
//                    finish();
//                }
//
//            }
//        });
//    }
//
//    // 请求微信登录用户接口
//    public void requestUserInfo(String code){
//        try{
//            JSONObject jsonObject = new JSONObject();
//
//            jsonObject.put("code",code);
//
//            Http.postJson(Config.getHostUrl(Config.WECHAT_LOGIN),jsonObject.toString(),new JsonRespHandler(this){
//
//                @Override
//                public boolean onMatchAppStatusCode(ReqInfo reqInfo, RespInfo respInfo, JsonModel resultBean) {
//                    return true;
//                }
//
//                @Override
//                public void onSuccessAll(ReqInfo reqInfo, RespInfo respInfo, JsonModel resultBean) {
//                    super.onSuccessAll(reqInfo, respInfo, resultBean);
//
//                    Sp.setUserToken(resultBean.getString("token"));
//                    Sp.setLogin(true);
//                    if("1".equals(resultBean.getRet())) {
//                        MainActivity.actionStart(WXEntryActivity.this);
//                    }else if("2".equals(resultBean.getRet())){
//                        WebViewActivity.actionStart(WXEntryActivity.this, Config.getHtmlUrl(Config.PASSPORT),"");
//                    }else{
////                        LoginActivity.actionStart(WXEntryActivity.this);
//                        Log.shortToast("登录失败!");
//                    }
//                    finish();
//                }
//            });
//        }catch (Exception e){e.printStackTrace();}
//    }

}
