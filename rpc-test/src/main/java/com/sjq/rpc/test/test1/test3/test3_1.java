package com.sjq.rpc.test.test1.test3;


import com.sjq.rpc.spring.annotation.RpcReference;
import com.sjq.rpc.test.test1.test2.api.test2_1;
import org.springframework.stereotype.Component;

@Component
public class test3_1 {

    @RpcReference
    private test2_1 test2_1;

}
