package com.tangsoft.xkr.jiujiaotianxia.helper;


import com.alibaba.fastjson.JSON;
import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

/**
 * Created by yujinping on 2015/12/2.
 */
public class JsonHelper {
    static final Logger logger = LoggerFactory.getLogger(JsonHelper.class);
    public static <T> T toBean(Class<T> glass, JSONObject object){
        if(object==null)
            return null;
        String txt = object.toString();
        return JSON.parseObject(txt,glass);
    }
    public static <T> List<T> toList(Class<T> glass, JSONArray array){
        if(array==null)
            return null;
        String txt = array.toString();
        return JSON.parseArray(txt,glass);
    }

    public static JSONObject findJsonObject(byte[]bytes,String selector){
        JSONObject object;
        try {
            object = new JSONObject(ApiConfig.convert(bytes));
            String[] sp = selector.split("\\.");
            for(String s:sp){
                if(object!=null)
                    object = object.getJSONObject(s);
            }
            return object;
        } catch (JSONException e) {
            if(ApiConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static JSONArray findJsonArray(byte[]bytes,String selector){
        JSONObject object;
        String body =ApiConfig.convert(bytes);
        try {
            object = new JSONObject(body);
            String[] sp = selector.split("\\.");
            JSONObject tmp=object;
            for(int i=0;i<sp.length-1;i++){
                if(tmp!=null)
                    tmp = tmp.optJSONObject(sp[i]);
            }

            if(tmp!=null)
                return tmp.optJSONArray(sp[sp.length-1]);
            else
                return null;
        } catch (JSONException e) {
            if(ApiConfig.DEBUG) {
                logger.error("JsonHelper.findJsonArray() called!body={}", body);
                e.printStackTrace();
            }
            return null;
        }
    }
    public static <T> T asBean(Class<T> glass,byte[] bytes){
        if(bytes==null||bytes.length==0)
            return null;
        String txt = ApiConfig.convert(bytes);
        try {
            if(txt.startsWith("{") && txt.endsWith("}"))
                return JSON.parseObject(txt,glass);
            else
                return null;
        }catch (Exception e){
            if(ApiConfig.DEBUG) {
                e.printStackTrace();
                logger.error("body={}", txt);
            }
            return null;
        }
    }
    public static <T> List<T> asList(Class<T> glass,byte[] bytes){
        if(bytes==null)
            return null;
        String txt = bytes.toString();
        if(txt.startsWith("[") && txt.endsWith("]"))
            return JSON.parseArray(txt,glass);
        else
            return null;
    }

    public static String toJsonString(Object o){
        return JSON.toJSONString(o);
    }
}
