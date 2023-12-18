package com.zwj.xfyun.service.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zwj.xfyun.service.constants.XfyunConstants;
import okhttp3.HttpUrl;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author zhouwenjie
 * @Description 讯飞星火api请求工具类
 * @Date 2023/10/31 14:25
 **/
public class XfyunApiHelper {

    private static Map<String, String> tokenMap = new ConcurrentHashMap<String, String>(){{
        put("token", "24.c73f2b2abdc6fb00d2526f3e6e23e088.2592000.1700468662.282335-36451778");
    }};
    private XfyunApiHelper() {
    }

    private static XfyunApiHelper xfyunApiHelper = new XfyunApiHelper();

    private static Lock lock = new ReentrantLock();
    public static XfyunApiHelper buildHelper() {
        return xfyunApiHelper;
    }

    public String getToken() {
        return getToken(false);
    }
    /**
     * 获取请求token
     * @param isUpdate 是否更新
     * @return
     */
    public String getToken(boolean isUpdate) {
        final String token_key = "token";
        String token = tokenMap.get(token_key);
        try {
            if (isUpdate || StringUtils.isEmpty(token)) {
                lock.lock();
                if (isUpdate || StringUtils.isEmpty(token)) {
                    String resultStr = HttpClient.sendGetRequest(getAuthUrl(XfyunConstants.HOST_URL, XfyunConstants.API_KEY, XfyunConstants.API_SECRET));
                    handleError(resultStr);
                    JSONObject resultObj = JSON.parseObject(resultStr);
                    token = resultObj.getString("access_token");
                }
                tokenMap.put(token_key, token);
                lock.unlock();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;
    }

    private void handleError(String resultStr) {
        if (StringUtils.isEmpty(resultStr)) {
            return;
        }
        JSONObject resultObj = JSON.parseObject(resultStr);
        if (StringUtils.isNotBlank(resultObj.getString("error"))) {
            if (Arrays.asList(
                    "Access token invalid or no longer valid",
                    "Access token expired",
                    "Get service token failed"
            ).contains(resultObj.getString("error"))) {
                tokenMap.clear();
            }
            throw new RuntimeException(resultObj.getString("error_description"));
        }
    }

    private String buildTokenBody() {
        JSONObject bodyParam = new JSONObject();
        bodyParam.put("grant_type", "client_credentials");
        bodyParam.put("client_id", XfyunConstants.API_KEY);
        bodyParam.put("client_secret", XfyunConstants.API_SECRET);
        return bodyParam.toJSONString();
    }

    /**
     * 使用ERNIE-Bot-4进行对话
     * @return
     */
    public String chatBot4(String issueStr, HttpServletResponse response) {
        String token = getToken();
        String resultStr = HttpClient.sendPostRequest(XfyunConstants.CHAT_BOT_4_API + "?access_token=" + token,
                buildChatBot4Body(issueStr));
        handleError(resultStr);
        JSONObject resultObj = JSON.parseObject(resultStr);
        return  resultObj.getString("result");
    }

    private String buildChatBot4Body(String issueStr) {
        JSONObject param = new JSONObject();
        param.put("messages", JSON.parseArray(issueStr));
        param.put("stream", false); //返回数据格式
        return param.toJSONString();
    }

    // 鉴权方法
    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        return httpUrl.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new XfyunApiHelper().getAuthUrl(XfyunConstants.HOST_URL, XfyunConstants.API_KEY, XfyunConstants.API_SECRET));
    }
}
