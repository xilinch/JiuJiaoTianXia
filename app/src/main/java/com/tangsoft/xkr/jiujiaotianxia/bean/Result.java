package com.tangsoft.xkr.jiujiaotianxia.bean;

import android.content.Context;
import android.content.Intent;

import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;
import com.tangsoft.xkr.jiujiaotianxia.helper.JsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class Result extends Base {
    public static final String NAME="Result";
    Integer result=-1;
    String message="";
    Integer recordcount = 0;
    byte[] response=new byte[0];

    public boolean isSuccess(){
        return result!=null && result.intValue()==0?true:false;
    }
    public boolean isLogicCookieExpired(){
        return (result!=null && result.intValue()==100);
    }
    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRecordcount() {
        return recordcount;
    }

    public void setRecordcount(Integer recordcount) {
        this.recordcount = recordcount;
    }
    public int getPageCount(){
        if(recordcount==null)
            return 1;
        int m = recordcount%ApiConfig.PAGE_SIZE;
        int page = (recordcount-m)/ ApiConfig.PAGE_SIZE+1;
        return page;
    }


    public byte[] getResponse() {

        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public String getResponseText(){

        return ApiConfig.convert(response);
    }

    public JSONObject getResponseJSONObject(){
        try {

            return new JSONObject(getResponseText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }
    public <T> List<T> getResponseObjectList(Class<T> k,String jsonNode){
        return JsonHelper.toList(k, JsonHelper.findJsonArray(getResponse(), jsonNode));
    }
    public <T> T getResponseObject(Class<T> k){
        return JsonHelper.asBean(k,getResponse());
    }



}
