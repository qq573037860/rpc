package com.sjq.rpc.register;

import com.sjq.rpc.domain.Instance;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.support.spi.ServiceLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Registers {

    private static final Map<String, Register> REGISTER_MAP = new ConcurrentHashMap<>();

    public static Register getRegisterCenter(String registerCenterUrl) {
        return REGISTER_MAP.computeIfAbsent(registerCenterUrl, value -> {
            Register register = ServiceLoader.load(Register.class);
            register.createClient(registerCenterUrl);
            return register;
        });
    }

    public static Register getRegisterCenterWithRegister(ServerConfig serverConfig) {
        return REGISTER_MAP.computeIfAbsent(serverConfig.getRegisterCenterUrl() + serverConfig.getRegisterServiceName(), value -> {
            Register register = ServiceLoader.load(Register.class);
            register.createClient(serverConfig.getRegisterCenterUrl());
            register.registerInstance(new Instance(serverConfig.getRegisterServiceName(), serverConfig.getServerIp(), serverConfig.getServerPort(), true));
            return register;
        });
    }


}
