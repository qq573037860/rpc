package com.sjq.rpc.proxy;

import com.sjq.rpc.domain.register.RegisterAnnotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServer {

    /**
     * 连接注册中心信息
     * @return
     */
    RegisterAnnotation[] register() default {};

}
