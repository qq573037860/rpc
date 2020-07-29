package com.sjq.rpc.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.register.AbstractRegister;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NacosRegister extends AbstractRegister {

    private NamingService namingService;

    @Override
    protected void doCreateClient(String registerCenterUrl) {
        try {
            this.namingService = NamingFactory.createNamingService(registerCenterUrl);
        } catch (NacosException e) {
            e.printStackTrace();
            throw new RpcException(String.format("连接注册中心[$s]失败", registerCenterUrl), e);
        }
    }

    @Override
    protected void doRegisterInstance(com.sjq.rpc.domain.Instance ins) {
        try {
            namingService.registerInstance(ins.getServiceName(), toNacosInstance(ins));
        } catch (NacosException e) {
            throw new RpcException(String.format("instance[%s]注册失败", ins), e);
        }
    }

    @Override
    protected void doDeregisterInstance(com.sjq.rpc.domain.Instance ins) {
        try {
            namingService.deregisterInstance(ins.getServiceName(), toNacosInstance(ins));
        } catch (NacosException e) {
            throw new RpcException(String.format("instance[%s]注销失败", ins), e);
        }
    }

    @Override
    protected List<com.sjq.rpc.domain.Instance> doSelectInstances(com.sjq.rpc.domain.Instance instance, boolean healthy) {
        try {
            List<Instance> instances = namingService.selectInstances(instance.getServiceName(), healthy);
            return CollectionUtils.isEmpty(instances) ? Collections.emptyList() :
                    instances.stream().map(value -> toRpcInstance(value)).collect(Collectors.toList());
        } catch (NacosException e) {
            throw new RpcException(String.format("查找%s的instance[%s]失败", healthy ? "健康" : "不健康", instance), e);
        }
    }

    @Override
    protected void doSubscribe(String serviceName, Consumer<List<com.sjq.rpc.domain.Instance>> callBack) {
        try {
            namingService.subscribe(serviceName, event -> {
                if (event instanceof NamingEvent) {
                    List<Instance> instances = ((NamingEvent) event).getInstances();
                    callBack.accept(Objects.isNull(instances) ? Collections.emptyList()
                            : instances.stream().map(value -> toRpcInstance(value)).collect(Collectors.toList()));
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
            throw new RpcException(String.format("添加服务[%s]监听失败", serviceName), e);
        }
    }

    @Override
    protected List<com.sjq.rpc.register.ServiceInfo> doGetSubscribeServices() {
        try {
            List<ServiceInfo> list = namingService.getSubscribeServices();
            return Objects.isNull(list) && list.isEmpty() ? Collections.emptyList()
                    : list.stream().map(value -> toRpcServiceInfo(value)).collect(Collectors.toList());
        } catch (NacosException e) {
            throw new RpcException("查询所有监听服务失败", e);
        }
    }

    @Override
    protected com.sjq.rpc.domain.Instance doSelectOneHealthyInstance(String serviceName) {
        try {
            Instance instance = namingService.selectOneHealthyInstance(serviceName);
            return Objects.isNull(instance) ? null : toRpcInstance(instance);
        } catch (NacosException e) {
            throw new RpcException("查询一个健康实例失败", e);
        }
    }

    private com.sjq.rpc.register.ServiceInfo toRpcServiceInfo(ServiceInfo serviceInfo) {
        com.sjq.rpc.register.ServiceInfo info = new com.sjq.rpc.register.ServiceInfo();
        info.setName(serviceInfo.getName());
        return info;
    }

    private Instance toNacosInstance(com.sjq.rpc.domain.Instance ins) {
        Instance instance = new Instance();
        instance.setServiceName(ins.getServiceName());
        instance.setIp(ins.getIp());
        instance.setPort(ins.getPort());
        return instance;
    }

    private com.sjq.rpc.domain.Instance toRpcInstance(Instance ins) {
        com.sjq.rpc.domain.Instance instance = new com.sjq.rpc.domain.Instance(ins.getServiceName(),
                ins.getIp(), ins.getPort(), ins.isHealthy());
        return instance;
    }

    @Override
    public boolean isSupportBalance() {
        return true;
    }
}
