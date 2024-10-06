package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
	@Column(name = "created_at", updatable = false, nullable = false)
	@CreationTimestamp
	private ZonedDateTime createdDate;

	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private ZonedDateTime updatedDate;
}
