package com.sjq.rpc.test;

import com.sjq.rpc.spring.annotation.EnableRpc;
import com.sjq.rpc.spring.annotation.RpcReference;
import com.sjq.rpc.test.test1.test2.api.test2_1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@EnableRpc
@SpringBootApplication(scanBasePackages = {"com.sjq.rpc"})
public class SpringClientTest {

    @RpcReference
    private test2_1 test2_1;

    private Integer connectTimeout;

    public static void main(String[] args) {
        SpringApplication.run(SpringClientTest.class, args);
    }

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(test2_1.hello("你好呀~"));;
        }).start();
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
