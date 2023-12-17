package com.zwj.geminiservice.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);
    // 发送 GET 请求
    public static String sendGetRequest(String urlString) {
        try {
            logger.info("请求外部api的URL：{}", urlString);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 GET
            connection.setRequestMethod("GET");

            // 设置连接和读取超时时间（可选）
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 获取响应代码
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                logger.info("请求外部api返回内容：{}", response);
                return response.toString();
            } else {
                // 处理响应失败的情况
                throw new RuntimeException("GET request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            logger.error("请求外部接口异常",e);
            throw new RuntimeException("GET request failed with exception: " + e.getMessage());
        }
    }

    // 发送 POST 请求
    public static String sendPostRequest(String urlString, String requestBody) {
        try {
            logger.info("请求外部api的URL：{}  参数：{}", urlString,requestBody);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 设置连接和读取超时时间（可选）
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(50000);

            // 设置请求头 (可选)
            connection.setRequestProperty("Content-Type", "application/json");

            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应代码
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                logger.info("请求外部api返回内容：{}", response);
                return response.toString();
            } else {
                // 处理响应失败的情况
                throw new RuntimeException("POST request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            logger.error("请求外部接口异常",e);
            throw new RuntimeException("POST request failed with exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String getUrl = "https://jsonplaceholder.typicode.com/posts/1";
        String postUrl = "https://jsonplaceholder.typicode.com/posts";
        String postRequestBody = "{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}";

        String getResponse = sendGetRequest(getUrl);
        System.out.println("GET Response:\n" + getResponse);

        String postResponse = sendPostRequest(postUrl, postRequestBody);
        System.out.println("POST Response:\n" + postResponse);
    }

    public static String sendPostRequest(String urlString, String requestBody, HttpServletResponse response) {
        try {
            response.setContentType("text/event-stream;charset=utf-8");
            logger.info("请求外部api的URL：{}  参数：{}", urlString,requestBody);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为 POST
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 设置连接和读取超时时间（可选）
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 设置请求头 (可选)
            connection.setRequestProperty("Content-Type", "application/json");

            // 发送请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 获取响应代码
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseStr = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.getOutputStream().write(line.getBytes());
                    responseStr.append(line);
                }
                reader.close();
                logger.info("请求外部api返回内容：{}", responseStr);
                return responseStr.toString();
            } else {
                // 处理响应失败的情况
                throw new RuntimeException("POST request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            logger.error("请求外部接口异常",e);
            throw new RuntimeException("POST request failed with exception: " + e.getMessage());
        }
    }
}
