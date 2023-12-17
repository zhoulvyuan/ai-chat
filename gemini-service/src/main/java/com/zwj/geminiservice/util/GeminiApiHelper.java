package com.zwj.geminiservice.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zwj.geminiservice.constants.GeminiConstants;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 文心一言api请求工具类
 */
public class GeminiApiHelper {

    private GeminiApiHelper() {
    }

    private static GeminiApiHelper geminiApiHelper = new GeminiApiHelper();

    public static GeminiApiHelper buildHelper() {
        return geminiApiHelper;
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
            }
            throw new RuntimeException(resultObj.getString("error_description"));
        }
    }

    /**
     * 使用ERNIE-Bot-4进行对话
     * @return
     */
    public String chatGemini(String issueStr, HttpServletResponse response) {
        String resultStr = HttpClient.sendPostRequest(GeminiConstants.CHAT_GEMINI_API + "?key=" + GeminiConstants.API_KEY,
                buildChatGeminiBody(issueStr));
        handleError(resultStr);
        JSONObject resultObj = JSON.parseObject(resultStr);
        return  resultObj.getString("text");
    }

    private String buildChatGeminiBody(String issueStr) {
        JSONObject param = new JSONObject();
        JSONArray contents = new JSONArray();
        param.put("contents", contents);

        //构建一次会话的聊天内容
        JSONObject contentItem = new JSONObject();
        contentItem.put("role", "user");
        JSONArray parts = new JSONArray();
        contentItem.put("parts", parts);
        parts.add(new JSONObject(){{put("text",issueStr);}});
        contents.add(contentItem);
        return param.toJSONString();
    }
}
