package com.sjq.rpc.proxy;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {

    String serverUrl();

    /**
     * 只针对配置了注册中心的rpc服务生效
     * @return
     */
    String serviceName();

    int requestTimeout() default 0;

}
