package com.zx.zx2000onlinevideo.api.util;


import com.ykcloud.sdk.utils.StringUtils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 2016/6/9.
 */
public class YoukuSign {

    public static String signApiRequest(Map params, String secret, String signMethod) throws IOException {
        // 第一步：检查参数是否已经排序
        String[] keys = (String[]) params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = (String) params.get(key);
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
                query.append(key).append(value);
            }
        }



        // 第三步：使用MD5/HMAC加密
        byte[] bytes;
        if ("HmacSHA256".equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
            // 第四步：把二进制转化为大写的十六进制
            return byte2hex(bytes);
        } else {
            query.append(secret);
            //32位小写md5加密
            return DigestUtils.md5Hex(query.toString());
        }

    }

    public static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toLowerCase());
        }
        return sign.toString();
    }
}
