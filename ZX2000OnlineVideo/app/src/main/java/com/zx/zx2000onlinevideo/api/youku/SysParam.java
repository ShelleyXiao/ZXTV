package com.zx.zx2000onlinevideo.api.youku;

/**
 * 关键词排行榜 的系统参数
 * Created by Administrator on 2016-05-04.
 */
public class SysParam {
    /*youku.search.keyword.rankinglist*/
    private String action;
    /*app key*/
    private String client_id;

    /*客户端当前时间戳，精确到秒，timestamp与开放平台请求时间误差为6分钟*/
    private String timestamp;
    /*3.0*/
    private String version;
    /*对API调用参数（除sign外）的md5加密值。详情见签名方法*/
    private String sign;

    private String access_token;
    private String format;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
