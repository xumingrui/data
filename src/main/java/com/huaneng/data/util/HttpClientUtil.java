package com.huaneng.data.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HttpClientUtil {
    public static String doGet(String url) {
        // 获取http客户端
        GetMethod getMethod = new GetMethod(url);
        org.apache.commons.httpclient.HttpClient httpClient = new HttpClient();
        httpClient.setConnectionTimeout(5000);
        httpClient.setTimeout(5000);
        try {
            int status = httpClient.executeMethod(getMethod);
            if (status == 200) {
                String resp = getMethod.getResponseBodyAsString();
                return resp;
            } else {
                log.error("httpClient 失败，status=" + status);
                return null;
            }
        } catch (Exception e) {
            log.error("doGet url:【{}】 失败:【{}】", url, e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    public static void doGetDownload(String fileURL,String saveDir,String fileName) throws IOException {
       URL url = new URL(fileURL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(50000);
        connection.setReadTimeout(5000);

        int responseode = connection.getResponseCode();
        if(responseode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = connection.getInputStream();
            String saveFilePath = saveDir+File.separator+fileName;

            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while((bytesRead = inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,bytesRead);
            }
            outputStream.close();
            inputStream.close();
            System.out.println("文件下载成功，保存至:"+saveFilePath);
        }else{
            System.out.println("文件下载失败");
        }
        connection.disconnect();
    }

    public static String doPost(String url, String paramJson) {
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(15000);

        PostMethod postMethod = new PostMethod(url);
        // 设置时间
        postMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        postMethod.addRequestHeader("Content-Type", "application/json");

        try {
            RequestEntity entity = new StringRequestEntity(paramJson, "application/json", "UTF-8");
            postMethod.setRequestEntity(entity);
            int status = httpClient.executeMethod(postMethod);
            if (status == 200) {
                String resp = postMethod.getResponseBodyAsString();
                return resp;
            } else {
                log.error("请求失败,status=【{}】", status);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("post 请求 【{}】失败,错误详情:【{}】", url, e.getMessage());
            return null;
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }

        }
    }
}
