package com.sjq.rpc.spring.register;

import com.alibaba.spring.beans.factory.annotation.AbstractAnnotationBeanPostProcessor;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.protocol.Protocol;
import com.sjq.rpc.proxy.RpcClient;
import com.sjq.rpc.spring.annotation.RpcReference;
import com.sjq.rpc.spring.config.SpringServerConfig;
import com.sjq.rpc.support.StringUtils;
import com.sjq.rpc.support.proxy.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAttributes;

import static com.alibaba.spring.util.AnnotationUtils.getAttributes;

public class ReferenceAnnotationBeanPostProcessor extends AbstractAnnotationBeanPostProcessor implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public ReferenceAnnotationBeanPostProcessor() {
        super(RpcReference.class);
    }

    @Override
    protected Object doGetInjectedBean(AnnotationAttributes attributes, Object bean, String beanName, Class<?> injectedType,
                                       InjectionMetadata.InjectedElement injectedElement) throws Exception {
        Protocol protocol = applicationContext.getBean(Protocol.class);
        return protocol.referToProxy(injectedType, applicationContext.getBean(SpringServerConfig.class));
    }

    @Override
    protected String buildInjectedObjectCacheKey(AnnotationAttributes attributes, Object bean, String beanName,
                                                 Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) {
        return buildReferencedBeanName(attributes, injectedType) +
                "#source=" + (injectedElement.getMember()) +
                "#attributes=" + getAttributes(attributes, getEnvironment());
    }

    private String buildReferencedBeanName(AnnotationAttributes attributes, Class<?> injectedType) {
        return String.format("injectedType=%s",
                ClassUtils.fullClassName(injectedType));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
