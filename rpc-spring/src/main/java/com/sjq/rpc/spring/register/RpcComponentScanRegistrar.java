package com.sjq.rpc.spring.register;

import com.sjq.rpc.protocol.DefaultProtocol;
import com.sjq.rpc.proxy.JavassistProxyFactory;
import com.sjq.rpc.spring.utils.BeanDefinitionRegistryUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

public class RpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        registerCommonBean(annotationMetadata, beanDefinitionRegistry);
    }

    private void registerCommonBean(AnnotationMetadata annotationMetadata,BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionRegistryUtils.registerBean(DefaultProtocol.class, new JavassistProxyFactory(), beanDefinitionRegistry);
        BeanDefinitionRegistryUtils.registerBean(ReferenceAnnotationBeanPostProcessor.class, beanDefinitionRegistry);
        BeanDefinitionRegistryUtils.registerBean(ServerBeanFactoryPostProcessor.class, ((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass(), beanDefinitionRegistry);
    }

}
