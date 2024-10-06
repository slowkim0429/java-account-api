package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Audited
@Table(name = "service_regions",
	indexes = {
		@Index(name = "idx_code", columnList = "code"),
		@Index(name = "idx_aws_code", columnList = "aws_code"),
		@Index(name = "idx_created_by", columnList = "created_by"),
		@Index(name = "idx_updated_by", columnList = "updated_by"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceRegion extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "code", nullable = false, length = 30, unique = true)
	private String code;

	@Column(name = "aws_code", nullable = false, length = 50)
	private String awsCode;
}
