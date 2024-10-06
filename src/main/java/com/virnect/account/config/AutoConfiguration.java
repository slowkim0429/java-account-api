package com.virnect.account.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({FeignTaskConfigurationProperties.class, MailTaskConfigurationProperties.class})
public class AutoConfiguration {
}
