package com.sjq.rpc.spring.annotation;

import com.sjq.rpc.spring.register.RpcComponentScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RpcComponentScanRegistrar.class)
public @interface RpcComponentScan {

    String[] basePackages() default {};

}
