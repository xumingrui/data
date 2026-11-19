package com.huaneng.data.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.util.StringUtils;

import java.io.*;

;

@Slf4j
public class SoapUtil {
    /**
     * 使用SOAP1.1发送消息
     *
     * @param apiHost
     * @param soapXml
     * @param methodName
     * @return
     */
    public static String doPostSoap(String apiHost, String soapXml, String methodName) throws IOException {
        PostMethod postMethod = new PostMethod(apiHost);
        byte[] bytesArray = soapXml.toString().getBytes("utf-8");
        InputStream is = new ByteArrayInputStream(bytesArray, 0, bytesArray.length);
        postMethod.setRequestEntity(new InputStreamRequestEntity(is, bytesArray.length, "text/xml; charset=UTF-8"));

        HttpClient httpClient = new HttpClient();
        httpClient.setConnectionTimeout(5000);
        httpClient.setTimeout(5000);
        String result = null;
        try {
            int status = httpClient.executeMethod(postMethod);
            if (status == 200) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();
                String res = "";
                while ((res = br.readLine()) != null) {
                    stringBuffer.append(res);
                }
                res = stringBuffer.toString();
                if (StringUtils.isEmpty(res) || res.indexOf("<return>") < 0) {
                    log.error("调用邮箱服务-【ERROR】：【{}】 返回为空, res=【{}】", methodName, res);
                    return result;
                }
                Document doc = Jsoup.parse(res);
                Element element = doc.getElementsByTag("return").get(0);
                Integer code = Integer.parseInt(element.getElementsByTag("code").get(0).text());
                if (code != 0) {
                    String message = ErrCodeEnum.message(code);
                    log.error("调用邮箱服务-【ERROR】，方法：【{}】，错误信息：【{}】", methodName, message);
                    return null;
                }
                result = element.getElementsByTag("result").get(0).text();
                return result;
            } else {
                log.error("调用邮箱服务-【ERROR】：方法：【{}】 ，status=【{}】", methodName, status);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("调用邮箱服务-【ERROR】,错误信息:{}", e.getMessage());
            return result;
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
            if (is != null) {
                is.close();
            }

        }

    }

    /**
     * 访问webservice接口
     *
     * @param namespace
     *            命名空间
     * @param postUrl
     *            webservice接口地址
     * @param method
     *            方法名
     * @param paramsNum
     *            参数个数
     * @param params
     *            参数组
     * @return 返回值
     */
    public static String SplicingMessage(String namespace, String postUrl, String method, int paramsNum,
        String[] params) throws IOException {
        // 自定义soap报文模板 注意：xmlns:web 与<soapenv:Body>后的属性（web）要一致
        StringBuffer soapXml = new StringBuffer();
        soapXml.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"  xmlns:apiw=\""
            + namespace + "\">");
        soapXml.append("<soapenv:Header/>");
        soapXml.append("<soapenv:Body>");
        soapXml.append("<apiw:" + method + ">");
        for (int i = 0; i < paramsNum; i++) {
            soapXml.append(params[i]);
        }
        soapXml.append("</apiw:" + method + ">");

        soapXml.append("</soapenv:Body></soapenv:Envelope>");
        String result = doPostSoap(postUrl, soapXml.toString(), method);
        return result;
    }
}
