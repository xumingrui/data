package com.huaneng.data.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SsqLotteryServiceImpl {

    /** 中国福彩网开奖公告查询接口 */
    private static final String API_URL = "https://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice";

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    /**
     * 按期号获取双色球开奖结果
     *
     * @param issue
     *            期号，格式为“年份 + 3位期号”，如 2024001
     */
    public JSONObject fetchByIssue(String issue) throws IOException {
        String url = API_URL + "?name=ssq&issueStart=" + issue + "&issueEnd=" + issue;

        HttpClient client = new HttpClient();
        client.getParams().setConnectionManagerTimeout(10_000); // 连接超时
        client.getParams().setSoTimeout(10_000); // 读取超时

        GetMethod get = new GetMethod(url);
        try {
            // 反爬要求：必须携带 Referer，否则服务器拒绝响应
            get.setRequestHeader("Referer", "https://www.cwl.gov.cn/kjxx/ssq/kjgg/");
            get.setRequestHeader("User-Agent", USER_AGENT);
            get.setRequestHeader("Accept", "application/json");

            int status = client.executeMethod(get);

            // 注意：不要用 getResponseBodyAsString()，
            // 3.1 不会按 UTF-8 解码，开奖日期里的中文星期会乱码
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(get.getResponseBodyAsStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                reader.close();
            }

            if (status != 200) {
                throw new IOException("请求失败，HTTP 状态码：" + status + "，响应：" + sb);
            }

            String body = sb.toString();

            // fetchByIssue 中替换掉 objectMapper.readTree(...) 一段：
            if (body.isEmpty() || body.contains("\"result\":[]")) {
                throw new IllegalStateException("未查询到期号 " + issue + " 的开奖结果");
            }
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            JsonArray result = root.getAsJsonArray("result");
            if (result == null || result.size() == 0) {
                throw new IllegalStateException("未查询到期号 " + issue + " 的开奖结果");
            }
            JsonObject node = result.get(0).getAsJsonObject();
            JSONObject r = new JSONObject();
            r.put("num", node.get("code").getAsString());
            r.put("yldate", node.get("date").getAsString().substring(0, 10));
            r.put("week", node.get("week").getAsString());
            r.put("red", node.get("red").getAsString());
            r.put("blue", node.get("blue").getAsString());
            r.put("sales", node.get("sales").getAsString());
            r.put("poolmoney", node.get("poolmoney").getAsString());
            return r;
        } finally {
            get.releaseConnection(); // 必须释放连接，否则连接池耗尽
        }
    }

    public static void main(String[] args) throws Exception {
        SsqLotteryServiceImpl service = new SsqLotteryServiceImpl();
        JSONObject result = service.fetchByIssue("2024001");
        System.out.println(result);
    }
}