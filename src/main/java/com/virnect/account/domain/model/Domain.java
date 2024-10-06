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
@Table(name = "domains",
	indexes = {
		@Index(name = "idx_record_name", columnList = "record_name"),
		@Index(name = "idx_service_region_id", columnList = "service_region_id"),
		@Index(name = "idx_created_by", columnList = "created_by"),
		@Index(name = "idx_updated_by", columnList = "updated_by"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Domain extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "url", nullable = false, length = 100)
	private String url;

	@Column(name = "record_name", nullable = false, length = 20)
	private String recordName;

	@Column(name = "record_value", nullable = false, length = 100)
	private String recordValue;

	@Column(name = "service_region_id")
	private Long serviceRegionId;
}
