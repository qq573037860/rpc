package com.sjq.rpc.spring.config;


import com.sjq.rpc.domain.ServerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app-config", ignoreInvalidFields = true)
public class SpringServerConfig extends ServerConfig {

}
