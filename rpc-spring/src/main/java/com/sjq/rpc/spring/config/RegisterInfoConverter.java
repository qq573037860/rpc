package com.sjq.rpc.spring.config;

import com.sjq.rpc.domain.register.RegisterInfo;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

@Configuration
@ConfigurationPropertiesBinding
public class RegisterInfoConverter implements Converter<Map<String, String>, RegisterInfo> {

    @Override
    public RegisterInfo convert(Map<String, String> map) {
        return new RegisterInfo(map.get("url"), map.get("type"), map.get("serviceName"));
    }

}
