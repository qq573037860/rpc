package com.sjq.rpc.spring.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@RpcComponentScan
public @interface EnableRpc {

    @AliasFor(annotation = RpcComponentScan.class, attribute = "basePackages")
    String[] basePackages() default {};

}
