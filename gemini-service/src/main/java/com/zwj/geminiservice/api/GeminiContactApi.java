package com.zwj.geminiservice.api;

import com.zwj.geminiservice.util.GeminiApiHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/gemini/api")
public class GeminiContactApi {

    @PostMapping("/send")
    public String sendIssue(@RequestParam String issueStr, HttpServletResponse response) {
        if (null == issueStr || StringUtils.isBlank(issueStr)) {
            throw new RuntimeException("参数不能为空！");
        }
      return GeminiApiHelper.buildHelper().chatGemini(issueStr, response);
    };
}
