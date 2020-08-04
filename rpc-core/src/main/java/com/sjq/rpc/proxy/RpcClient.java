package com.sjq.rpc.proxy;

import com.sjq.rpc.domain.RegisterAnnotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcClient {

    String serverUrl() default "";

    /**
     * 链接注册中心信息
     * @return
     */
    RegisterAnnotation[] register() default {};

    int requestTimeout() default 0;

}
