package com.sjq.rpc.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class HttpClientTest {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 2; i++) {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://127.0.0.1:8001/hello");
            post.setEntity(new StringEntity("你好啊"));
            long st = System.currentTimeMillis();
            CloseableHttpResponse response = client.execute(post);
            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            System.out.println("耗时：" + (System.currentTimeMillis() - st));
            response.close();
            client.close();
            Thread.sleep(100);
        }
    }



}
