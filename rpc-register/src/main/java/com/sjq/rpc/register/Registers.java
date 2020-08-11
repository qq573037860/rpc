package com.sjq.rpc.register;

import com.sjq.rpc.domain.register.Instance;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.domain.register.RegisterInfo;
import com.sjq.rpc.support.spi.ServiceLoaders;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Registers {

    private static final Map<String, Register> REGISTER_MAP = new ConcurrentHashMap<>();

    public static Register getRegisterCenter(RegisterInfo registerInfo) {
        return REGISTER_MAP.computeIfAbsent(registerInfo.getUrl(), value -> {
            Register register = getRegister(registerInfo);
            register.createClient(registerInfo.getUrl());
            return register;
        });
    }

    public static Register getRegisterCenterWithRegister(ServerConfig serverConfig) {
        RegisterInfo registerInfo = serverConfig.getRegister();
        return REGISTER_MAP.computeIfAbsent(registerInfo.getUrl() + "#" + registerInfo.getServiceName(), value -> {
            Register register = getRegister(registerInfo);
            register.createClient(registerInfo.getUrl());
            register.registerInstance(new Instance(registerInfo.getServiceName(), serverConfig.getServerIp(), serverConfig.getServerPort(), true));
            return register;
        });
    }

    private static Register getRegister(RegisterInfo registerInfo) {
        return ServiceLoaders.load(Register.class, registerInfo.getType());
    }

}
