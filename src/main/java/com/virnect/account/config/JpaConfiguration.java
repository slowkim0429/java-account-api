package com.virnect.account.config;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.virnect.account.security.Auditor;

@Configuration
@EnableJpaAuditing
public class JpaConfiguration {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public AuditorAware<Long> auditorProvider() {
		return new Auditor();
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}
}
