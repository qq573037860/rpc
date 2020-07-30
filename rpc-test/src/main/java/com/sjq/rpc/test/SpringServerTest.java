package com.sjq.rpc.test;

import com.sjq.rpc.spring.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*@EnableRpc
@SpringBootApplication(scanBasePackages = {"com.sjq.rpc"})*/
public class SpringServerTest {

    public static void main(String[] args) {
        SpringApplication.run(SpringServerTest.class, args);
    }

}
