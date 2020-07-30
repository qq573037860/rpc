package com.sjq.rpc.spring.register;

import com.sjq.rpc.protocol.Protocol;
import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.spring.annotation.EnableRpc;
import com.sjq.rpc.spring.annotation.RpcComponentScan;
import com.sjq.rpc.spring.config.SpringServerConfig;
import com.sjq.rpc.support.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.Objects;

public class ServerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(ServerBeanFactoryPostProcessor.class);

    private String[] basePackages;

    public ServerBeanFactoryPostProcessor(Class<?> introspectedClass) {
        initRpcConfig(introspectedClass);
    }

    private void initRpcConfig(Class<?> introspectedClass) {
        EnableRpc enableRpc = introspectedClass.getAnnotation(EnableRpc.class);
        String[] basePackages = enableRpc.basePackages();
        if (basePackages.length == 0) {
            RpcComponentScan rpcComponentScan = introspectedClass.getAnnotation(RpcComponentScan.class);
            if (Objects.nonNull(rpcComponentScan)) {
                basePackages = rpcComponentScan.basePackages();
            }
        }
        if (basePackages.length == 0) {
            basePackages = new String[]{""};
        }
        this.basePackages = basePackages;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        registerServerBean(beanFactory);
    }

    private void registerServerBean(ConfigurableListableBeanFactory beanFactory) {
        Protocol protocol = beanFactory.getBean(Protocol.class);
        SpringServerConfig baseConfig = beanFactory.getBean(SpringServerConfig.class);
        for (String scanPackage: basePackages) {
            PackageScanner.scanClassByPackagePathAndAnnotaion(scanPackage, new Class[]{RpcServer.class})
                    .stream().forEach(cls -> {
                Object bean = null;
                try {
                    bean = beanFactory.getBean(cls);
                } catch (Exception e) {
                }
                if (Objects.isNull(bean)) {
                    try {
                        bean = cls.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                protocol.referToInvoker(bean, cls.getInterfaces()[0], baseConfig);
                logger.info(cls + "服务暴露成功");
            });
        }
    }
}
