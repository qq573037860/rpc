package com.sjq.rpc.spring.utils;

import com.sjq.rpc.support.proxy.ClassUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Objects;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

public class BeanDefinitionRegistryUtils {

    public static void registerBean(Class<?> cls, BeanDefinitionRegistry registry) {
        registerBean(cls, null, registry);
    }

    public static void registerBean(Class<?> cls, Object constructorArgValue, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(ClassUtils.humpClassName(cls))) {
            BeanDefinitionBuilder builder = rootBeanDefinition(cls);
            if (Objects.nonNull(constructorArgValue)) {
                builder.addConstructorArgValue(constructorArgValue);
            }
            builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, registry);
        }
    }
}
