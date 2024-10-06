package com.virnect.account.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "async.mail-task")
@Getter
@Setter
public class MailTaskConfigurationProperties {
	private Integer maxPoolSize = 10;
	private Integer corePoolSize = 5;
	private Integer queueCapacity = 20;
}
