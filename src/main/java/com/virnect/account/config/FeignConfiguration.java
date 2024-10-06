package com.virnect.account.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableFeignClients(basePackages = {"com.virnect.account"})
@RequiredArgsConstructor
public class FeignConfiguration {
	private final ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	public Retryer retryer() {
		return Retryer.NEVER_RETRY;
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	public Encoder feignFormEncoder() {
		return new SpringFormEncoder(new SpringEncoder(messageConverters));
	}
}
