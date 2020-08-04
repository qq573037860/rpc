package com.sjq.rpc.proxy;

import com.sjq.rpc.domain.RegisterAnnotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServer {

    /**
     * 链接注册中心信息
     * @return
     */
    RegisterAnnotation[] register() default {};

}
