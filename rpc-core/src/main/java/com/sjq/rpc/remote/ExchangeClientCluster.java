package com.sjq.rpc.remote;

import com.alibaba.fastjson.JSONObject;
import com.sjq.rpc.domain.Instance;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.register.Register;
import com.sjq.rpc.register.Registers;
import com.sjq.rpc.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ExchangeClientCluster {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeClientCluster.class);

    private static final Map<String, AbstractExchangeClient> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Register> REGISTER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> REGISTER_CLIENT_MAP = new ConcurrentHashMap<>();

    private List<String> keys;
    private boolean useRegisterCenter;
    private String registerServiceName;
    //记录上一次获取client的位置，赋值的代码没有加锁存在并发问题，但影响不大
    private volatile int currentClientIndex = 0;

    private ExchangeClientCluster(List<String> keys) {
        this(keys, false, null);
    }

    private ExchangeClientCluster(List<String> keys, boolean useRegisterCenter, String registerServiceName) {
        this.keys = keys;
        this.useRegisterCenter = useRegisterCenter;
        this.registerServiceName = registerServiceName;
    }

    public static ExchangeClientCluster getClient(ServerConfig serviceConfig, ChannelHandler handler) {
        Objects.requireNonNull(serviceConfig);

        List<String> keys = new ArrayList<>();
        if (serviceConfig.isRegisterCenterForClient()) {
            for (URI uri : serviceConfig.getServerUrls()) {
                String key = getKey(uri.getHost(), uri.getPort());
                Register register = Registers.getRegisterCenter(key);
                String registerKey = getKey(uri.getHost(), uri.getPort(), serviceConfig.getRegisterServiceName());
                if (!REGISTER_MAP.containsKey(registerKey)) {
                    REGISTER_MAP.put(registerKey, register);
                    register.subscribe(serviceConfig.getRegisterServiceName(), instances -> {//监听服务实例
                        List<String> list = REGISTER_CLIENT_MAP.computeIfAbsent(registerKey, value -> Collections.synchronizedList(new ArrayList<>()));
                        instances.stream().forEach(instance -> {
                            //保存健康实例
                            if (instance.isHealthy()) {
                                String clientKey = getKey(instance.getIp(), instance.getPort());
                                CLIENT_MAP.computeIfAbsent(clientKey, value ->
                                        new DefaultExchangeClient(Transporters.getTransporter().connect(instance.getIp(), instance.getPort(), serviceConfig, handler, () -> {
                                            //实例关闭回调，移除实例
                                            CLIENT_MAP.remove(clientKey);
                                        })));
                                list.add(clientKey);
                            }
                        });
                        logger.info("update service provider list {}", JSONObject.toJSONString(instances));
                    });
                }
                keys.add(key);
            }
            return new ExchangeClientCluster(keys, true, serviceConfig.getRegisterServiceName());
        } else {
            for (URI uri : serviceConfig.getServerUrls()) {
                String key = getKey(uri.getHost(), uri.getPort());
                CLIENT_MAP.computeIfAbsent(key, value ->
                        new DefaultExchangeClient(Transporters.getTransporter().connect(uri.getHost(), uri.getPort(), serviceConfig, handler)));
                keys.add(key);
            }
            return new ExchangeClientCluster(keys);
        }
    }

    private static String getKey(String ip, int port, String serviceName) {
        return getKey(getKey(ip, port), serviceName);
    }

    private static String getKey(String ip, int port) {
        return ip + ":" + port;
    }

    private static String getKey(String ipPortInfo, String registerServiceName) {
        return ipPortInfo + "/" + registerServiceName;
    }

    public AbstractExchangeClient getClient() {
        if (Objects.isNull(keys) || keys.isEmpty()) {
            return null;
        }

        if (useRegisterCenter) {
            return getClientByRegister();
        } else {
            return getClientByDefault();
        }
    }

    private AbstractExchangeClient getClientByRegister() {
        int i = 0;
        for (;;) {
            String registerKey = getKey(keys.get(currentClientIndex), registerServiceName);
            currentClientIndex = currentClientIndex == keys.size() - 1 ? 0 : (currentClientIndex + 1);
            if (StringUtils.isNotEmpty(registerKey)) {
                Register register = REGISTER_MAP.get(registerKey);
                if (Objects.nonNull(register)) {
                    Instance instance = register.selectOneHealthyInstance(registerServiceName);
                    if (Objects.nonNull(instance)) {
                        String key = getKey(instance.getIp(), instance.getPort());
                        AbstractExchangeClient client = CLIENT_MAP.get(key);
                        if (Objects.nonNull(client) && client.isActive()) {
                            return client;
                        }
                    }
                }
            }
            if (++i >= keys.size()) {
                break;
            }
        }

        //随机取一个
        Random random = new Random();
        String registerKey = getKey(keys.get(random.nextInt(keys.size())), registerServiceName);
        List<String> clientKeys = REGISTER_CLIENT_MAP.get(registerKey);
        if (Objects.nonNull(clientKeys) && !clientKeys.isEmpty()) {
            return CLIENT_MAP.get(clientKeys.get(random.nextInt(clientKeys.size())));
        }
        return null;
    }

    private AbstractExchangeClient getClientByDefault() {
        int i = 0;
        for (;;) {
            AbstractExchangeClient client = CLIENT_MAP.get(keys.get(currentClientIndex));
            currentClientIndex = currentClientIndex == keys.size() - 1 ? 0 : (currentClientIndex + 1);
            if (Objects.isNull(client) || !client.isActive()) {
                if (++i >= keys.size()) {
                    break;
                }
                continue;
            }
            return client;
        }
        return null;
    }
}
