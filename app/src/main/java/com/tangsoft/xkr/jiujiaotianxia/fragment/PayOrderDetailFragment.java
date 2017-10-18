package com.tangsoft.xkr.jiujiaotianxia.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tangsoft.xkr.jiujiaotianxia.MainActivity;
import com.tangsoft.xkr.jiujiaotianxia.R;
import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;
import com.tangsoft.xkr.jiujiaotianxia.listener.DownloadResultDealWithListener;
import com.tangsoft.xkr.jiujiaotianxia.wxapi.WXPayEntryActivity;
import com.tangsoft.xkr.jiujiaotianxia.zfbapi.PayResult;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016-12-29.
 */
public class PayOrderDetailFragment extends DialogFragment {

    @Bind(R.id.lin_pay_way)
    LinearLayout linPayWay;

    Dialog dialog = null;

    String orderId = "";
    Context context;

    String zfbInfo = "";

    String wxInfo = "";

    ProgressDialog progressDialog;

    MainActivity activity = null;

    public static final PayOrderDetailFragment newInstance(String orderId) {
        PayOrderDetailFragment fragment = new PayOrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId", orderId);
        fragment.setArguments(bundle);
        return fragment;
    }


    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (activity != null) {
                            activity.loadPayResult();
                            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("TAG", "支付宝支付成功");


                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (activity != null) {
                            Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                        }

                        Log.i("TAG", "支付宝支付失败");
                    }
                    break;
                }
            }
        }

        ;
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.fragment_pay_order_deatil);
        ButterKnife.bind(getActivity());
        dialog.setCanceledOnTouchOutside(true); // 外部点击取消
        // 设置宽度为屏宽, 靠近屏幕底部。
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM; // 紧贴底部

        //屏幕宽高 getHeight()过时
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        lp.height = height * 3 / 5;
        window.setAttributes(lp);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = inflater.inflate(R.layout.fragment_pay_order_deatil, container, false);
        ButterKnife.bind(this, rootView);
        orderId = getArguments().getString("orderId");
        activity = (MainActivity) getActivity();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在获取支付订单，请稍后...");
        progressDialog.setCanceledOnTouchOutside(false);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.image_cancel,
            R.id.tv_pay_zfb, R.id.tv_pay_wx, R.id.tv_pay_unionpay})
    public void onClick(View view) {

        Animation slide_left_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_left);
        Animation slide_right_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_right_to_left);
        Animation slide_left_to_right = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_right);
        Animation slide_left_to_left_in = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_left_to_left_in);
        switch (view.getId()) {
            case R.id.image_cancel:     //关闭弹出框
                dialog.cancel();
                break;

            case R.id.tv_pay_zfb:
                payZFB();
                dialog.cancel();
                break;
            case R.id.tv_pay_wx:            //微信支付
//                linPayWay.startAnimation(slide_left_to_right);
//                linPayWay.setVisibility(View.GONE);
                payWX();
                dialog.cancel();
                break;
            case R.id.tv_pay_unionpay:  //银联支付
                linPayWay.startAnimation(slide_left_to_right);
                linPayWay.setVisibility(View.GONE);
                break;
        }
    }

    private void payZFB() {
        try {
            final URL url = new URL(ApiConfig.getHost() + "/WebChat/Api/AliPay/AliPayRequest.aspx");
            Map<String, String> params = new HashMap<>();
            params.put("orderno", orderId);
            final JSONObject object = new JSONObject(params);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("正在获取支付订单数据,请稍后...");
                    String data = postAliPay(url, object.toString(), "UTF-8");
                    toPayAliPay(data);
                }
            }).start();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void toPayAliPay(String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            Log.i("TAG", "data = " + data);
            zfbInfo = jsonObject.getString("alipay");
            Log.i("TAG", "zfbInfo = " + zfbInfo);
            if (activity != null && zfbInfo.length() > 0) {
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(activity);
                        Map<String, String> result = alipay.payV2(zfbInfo, true);
                        Log.i("msp", result.toString());
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            } else {
                Toast.makeText(activity, "调用支付宝异常!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String postAliPay(URL url, String params, String encode) {

        byte[] data = new byte[0];
        try {
            data = params.getBytes(encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);       //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "text/xml");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
            progressDialog.cancel();
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.cancel();
        }
        return "";
    }

    public String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    private void payWX() {
        try {
            final URL url = new URL(ApiConfig.getHost() + "/WebChat/Api/WxPay/WeiXinRequest.aspx");
            Map<String, String> params = new HashMap<>();
            params.put("orderno", orderId);
            final JSONObject object = new JSONObject(params);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage("正在获取支付订单数据,请稍后...");
                    final String data = postAliPay(url, object.toString(), "UTF-8");
                    toPayWxPay(data);
//                    if(getActivity() != null){
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                toPayWxPay(data);
//                            }
//                        });
//                    }
                }
            }).start();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void toPayWxPay(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Log.i("TAG", "data = " + data);
            //{"result":0,"message":"生成预支付交易单成功！","appid":"wx0b0180abce364a9d","partnerid":"1481491612",
            // "prepayid":"wx201710182119147a4dda58b80996008510","package":"Sign=WXPay",
            // "noncestr":"ca33ad5c416d4461a84884720b72fdb8","timestamp":"1508332755","sign":"3103569A268E318A74E797CB08436DEA"}
            wxInfo = data;
            if (activity != null && wxInfo.length() > 0) {
                IWXAPI msgApi = WXAPIFactory.createWXAPI(activity, WXPayEntryActivity.WX_APPID);
                try{
                    if(!msgApi.isWXAppInstalled()){
                        Toast.makeText(getContext(), "没有安装微信支付", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!msgApi.isWXAppSupportAPI()){
                        Toast.makeText(getContext(), "微信不支持", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PayReq request = new PayReq();
                    request.appId = jsonObject.optString("appid");
                    request.partnerId = jsonObject.optString("partnerid");
                    request.prepayId= jsonObject.optString("partnerid");
                    request.packageValue = jsonObject.optString("package");
                    request.nonceStr= jsonObject.optString("noncestr");
                    request.timeStamp= jsonObject.optString("timestamp");
                    request.sign= jsonObject.optString("sign");
                    msgApi.sendReq(request);
                }catch (Exception e){
                    Toast.makeText(getContext(), "没有安装微信支付", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();}
            } else {
                Toast.makeText(activity, "调用支付宝异常!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
