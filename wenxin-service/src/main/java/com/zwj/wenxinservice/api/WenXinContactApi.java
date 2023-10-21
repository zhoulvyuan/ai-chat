package com.zwj.wenxinservice.api;

import com.alibaba.fastjson2.JSONArray;
import com.zwj.wenxinservice.util.WenXinApiHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/wen-xin/api")
public class WenXinContactApi {

    @PostMapping("/send")
    public String sendIssue(@RequestBody JSONArray issueStr, HttpServletResponse response) {
        if (null == issueStr || StringUtils.isBlank(issueStr.toJSONString())) {
            throw new RuntimeException("参数不能为空！");
        }
      return WenXinApiHelper.buildHelper().chatBot4(issueStr.toJSONString(), response);
    };
}
