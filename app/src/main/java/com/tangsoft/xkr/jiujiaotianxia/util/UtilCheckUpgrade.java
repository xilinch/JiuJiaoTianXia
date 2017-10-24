package com.tangsoft.xkr.jiujiaotianxia.util;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tangsoft.xkr.jiujiaotianxia.MainActivity;
import com.tangsoft.xkr.jiujiaotianxia.UpgradeActivity;
import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import org.json.JSONObject;

import java.util.Date;

import cz.msebera.android.httpclient.message.BasicNameValuePair;


/**
 * Created by xilinch on 2016/6/6.
 * 检查升级
 */
public class UtilCheckUpgrade {
    private static final String TAG = "UtilCheckUpgrade";

    /**
     * 普通的检查升级
     *
     * @param activity
     */
    public static void checkUpgrade(final Activity activity) {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("platform","android");
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("version", UtilSystem.getVersionName(activity));
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("osversion",UtilSystem.getOSVersion());
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("manufactor", UtilApp.getManufactor());
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("channel",UtilApp.getChanel(activity));
//        BasicNameValuePair nameValuePair = new BasicNameValuePair("versioncode ", UtilSystem.getVersionCode(activity) + "");
        params.addBodyParameter("platform","android");
        params.addBodyParameter("version", UtilSystem.getVersionName(activity));
        params.addBodyParameter("osversion",UtilSystem.getOSVersion());
        params.addBodyParameter("manufactor", UtilApp.getManufactor());
        params.addBodyParameter("channel",UtilApp.getChanel(activity));
        params.addBodyParameter("versioncode ", UtilSystem.getVersionCode(activity) + "");
        String url = "api.xtyxmall.com/index.php/AppVersion/jjtx";
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (responseInfo != null) {
                    String response = responseInfo.result;
                    if (!TextUtils.isEmpty(response)) {
                        //解析数据
                        Log.i("my", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                            String stateCode = jsonObject.optString("stateCode");
                            int systemCode = jsonObject.optInt("systemCode");
                            int stateCode_int = Integer.parseInt(stateCode);
                            if (stateCode_int == 0 && systemCode == 200 && dataJsonObject != null && !TextUtils.isEmpty(dataJsonObject.toString())) {
                                //接口成功
//                                UpgradeModel upgradeModel = FastJsonUtil.parseObject(jsonObject.toString(), UpgradeModel.class);
                                UpgradeModel upgradeModel = new UpgradeModel();

                                //toUpgrade
                                toUpgrade(activity, upgradeModel);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    public static void toUpgrade(Activity activity, UpgradeModel upgradeModel) {
        if (upgradeModel != null && "1".equals(upgradeModel.isForcedUpdate)) {
            //强制更新
            Intent intent = new Intent(activity, UpgradeActivity.class);
            intent.putExtra(UpgradeModel.TAG, upgradeModel);
            activity.startActivity(intent);
        } else if (upgradeModel != null && "0".equals(upgradeModel.isForcedUpdate)) {
            //可选升级
            Intent intent = new Intent(activity, UpgradeActivity.class);
            intent.putExtra(UpgradeModel.TAG, upgradeModel);
            //强制升级，需要判断是否已忽略版本
            if ("1".equals(upgradeModel.isForcedUpdate)) {
                activity.startActivity(intent);
            } else {
                String version = upgradeModel.versionName;
                String ignoreVersion = SPHelper.make(activity).getStringData(version, "");
                String dateString = UtilDateString.format(new Date(), UtilDateString.FORMAT_SHORT);
                String ignoreDateString = SPHelper.make(activity).getStringData(dateString, "");
                Logger.i("版本升级：ignoreVersion:" + ignoreVersion + "  ignoreDateString:" + ignoreDateString);
                if (NetworkUtils.isWifiNet(activity)) {
                    if (activity != null && activity instanceof MainActivity && (TextUtils.isEmpty(ignoreVersion) && TextUtils.isEmpty(ignoreDateString))) {
                        ((MainActivity) activity).upgradeBySilence(upgradeModel);
                    }
                } else {
                    if (TextUtils.isEmpty(ignoreVersion) && TextUtils.isEmpty(ignoreDateString)) {
                        activity.startActivity(intent);
                    } else {
                        //说明已经忽略
                        Logger.i("忽略版本升级：" + version);
                    }
                }
            }

        }
    }

    /**
     * 生成升级请求参数
     *
     * @param activity
     * @return
     */
//    private static HttpEntity createUpgradeHttpEntity(Activity activity) {
//        //insideVersion内部版本号，100
//        //currentVersion对外版本号 2.0.1
//        //clientUseragent渠道号
//        //appType 客户端类型 android0，ios 1，其他2
//        //netstate 网络类型，wifi则为wifi，
//        JSONObject jsonObject = new JSONObject();
//        HttpEntity httpEntity = null;
//        try {
////            jsonObject.put("insideVersion", AppUtil.getVersionCode(activity));
//            jsonObject.put("currentVersion", AppUtil.getVersionName(activity));
////            jsonObject.put("currentVersion", "v3.2.0");
//            String channel = ChannelUtil.getChannelFromApk(activity);
////            String channel= "vivo";
//            if (!StringUtils.isBlank(channel) && !channel.equals("unkonwn")) {
//                jsonObject.put("clientUseragent", channel);
//            }
//            jsonObject.put("appType", "0");
//            jsonObject.put("netstate", NetworkUtils.getNetState(activity));
//            httpEntity = new StringEntity(jsonObject.toString());
//        } catch (Exception exception) {
//            exception.printStackTrace();
//
//        } finally {
//            return httpEntity;
//        }
//    }

}
