package com.tangsoft.xkr.jiujiaotianxia.api;

import android.util.Log;


import com.tangsoft.xkr.jiujiaotianxia.bean.Result;
import com.tangsoft.xkr.jiujiaotianxia.helper.JsonHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 *
 */
public class Rest {
    private static final Logger logger = LoggerFactory.getLogger(Rest.class);
    private static final int TIME_OUT = 30000;

    private CookieStore cookieStore = null;

    public CookieStore getCookieStore() {
        if (cookieStore == null)
            cookieStore = new BasicCookieStore();
        return cookieStore;
    }

    public Cookie getCookie(String cookieName) {
        CookieStore store = getCookieStore();
        List<Cookie> cookies = store.getCookies();
        if (cookies != null && cookies.size() > 0) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName()))
                    return cookie;
            }
        }
        return null;
    }
    public void signOut(){
        getCookieStore().clear();
    }
    public boolean isSignIn(){
        Cookie cookie = Rest.getInstance().getCookie("Web.Member");
        if(cookie!=null && !cookie.isExpired(new Date()))
            return true;
        return  false;
    }
    private static Rest instance = null;

    public static Rest getInstance() {
        if (instance == null) {
            instance = new Rest();
        }
        return instance;
    }

    public Result get(String uri, Map<String, String> params) {
        return execute(false, uri, params);
    }

    public Result post(String uri, Map<String, String> params) {
        return execute(true, uri, params);
    }

    private Result execute(boolean isPost, String uri, Map<String, String> params) {
        long sid = System.currentTimeMillis();
        String msg = "";
        URI location = null;
        Result result = new Result();
        result.setResult(Integer.MAX_VALUE);
        result.setMessage("网络IO错误!");
        try {
            location = new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(getCookieStore())
                .build();

        RequestBuilder builder;
        if (isPost)
            builder = RequestBuilder.post().setUri(location);
        else
            builder = RequestBuilder.get().setUri(location);

        builder.addHeader("Content-Type", "text/html;charset=UTF-8");

        if (params != null && params.size() > 0) {
            List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, String> e : params.entrySet()) {
                pairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
                //builder.addParameter(e.getKey(), encode(e.getValue()));
            }
            if (ApiConfig.DEBUG) {
                logger.info("Rest Req_{}.URL= {}", sid, uri);
                logger.info(">>Parameters start>>");
                for (NameValuePair pair : pairs) {
                    logger.info("{}={}", pair.getName(), pair.getValue());
                }
                logger.info("<<Parameters end.<<");
            }
            try {
                builder.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (ApiConfig.DEBUG)
                logger.info("Rest Req_{}.URL= {} ", sid, uri);
        }

        HttpUriRequest request = builder.build();//request.getParams()

        CloseableHttpResponse response = null;
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIME_OUT)
                    .setConnectTimeout(TIME_OUT)
                   .setConnectionRequestTimeout(TIME_OUT)
                    .build();
            HttpClientContext context = HttpClientContext.create();
            context.setRequestConfig(requestConfig);
            response = httpclient.execute(request, context);
            int status = response.getStatusLine().getStatusCode();
            byte[] content = null;
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                content = EntityUtils.toByteArray(entity);
            }
            if (content != null)
                result.setResponse(content);
            if (ApiConfig.DEBUG)
                logger.info("Rest Response_{}.Content = {}", sid, ApiConfig.convert(content));

            Log.i("TAG","status = "+status);
            if (status >= 200 && status < 300) {
                Result result1 = JsonHelper.asBean(Result.class, content);
                if (result1 != null) {
                    result.setResult(result1.getResult());
                    result.setMessage(result1.getMessage());
                    result.setRecordcount(result1.getRecordcount());
                    result.setResponse(content);
                }
            } else {
                result.setMessage(String.format("错误:%s\n内容:%s", status, ApiConfig.convert(result.getResponse())));
            }
        } catch (java.net.SocketTimeoutException e) {

            msg = "连接服务器超时,请稍后重试!!";

        } catch (ClientProtocolException e) {
            msg = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            msg = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
                if (ApiConfig.DEBUG)
                    e.printStackTrace();
            }
            try {
                if (httpclient != null)
                    httpclient.close();
            } catch (IOException e) {
                if (ApiConfig.DEBUG)
                    e.printStackTrace();
            }
            if (msg != null && msg.length() > 0) {
                result = new Result();
                result.setResult(Integer.MAX_VALUE);
                result.setMessage(msg);
            }
        }
        return result;
    }
}
