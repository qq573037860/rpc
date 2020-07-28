package com.sjq.rpc.remote.cluster;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sjq.rpc.domain.Instance;
import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.register.Register;
import com.sjq.rpc.register.Registers;
import com.sjq.rpc.remote.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterDirectory implements Directory {

    private static final Logger logger = LoggerFactory.getLogger(RegisterDirectory.class);

    private ServerConfig serviceConfig;
    private ChannelHandler handler;
    private List<Register> registers = Lists.newArrayList();
    private final Map<String, ExchangeClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public RegisterDirectory(ServerConfig serviceConfig, ChannelHandler handler) {
        this.serviceConfig = serviceConfig;
        this.handler = handler;

        init();
    }

    private void init() {
        for (URI uri : serviceConfig.getServerUrls()) {
            String key = getKey(uri.getHost(), uri.getPort());
            Register register = Registers.getRegisterCenter(key);
            if (!registers.contains(register)) {
                registers.add(register);
            }
            register.subscribe(serviceConfig.getRegisterServiceName(), instances -> {
                instances.stream().forEach(instance -> {
                    //保存健康实例
                    if (instance.isHealthy()) {
                        getAndAddClient(instance);
                    }
                    logger.info("update service provider list {}", JSONObject.toJSONString(instances));
                });
            });
        }
    }

    private ExchangeClient getAndAddClient(Instance instance) {
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
        Instance instance = registers.get(new Random().nextInt(registers.size())).selectOneHealthyInstance(serviceConfig.getRegisterServiceName());
        return getAndAddClient(instance);
    }

    @Override
    public boolean isRegisterSupportBalance() {
        return true;
    }
}
