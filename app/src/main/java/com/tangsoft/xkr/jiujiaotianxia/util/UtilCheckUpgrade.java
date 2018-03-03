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
import com.lidroid.xutils.util.LogUtils;
import com.tangsoft.xkr.jiujiaotianxia.MainActivity;
import com.tangsoft.xkr.jiujiaotianxia.UpgradeActivity;
import com.tangsoft.xkr.jiujiaotianxia.model.UpgradeModel;
import org.json.JSONObject;

import java.util.Date;


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
        params.addBodyParameter("platform","android");
        params.addBodyParameter("version", UtilSystem.getVersionName(activity));
        params.addBodyParameter("osversion",UtilSystem.getOSVersion());
        params.addBodyParameter("manufactor", UtilApp.getManufactor());
        params.addBodyParameter("channel",UtilApp.getChanel(activity));
        params.addBodyParameter("versioncode", UtilSystem.getVersionCode(activity) + "");
        String url = "http://api.xtyxmall.com/index.php/AppVersion/jjtx";
        Log.e("my","params:" + params.toString());
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
                            JSONObject dataJsonObject = jsonObject.optJSONObject("datas");


                            String systemCode = jsonObject.optString("code");
                            int stateCode_int = Integer.parseInt(systemCode);
                            if (stateCode_int == 1 && dataJsonObject != null) {
                                String is_mandatory_update = dataJsonObject.optString("is_mandatory_update");
                                String notice = dataJsonObject.optString("notice");
                                String update_version = dataJsonObject.optString("update_version");
                                String url = dataJsonObject.optString("url");
                                //接口成功
                                UpgradeModel upgradeModel = new UpgradeModel();
                                //测试代码
//                                upgradeModel.is_mandatory_update = "0";
//                                upgradeModel.downloadUrl = "http://gdown.baidu.com/data/wisegame/68d7f8ddbbf3480b/yingyongbao_7122130.apk";
                                //测试代码 end
                                upgradeModel.is_mandatory_update = is_mandatory_update;
                                upgradeModel.updateContent = notice;
                                upgradeModel.versionName = update_version;
                                upgradeModel.downloadUrl = url;

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
                LogUtils.e(s);
            }
        });
    }

    public static void toUpgrade(Activity activity, UpgradeModel upgradeModel) {
        if (upgradeModel != null && "1".equals(upgradeModel.is_mandatory_update)) {
            //强制更新
            Intent intent = new Intent(activity, UpgradeActivity.class);
            intent.putExtra(UpgradeModel.TAG, upgradeModel);
            activity.startActivity(intent);
        } else if (upgradeModel != null && "0".equals(upgradeModel.is_mandatory_update)) {
            //可选升级
            Intent intent = new Intent(activity, UpgradeActivity.class);
            intent.putExtra(UpgradeModel.TAG, upgradeModel);
            //强制升级，需要判断是否已忽略版本
            if ("1".equals(upgradeModel.is_mandatory_update)) {
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

}
