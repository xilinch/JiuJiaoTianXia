package com.tangsoft.xkr.jiujiaotianxia.webservice;


import com.tangsoft.xkr.jiujiaotianxia.api.ApiConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseWebService {
    private static final Logger logger = LoggerFactory.getLogger(BaseWebService.class);
    protected int getDefaultPageSize(){
        return ApiConfig.PAGE_SIZE;
    }
    protected String service(String uri) {
        String HOST = ApiConfig.getHost();
        if(HOST==null ||HOST.length()==0)
            return "";
        String url = String.format("%s%s", HOST, uri);
        logger.debug("URL={}",url);
        return url;
    }
    protected static final String op(Class<?> glass, String op){
        return String.format("/%s.asmx/%s",glass.getSimpleName(),op);
    }


    protected String serviceWxPay(String uri) {
        String HOST = ApiConfig.getHost()+"/WxPay";
        if(HOST==null ||HOST.length()==0)
            return "";
        String url = String.format("%s%s", HOST, uri);
        logger.debug("URL={}",url);
        return url;
    }

    protected String serviceAlipayPay(String uri) {
        String HOST = ApiConfig.getHost()+"/Alipay";
        if(HOST==null ||HOST.length()==0)
            return "";
        String url = String.format("%s%s", HOST, uri);
        logger.debug("URL={}",url);
        return url;
    }

    protected String serviceUnionPay(String uri) {
        String HOST = ApiConfig.getHost()+"/UnionPay";
        if(HOST==null ||HOST.length()==0)
            return "";
        String url = String.format("%s%s", HOST, uri);
        logger.debug("URL={}",url);
        return url;
    }
}
