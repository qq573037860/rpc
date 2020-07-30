package com.sjq.rpc.register;

import com.google.common.collect.Lists;
import com.sjq.rpc.domain.Instance;
import com.sjq.rpc.domain.RpcException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract  class AbstractRegister implements Register {

    //保证统一个serviceName的监听只注册一个
    private static final Map<String, List<Consumer<List<Instance>>>> CONSUMER_MAP = new ConcurrentHashMap<>();

    @Override
    public void createClient(String registerCenterUrl) {
        doCreateClient(registerCenterUrl);
    }

    @Override
    public void registerInstance(Instance instance) {
        doRegisterInstance(instance);
    }

    @Override
    public void deregisterInstance(Instance instance) {
        doDeregisterInstance(instance);
    }

    @Override
    public List<Instance> selectInstances(Instance instance, boolean healthy) {
        return doSelectInstances(instance, healthy);
    }

    @Override
    public void subscribe(String serviceName, Consumer<List<Instance>> callBack) {
        List<Consumer<List<Instance>>> consumers = CONSUMER_MAP.computeIfAbsent(serviceName, v -> Collections.synchronizedList(Lists.newArrayList()));
        if (consumers.size() == 0) {
            doSubscribe(serviceName, instances -> {
                consumers.forEach(listConsumer -> {
                    listConsumer.accept(instances);
                });
            });
        } else {
            consumers.add(callBack);
        }
    }

    @Override
    public List<ServiceInfo> getSubscribeServices() {
        return doGetSubscribeServices();
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName) {
        return doSelectOneHealthyInstance(serviceName);
    }

    protected abstract void doCreateClient(String registerCenterUrl);

    protected abstract void doRegisterInstance(Instance instance);

    protected abstract void doDeregisterInstance(Instance instance);

    protected abstract List<Instance> doSelectInstances(Instance instance, boolean healthy);

    protected abstract void doSubscribe(String serviceName, Consumer<List<Instance>> callBack);

    protected abstract List<ServiceInfo> doGetSubscribeServices();

    protected abstract Instance doSelectOneHealthyInstance(String serviceName);
}
