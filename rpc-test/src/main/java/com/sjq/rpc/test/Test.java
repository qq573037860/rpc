package com.sjq.rpc.test;

import com.sjq.rpc.RpcBootstrap;
import com.sjq.rpc.RpcServerBootstrap;
import com.sjq.rpc.domain.Constants;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.test.test1.test1_1;
import com.sjq.rpc.test.test1.test2.api.test2_1;
import com.sjq.rpc.test.test1.test2.api.test2_2;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Test {

    private static final ClassLoader cl = Thread.currentThread().getContextClassLoader();

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread b = new Thread(() -> {
            RpcBootstrap bootstrap = new RpcBootstrap();
            ServerConfig serverConfig = new ServerConfig();
            serverConfig.setRequestTimeout(1000*60*60);
            bootstrap.serverConfig(serverConfig).start();
            System.out.println("RpcBootstrap 启动完毕");

            test2_1 service = bootstrap.getBean(test2_1.class);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 5; i++) {
                long st = System.currentTimeMillis();
                System.out.println("结果：" + service.hello("你好呀~~~"));
                System.out.println("耗时：" + (System.currentTimeMillis() - st));
                st = System.currentTimeMillis();
                System.out.println("结果：" + service.hello("你好呀~~~"));
                System.out.println("耗时：" + (System.currentTimeMillis() - st));
            }

            System.out.println("==================================================================");


            test2_2 service2 = bootstrap.getBean(test2_2.class);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 5; i++) {
                long st = System.currentTimeMillis();
                System.out.println("结果：" + service2.hello("你好呀~~~"));
                System.out.println("耗时：" + (System.currentTimeMillis() - st));
                st = System.currentTimeMillis();
                System.out.println("结果：" + service2.hello("你好呀~~~"));
                System.out.println("耗时：" + (System.currentTimeMillis() - st));
            }


            /*System.out.println("==================================当前==================================");
            TextRunningThread.listAllThreads();*/
        });
        b.setName("client");
        b.start();

        /*Thread thread = new Thread(() -> {
            System.out.println("==================================退出前==================================");
            TextRunningThread.listAllThreads();
        });
        thread.setName("print");
        Runtime.getRuntime().addShutdownHook(thread);*/
        Test test = new Test();
        synchronized (test) {
            test.wait();
        }
    }

}
