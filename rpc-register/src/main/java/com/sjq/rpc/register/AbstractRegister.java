package com.sjq.rpc.register;

import com.sjq.rpc.domain.Instance;

import java.util.List;
import java.util.function.Consumer;

public abstract  class AbstractRegister implements Register {

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
        doSubscribe(serviceName, callBack);
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
