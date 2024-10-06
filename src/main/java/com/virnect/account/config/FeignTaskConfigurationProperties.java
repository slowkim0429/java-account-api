package com.virnect.account.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "async.feign-task")
@Getter
@Setter
public class FeignTaskConfigurationProperties {
	private Integer maxPoolSize = 30;
	private Integer corePoolSize = 10;
	private Integer queueCapacity = 50;
}
