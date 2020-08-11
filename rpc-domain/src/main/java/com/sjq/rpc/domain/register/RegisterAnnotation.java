package com.sjq.rpc.domain.register;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterAnnotation {

    String url() default "";

    String type() default "";

    String serviceName() default "";

}
