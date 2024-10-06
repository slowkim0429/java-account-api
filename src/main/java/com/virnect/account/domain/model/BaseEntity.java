package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity {
	@CreatedBy
	@Column(name = "created_by", updatable = false, nullable = false)
	private Long createdBy;

	@Column(name = "updated_by", nullable = false)
	@LastModifiedBy
	private Long lastModifiedBy;
}
