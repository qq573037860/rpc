package com.sjq.rpc.proxy;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServer {

    /**
     * 当配置了注册中心的改配置才生效
     * @return
     */
    String serviceName() default "";

}
