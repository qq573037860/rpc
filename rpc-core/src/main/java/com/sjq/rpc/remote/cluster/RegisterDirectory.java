package com.sjq.rpc.remote.cluster;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sjq.rpc.domain.register.Instance;
import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.domain.register.RegisterInfo;
import com.sjq.rpc.register.Register;
import com.sjq.rpc.register.Registers;
import com.sjq.rpc.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RegisterDirectory implements Directory {

    private static final Logger logger = LoggerFactory.getLogger(RegisterDirectory.class);

    private final ServerConfig serviceConfig;
    private final ChannelHandler handler;
    private final boolean useRegisterBalance;
    private final List<Register> REGISTERS = Lists.newArrayList();
    private final Map<String, ExchangeClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public RegisterDirectory(ServerConfig serviceConfig, ChannelHandler handler) {
        this(serviceConfig, handler, true);
    }

    public RegisterDirectory(ServerConfig serviceConfig, ChannelHandler handler, boolean useRegisterBalance) {
        this.serviceConfig = serviceConfig;
        this.handler = handler;
        this.useRegisterBalance = useRegisterBalance;

        init();
    }

    private void init() {
        RegisterInfo registerInfo = serviceConfig.getRegister();
        Register register = Registers.getRegisterCenter(registerInfo);
        if (!REGISTERS.contains(register)) {
            REGISTERS.add(register);
        }
        register.subscribe(registerInfo.getServiceName(), instances -> {
            instances.forEach(instance -> {
                //保存健康实例
                if (instance.isHealthy()) {
                    setAndGetClient(instance);
                }
                logger.info("update service provider list {}", JSONObject.toJSONString(instances));
            });
        });
    }

    private ExchangeClient setAndGetClient(Instance instance) {
        String clientKey = getKey(instance.getIp(), instance.getPort());
        return CLIENT_MAP.computeIfAbsent(clientKey, value ->
                new DefaultExchangeClient(Transporters.getTransporter().connect(instance.getIp(), instance.getPort(), serviceConfig, handler, () -> {
                    //实例关闭回调，移除实例
                    CLIENT_MAP.remove(clientKey);
                })));
    }

    @Override
    public List<ExchangeClient> list(Request request) {
        return Lists.newArrayList(CLIENT_MAP.values());
    }

    @Override
    public ExchangeClient findWithRegister(Request request) {
        List<Register> balanceRegisters = REGISTERS.stream().filter(Register::isSupportBalance).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(balanceRegisters)) {
            throw new RpcException(RpcException.EXECUTION_EXCEPTION, "no available service provider");
        }
        Instance instance = balanceRegisters.get(ThreadLocalRandom.current().nextInt(balanceRegisters.size()))
                .selectOneHealthyInstance(serviceConfig.getRegister().getServiceName());
        if (Objects.isNull(instance)) {
            throw new RpcException(RpcException.EXECUTION_EXCEPTION, "no available service provider");
        }
        return setAndGetClient(instance);
    }

    @Override
    public boolean useRegisterBalance() {
        return useRegisterBalance;
    }
}
