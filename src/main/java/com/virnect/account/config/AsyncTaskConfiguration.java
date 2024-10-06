package com.virnect.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequest;
import com.virnect.account.adapter.inbound.dto.request.CustomHttpServletRequestContextHolder;

@EnableAsync
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AsyncTaskConfiguration {
	private final FeignTaskConfigurationProperties feignTaskConfigurationProperties;
	private final MailTaskConfigurationProperties mailTaskConfigurationProperties;

	@Bean
	public TaskExecutor asyncTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(mailTaskConfigurationProperties.getMaxPoolSize());
		taskExecutor.setCorePoolSize(mailTaskConfigurationProperties.getCorePoolSize());
		taskExecutor.setQueueCapacity(mailTaskConfigurationProperties.getQueueCapacity());
		taskExecutor.setThreadNamePrefix("async-task-exec-");
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(15);
		return taskExecutor;
	}

	@Bean
	public TaskExecutor asyncFeignTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(feignTaskConfigurationProperties.getMaxPoolSize());
		taskExecutor.setCorePoolSize(feignTaskConfigurationProperties.getCorePoolSize());
		taskExecutor.setQueueCapacity(feignTaskConfigurationProperties.getQueueCapacity());
		taskExecutor.setThreadNamePrefix("feign-task-exec-");
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(15);
		taskExecutor.setTaskDecorator(runnable -> {
			CustomHttpServletRequest customHttpServletRequest = CustomHttpServletRequestContextHolder.httpServletRequestContext.get();
			return () -> {
				try {
					CustomHttpServletRequestContextHolder.httpServletRequestContext.set(customHttpServletRequest);
					runnable.run();
				} finally {
					CustomHttpServletRequestContextHolder.httpServletRequestContext.remove();
				}
			};
		});
		return taskExecutor;
	}
}
