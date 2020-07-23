package com.sjq.rpc.register;

import com.sjq.rpc.domain.Instance;

import java.util.List;
import java.util.function.Consumer;

public interface Register {

    void createClient(String registerCenterUrl);

    void registerInstance(Instance instance);

    void deregisterInstance(Instance instance);

    List<Instance> selectInstances(Instance instance, boolean healthy);

    void subscribe(String serviceName, Consumer<List<Instance>> callBack);

    List<ServiceInfo> getSubscribeServices();

    Instance selectOneHealthyInstance(String serviceName);

}
