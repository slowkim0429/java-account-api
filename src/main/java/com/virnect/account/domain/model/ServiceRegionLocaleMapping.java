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
@Table(name = "service_region_locale_mappings",
	indexes = {
		@Index(name = "idx_code", columnList = "code"),
		@Index(name = "idx_service_region_id", columnList = "service_region_id"),
		@Index(name = "idx_created_by", columnList = "created_by"),
		@Index(name = "idx_updated_by", columnList = "updated_by"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceRegionLocaleMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "code", nullable = false, length = 30, unique = true)
	private String code;

	@Column(name = "continent", nullable = false, length = 100)
	private String continent;

	@Column(name = "sub_continent", nullable = false, length = 100)
	private String subContinent;

	@Column(name = "intermediate_continent", length = 100)
	private String intermediateContinent;

	@Column(name = "service_region_id", nullable = false)
	private Long serviceRegionId;

	@Column(name = "country_calling_code", nullable = false)
	private Integer countryCallingCode;
}
