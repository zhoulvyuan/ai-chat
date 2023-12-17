package com.zwj.wenxinservice.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zwj.wenxinservice.constants.WenXinConstants;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文心一言api请求工具类
 */
public class WenXinApiHelper {

    private static Map<String, String> tokenMap = new ConcurrentHashMap<String, String>(){{
        put("token", "24.c73f2b2abdc6fb00d2526f3e6e23e088.2592000.1700468662.282335-36451778");
    }};
    private WenXinApiHelper() {
    }

    private static WenXinApiHelper wenXinApiHelper = new WenXinApiHelper();

    private static Lock lock = new ReentrantLock();
    public static WenXinApiHelper buildHelper() {
        return wenXinApiHelper;
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
        if (isUpdate || StringUtils.isEmpty(token)) {
            lock.lock();
            if (isUpdate || StringUtils.isEmpty(token)) {
                String resultStr = HttpClient.sendPostRequest(WenXinConstants.GET_TOKEN_API,buildTokenBody());
                handleError(resultStr);
                JSONObject resultObj = JSON.parseObject(resultStr);
                token = resultObj.getString("access_token");
            }
            tokenMap.put(token_key, token);
            lock.unlock();
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
        bodyParam.put("client_id", WenXinConstants.API_KEY);
        bodyParam.put("client_secret", WenXinConstants.SECRET_KEY);
        return bodyParam.toJSONString();
    }

    /**
     * 使用ERNIE-Bot-4进行对话
     * @return
     */
    public String chatBot4(String issueStr, HttpServletResponse response) {
        String token = getToken();
        String resultStr = HttpClient.sendPostRequest(WenXinConstants.CHAT_BOT_4_API + "?access_token=" + token,
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
}
